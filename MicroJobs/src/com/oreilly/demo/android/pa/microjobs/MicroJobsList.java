package com.oreilly.demo.android.pa.microjobs;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;

import com.oreilly.demo.android.pa.microjobs.MicroJobsDatabase.JobsCursor;


/**
 * MicroJobsList
 */
public class MicroJobsList extends Activity {

    private static class mjButton extends Button {
        protected int jrow; // mjButton is just a button that knows which job number it's associated with

        public mjButton(Context btnContext) {
            super(btnContext);
        }
    }

    private static Button btnTitle;
    private static Button btnEmployer;
    static TableLayout tblJobs;


    // Create a button click listener for the Title button.
    private final Button.OnClickListener btnTitleOnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Remove any TextViews we added to the Table
            for (View vw : lstTable) {
                tblJobs.removeView(vw);
            }
            fillData(JobsCursor.SortBy.title);
        }
    };

    // Create a button click listener for the Employer button.
    private final Button.OnClickListener btnEmployerOnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (View vw : lstTable) {
                tblJobs.removeView(vw);
            }
            fillData(JobsCursor.SortBy.employer_name);
        }
    };

    // Create a button click listener for the Title buttons in the list
    // Clicking on any of these should take us to a detail listing for that
    // job
    private final Button.OnClickListener onTitleClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(MicroJobsList.this, MicroJobsDetail.class);
            Bundle b = new Bundle();
            mjButton vb = (mjButton) v;
            cursor.moveToPosition(vb.jrow);
            b.putInt("_id", (int) cursor.getColJobsId());
            i.putExtras(b);

            startActivity(i);
        }
    };

    JobsCursor cursor;
    private MicroJobsDatabase db;

    ArrayList<View> lstTable;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.microjobslist);
        tblJobs = (TableLayout) findViewById(R.id.tblJobs);
        btnTitle = (Button) findViewById(R.id.btnTitle);
        btnTitle.setText("Title");
        btnTitle.setOnClickListener(btnTitleOnClick);
        btnEmployer = (Button) findViewById(R.id.btnEmployer);
        btnEmployer.setText("Employer");
        btnEmployer.setOnClickListener(btnEmployerOnClick);

        db = new MicroJobsDatabase(this);

        fillData(JobsCursor.SortBy.title);
    }

    /**
     * @see android.app.Activity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * @param icicle
     */
    public void onPause(Bundle icicle) {
        super.onPause();
    }

    /**
     * Make sure to stop the animation when we're no longer on screen, failing
     * to do so will cause a lot of unnecessary cpu-usage!
     */
    @Override
    public void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
    }

    /**
     * Setup menus for this page
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean supRetVal = super.onCreateOptionsMenu(menu);
        menu.add(0, 0, Menu.NONE, getString(R.string.list_menu_back_to_map));
        menu .add(0, 1, Menu.NONE, getString(R.string.list_menu_sort_by_title));
        menu.add(0, 2, Menu.NONE, getString(R.string.list_menu_sort_by_employer));
        menu.add(0, 3, Menu.NONE, getString(R.string.list_menu_add_job));
        return supRetVal;
    }


    /**
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                // Go back to the map page
                finish();
                return true;
            case 1:
                // Sort the list by job title
                for (View vw : lstTable) {
                    tblJobs.removeView(vw);
                }
                fillData(JobsCursor.SortBy.title);
                return true;
            case 2:
                // Sort the list by employer name
                for (View vw : lstTable) {
                    tblJobs.removeView(vw);
                }
                fillData(JobsCursor.SortBy.employer_name);
                return true;
            case 3:
                // Add a new job
                Intent i = new Intent(MicroJobsList.this, AddJob.class);
                startActivity(i);
                     return true;
        }

        return false;
    }

    /**
     * @param sortBy
     */
    void fillData(JobsCursor.SortBy sortBy) {
        // Create a new list to track the addition of TextViews
        lstTable = new ArrayList<View>(); // a list of the TableRow's added
        // Get all of the rows from the database and create the table
        // Keep track of the TextViews added in list lstTable
        cursor = db.getJobs(sortBy);
        // Create a table row that contains two lists
        // (one for job titles, one for employers)
        // Now load the lists with job title and employer name
        //for (Jobs row : rows) {
        for( int rowNum=0; rowNum<cursor.getCount(); rowNum++){
            cursor.moveToPosition(rowNum);
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new LayoutParams(
                android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            // Create a Button for the job title.
            mjButton btn1 = new mjButton(this);
            // Button btn1 = new Button(this);
            btn1.jrow = rowNum;
            btn1.setText(cursor.getColTitle());
            btn1.setPadding(1, 0, 3, 0);
            btn1.setHeight(40);
            btn1.setGravity(android.view.Gravity.CENTER);
            btn1.setBackgroundColor(colorByStatus((int) cursor.getColStatus()));
            btn1.setOnClickListener(onTitleClick);
            // Add job title to job list.
            tr.addView(btn1);

            Button btn2 = new Button(this);
            btn2.setPadding(1, 0, 3, 0);
            btn2.setText(cursor.getColEmployerName());
            btn2.setHeight(40);
            btn2.setGravity(android.view.Gravity.CENTER);
            btn2.setBackgroundColor(Color.WHITE);
            /* Add employer name to that list. */
            tr.addView(btn2);

            tblJobs.addView(tr);
            // lstJobs.add(btn1.getId()); // keep job id to get more info later if needed
            lstTable.add(tr); // keep track of the rows we've added (to remove later)
        }
    }

    // Set a background color based on the current status of a job
    private int colorByStatus(int status) {
        switch (status) {
            case 1: // Position is taken
                return Color.argb(150, 255, 0, 0); // red
            case 2: // There are applicants for the position
                return Color.argb(150, 44, 211, 207); // yellow
            case 3: // The position is available
                return Color.argb(150, 0, 255, 0); // green
            default:
                return Color.WHITE;
        }
    }
}
