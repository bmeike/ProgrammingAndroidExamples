package com.oreilly.demo.android.pa.searchdemo;

import com.oreilly.demo.android.pa.R;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SearchActivity extends ListActivity {
	public final static String CONTENT = "content://";
	
	public static SearchLogicInterface SEARCHLOGIC;
	
	public static void initializeSearchLogic(Context context) throws Exception {
		initializeSearchLogic(context, R.raw.sonnets);
	}
	
	public static void initializeSearchLogic(Context context, int resourceId) throws Exception {
		if(SEARCHLOGIC == null) {
			// if UseDBSearchLogic (in res/values/bool.xml) 
			// is true then use the SearchDBLogic else use SearchLogic
			
			SEARCHLOGIC = context.getResources().getBoolean(R.bool.UseDBSearchLogic) ? 
								new SearchDBLogic(context, resourceId) : 
								new SearchLogic(context, resourceId);
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search);
        
        try { 
        	initializeSearchLogic(this);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        loadView(null);
    }
    
    private void loadView(Sonnet son) {
    	clearUI();
    	if(SEARCHLOGIC == null) {
         	TextView txt = (TextView) findViewById(R.id.title);
         	txt.setText(R.string.nosonnetsloaded);
         	txt.setVisibility(View.VISIBLE);
        } else if(son != null){
        	TextView txt = (TextView) findViewById(R.id.title);
         	txt.setText("Sonnet ".intern() + son.title);
         	txt.setVisibility(View.VISIBLE);
        	for(int i=0;i<son.lines.length;i++) {
        		TextView tline = (TextView) 
        							findViewById(getResources().getIdentifier(("line".intern()) +
        									(i+1), 
        						"id".intern(), getPackageName()));
        		if(tline != null) {
	        		tline.setText(son.lines[i]);
	        		tline.setVisibility(View.VISIBLE);
        		}
        	}
        } else {
        	Intent intent = getIntent();
        	String query = null;
        	
        	if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
        		query = intent.getStringExtra(SearchManager.QUERY).toLowerCase();
        		search(query);
        	} else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
        		Uri data = intent.getData();
        		if(data != null) {
            		query = data.toString().substring(CONTENT.length()).trim();
            		try { 
            			loadView(SEARCHLOGIC.getSonnet(Integer.parseInt(query)));
            		} catch (Exception e) { 
            			e.printStackTrace(); 
            		}
            		return;
        		} else if(intent.getDataString() != null) {
        			query = intent.getDataString();
        		} else if(intent.getStringExtra(SearchManager.QUERY) != null) {
        			query = intent.getStringExtra(SearchManager.QUERY).toLowerCase();
        		} else if(intent.getStringExtra(SearchManager.USER_QUERY) != null) {
        			query = intent.getStringExtra(SearchManager.USER_QUERY).toLowerCase();
        		}
        		
        		search(query);
        	} 
        }
    }
    
    private final Handler loadViewHandler = new Handler() { 
    	public void handleMessage(Message msg) { 
    		if(msg != null) {
    			loadView(SEARCHLOGIC.getSonnet(msg.what));
    		}
    	}
    };
    
    private void search(String query) {
    	if(query != null) {
			SearchRecentSuggestions suggestions = 
					new SearchRecentSuggestions(this, 
												CustomSearchSuggestionProvider.AUTHORITY, 
												CustomSearchSuggestionProvider.MODE);
	        suggestions.saveRecentQuery(query, null);
	        
	        final SonnetFragment[] sfrags = SEARCHLOGIC.search(query);
	        
	        if(sfrags != null && sfrags.length > 0) {
	        	getListView().setVisibility(View.VISIBLE);
	        	getListView().addHeaderView(View.inflate(this, R.layout.searchheader, null), 
	        									null, false);
	        	
	        	ArrayAdapter<SonnetFragment> arr = 
	        				new ArrayAdapter<SonnetFragment>(this, R.layout.searchrow, sfrags);
	        	setListAdapter(arr);
	    		getListView().setOnItemClickListener(new OnItemClickListener() {
	    			public void onItemClick(AdapterView<?> adpt, View view, int pos, long id) {
	    				Message.obtain(loadViewHandler, sfrags[pos - 1].num).sendToTarget();
	    			}
	    		});
	        }
		}
    }
    
    private void clearUI() {
    	getListView().setVisibility(View.GONE);
    	findViewById(R.id.title).setVisibility(View.GONE);
    	findViewById(R.id.line1).setVisibility(View.GONE);
    	findViewById(R.id.line2).setVisibility(View.GONE);
    	findViewById(R.id.line3).setVisibility(View.GONE);
    	findViewById(R.id.line4).setVisibility(View.GONE);
    	findViewById(R.id.line5).setVisibility(View.GONE);
    	findViewById(R.id.line6).setVisibility(View.GONE);
    	findViewById(R.id.line7).setVisibility(View.GONE);
    	findViewById(R.id.line8).setVisibility(View.GONE);
    	findViewById(R.id.line9).setVisibility(View.GONE);
    	findViewById(R.id.line10).setVisibility(View.GONE);
    	findViewById(R.id.line11).setVisibility(View.GONE);
    	findViewById(R.id.line12).setVisibility(View.GONE);
    	findViewById(R.id.line13).setVisibility(View.GONE);
    	findViewById(R.id.line14).setVisibility(View.GONE);
    	findViewById(R.id.line15).setVisibility(View.GONE);
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