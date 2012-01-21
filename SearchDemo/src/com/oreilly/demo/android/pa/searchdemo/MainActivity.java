package com.oreilly.demo.android.pa.searchdemo;

import com.oreilly.demo.android.pa.R;

import android.app.Activity;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);

	     setContentView(R.layout.main);
	        
	     findViewById(R.id.search).setOnClickListener(new OnClickListener() {
	    	 @Override
	    	 public void onClick(View v) {
	    		 onSearchRequested();
	    	 }
	     });
	 }
	 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {	
		 MenuItem search = menu.add(Menu.NONE, Menu.NONE, 0, R.string.search);
		 search.setIcon(android.R.drawable.ic_search_category_default);
		 search.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem _menuItem) {
				onSearchRequested();
				return true;
			}
		 });
    	
		 MenuItem clearhistory = menu.add(Menu.NONE,Menu.NONE,1, R.string.menu_clearhistory);
		 clearhistory.setIcon(android.R.drawable.ic_delete);
		 clearhistory.setOnMenuItemClickListener(new OnMenuItemClickListener() {
    		public boolean onMenuItemClick(MenuItem _menuItem) {
    			SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getBaseContext(), CustomSearchSuggestionProvider.AUTHORITY, CustomSearchSuggestionProvider.MODE);
    			suggestions.clearHistory();
    			return true;
    		}
		 });
		 return true;
	}
}
