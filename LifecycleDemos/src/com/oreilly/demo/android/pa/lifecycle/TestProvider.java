package com.oreilly.demo.android.pa.lifecycle;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * A stub content provider.
 */
public class TestProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String where,
                        String[] whereArgs, String sortBy)
    {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues)
    {
        return null;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs)
    {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues,
                      String where, String[] whereArgs)
    {
        return 0;
    }
}
