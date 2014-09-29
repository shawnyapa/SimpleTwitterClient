package com.codepath.apps.basictwitter;

import java.util.ArrayList;

import org.json.JSONArray;

import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TimelineActivity extends Activity {
	public TwitterClient client;
	private ArrayList<Tweet> tweets;
	private ArrayAdapter<Tweet> adapterTweets;
	private ListView lvTweets;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		client = TwitterApplication.getRestClient();
		lvTweets = (ListView) findViewById(R.id.lvTweets);
		tweets = new ArrayList<Tweet>();
		adapterTweets = new TweetArrayAdapter(this, tweets);
		lvTweets.setAdapter(adapterTweets);
		
		populateTimeline();
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }
	
	public void populateTimeline() {
		String sinceID = "1";
		client.getHomeTimeline(sinceID, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {
				//Log.d("debug", json.toString());
				adapterTweets.addAll(Tweet.fromJSONArray(json));
				
			}
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug", e.toString());
				Log.d("debug", s.toString());
			}
		});
		
	}
	
	private void refreshTimeline() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.postTweet:
	            onCompose(item);
	            return true;
	        case R.id.refreshTimeline:
	            refreshTimeline();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	


	public void onCompose(MenuItem mi) {
		Intent i = new Intent(this, CreateTweetActivity.class);
		startActivity(i);
	}
}
