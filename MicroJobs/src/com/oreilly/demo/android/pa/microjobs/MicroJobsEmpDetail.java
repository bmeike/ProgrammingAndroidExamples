package com.oreilly.demo.android.pa.microjobs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.oreilly.demo.android.pa.microjobs.MicroJobsDatabase.JobDetailCursor;


/**
 * MicroJobsEmpDetail
 */
public class MicroJobsEmpDetail extends Activity {
    private static TextView txtEmployer;
    private static TextView txtContact;
    private static TextView txtWebsite;
    private static TextView txtRating;
    private static TextView txtAddress;
    private static TextView txtCity;
    private static TextView txtState;
    private static TextView txtZIP;
    private static TextView txtPhone;
    private static TextView txtEmail;
    private static ImageButton btnPhone;
    private static ImageButton btnBrowser;
    private static Integer job_id;

    JobDetailCursor job;


    // Create a button click listener for the Dial and Browser buttons.
    private final Button.OnClickListener btnPhoneOnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                startActivity(Intent.getIntent("tel:" + job.getColPhone()));
            }
            catch (Exception e) {}
        }
    };

    private final Button.OnClickListener btnBrowserOnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Intent openBrowser = Intent.getIntent("http://" + job.getColWebsite());
                startActivity(openBrowser);
            }
            catch (Exception e) {}
        }
    };

    /**
     * Called when the activity is first created.
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.microjobsempdetail);
        txtEmployer = (TextView) findViewById(R.id.txtEmployer);
        txtContact = (TextView) findViewById(R.id.txtContact);
        txtWebsite = (TextView) findViewById(R.id.txtWebsite);
        txtRating = (TextView) findViewById(R.id.txtRating);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtCity = (TextView) findViewById(R.id.txtCity);
        txtState = (TextView) findViewById(R.id.txtState);
        txtZIP = (TextView) findViewById(R.id.txtZIP);
        txtPhone = (TextView) findViewById(R.id.txtPhone);
        txtEmail = (TextView) findViewById(R.id.txtEmail);

        // get the _id for this job from the bundle passed by MicroJobsList
        Bundle b = this.getIntent().getExtras();
        job_id = Integer.valueOf(b.getInt("_id"));
        MicroJobsDatabase db = new MicroJobsDatabase(this);
        job = db.getJobDetails(job_id.longValue());
        startManagingCursor(job);


        // fill in the form and display
        txtEmployer.setText(job.getColEmployerName());
        txtContact.setText(job.getColContactName());
        txtWebsite.setText(job.getColWebsite());
        Double temp_rating = Double.valueOf(job.getColRating() / 10.0D);
        txtRating.setText(temp_rating.toString());
        txtAddress.setText(job.getColStreet());
        txtCity.setText(job.getColCity());
        txtState.setText(job.getColState());
        txtZIP.setText(job.getColZip());
        txtPhone.setText(job.getColPhone());
        txtEmail.setText(job.getColEmail());

        // Implement callback for the dial and browser buttons
        btnPhone = (ImageButton) findViewById(R.id.btnPhone);
        btnPhone.setOnClickListener(btnPhoneOnClick);
        btnPhone.setImageResource(R.drawable.phone);
        btnBrowser = (ImageButton) findViewById(R.id.btnBrowser);
        btnBrowser.setOnClickListener(btnBrowserOnClick);
        btnBrowser.setImageResource(R.drawable.browser);
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
     * Setup menus for this page.
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean supRetVal = super.onCreateOptionsMenu(menu);
        menu.add(0, 0, Menu.NONE, getString(R.string.emp_detail_menu_back_to_job_info));
        return supRetVal;
    }

    /**
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                // Go back to the list page
                finish();
                return true;
            case 1:
                // Go to the employer detail page
                Intent i = new Intent(MicroJobsEmpDetail.this, MicroJobsDetail.class);
                Bundle b = new Bundle();
                b.putInt("_id", job_id.intValue());
                i.putExtras(b);

                startActivity(i);
                return true;
            default:
                return false;
        }
    }
}
