package com.oreilly.demo.android.pa.microjobs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.oreilly.demo.android.pa.microjobs.MicroJobsDatabase.JobDetailCursor;


/**
 * MicroJobsDetail
 */
public class MicroJobsDetail extends Activity {

    private static TextView txtTitle;
    private static TextView txtEmployer;
    private static TextView txtDescription;
    private static TextView txtContact;
    private static TextView txtPhone;
    private static ImageButton btnPhone;
    static Integer job_id;

    MicroJobsDatabase db;

    JobDetailCursor job;


    // Create a button click listener for the Dial button.
    private final Button.OnClickListener btnPhoneOnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                startActivity(Intent.getIntent("tel:" + job.getColPhone()));
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
        setContentView(R.layout.microjobsdetail);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtEmployer = (TextView) findViewById(R.id.txtEmployer);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtContact = (TextView) findViewById(R.id.txtContact);
        txtPhone = (TextView) findViewById(R.id.txtPhone);

        // get the job_id for this job from the bundle passed by MicroJobsList
        Bundle bIn = this.getIntent().getExtras();
        job_id = Integer.valueOf(bIn.getInt("_id"));

        db = new MicroJobsDatabase(this);
        job = db.getJobDetails(job_id.longValue());
        startManagingCursor(job);

        // fill in the form and display
        txtTitle.setText(job.getColTitle());
        txtEmployer.setText(job.getColEmployerName());
        txtDescription.setText(job.getColDescription());
        txtContact.setText(job.getColContactName());
        txtPhone.setText(job.getColPhone());

        // Implement callback for the dial button
        btnPhone = (ImageButton) findViewById(R.id.btnPhone);
        btnPhone.setOnClickListener(btnPhoneOnClick);
        btnPhone.setImageResource(R.drawable.phone);
    }

    /**
     * Setup menus for this page.
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean supRetVal = super.onCreateOptionsMenu(menu);
        menu.add(0, 0, Menu.NONE, getString(R.string.detail_menu_back_to_list));
        menu.add(0, 1, Menu.NONE, getString(R.string.detail_menu_employer_info));
        menu.add(0, 2, Menu.NONE, getString(R.string.detail_menu_delete_job));
        menu.add(0, 3, Menu.NONE, getString(R.string.detail_menu_edit_job));
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
                Intent iEmp = new Intent(MicroJobsDetail.this, MicroJobsEmpDetail.class);
                Bundle bEmp = new Bundle();
                bEmp.putInt("_id", job_id.intValue());
                iEmp.putExtras(bEmp);

                startActivity(iEmp);
                return true;
            case 2:
                // Delete this job
                // Setup Delete Alert Dialog
                final int DELETE_JOB = 0;
                final int CANCEL_DELETE = 1;

                Handler mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case DELETE_JOB:
                            db.deleteJob(job_id.longValue());
                            startActivity(new Intent(MicroJobsDetail.this, MicroJobsList.class));
                            break;

                            case CANCEL_DELETE:
                            // Do nothing
                            break;
                        }
                    }
                };
                // "Answer" callback.
                final Message acceptMsg = Message.obtain();
                acceptMsg.setTarget(mHandler);
                acceptMsg.what = DELETE_JOB;

                // "Cancel" callback.
                final Message rejectMsg = Message.obtain();
                rejectMsg.setTarget(mHandler);
                rejectMsg.what = CANCEL_DELETE;

                new AlertDialog.Builder(this)
                  .setMessage("Are you sure you want to delete this job?")
                  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                      @Override
                    public void onClick(DialogInterface dialog, int value) {
                          rejectMsg.sendToTarget();
                      }})
                  .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                      @Override
                    public void onClick(DialogInterface dialog, int value) {
                              acceptMsg.sendToTarget();
                      }})
                  .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                          rejectMsg.sendToTarget();
                      }})
                      .show();
                return true;
            case 3:
                // Edit this job
                // Start the Edit Job Activity, passing this job's id
                Intent iEdit = new Intent(MicroJobsDetail.this, EditJob.class);
                Bundle bEdit = new Bundle();
                bEdit.putInt("_id", job_id.intValue());
                iEdit.putExtras(bEdit);
                startActivity(iEdit);
                return true;

            default:
                return false;
        }
    }
}
