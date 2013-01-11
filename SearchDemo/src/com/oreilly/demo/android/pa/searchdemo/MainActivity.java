package com.oreilly.demo.android.pa.searchdemo;

import com.oreilly.demo.android.pa.R;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SearchView;

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
	     
	     if(getResources().getBoolean(R.bool.UseDBSearchLogic)) {
	    	 ((android.widget.TextView) findViewById(R.id.searchlogictext)).
	    	 			setText("Using SearchDBLogic (DB Based Search)");
	     } else {
	    	 ((android.widget.TextView) findViewById(R.id.searchlogictext)).
	    	 			setText("Using SearchLogic (Basic Index System)");
	     }
	 }
	 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
		
		 // check to see if we are using Android 3.0+
		 if(android.os.Build.VERSION.SDK_INT >= 
			 						android.os.Build.VERSION_CODES.HONEYCOMB) {
			 // Inflate the options menu from XML
		     MenuInflater inflater = getMenuInflater();
		     inflater.inflate(R.menu.search_menu, menu);
	
		     // Get the SearchView and set the searchable configuration
		     SearchManager searchManager = 
		    	 		(SearchManager) getSystemService(Context.SEARCH_SERVICE);
		     SearchView searchView = 
		    	 	(SearchView) menu.findItem(R.id.menu_search).getActionView();
		     searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		     // Do not iconify the widget; expand it by default
		     searchView.setIconifiedByDefault(false); 
		 }
		 
		 MenuItem clearhistory = menu.add(Menu.NONE,Menu.NONE,1, R.string.menu_clearhistory);
		 clearhistory.setIcon(android.R.drawable.ic_delete);
		 clearhistory.setOnMenuItemClickListener(new OnMenuItemClickListener() {
    		public boolean onMenuItemClick(MenuItem _menuItem) {
    			SearchRecentSuggestions suggestions = 
    				new SearchRecentSuggestions(getBaseContext(), 
    											CustomSearchSuggestionProvider.AUTHORITY, 
    											CustomSearchSuggestionProvider.MODE);
    			suggestions.clearHistory();
    			return true;
    		}
		 });
		 return true;
	}
}
