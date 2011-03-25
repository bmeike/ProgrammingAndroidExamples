/* $Id: $
 */
package com.oreilly.demo.android.ch085.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.oreilly.demo.android.ch085.fragments.R;


/**
 * TestFragment
 */
public class TestFragment extends Fragment {
    private int nAndroids;

    /**
     * Ctor: default
     */
    public TestFragment() { this(0); }

    /**
     * Ctor: explicit creation.
     * 
     * @param nAndroids
     */
    public TestFragment(int nAndroids) {
        this.nAndroids = nAndroids;
    }

    /**
     * If we are being created with saved state, restore our state
     */
    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        if (null != saved) {
            nAndroids = saved.getInt("nAndroids");
        }
    }

    /**
     * Save the number of Androids to be displayed
     */
    @Override
    public void onSaveInstanceState(Bundle toSave) {
        toSave.putInt("nAndroids", nAndroids);
    }

    /**
     * Make a grid and fill it with n Androids
     */
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle saved)
    {
        int n;
        Context c = getActivity().getApplicationContext();
        LinearLayout l = new LinearLayout(c);
        for (n = 0; n < nAndroids; n++) {
            ImageView i = new ImageView(c);
            i.setImageResource(R.drawable.android);
            l.addView(i);
        }
        return l;
    }
}
