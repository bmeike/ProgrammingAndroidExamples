package com.oreilly.demo.android.pa.finchvideo.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Simple content provider that demonstrates the basics of creating a content
 * provider that stores basic video meta-data.
 */
public class SimpleFinchVideoContentProvider extends ContentProvider {
    public static final String SIMPLE_VIDEO = "simple_video";
    public static final String VIDEO_TABLE_NAME = "videos";

    private static final int VIDEOS = 1;
    private static final int VIDEO_ID = 2;
    private static UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(FinchVideo.SIMPLE_AUTHORITY, FinchVideo.SimpleVideos.VIDEO_NAME,
        		VIDEOS);
        // use of the hash character indicates matching of an id
        sUriMatcher.addURI(FinchVideo.SIMPLE_AUTHORITY,
        		FinchVideo.SimpleVideos.VIDEO_NAME + "/#", VIDEO_ID);
    }

    private static HashMap<String, String> sVideosProjectionMap;
    static {
        // example projection map, not actually used in this application
        sVideosProjectionMap = new HashMap<String, String>();
        sVideosProjectionMap.put(BaseColumns._ID, BaseColumns._ID);
        sVideosProjectionMap.put(FinchVideo.Videos.TITLE, FinchVideo.Videos.TITLE);
        sVideosProjectionMap.put(FinchVideo.Videos.VIDEO, FinchVideo.Videos.VIDEO);
        sVideosProjectionMap.put(FinchVideo.Videos.DESCRIPTION, FinchVideo.Videos.DESCRIPTION);
    }

    private static class SimpleVideoDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = SIMPLE_VIDEO + ".db";
        private static int DATABASE_VERSION = 2;

        SimpleVideoDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            createTable(sqLiteDatabase);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase,
                              int oldv, int newv)
        {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +
                    VIDEO_TABLE_NAME + ";");
            createTable(sqLiteDatabase);
        }

        private void createTable(SQLiteDatabase sqLiteDatabase) {
            String qs = "CREATE TABLE " + VIDEO_TABLE_NAME + " (" +
                    BaseColumns._ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FinchVideo.SimpleVideos.TITLE_NAME + " TEXT, " +
                    FinchVideo.SimpleVideos.DESCRIPTION_NAME + " TEXT, " +
                    FinchVideo.SimpleVideos.URI_NAME + " TEXT);";
            sqLiteDatabase.execSQL(qs);
        }
    }


    private SimpleVideoDbHelper mOpenDbHelper;

    @Override
    public boolean onCreate() {
        mOpenDbHelper = new SimpleVideoDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case VIDEOS:
                return FinchVideo.SimpleVideos.CONTENT_TYPE;

            case VIDEO_ID:
                return FinchVideo.SimpleVideos.CONTENT_VIDEO_TYPE;

            default:
                throw new IllegalArgumentException("Unknown video type: " +
                        uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String where,
                        String[] whereArgs, String sortOrder)
    {
        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = FinchVideo.SimpleVideos.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        int match = sUriMatcher.match(uri);

        Cursor c;
        switch (match) {
            case VIDEOS:
                // query the database for all videos
                c = getDb().query(VIDEO_TABLE_NAME, projection,
                        where, whereArgs,
                        null, null, orderBy);

                c.setNotificationUri(getContext().getContentResolver(),
                        FinchVideo.SimpleVideos.CONTENT_URI);
                break;
            case VIDEO_ID:
                // query the database for a specific video
                long videoID = ContentUris.parseId(uri);
                c = getDb().query(VIDEO_TABLE_NAME, projection,
                        BaseColumns._ID + " = " + videoID +
                                (!TextUtils.isEmpty(where) ?
                                        " AND (" + where + ')' : ""),
                        whereArgs, null, null, orderBy);
                c.setNotificationUri(getContext().getContentResolver(),
                        FinchVideo.SimpleVideos.CONTENT_URI);
                break;
            default:
                throw new IllegalArgumentException("unsupported uri: " + uri);
        }

        return c;
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

        verifyValues(values);

        // insert the initialValues into a new database row
        SQLiteDatabase db = mOpenDbHelper.getWritableDatabase();
        long rowId = db.insert(VIDEO_TABLE_NAME,
                FinchVideo.SimpleVideos.VIDEO_NAME, values);
        if (rowId > 0) {
            Uri videoURi = ContentUris.withAppendedId(
                    FinchVideo.SimpleVideos.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(videoURi, null);
            return videoURi;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    private void verifyValues(ContentValues values) {
        // Make sure that the fields are all set
        if (!values.containsKey(FinchVideo.SimpleVideos.TITLE_NAME)) {
            Resources r = Resources.getSystem();
            values.put(FinchVideo.SimpleVideos.TITLE_NAME,
                    r.getString(android.R.string.untitled));
        }

        if (!values.containsKey(FinchVideo.SimpleVideos.DESCRIPTION_NAME)) {
            values.put(FinchVideo.SimpleVideos.DESCRIPTION_NAME, "");
        }

        if (!values.containsKey(FinchVideo.SimpleVideos.URI_NAME)) {
            values.put(FinchVideo.SimpleVideos.URI_NAME, "");
        }
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int match = sUriMatcher.match(uri);
        int affected;

        switch (match) {
            case VIDEOS:
                affected = getDb().delete(VIDEO_TABLE_NAME,
                        (!TextUtils.isEmpty(where) ?
                                " AND (" + where + ')' : ""),
                        whereArgs);
                break;
            case VIDEO_ID:
                long videoId = ContentUris.parseId(uri);
                affected = getDb().delete(VIDEO_TABLE_NAME,
                        BaseColumns._ID + "=" + videoId
                                + (!TextUtils.isEmpty(where) ?
                                " AND (" + where + ')' : ""),
                        whereArgs);
                break;
            default:
                throw new IllegalArgumentException("unknown video element: " +
                        uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return affected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where,
                      String[] whereArgs)
    {
        int affected;

        switch (sUriMatcher.match(uri)) {
            case VIDEOS:
                affected = getDb().update(VIDEO_TABLE_NAME, values,
                        where, whereArgs);
                break;

            case VIDEO_ID:
                String videoId = uri.getPathSegments().get(1);
                affected = getDb().update(VIDEO_TABLE_NAME, values,
                        BaseColumns._ID + "=" + videoId
                                + (!TextUtils.isEmpty(where) ?
                                " AND (" + where + ')' : ""),
                        whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return affected;
    }

    private SQLiteDatabase getDb() { return mOpenDbHelper.getWritableDatabase(); }
}
