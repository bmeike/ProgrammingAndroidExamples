package pa.ch16.geo;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.finchframework.finch.rest.RESTfulContentProvider;
import com.finchframework.finch.rest.UriRequestTask;
import com.finchframework.finch.rest.FileHandler;
import org.apache.http.client.methods.HttpGet;
import pa.ch16.Ch16;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Content provider that loads user generated map content from google maps.
 */
public class SiteContentProvider extends RESTfulContentProvider {

    public static final String SITE = "site";
    public static final String SITE_DB = SITE + ".db";

    public static final String SITES_TABLE = "sites";

    private static final int SITES = 0;
    private static final int SITE_ID = 1;

    private static UriMatcher mContentUriMatcher;

    private DatabaseHelper mCacheHelper;

    private ContentResolver mContentResolver;
    private SQLiteDatabase mDb;

    /** url for querying video, expects appending keywords. */
    private String siteQueryStart =
            "http://maps.google.com/maps/user?uid=";
    private String siteQueryEnd = "&output=kml&ptab=2";

    private final Map<String, UriRequestTask> mRequestsInProgress =
            new HashMap<String, UriRequestTask>();

    static {
        // Add more columns here for more robust Live Folders.
        mContentUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mContentUriMatcher.addURI(Geo.AUTHORITY,
                Geo.Sites.SITES, SITES);
        // use of the hash character indicates matching of an id
        mContentUriMatcher.addURI(Geo.AUTHORITY,
                Geo.Sites.SITES + "/#",
                SITE_ID);
    }

    public SiteContentProvider() {
    }

    public SiteContentProvider(Context context) {
        init();
    }

    @Override
    public boolean onCreate() {
        init();
        return true;
    }

    private void init() {
        Context ctx = getContext();
        mContentResolver = ctx.getContentResolver();
        mCacheHelper = new DatabaseHelper(ctx, SITE_DB, null);
        mDb = mCacheHelper.getWritableDatabase();
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder)
    {
        Cursor queryCursor;

        // the query is passed out of band of other information passed
        // to this method -- its not an argument.
        String guid = uri.
                getQueryParameter(Geo.Sites.GUID);
        String queryUri = siteQueryStart + encode(guid) + siteQueryEnd;

        int match = mContentUriMatcher.match(uri);
        switch (match) {
            case SITES:

                if (guid == null) {
                    // A null cursor is an acceptable argument to the method,
                    // CursorAdapter.changeCursor(Cursor c), which interprets
                    // the value by canceling all adapter state so that the
                    // component for which the cursor is adapting data will
                    // display no content.
                    return null;
                }

                String select = Geo.Sites.GUID + " = '" +  guid + "'";

                // quickly return already matching data
                queryCursor =
                        mDb.query(SITES_TABLE, projection,
                                select,
                                selectionArgs,
                                null,
                                null, sortOrder);

                // make the cursor observe the requested query
                queryCursor.setNotificationUri(mContentResolver, uri);


                /**
                 * Always try to update results with the latest data from the
                 * network.
                 *
                 * Spawning an asynchronous load task thread, guarantees that
                 * the load has no chance to bock the UI thread.
                 *
                 * While the request loads, we return the cursor with existing
                 * data to the client.
                 *
                 * If the existing cursor is empty, the UI will render no
                 * content until it receives URI notification.
                 *
                 * Content updates that arrive when the asynchronous network
                 * request completes will appear in the already returned cursor,
                 * since that cursor query will match that of
                 * newly arrived items.
                 */
                if (!"".equals(guid)) {
                    synchronized (mRequestsInProgress) {
                        UriRequestTask requestTask = getRequestTask(guid);
                        if (requestTask == null) {
                            requestTask = newRequestTask(guid, queryUri);
                            Thread t = new Thread(requestTask);
                            // allows other requets to run in parallel.
                            t.start();
                        }
                    }
                }
                break;
            case SITE_ID:
                long siteID = ContentUris.parseId(uri);
                queryCursor =
                        mDb.query(SITES_TABLE, projection,
                                Geo.Sites._ID + " = " + siteID,
                                selectionArgs, null, null, null);
                break;
            default:
                throw new IllegalArgumentException("unsupported uri: " +
                        queryUri);
        }

        return queryCursor;
    }

    /**
     * Provides read only access to files that have been downloaded and stored
     * in the provider cache.
     */
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
            throws FileNotFoundException
    {
        // only support read only files
        if (!"r".equals(mode.toLowerCase())) {
            throw new FileNotFoundException("only read only supported");
        }

        int match = mContentUriMatcher.match(uri);
        switch (match) {
            case SITE_ID:
                long siteID = ContentUris.parseId(uri);
                return ParcelFileDescriptor.open(FileHandler.getFile(siteID),
                        ParcelFileDescriptor.MODE_READ_ONLY);
            default:
                throw new IllegalArgumentException("unsupported uri: " + uri);
        }
    }

    /**
     * Spawns a thread to download bytes from a url and store them in a file,
     * such as for storing a thumbnail.
     *
     * @param idString the database id used to reference the downloaded url.
     */
    public void cacheUri(String idString, String url) {
        // use media id as a unique request tag
        final HttpGet get = new HttpGet(url);
        UriRequestTask thumbTask = new UriRequestTask(
                get, new FileHandler(idString),
                getContext());
        Thread t = new Thread(thumbTask);
        t.start();
    }

    private UriRequestTask getRequestTask(String queryText) {
        return mRequestsInProgress.get(queryText);
    }

    UriRequestTask newRequestTask(String requestTag, String url) {
        UriRequestTask requestTask;

        final HttpGet get = new HttpGet(url);
        SiteHandler handler = new SiteHandler(
                SiteContentProvider.this, mDb, requestTag);
        requestTask = new UriRequestTask(requestTag, this, get,
                handler, getContext());

        mRequestsInProgress.put(requestTag, requestTask);
        return requestTask;
    }

    public void requestComplete(String mQueryText) {
        synchronized (mRequestsInProgress) {
            mRequestsInProgress.remove(mQueryText);
        }
    }

    private String encode(String gDataQuery) {
        try {
            return URLEncoder.encode(gDataQuery, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.d(Ch16.LOG_TAG, "could not decode UTF-8," +
                    " this should not happen");
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        switch (mContentUriMatcher.match(uri)) {
            case SITES:
                return Geo.Sites.CONTENT_SITE_TYPE;
            case SITE_ID:
                return Geo.Sites.CONTENT_SITE_TYPE;
            default:
                throw new IllegalArgumentException("Unknown video type: " +
                        uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues cv) {
        SQLiteDatabase db = mCacheHelper.getWritableDatabase();
        return insert(uri, cv, db);
    }

    private boolean siteExists(SQLiteDatabase db, String msid) {
        Cursor cursor = null;
        try {
            cursor = db.query(SITES_TABLE, null,
                    Geo.Sites.MSID + " = '" + msid + "'",
                    null, null, null, null);
            return (cursor.getCount() > 0);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public Uri insert(Uri uri, ContentValues cv, SQLiteDatabase db) {
        // Validate the requested uri
        int m = mContentUriMatcher.match(uri);
        if (m != SITES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        int match = mContentUriMatcher.match(uri);
        Uri insertUri = null;
        switch (match) {
            case SITES:
                // insert the values into a new database row
                String msid = (String) cv.get(Geo.Sites.MSID);
                if (!siteExists(db, msid)) {
                    long time = System.currentTimeMillis();
                    cv.put(Geo.Sites.TIMESTAMP, time);
                    long rowId = db.insert(SITES_TABLE,
                            Geo.Sites.SITES, cv);
                    if (rowId >= 0) {
                        insertUri =
                                ContentUris.withAppendedId(
                                        Geo.Sites.CONTENT_URI, rowId);
                        mContentResolver.notifyChange(insertUri, null);
                    } else {
                        throw new IllegalStateException("could not insert " +
                                "content values: " + cv);

                    }
                }
                break;
            default:
                throw new IllegalArgumentException("unknown video element: " +
                        uri);
        }

        return insertUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        long id = ContentUris.parseId(uri);
        int affected = mDb.delete(SITES_TABLE,
                Geo.Sites._ID + " = " + id, null);
        mContentResolver.notifyChange(uri, null);
        return affected;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s,
                      String[] strings)
    {
        long id = ContentUris.parseId(uri);
        int affected =
                mDb.update(SITES_TABLE, contentValues,
                        Geo.Sites._ID + " = " + id, null);
        mContentResolver.notifyChange(uri, null);
        return affected;
    }

    private static int DATABASE_VERSION = 2;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private DatabaseHelper(Context context, String name,
                               SQLiteDatabase.CursorFactory factory)
        {
            super(context, name, factory, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            createTable(sqLiteDatabase);
        }

        private void createTable(SQLiteDatabase sqLiteDatabase) {
            String createvideoTable = "create table " + SITES_TABLE + " (" +
                    Geo.Sites._ID + " integer primary key, " +
                    Geo.Sites.TITLE + " text, " +
                    Geo.Sites.DESCRIPTION + " text, " +
                    Geo.Sites.GUID + " text, " +
                    Geo.Sites.MSID + " text, " +
                    Geo.Sites.TIMESTAMP + " text);";
            sqLiteDatabase.execSQL(createvideoTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldv, int newv)
        {
            sqLiteDatabase.execSQL("drop table if exists " + SITES_TABLE + ";");
            createTable(sqLiteDatabase);
        }
    }
}
