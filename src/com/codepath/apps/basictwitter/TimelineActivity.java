package com.codepath.apps.basictwitter;

import java.util.ArrayList;
import org.json.JSONArray;
import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Activity;
import android.content.Context;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;


public class TimelineActivity extends Activity {
	public TwitterClient client;
	private ArrayList<Tweet> tweets;
	private ArrayAdapter<Tweet> adapterTweets;
	private ListView lvTweets;
	private int count;
	private long maxId;
	private long sinceId;
	private Boolean isFirstLoad = true;
	private final int REQUEST_CODE_COMPOSE = 10;
	private SwipeRefreshLayout swipeContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		client = TwitterApplication.getRestClient();
		lvTweets = (ListView) findViewById(R.id.lvTweets);
		tweets = new ArrayList<Tweet>();
		adapterTweets = new TweetArrayAdapter(this, tweets);
		count = 20;
		lvTweets.setAdapter(adapterTweets);
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
        	@Override
	    	public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
        		if(isNetworkAvailable()) {
        		addOlderTweetstoTimeline(count, maxId, 0);
        		} else { networkUnavailableToast(); }
	    	}
        });
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
            	refreshTimeline();
            } 
        });
        
       // Pull Tweets from Active Android
        
        if(isNetworkAvailable()) {
		addOlderTweetstoTimeline(count, maxId, 0);
        } else { networkUnavailableToast(); }
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }
	
	public void addOlderTweetstoTimeline(int count, long maxId, long sinceId) {
		
		client.getHomeTimeline(count, maxId, sinceId, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {
				ArrayList<Tweet> newTweets = Tweet.fromJSONArray(json);
				setmaxId(newTweets);
				
				// Add Active Android Put Data
				// clear ArrayList
				// Pull Data from Active Android
				
				adapterTweets.addAll(newTweets);
				Boolean firstLoad = checkFirstLoad();
				if (firstLoad == true) {
				setsinceId(tweets);
				}
				setFirstLoad(false);
			}

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("Debug", e.toString());
				Log.d("Debug", s.toString());
			}
		});
		
	}
	
	public void addNewerTweetstoTimeline(int count, long maxId, long sinceId) {
		
		client.getHomeTimeline(count, 0, sinceId, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {
				ArrayList<Tweet> newTweets = Tweet.fromJSONArray(json);
				if (newTweets.size() > 0) {
					setsinceId(newTweets);
					
					// Add Active Android Put Data
					// clear ArrayList
					// Pull Data from Active Android										
					
					tweets.addAll(0, newTweets);
					adapterTweets.notifyDataSetChanged();
					swipeContainer.setRefreshing(false);
				}			
			}
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("Debug", e.toString());
				Log.d("Debug", s.toString());
				swipeContainer.setRefreshing(false);
			}
			
		});
		
	}
	
	public void setmaxId(ArrayList<Tweet> newTweets) {
		Tweet lastTweet = newTweets.get(newTweets.size() - 1);
		maxId = (lastTweet.getUid())-1;
	}
	
	public void setsinceId(ArrayList<Tweet> newTweets) {
		Tweet firstTweet = newTweets.get(0);
		sinceId = firstTweet.getUid();
	}
	
	private Boolean checkFirstLoad() {
		return isFirstLoad;
	}
	
	private void setFirstLoad(boolean b) {
		isFirstLoad = b;		
	}
	
	public void refreshTimeline() {
		if(isNetworkAvailable()) {
			addNewerTweetstoTimeline(count, maxId, sinceId);
		} else { networkUnavailableToast(); }
		
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
		startActivityForResult(i, REQUEST_CODE_COMPOSE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  // REQUEST_CODE is defined above
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_COMPOSE) {
			Tweet newTweet = (Tweet) data.getSerializableExtra("newTweet");
			checkAndAddNewTweet(newTweet);
		}
	}
	
	private void checkAndAddNewTweet(Tweet newTweet) {
		tweets.add(0, newTweet);
		
		// Add Active Android Put Data
		// clear ArrayList
		// Pull Data from Active Android
		
		adapterTweets.notifyDataSetChanged();
		sinceId = newTweet.getUid();
		
	}
	
	private Boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
	public void networkUnavailableToast() {
		Toast.makeText(this, "Network is Unavailable", Toast.LENGTH_LONG).show(); 
	}
}
