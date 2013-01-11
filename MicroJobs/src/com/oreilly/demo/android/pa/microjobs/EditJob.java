package com.oreilly.demo.android.pa.microjobs;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


/**
 * EditJob
 */
public class EditJob extends ListActivity {
    static Spinner spnEmployer;
    static TextView txtTitle;
    static TextView txtDescription;
    static Integer job_id;
    private MicroJobsDatabase.JobDetailCursor job;
    private static Button btnUpdate, btnCancel;

    MicroJobsDatabase db;

    private class Employer {
        public String employerName;
        public long id;
        Employer( long id, String employerName){
            this.id = id;
            this.employerName = employerName;
        }
        @Override
        public String toString() {
            return this.employerName;
        }
    }


    // Create a button click listener for the Update button.
    private final Button.OnClickListener btnUpdateOnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Employer employer = (Employer)spnEmployer.getSelectedItem();
//            Toast.makeText(
//                    EditJob.this,
//                    String.format(
//                            "Job: %d\nEmployer: %s (%d)\nTitle: %s\nDesc: %s",
//                            job_id,
//                            employer.employerName,
//                            employer.id,
//                            txtTitle.getText(),
//                            txtDescription.getText()
//                    ),
//                    Toast.LENGTH_SHORT
//            ).show();
            if ((txtTitle.getText().length()==0) || (txtDescription.getText().length()==0)){
                Toast.makeText(EditJob.this, "Fill out the form completely first.", Toast.LENGTH_LONG).show();
            } else {
                db.editJob(job_id.longValue(), employer.id, txtTitle.getText().toString(), txtDescription.getText().toString());
                Toast.makeText(EditJob.this, "Job updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };

    // Create a button click listener for the Cancel button.
    private final Button.OnClickListener btnCancelOnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
                finish();
            }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editjob);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        spnEmployer = (Spinner) findViewById(R.id.spnEmployer);

        // get the job_id for this job from the bundle passed by MicroJobsDetail
        Bundle bIn = this.getIntent().getExtras();
        job_id = Integer.valueOf(bIn.getInt("_id"));

        db = new MicroJobsDatabase(this);
        job = db.getJobDetails(job_id.longValue());

        txtTitle.setText(job.getColTitle());
        txtDescription.setText(job.getColDescription());

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(btnUpdateOnClick);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(btnCancelOnClick);

        // spnEmployer
        List<Employer> employersList = new ArrayList<Employer>();
        MicroJobsDatabase.EmployersCursor c = db.getEmployers();
        startManagingCursor(c);
        int position=0;
        for(int i=0; i<c.getCount(); i++){
            c.moveToPosition(i);
            employersList.add(new Employer(c.getColId(),c.getColEmployerName()));
            if (c.getColId()==job.getColEmployersId()) {
                position=i;
            }
        }

        ArrayAdapter<Employer> aspnEmployers = new ArrayAdapter<Employer>(
                this, android.R.layout.simple_spinner_item, employersList);
        aspnEmployers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnEmployer.setAdapter(aspnEmployers);
        spnEmployer.setSelection(position);
    }
}
