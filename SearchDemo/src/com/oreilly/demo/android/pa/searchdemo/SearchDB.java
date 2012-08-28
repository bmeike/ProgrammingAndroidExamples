package com.oreilly.demo.android.pa.searchdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

public class SearchDB {	
	public static final String SONNETTABLE			= "SONNETS";
	public static final String SONNETNUM			= SearchManager.SUGGEST_COLUMN_TEXT_1;
	public static final String SONNETSTR			= "SONNETSTR";
	public static final String LINENUM				= "LINENUM";
	public static final String LINETXT				= SearchManager.SUGGEST_COLUMN_TEXT_2;
	private static final String[] COLS 				= {BaseColumns._ID, 
														SONNETNUM, 
														SONNETSTR, 
														LINENUM, 
														LINETXT};
	
	private SonnetsSQLOpenHelper sql;
	
	public SearchDB(Context context, int resourceId) throws Exception {
		sql = new SonnetsSQLOpenHelper(context);
		sql.getWritableDatabase();
		readInSonnets(context, resourceId);
	}
	
	public SonnetFragment[] search(String query) {
		SonnetFragment[] frags = new SonnetFragment[0];
		Cursor cur = searchDB(query);
		if(cur != null) {
			ArrayList<SonnetFragment> arr = new ArrayList<SonnetFragment>();
			while(cur.moveToNext()) {
				arr.add(
						new SonnetFragment(cur.getInt(cur.getColumnIndex(SONNETNUM)), 
					    cur.getString(cur.getColumnIndex(LINETXT))));
			}
			if(!arr.isEmpty()) {
				frags = arr.toArray(new SonnetFragment[arr.size()]);
			}
		}
		return frags;
	}
	
	public Sonnet getSonnet(int i) {
		Sonnet sonnet = null;
		Cursor cur = getSonnetCursor(i);
		if(cur != null) {
			String num = null;
			ArrayList<String> arr = new ArrayList<String>();
			while(cur.moveToNext()) {
				if(num == null) 
					num = cur.getString(cur.getColumnIndex(SONNETSTR));
				arr.add(cur.getString(cur.getColumnIndex(LINETXT)));
			}
			if(!arr.isEmpty()) {
				sonnet = new Sonnet(num, arr.toArray(new String[arr.size()]));
			}
		}
		 
		return sonnet;
	}
	
	public Cursor getSonnetCursor(int i) {
		String selection = SONNETNUM + " = ?";
		String[] selectionArgs = new String[] {i+""};
		Cursor cur = query(selection, selectionArgs, COLS, LINENUM);
		return cur;
	}
	
	public Cursor searchDB(String query) {
		return searchDB(query, COLS);
	}
	
	public Cursor searchDB(String query, String[] columns) {
		query = query.toLowerCase();
		String selection = LINETXT + " LIKE ?";
        String[] selectionArgs = new String[] {"%"+query+"%"};
		return query(selection, selectionArgs, columns, null);
	}
	
	private Cursor query(String selection, String[] selectionArgs, String[] columns, String sort) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(SONNETTABLE);

        Cursor cursor = 
        	builder.query(sql.getReadableDatabase(), 
        					columns, selection, 
        					selectionArgs, 
        					null, 
        					null, 
        					sort);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
	
	private void readInSonnets(Context context, int resourceId) throws Exception {
		BufferedReader br = null;
		try {
			br = new BufferedReader(
					new InputStreamReader(context.getResources().openRawResource(resourceId)));
			String line = null;
			String num = null;
			ArrayList<String> ls = new ArrayList<String>();
			int id = 0;
			
			while ((line = br.readLine()) != null) {
				if(line.length() < 15 && line.endsWith(".".intern())) {
					String curnum = line.substring(0, line.length() - 1);
					if(num != null) {
						int size = ls.size();
						Sonnet sonnet = new Sonnet(num, ls.toArray(new String[size]));
						if(sql != null) {
							for(int i=0;i<size;i++) {
								sql.addSonnet(	id++, 
												sonnet.num, 
												sonnet.title, 
												i, 
												sonnet.lines[i]);
							}
						}
					} 
					num = curnum;
					ls.clear();
				} else if(line.trim().length() > 1) {
					ls.add(line);
				}
			}
		} finally {
            try {
            	if(br != null) {
            		br.close();
            	}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
	private static class SonnetsSQLOpenHelper extends SQLiteOpenHelper {
		private SQLiteDatabase sonnetdb;
		
		public SonnetsSQLOpenHelper(Context context) {
			super(context, null, null, 1);  // in-memory db
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			sonnetdb = db;
			sonnetdb.execSQL("CREATE TABLE "+SONNETTABLE+
								" ("+BaseColumns._ID +" INTEGER, "+
								SONNETNUM+" INTEGER, "+
								SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID+
								" TEXT, "+SONNETSTR+" TEXT, "+
								LINENUM+" INTEGER, "+LINETXT+
								" TEXT);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// ignore upgrade case since we are doing in memory
		}
		
		public long addSonnet(int id, int sonnetnum, String sonnetstr, int linenum, String line) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(BaseColumns._ID, id);
            initialValues.put(SONNETNUM, sonnetnum);
            initialValues.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, ""+sonnetnum);
            initialValues.put(LINENUM, linenum);
            initialValues.put(SONNETSTR, sonnetstr);
            initialValues.put(LINETXT, line);

            return sonnetdb.insert(SONNETTABLE, null, initialValues);
        }
	}
}
