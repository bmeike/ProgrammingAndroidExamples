package com.oreilly.demo.android.pa.microjobs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;


/**
 * Provides access to the MicroJobs database.  Since this is not a Content
 * Provider, no other applications will have access to the database.
 */
public class MicroJobsDatabase extends SQLiteOpenHelper {
    /** The name of the database file on the file system */
    private static final String DATABASE_NAME = "MicroJobs";
    /** The version of the database that this class understands. */
    private static final int DATABASE_VERSION = 1;
    /** Keep track of context so that we can load SQL from string resources */
    private final Context mContext;

    /**
     * Provides self-contained query-specific cursor for Employers.
     * The query and all Accessor methods are in the class.
     */
    public static class EmployersCursor extends SQLiteCursor{
        /** The query for this cursor */
        private static final String QUERY =
            "SELECT _id, employer_name "+
            "FROM employers " +
            "ORDER BY employer_name";

        /** Cursor constructor */
        EmployersCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
                String editTable, SQLiteQuery query) {
            super(db, driver, editTable, query);
        }

        /** Private factory class necessary for rawQueryWithFactory() call */
        private static class Factory implements SQLiteDatabase.CursorFactory{
            @Override
            public Cursor newCursor(SQLiteDatabase db,
                    SQLiteCursorDriver driver, String editTable,
                    SQLiteQuery query) {
                return new EmployersCursor(db, driver, editTable, query);
            }
        }

        /* Accessor functions -- one per database column */
        public long getColId(){return getLong(getColumnIndexOrThrow("_id"));}
        public String getColEmployerName(){
            return getString(getColumnIndexOrThrow("employer_name"));
        }
    }

    /**
     * Provides self-contained query-specific cursor for Job Detail.
     * The query and all Accessor methods are in the class.
     */
    public static class JobDetailCursor extends SQLiteCursor {
        /** The query for this cursor */
        private static final String QUERY =
            "SELECT jobs._id, employers._id, employers.website, title," +
                    " description, start_time, end_time, employer_name, " +
                    "contact_name, rating, street, city, state, zip, phone, " +
                    "email, latitude, longitude, status FROM jobs, employers "+
                    "WHERE jobs.employer_id = employers._id "+
                    "AND jobs._id = ";
        /** Cursor constructor */
        private JobDetailCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
                String editTable, SQLiteQuery query) {
            super(db, driver, editTable, query);
        }
        /** Private factory class necessary for rawQueryWithFactory() call */
        private static class Factory implements SQLiteDatabase.CursorFactory{
            @Override
            public Cursor newCursor(SQLiteDatabase db,
                    SQLiteCursorDriver driver, String editTable,
                    SQLiteQuery query) {
                return new JobDetailCursor(db, driver, editTable, query);
            }
        }
        /* Accessor functions -- one per database column */
        public long getColJobsId() {
            return getLong(getColumnIndexOrThrow("jobs._id"));
        }
        public long getColEmployersId() {
            return getLong(getColumnIndexOrThrow("employers._id"));
        }
        public String getColWebsite() {
            return getString(getColumnIndexOrThrow("employers.website"));
        }
        public String getColTitle() {
            return getString(getColumnIndexOrThrow("title"));
        }
        public String getColDescription() {
            return getString(getColumnIndexOrThrow("description"));
        }
        public long getColStartTime() {
            return getLong(getColumnIndexOrThrow("start_time"));
        }
        public long getColEndTime() {
            return getLong(getColumnIndexOrThrow("end_time"));
        }
        public String getColEmployerName() {
            return getString(getColumnIndexOrThrow("employer_name"));
        }
        public String getColContactName() {
            return getString(getColumnIndexOrThrow("contact_name"));
        }
        public long getColRating() {
            return getLong(getColumnIndexOrThrow("rating"));
        }
        public String getColStreet() {
            return getString(getColumnIndexOrThrow("street"));
        }
        public String getColCity() {
            return getString(getColumnIndexOrThrow("city"));
        }
        public String getColState() {
            return getString(getColumnIndexOrThrow("state"));
        }
        public String getColZip(){
            return getString(getColumnIndexOrThrow("zip"));
        }
        public String getColPhone() {
            return getString(getColumnIndexOrThrow("phone"));
        }
        public String getColEmail(){
            return getString(getColumnIndexOrThrow("email"));
        }
        public long getColLatitude() {
            return getLong(getColumnIndexOrThrow("latitude"));
        }
        public long getColLongitude() {
            return getLong(getColumnIndexOrThrow("longitude"));
        }
        public long getColStatus() {
            return getLong(getColumnIndexOrThrow("status"));
        }
    }

    public static class JobsCursor extends SQLiteCursor {
        public static enum SortBy{
            title,
            employer_name
        }
        private static final String QUERY =
            "SELECT jobs._id, title, employer_name, latitude, longitude, " +
                    "status FROM jobs, employers " +
                    "WHERE jobs.employer_id = employers._id ORDER BY ";

        private JobsCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
                String editTable, SQLiteQuery query) {
            super(db, driver, editTable, query);
        }

        private static class Factory implements SQLiteDatabase.CursorFactory{
            @Override
            public Cursor newCursor(SQLiteDatabase db,
                    SQLiteCursorDriver driver, String editTable,
                    SQLiteQuery query) {
                return new JobsCursor(db, driver, editTable, query);
            }
        }

        public long getColJobsId() {
            return getLong(getColumnIndexOrThrow("jobs._id"));
        }

        public String getColTitle() {
            return getString(getColumnIndexOrThrow("title"));
        }

        public String getColEmployerName() {
            return getString(getColumnIndexOrThrow("employer_name"));
        }

        public long getColLatitude() {
            return getLong(getColumnIndexOrThrow("latitude"));
        }

        public long getColLongitude() {
            return getLong(getColumnIndexOrThrow("longitude"));
        }

        public long getColStatus() {
            return getLong(getColumnIndexOrThrow("status"));
        }
    }

    /**
     * Provides self-contained query-specific cursor for Worker info.
     * The query and all Accessor methods are in the class.
     * Note: for now there is only one record in this table, so this is a lot of
     * work to store/retrieve that data.  We do it this way in anticipation of a
     * day when there would be more than one worker in the table.
     */
    public static class WorkerCursor extends SQLiteCursor{
        /** The query for this cursor */
        private static final String QUERY =
            "SELECT workers._id, name, username, passhash, rating,"+
            "city, state, zip, phone, email, loc1_name, loc1_lat, loc1_long,"+
            "loc2_name, loc2_lat, loc2_long, loc3_name, loc3_lat, loc3_long "+
            "FROM workers ";
        /** Cursor constructor */
        WorkerCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
                String editTable, SQLiteQuery query) {
            super(db, driver, editTable, query);
        }
        /** Private factory class necessary for rawQueryWithFactory() call */
        private static class Factory implements SQLiteDatabase.CursorFactory{
            @Override
            public Cursor newCursor(SQLiteDatabase db,
                    SQLiteCursorDriver driver, String editTable,
                    SQLiteQuery query) {
                return new WorkerCursor(db, driver, editTable, query);
            }
        }
        /* Accessor functions -- one per database column */
        public long getColId() {
            return getLong(getColumnIndexOrThrow("workers._id"));
        }
        public String getColName() {
            return getString(getColumnIndexOrThrow("name"));
        }
        public String getColUserName() {
            return getString(getColumnIndexOrThrow("username"));
        }
        public String getColPassHash() {
            return getString(getColumnIndexOrThrow("passhash"));
        }
        public String getColStreet() {
            return getString(getColumnIndexOrThrow("street"));
        }
        public String getColCity() {
            return getString(getColumnIndexOrThrow("city"));
        }
        public String getColState() {
            return getString(getColumnIndexOrThrow("state"));
        }
        public String getColZip() {
            return getString(getColumnIndexOrThrow("zip"));
        }
        public String getColPhone() {
            return getString(getColumnIndexOrThrow("phone"));
        }
        public String getColEmail() {
            return getString(getColumnIndexOrThrow("email"));
        }
        public String getColLoc1Name() {
            return getString(getColumnIndexOrThrow("loc1_name"));
        }
        public long getColLoc1Lat() {
            return getLong(getColumnIndexOrThrow("loc1_lat"));
        }
        public long getColLoc1Long() {
            return getLong(getColumnIndexOrThrow("loc1_long"));
        }
        public String getColLoc2Name() {
            return getString(getColumnIndexOrThrow("loc2_name"));
        }
        public long getColLoc2Lat() {
            return getLong(getColumnIndexOrThrow("loc2_lat"));
        }
        public long getColLoc2Long() {
            return getLong(getColumnIndexOrThrow("loc2_long"));
        }
        public String getColLoc3Name() {
            return getString(getColumnIndexOrThrow("loc3_name"));
        }
        public long getColLoc3Lat() {
            return getLong(getColumnIndexOrThrow("loc3_lat"));
        }
        public long getColLoc3Long() {
            return getLong(getColumnIndexOrThrow("loc3_long"));
        }
    }

    /** Constructor */
    public MicroJobsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    /**
     * Execute all of the SQL statements in the String[] array
     * @param db The database on which to execute the statements
     * @param sql An array of SQL statements to execute
     */
    private void execMultipleSQL(SQLiteDatabase db, String[] sql){
        for( String s : sql ) {
            if (s.trim().length()>0) {
                db.execSQL(s);
            }
        }
    }

    /** Called when it is time to create the database */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String[] sql = mContext.getString(
                R.string.MicroJobsDatabase_onCreate).split("\n");
        db.beginTransaction();
        try {
            // Create tables & test data
            execMultipleSQL(db, sql);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("Error creating tables and debug data", e.toString());
        } finally {
            db.endTransaction();
        }
    }

    /** Called when the database must be upgraded */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MicroJobs.LOG_TAG,
                "Upgrading database from version " + oldVersion + " to " +
                newVersion + ", which will destroy all old data");

        String[] sql = mContext.getString(
                R.string.MicroJobsDatabase_onUpgrade).split("\n");
        db.beginTransaction();
        try {
            // Create tables & test data
            execMultipleSQL(db, sql);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("Error creating tables and debug data", e.toString());
        } finally {
            db.endTransaction();
        }

        // This is cheating.  In the real world, you'll need to add columns,
        // not rebuild from scratch
        onCreate(db);
    }

    /**
     * Add a new job to the database.  The job will have a status of open.
     * @param employer_id    The employer offering the job
     * @param title            The job title
     * @param description    The job description
     */
//    public void addJob(long employer_id, String title, String description){
//        String sql =
//            "INSERT INTO jobs (_id, employer_id, title, description, start_time,
// end_time, status) " +
//            "VALUES (          NULL, ?,          ?,     ?,         0,
// 0,        3)";
//        Object[] bindArgs = new Object[]{employer_id, title, description};
//        try{
//            getWritableDatabase().execSQL(sql, bindArgs);
//        } catch (SQLException e) {
//            Log.e("Error writing new job", e.toString());
//        }
//    }
    public void addJob(long employer_id, String title, String description){
        ContentValues map = new ContentValues();
        map.put("employer_id", Long.valueOf(employer_id));
        map.put("title", title);
        map.put("description", description);
        try{
            getWritableDatabase().insert("jobs", null, map);
        } catch (SQLException e) {
            Log.e("Error writing new job", e.toString());
        }
    }

    /**
     * Update a job in the database.
     * @param job_id        The job id of the existing job
     * @param employer_id    The employer offering the job
     * @param title            The job title
     * @param description    The job description
     */
//    public void editJob(long job_id, long employer_id, String title,
// String description) {
//        String sql =
//            "UPDATE jobs " +
//            "SET employer_id = ?, "+
//            " title = ?,  "+
//            " description = ? "+
//            "WHERE _id = ? ";
//        Object[] bindArgs = new Object[]{employer_id, title,
// description, job_id};
//        try{
//            getWritableDatabase().execSQL(sql, bindArgs);
//        } catch (SQLException e) {
//            Log.e("Error writing new job", e.toString());
//        }
//    }
    public void editJob(long job_id, long employer_id, String title,
                        String description)
    {
        ContentValues map = new ContentValues();
        map.put("employer_id", Long.valueOf(employer_id));
        map.put("title", title);
        map.put("description", description);
        String[] whereArgs = new String[]{Long.toString(job_id)};
        try{
            getWritableDatabase().update("jobs", map, "_id=?", whereArgs);
        } catch (SQLException e) {
            Log.e("Error writing new job", e.toString());
        }
    }

    /**
     * Delete a job from the database.
     * @param job_id        The job id of the job to delete
     */
//    public void deleteJob(long job_id) {
//        String sql = String.format(
//                "DELETE FROM jobs " +
//                "WHERE _id = '%d' ",
//                job_id);
//        try{
//            getWritableDatabase().execSQL(sql);
//        } catch (SQLException e) {
//            Log.e("Error deleteing job", e.toString());
//        }
//    }
    public void deleteJob(long job_id) {
        String[] whereArgs = new String[]{Long.toString(job_id)};
        try{
            getWritableDatabase().delete("jobs", "_id=?", whereArgs);
        } catch (SQLException e) {
            Log.e("Error deleteing job", e.toString());
        }
    }

    /** Returns the number of Jobs */
    public int getJobsCount(){

        Cursor c = null;
        try {
            c = getReadableDatabase().rawQuery(
                "SELECT count(*) FROM jobs",
                null);
            if (0 >= c.getCount()) { return 0; }
            c.moveToFirst();
            return c.getInt(0);
        }
        finally {
            if (null != c) {
                try { c.close(); }
                catch (SQLException e) { }
            }
        }
    }

    /** Returns a EmployersCursor for all Employers
     */
    public EmployersCursor getEmployers() {
        SQLiteDatabase d = getReadableDatabase();
        EmployersCursor c = (EmployersCursor) d.rawQueryWithFactory(
            new EmployersCursor.Factory(),
            EmployersCursor.QUERY,
            null,
            null);
        c.moveToFirst();
        return c;
    }

    /** Returns a JobDetailCursor for the specified jobId
     * @param jobId The _id of the job
     */
    public JobDetailCursor getJobDetails(long jobId) {
        String sql = JobDetailCursor.QUERY + jobId;
        SQLiteDatabase d = getReadableDatabase();
        JobDetailCursor c = (JobDetailCursor) d.rawQueryWithFactory(
            new JobDetailCursor.Factory(),
            sql,
            null,
            null);
        c.moveToFirst();
        return c;
    }

    /** Return a sorted JobsCursor
     * @param sortBy the sort criteria
     */
    public JobsCursor getJobs(JobsCursor.SortBy sortBy) {
        String sql = JobsCursor.QUERY+sortBy.toString();
        SQLiteDatabase d = getReadableDatabase();
        JobsCursor c = (JobsCursor) d.rawQueryWithFactory(
            new JobsCursor.Factory(),
            sql,
            null,
            null);
        c.moveToFirst();
        return c;
    }
    /** Returns the WorkerCursor
     *
     */
    public WorkerCursor getWorker() {
        String sql = WorkerCursor.QUERY;
        SQLiteDatabase d = getReadableDatabase();
        WorkerCursor c = (WorkerCursor) d.rawQueryWithFactory(
            new WorkerCursor.Factory(),
            sql,
            null,
            null);
        c.moveToFirst();
        return c;
    }
}
