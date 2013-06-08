package com.snda.inote.provider;

import android.content.SearchRecentSuggestionsProvider;

/*
 * @Author KevinComo@gmail.com
 * 2010-6-29
 */

public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {
	public final static String AUTHORITY = "com.snda.inote.SuggestionProvider";
	
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public SearchSuggestionProvider() {
        super();
        setupSuggestions(AUTHORITY, MODE);
    }
}
