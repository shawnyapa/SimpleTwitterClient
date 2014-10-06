package com.codepath.apps.basictwitter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.codepath.apps.basictwitter.fragments.HomeTimelineFragment;
import com.codepath.apps.basictwitter.fragments.MentionsTimelineFragment;
import com.codepath.apps.basictwitter.fragments.TweetsListFragment;
import com.codepath.apps.basictwitter.listeners.FragmentTabListener;
import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;


public class TimelineActivity extends FragmentActivity {

	public final int REQUEST_CODE_COMPOSE = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_timeline);
		setupTabs();
	}
	
	private void setupTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		Tab tab1 = actionBar
			.newTab()
			.setText("Home")
			.setIcon(R.drawable.ic_home)
			.setTag("HomeTimelineFragment")
			.setTabListener(
				new FragmentTabListener<HomeTimelineFragment>(R.id.flContainer, this, "first",
								HomeTimelineFragment.class));

		actionBar.addTab(tab1);
		actionBar.selectTab(tab1);

		Tab tab2 = actionBar
			.newTab()
			.setText("Mentions")
			.setIcon(R.drawable.ic_mentions)
			.setTag("MentionsTimelineFragment")
			.setTabListener(
			    new FragmentTabListener<MentionsTimelineFragment>(R.id.flContainer, this, "second",
								MentionsTimelineFragment.class));

		actionBar.addTab(tab2);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.postTweet:
	            onCompose(item);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	public void onCompose(MenuItem mi) {
		Intent i = new Intent(this, CreateTweetActivity.class);
		startActivityForResult(i, REQUEST_CODE_COMPOSE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  // REQUEST_CODE is defined above
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_COMPOSE) {
			Tweet newTweet = (Tweet) data.getSerializableExtra("newTweet");
			//checkAndAddNewTweet(newTweet);
		}
	}
	/*
	public void checkAndAddNewTweet(Tweet newTweet) {
		tweets.add(0, newTweet);
		// clear ArrayList
		// Pull Data from Active Android
		clearAndReloadTweetsfromActiveAndroid();
		sinceId = newTweet.getUid();
		
	}
	*/

}
