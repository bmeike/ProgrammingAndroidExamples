package com.finchframework.uiframework;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

public class TabManager {

	/**
	 * Common utility code for initializing tabs, shared by activities that have
	 * fragments and tabs
	 * 
	 * Assumes the fragments are already instantiated, and that they were
	 * specified in resources
	 * 
	 * This can be called without knowing if the tab fragments are present in
	 * the layout, fails silently if it can't find the
	 * 
	 * @param activity
	 *            The activity that hosts the tabs and corresponding fragments
	 * @param defaultIndex
	 *            The index of the Fragment shown first
	 * @param nameIDs
	 *            an array of ID for tab names
	 * @param fragmentIDs
	 *            an array of IDs of Fragment resources
	 */
	public static void initialize(Activity activity, int defaultIndex,
			int[] nameIDs, int[] fragmentIDs) {

		// How many do we have?
		int n = nameIDs.length;
		int i = 0;

		// Find at least one fragment that should implement TabListener
		TabListener f = (TabListener) activity.getFragmentManager()
				.findFragmentById(fragmentIDs[i]);

		// Null check - harmless to call if there are no such fragments
		if (null != f) {

			// Get the action bar and remove existing tabs
			ActionBar b = activity.getActionBar();
			b.removeAllTabs();

			// Make new tabs and assign tags and listeners
			for (; i < n; i++) {
				f = (TabListener) activity.getFragmentManager()
						.findFragmentById(fragmentIDs[i]);
				Tab t = b.newTab().setText(nameIDs[i]).setTag(f)
						.setTabListener(f);
				b.addTab(t);
			}
			b.getTabAt(defaultIndex).select();
		}
	}

	/**
	 * If we have tabs and fragments in this activity, pass the bundle data to
	 * the fragments. Otherwise start an activity that should contain the
	 * fragments.
	 * 
	 * @param activity
	 * @param data
	 */
	public static void loadTabFragments(Activity activity, Bundle data) {
		int n = activity.getActionBar().getTabCount();
		if (0 != n) {
			doLoad(activity, n, data);
		} else {
			activity.startActivity(new Intent(activity,
					ContentControlActivity.class).putExtras(data));
		}
	}

	/**
	 * An interface to pass data to a Fragment
	 */
	public interface SetData {
		public void setData(Bundle data);
	}

	/**
	 * Iterate over the tabs, get their tags, and use these as Fragment
	 * references to pass the bundle data to the fragments
	 * 
	 * @param activity
	 * @param n
	 * @param data
	 */
	private static void doLoad(Activity activity, int n, Bundle data) {
		int i;
		ActionBar actionBar = activity.getActionBar();
		
		for (i = 0; i < n; i++) {
			SetData f = (SetData) actionBar.getTabAt(i).getTag();
			f.setData(data);
		}
		actionBar.selectTab(actionBar.getTabAt(0));
	}

}
