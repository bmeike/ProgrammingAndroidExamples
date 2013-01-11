package com.oreilly.demo.android.pa.searchdemo;

import android.content.SearchRecentSuggestionsProvider;

public class CustomSearchSuggestionProvider extends SearchRecentSuggestionsProvider {
	public final static String AUTHORITY = 
			"com.oreilly.demo.android.pa.searchdemo.CustomSearchSuggestionProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public CustomSearchSuggestionProvider() {
		super();
		setupSuggestions(AUTHORITY, MODE);
	}
}
