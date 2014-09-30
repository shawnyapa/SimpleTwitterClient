package com.codepath.apps.basictwitter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateTweetActivity extends Activity {
	public TwitterClient client;
	private EditText etTweet;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_tweet);
		etTweet = (EditText) findViewById(R.id.etTweet);
		client = TwitterApplication.getRestClient();
	}
	
	public void postTweet(View v) {
		Tweet tweet = new Tweet();
		String tweetBody = etTweet.getText().toString();
		tweet.setBody(tweetBody);
		//Toast.makeText(this, "Body: " + tweetBody, Toast.LENGTH_SHORT).show();
		// Initiate Spinner
		
		client.postTweet(tweetBody, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject json) {
				//Log.d("debug", json.toString());
				//Remove Spinner
				CreateTweetActivity.this.finish();
			}
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug", e.toString());
				Toast.makeText(CreateTweetActivity.this, "Unable to Tweet your Status", Toast.LENGTH_SHORT).show();
				// Remove Spinner
			}
		});
		

	}
}
