package com.oreilly.demo.android.pa.microjobs;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


/**
 * AddJob
 */
public class AddJob extends Activity {
    private static Button btnAddJob;
    static Spinner spnEmployer;
    static TextView txtTitle;
    static TextView txtDescription;

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

    // Create a button click listener for the AddJob button.
    private final Button.OnClickListener btnAddJobOnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Employer employer = (Employer)spnEmployer.getSelectedItem();
//            Toast.makeText(
//                AddJob.this,
//                String.format(
//                    "Employer: %s (%d)\nTitle: %s\nDesc: %s",
//                    employer.employerName,
//                    employer.id,
//                    txtTitle.getText(),
//                    txtDescription.getText()
//                ),
//                Toast.LENGTH_SHORT
//            ).show();
            if ((employer.id<0) || (txtTitle.getText().length()==0) || (txtDescription.getText().length()==0)){
                Toast.makeText(AddJob.this, "Fill out the form completely first.", Toast.LENGTH_LONG).show();
            } else {
                db.addJob(employer.id, txtTitle.getText().toString(), txtDescription.getText().toString());
                Toast.makeText(AddJob.this, "Job added", Toast.LENGTH_SHORT).show();
                //spnEmployer.setSelection(0); // select "choose an employer"
                txtTitle.setText("");
                txtDescription.setText("");
            }
        }
    };


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new MicroJobsDatabase(this);
        setContentView(R.layout.addjob);

        // Find the controls
        btnAddJob = (Button) findViewById(R.id.btnAddJob);
        spnEmployer = (Spinner) findViewById(R.id.spnEmployer);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtDescription = (TextView) findViewById(R.id.txtDescription);

        // btnAddJob
        btnAddJob.setOnClickListener(btnAddJobOnClick);

        // spnEmployer
        List<Employer> employersList = new ArrayList<Employer>();
        employersList.add(new Employer(-1, "Choose an employer"));
        MicroJobsDatabase.EmployersCursor c = db.getEmployers();
        startManagingCursor(c);

        for(int i=0; i<c.getCount(); i++){
            c.moveToPosition(i);
            employersList.add(new Employer(c.getColId(),c.getColEmployerName()));
        }

        ArrayAdapter<Employer> aspnEmployers = new ArrayAdapter<Employer>(
                this, android.R.layout.simple_spinner_item, employersList);
        aspnEmployers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnEmployer.setAdapter(aspnEmployers);
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

}
