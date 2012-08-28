package com.oreilly.demo.android.pa.searchdemo;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class SearchDBProvider extends ContentProvider {
	public final static String AUTHORITY = 
					"com.oreilly.demo.android.pa.searchdemo.SearchDBProvider";
	public static final Uri CONTENT_URI = 
					Uri.parse("content://" + AUTHORITY + "/sonnets");
	public final static int MODE = 1;
	
	public static final String MIME_TYPE = 
					ContentResolver.CURSOR_DIR_BASE_TYPE + 
						"/vnd.oreilly.demo.android.pa.searchdemo";
	public static final String SONNET_MIME_TYPE = 
					ContentResolver.CURSOR_ITEM_BASE_TYPE + 
						"/vnd.oreilly.demo.android.pa.searchdemo";
	
	private static final int SEARCH = 0;
	private static final int GET_SONNET = 1;
    private static final int SEARCH_SUGGEST = 2;
    
    private static final UriMatcher matcher = buildUriMatcher();
    
    private static UriMatcher buildUriMatcher() {
        UriMatcher umatcher = new UriMatcher(UriMatcher.NO_MATCH);

        umatcher.addURI(AUTHORITY, "sonnets", SEARCH);
        umatcher.addURI(AUTHORITY, "sonnets/#", GET_SONNET);
        
        umatcher.addURI(AUTHORITY, 
        				SearchManager.SUGGEST_URI_PATH_QUERY, 
        				SEARCH_SUGGEST);
        umatcher.addURI(AUTHORITY, 
        				SearchManager.SUGGEST_URI_PATH_QUERY + "/*", 
        				SEARCH_SUGGEST);
        
        return umatcher;
    }
	
	@Override
	public boolean onCreate() {
		try {
			SearchActivity.initializeSearchLogic(getContext());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public String getType(Uri uri) {
		 switch (matcher.match(uri)) {
	         case SEARCH:
	             return MIME_TYPE;
	         case GET_SONNET:
	             return SONNET_MIME_TYPE;
	         case SEARCH_SUGGEST:
	             return SearchManager.SUGGEST_MIME_TYPE;
	         default:
	             throw new IllegalArgumentException("Unknown URL " + uri);
		 }
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		 switch (matcher.match(uri)) {
	         case SEARCH_SUGGEST:
	             if (selectionArgs == null) {
	               throw new IllegalArgumentException(
	                   "selectionArgs must be provided for the Uri: " + uri);
	             }
	             return getSuggestions(selectionArgs[0]);
	         case SEARCH:
	             if (selectionArgs == null) {
	               throw new IllegalArgumentException(
	                   "selectionArgs must be provided for the Uri: " + uri);
	             }
	             return search(selectionArgs[0]);
	         case GET_SONNET:
	             return getSonnet(uri);
	         default:
	             throw new IllegalArgumentException("Unknown Uri: " + uri);
		 }
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}
	
	private Cursor getSuggestions(String query) {
		query = query.toLowerCase();
		String[] columns = new String[] {
											BaseColumns._ID,
											SearchDBLogic.SONNETNUM,
											SearchDBLogic.LINETXT,
											SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
										};

		return ((SearchDBLogic) SearchActivity.SEARCHLOGIC).searchDB(query, columns);
    }

    private Cursor search(String query) {
    	return ((SearchDBLogic) SearchActivity.SEARCHLOGIC).searchDB(query);
    }

    private Cursor getSonnet(Uri uri) {
    	String rowId = uri.getLastPathSegment();
    	return ((SearchDBLogic) SearchActivity.SEARCHLOGIC).getSonnetCursor(Integer.parseInt(rowId));
    }

}
