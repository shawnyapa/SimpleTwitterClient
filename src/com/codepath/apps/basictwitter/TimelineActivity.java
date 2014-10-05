package com.codepath.apps.basictwitter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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
	private final int REQUEST_CODE_COMPOSE = 10;
	private SwipeRefreshLayout swipeContainer;
	public enum TweetQueryType {
		FIRST_LOAD, OLDER_TWEETS, NEWER_TWEETS
		}
	public TweetQueryType newTweetType = TweetQueryType.FIRST_LOAD;

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
        		newTweetType = TweetQueryType.OLDER_TWEETS;
        		addTweetstoTimeline(count, maxId, 0);
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
        
        if(isNetworkAvailable()) {
		addTweetstoTimeline(count, maxId, 0);
        } else { 
        	networkUnavailableToast();
        	// Check if this is the First Ever Application Launch and if the DB Exists
        	Context context = getApplicationContext();
        	ContextWrapper contextWrapper = new ContextWrapper(context);
        	if (doesDatabaseExist(contextWrapper, "RestClient.db")) {
        		clearAndReloadTweetsfromActiveAndroid();
        	}
        }
	}
	
	private static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
	    File dbFile = context.getDatabasePath(dbName);
	    return dbFile.exists();
	}
	
    private void clearAndReloadTweetsfromActiveAndroid() {
		List <Tweet> activeAndroidTweets =  Tweet.getAll();
		tweets.clear();
		tweets.addAll(activeAndroidTweets);
		adapterTweets.notifyDataSetChanged();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

	public void addTweetstoTimeline(int count, long maxId, long sinceId) {
		
		client.getHomeTimeline(count, maxId, sinceId, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {
				ArrayList<Tweet> newTweets = Tweet.fromJSONArray(json);			 
				if (newTweets.size() > 0) {				
										
					if (newTweetType == TweetQueryType.NEWER_TWEETS) {
						tweets.addAll(0, newTweets);
					} else {
						tweets.addAll(newTweets);
					}
					// clear ArrayList
					// Pull Data from Active Android
					clearAndReloadTweetsfromActiveAndroid();
					checkTweetTypeAndSetSinceIdAndMaxId();
				}
				stopRefreshing();
			}

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("Debug", e.toString());
				Log.d("Debug", s.toString());
				stopRefreshing();
			}
		});
		
	}
	
	public void setmaxId(ArrayList<Tweet> tweets) {
		Tweet lastTweet = tweets.get(tweets.size() - 1);
		maxId = (lastTweet.getUid())-1;
	}
	
	public void setsinceId(ArrayList<Tweet> tweets) {
		Tweet firstTweet = tweets.get(0);
		sinceId = firstTweet.getUid();
	}
	
	public void checkTweetTypeAndSetSinceIdAndMaxId() {
		
		if(newTweetType == TweetQueryType.NEWER_TWEETS) {
			setsinceId(tweets);
		}
		if(newTweetType == TweetQueryType.OLDER_TWEETS) {
			setmaxId(tweets);
		}
		if(newTweetType == TweetQueryType.FIRST_LOAD) {
			setsinceId(tweets);
			setmaxId(tweets);
		}
		
	}
	
	public void refreshTimeline() {
		if(isNetworkAvailable()) {
			newTweetType= TweetQueryType.NEWER_TWEETS;
			addTweetstoTimeline(count, 0, sinceId);
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
		// clear ArrayList
		// Pull Data from Active Android
		clearAndReloadTweetsfromActiveAndroid();
		sinceId = newTweet.getUid();
		
	}
	
	private void stopRefreshing() {
		swipeContainer.setRefreshing(false);
		
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
