package com.codepath.apps.basictwitter.models;

import java.io.Serializable;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@Table(name = "Tweets")
public class Tweet extends Model implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long uid;
	@Column(name="body")
	private String body;
	@Column(name="createdAt")
	private Date createdAt;
	@Column(name = "user", onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE)
	private User user;

	public Tweet() {
		super();
	}
	
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
			/*
			List<User> Userlist;
			Userlist = User.checkforUserbyUid(jsonObject.getJSONObject("user").getLong("id"));
			if (Userlist.size() > 0) {
				tweet.user = Userlist.get(0);
			} else {
				tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
			}
			tweet.save();
			*/
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
	
    public static List<Tweet> getAllTweets() {
        // This is how you execute a query
        return new Select()
          .from(Tweet.class)
          //.where("Category = ?", category.getId())
          .orderBy("uid DESC")
          .execute();
    }
    
    public static List<Tweet> getAllMentions() {
        // This is how you execute a query
        return new Select()
          .from(Tweet.class)
          //.where("Category = ?", category.getId())
          .orderBy("uid DESC")
          .execute();
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

	public static List<Tweet> getAllUserTweets() {
		
        return new Select()
        .from(Tweet.class)
        //.where("Category = ?", category.getId())
        .orderBy("uid DESC")
        .execute();
	}
	
}
