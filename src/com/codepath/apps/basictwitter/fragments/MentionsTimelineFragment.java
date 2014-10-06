package com.codepath.apps.basictwitter.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MentionsTimelineFragment extends TweetsListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setType("mentions");
        if(isNetworkAvailable()) {
		addTweetstoTimeline(count, maxId, 0);
        } else { 
        	networkUnavailableToast();
        	// Check if this is the First Ever Application Launch and if the DB Exists
        	if (doesDatabaseExist()) {
        		clearAndReloadTweetsfromActiveAndroid();
        	}
        }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
}
