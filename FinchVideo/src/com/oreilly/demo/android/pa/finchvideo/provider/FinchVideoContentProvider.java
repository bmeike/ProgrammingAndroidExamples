package com.oreilly.demo.android.pa.finchvideo.provider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.BaseColumns;
import android.text.TextUtils;
import com.finchframework.finch.rest.FileHandlerFactory;
import com.finchframework.finch.rest.RESTfulContentProvider;
import com.finchframework.finch.rest.ResponseHandler;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Content provider that loads video video data from the google YouTube API. The
 * following uri documents the RESTful gdata video web service:
 *
 * http://gdata.video.com/
 */
public class FinchVideoContentProvider extends RESTfulContentProvider {
    public static final String VIDEO = "video";
    public static final String DATABASE_NAME = VIDEO + ".db";
    static int DATABASE_VERSION = 2;

    public static final String VIDEOS_TABLE_NAME = "video";

    private static final String FINCH_VIDEO_FILE_CACHE = "finch_video_file_cache";

    private static final int VIDEOS = 1;
    private static final int VIDEO_ID = 2;
    private static final int THUMB_VIDEO_ID = 3;
    private static final int THUMB_ID = 4;

    private static UriMatcher sUriMatcher;

    // Statically construct a uri matcher that can detect URIs referencing
    // more than 1 video, a single video, or a single thumb nail image.
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(FinchVideo.AUTHORITY,
                FinchVideo.Videos.VIDEO, VIDEOS);
        // use of the hash character indicates matching of an id
        sUriMatcher.addURI(FinchVideo.AUTHORITY,
                FinchVideo.Videos.VIDEO + "/#",
                VIDEO_ID);
        sUriMatcher.addURI(FinchVideo.AUTHORITY,
                FinchVideo.Videos.THUMB + "/#",
                THUMB_VIDEO_ID);
        sUriMatcher.addURI(FinchVideo.AUTHORITY,
                FinchVideo.Videos.THUMB + "/*",
                THUMB_ID);
    }

    /** uri for querying video, expects appending keywords. */
    private static final String QUERY_URI =
            "http://gdata.youtube.com/feeds/api/videos?" +
                    "max-results=15&format=1&q=";

    private DatabaseHelper mOpenHelper;
    private SQLiteDatabase mDb;

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
            String createvideoTable =
                    "CREATE TABLE " + VIDEOS_TABLE_NAME + " (" +
                            BaseColumns._ID +
                            " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            FinchVideo.Videos.TITLE + " TEXT, " +
                            FinchVideo.Videos.DESCRIPTION + " TEXT, " +
                            FinchVideo.Videos.THUMB_URI_NAME + " TEXT," +
                            FinchVideo.Videos.THUMB_WIDTH_NAME + " TEXT," +
                            FinchVideo.Videos.THUMB_HEIGHT_NAME + " TEXT," +
                            FinchVideo.Videos.TIMESTAMP + " TEXT, " +
                            FinchVideo.Videos.QUERY_TEXT_NAME + " TEXT, " +
                            FinchVideo.Videos.MEDIA_ID_NAME + " TEXT UNIQUE," +
                            FinchVideo.Videos.THUMB_CONTENT_URI_NAME +
                            " TEXT UNIQUE," +
                            FinchVideo.Videos._DATA + " TEXT UNIQUE" +
                            ");";
            sqLiteDatabase.execSQL(createvideoTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldv,
                              int newv)
        {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +
                    VIDEOS_TABLE_NAME + ";");
            createTable(sqLiteDatabase);
        }
    }

    public FinchVideoContentProvider() {
    }

    public FinchVideoContentProvider(Context context) {
    }

    @Override
    public boolean onCreate() {
        FileHandlerFactory fileHandlerFactory =
                new FileHandlerFactory(new File(getContext().getFilesDir(),
                        FINCH_VIDEO_FILE_CACHE));
        setFileHandlerFactory(fileHandlerFactory);

        mOpenHelper = new DatabaseHelper(getContext(), DATABASE_NAME, null);
        mDb = mOpenHelper.getWritableDatabase();

        return true;
    }

    @Override
    public SQLiteDatabase getDatabase() {
        return mDb;
    }

    /**
     * Content provider query method that converts its parameters into a YouTube
     * RESTful search query.
     *
     * @param uri a reference to the query for videos, the query string can
     * contain, "q='key_words'".  The keywords are sent to the google YouTube
     * API where they are used to search the YouTube video database.
     * @param projection
     * @param where not used in this provider.
     * @param whereArgs not used in this provider.
     * @param sortOrder not used in this provider.
     * @return a cursor containing the results of a YouTube search query.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String where,
                        String[] whereArgs, String sortOrder)
    {
        Cursor queryCursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case VIDEOS:
                // the query is passed out of band of other information passed
                // to this method -- its not an argument.
                String queryText = uri.
                        getQueryParameter(FinchVideo.Videos.QUERY_PARAM_NAME);

                if (queryText == null) {
                    // A null cursor is an acceptable argument to the method,
                    // CursorAdapter.changeCursor(Cursor c), which interprets
                    // the value by canceling all adapter state so that the
                    // component for which the cursor is adapting data will
                    // display no content.
                    return null;
                }

                String select = FinchVideo.Videos.QUERY_TEXT_NAME +
                        " = '" +  queryText + "'";

                // quickly return already matching data
                queryCursor =
                        mDb.query(VIDEOS_TABLE_NAME, projection,
                                select,
                                whereArgs,
                                null,
                                null, sortOrder);

                // make the cursor observe the requested query
                queryCursor.setNotificationUri(
                        getContext().getContentResolver(), uri);

                /**
                 * Always try to update results with the latest data from the
                 * network.
                 *
                 * Spawning an asynchronous load task thread, guarantees that
                 * the load has no chance to block any content provider method,
                 * and therefore no chance to block the UI thread.
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
                if (!"".equals(queryText)) {
                    asyncQueryRequest(queryText, QUERY_URI + encode(queryText));
                }
                break;
            case VIDEO_ID:
            case THUMB_VIDEO_ID:
                long videoID = ContentUris.parseId(uri);
                queryCursor =
                        mDb.query(VIDEOS_TABLE_NAME, projection,
                                BaseColumns._ID + " = " + videoID,
                                whereArgs, null, null, null);
                queryCursor.setNotificationUri(
                        getContext().getContentResolver(), uri);
                break;
            case THUMB_ID:
                String uriString = uri.toString();
                int lastSlash = uriString.lastIndexOf("/");
                String mediaID = uriString.substring(lastSlash + 1);

                queryCursor =
                        mDb.query(VIDEOS_TABLE_NAME, projection,
                                FinchVideo.Videos.MEDIA_ID_NAME + " = " +
                                        mediaID,
                                whereArgs, null, null, null);
                queryCursor.setNotificationUri(
                        getContext().getContentResolver(), uri);
                break;

            default:
                throw new IllegalArgumentException("unsupported uri: " +
                        QUERY_URI);
        }

        return queryCursor;
    }

    /**
     * Provides a handler that can parse YouTube gData RSS content.
     *
     * @param requestTag unique tag identifying this request.
     * @return a YouTubeHandler object.
     */
    @Override
    protected ResponseHandler newResponseHandler(String requestTag) {
        return new YouTubeHandler(this, requestTag);
    }

    /**
     * Provides read only access to files that have been downloaded and stored
     * in the provider cache. Specifically, in this provider, clients can
     * access the files of downloaded thumbnail images.
     */
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
            throws FileNotFoundException
    {
        // only support read only files
        if (!"r".equals(mode.toLowerCase())) {
            throw new FileNotFoundException("Unsupported mode, " + mode + ", for uri: " + uri);
        }

        return openFileHelper(uri, mode);
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case VIDEOS:
                return FinchVideo.Videos.CONTENT_TYPE;

            case VIDEO_ID:
                return FinchVideo.Videos.CONTENT_VIDEO_TYPE;

            case THUMB_ID:
                return FinchVideo.Videos.CONTENT_THUMB_TYPE;

            default:
                throw new IllegalArgumentException("Unknown video type: " +
                        uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != VIDEOS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = getDatabase();
        return insert(uri, initialValues, db);
    }

    private void verifyValues(ContentValues values)
    {
        if (!values.containsKey(FinchVideo.Videos.TITLE)) {
            Resources r = Resources.getSystem();
            values.put(FinchVideo.Videos.TITLE,
                    r.getString(android.R.string.untitled));
        }

        if (!values.containsKey(FinchVideo.Videos.DESCRIPTION)) {
            Resources r = Resources.getSystem();
            values.put(FinchVideo.Videos.DESCRIPTION,
                    r.getString(android.R.string.untitled));
        }

        if (!values.containsKey(FinchVideo.Videos.THUMB_URI_NAME)) {
            throw new IllegalArgumentException("Thumb uri not specified: " +
                    values);
        }

        if (!values.containsKey(FinchVideo.Videos.THUMB_WIDTH_NAME)) {
            throw new IllegalArgumentException("Thumb width not specified: " +
                    values);
        }

        if (!values.containsKey(FinchVideo.Videos.THUMB_HEIGHT_NAME)) {
            throw new IllegalArgumentException("Thumb height not specified: " +
                    values);
        }

        // Make sure that the fields are all set
        if (!values.containsKey(FinchVideo.Videos.TIMESTAMP)) {
            Long now = System.currentTimeMillis();
            values.put(FinchVideo.Videos.TIMESTAMP, now);
        }

        if (!values.containsKey(FinchVideo.Videos.QUERY_TEXT_NAME)) {
            throw new IllegalArgumentException("Query Text not specified: " +
                    values);
        }

        if (!values.containsKey(FinchVideo.Videos.MEDIA_ID_NAME)) {
            throw new IllegalArgumentException("Media ID not specified: " +
                    values);
        }
    }

    /**
     * The delegate insert method, which also takes a database parameter. Note
     * that this method is a direct implementation of a content provider method.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values, SQLiteDatabase db) {
        verifyValues(values);

        // Validate the requested uri
        int m = sUriMatcher.match(uri);
        if (m != VIDEOS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // insert the values into a new database row
        String mediaID = (String) values.get(FinchVideo.Videos.MEDIA_ID_NAME);

        Long rowID = mediaExists(db, mediaID);
        if (rowID == null) {
            long time = System.currentTimeMillis();
            values.put(FinchVideo.Videos.TIMESTAMP, time);
            long rowId = db.insert(VIDEOS_TABLE_NAME,
                    FinchVideo.Videos.VIDEO, values);
            if (rowId >= 0) {
                Uri insertUri =
                        ContentUris.withAppendedId(
                                FinchVideo.Videos.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(insertUri, null);
                return insertUri;
            }

            throw new IllegalStateException("could not insert " +
                    "content values: " + values);
        }

        return ContentUris.withAppendedId(FinchVideo.Videos.CONTENT_URI, rowID);
    }

    private Long mediaExists(SQLiteDatabase db, String mediaID) {
        Cursor cursor = null;
        Long rowID = null;
        try {
            cursor = db.query(VIDEOS_TABLE_NAME, null,
                    FinchVideo.Videos.MEDIA_ID_NAME + " = '" + mediaID + "'",
                    null, null, null, null);
            if (cursor.moveToFirst()) {
                rowID = cursor.getLong(FinchVideo.ID_COLUMN);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return rowID;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int match = sUriMatcher.match(uri);
        int affected;

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (match) {
            case VIDEOS:
                affected = db.delete(VIDEOS_TABLE_NAME,
                        (!TextUtils.isEmpty(where) ?
                                " AND (" + where + ')' : ""),
                        whereArgs);
                break;
            case VIDEO_ID:
                long videoId = ContentUris.parseId(uri);
                affected = db.delete(VIDEOS_TABLE_NAME,
                        BaseColumns._ID + "=" + videoId
                                + (!TextUtils.isEmpty(where) ?
                                " AND (" + where + ')' : ""),
                        whereArgs);
                getContext().getContentResolver().notifyChange(uri, null);

                break;
            default:
                throw new IllegalArgumentException("unknown video element: " +
                        uri);
        }

        return affected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where,
                      String[] whereArgs)
    {
        getContext().getContentResolver().notifyChange(uri, null);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case VIDEOS:
                count = db.update(VIDEOS_TABLE_NAME, values, where, whereArgs);
                break;

            case VIDEO_ID:
                String videoId = uri.getPathSegments().get(1);
                count = db.update(VIDEOS_TABLE_NAME, values,
                        BaseColumns._ID + "=" + videoId
                                + (!TextUtils.isEmpty(where) ?
                                " AND (" + where + ')' : ""),
                        whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
