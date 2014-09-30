package com.codepath.apps.basictwitter.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Tweet {
	private String body;
	private long uid;
	private Date createdAt;
	private User user;

	public String getBody() {
		return body;
	}

	public long getUid() {
		return uid;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public User getUser() {
		return user;
	}
	
	public void setBody(String body) {
		this.body = body;
	}

	public static Tweet fromJSON(JSONObject jsonObject) {
		Tweet tweet = new Tweet();
		try {
			tweet.body = jsonObject.getString("text");
			tweet.uid = jsonObject.getLong("id");
			tweet.createdAt = setDateFromString(jsonObject.getString("created_at"));		
			tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		return tweet;
	}

	public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());
		
		for (int i=0; i<jsonArray.length(); i++) {
			JSONObject tweetJson = null;
			try {
				tweetJson = jsonArray.getJSONObject(i);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			Tweet tweet = Tweet.fromJSON(tweetJson);
			if (tweet != null ) {
				tweets.add(tweet);
			}
		}
		return tweets;
	}
	public static Date setDateFromString(String date) {
	    SimpleDateFormat sf = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.US);
	    sf.setLenient(true);
	    Date tweetDate = new Date();
	    try {
			tweetDate = sf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    return tweetDate;
	}
	
}
