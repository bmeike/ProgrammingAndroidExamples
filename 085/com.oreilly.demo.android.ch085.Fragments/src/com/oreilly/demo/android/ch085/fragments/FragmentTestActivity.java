/* $Id: $
 */
package com.oreilly.demo.android.ch085.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.oreilly.demo.android.ch085.fragments.R;


/**
 * FragmentTestActivity
 */
public class FragmentTestActivity extends Activity implements OnItemClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ListView l = (ListView) findViewById(R.id.number_list);
        ArrayAdapter<String> numbers = new ArrayAdapter<String>(
            getApplicationContext(),
            android.R.layout.simple_list_item_1, 
            new String [] {
                "one", "two", "three", "four", "five", "six"
        });
        l.setAdapter(numbers);
        l.setOnItemClickListener(this);
    }

    
    /**
     * Add a Fragment to our stack with n Androids in it
     */
    private void stackAFragment(int nAndroids) {
    	Fragment f = new TestFragment(nAndroids);
    	
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.the_frag, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

	/**
	 * Called when a number gets clicked
	 */
	@Override
    public void onItemClick(
	    AdapterView<?> parent,
	    View view,
	    int position,
	    long id)
	{
		stackAFragment(position + 1);
	}
}
