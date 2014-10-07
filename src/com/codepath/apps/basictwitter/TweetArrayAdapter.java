package com.codepath.apps.basictwitter;


import java.util.Date;
import java.util.List;

import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.text.format.DateUtils;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {
	
	ImageLoader imageLoader;

	public TweetArrayAdapter(Context context, List<Tweet> tweets) {
		super(context, 0, tweets);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		Tweet tweet = getItem(position);	
		View v;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			v = inflater.inflate(R.layout.tweet_item, parent, false);
		} else {
			v = convertView;
		}
		ImageView ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
		TextView tvScreenName = (TextView) v.findViewById(R.id.tvScreenName);
		TextView tvBody = (TextView) v.findViewById(R.id.tvBody);
		TextView tvTimeDelta = (TextView) v.findViewById(R.id.tvTimeDelta);
		ivProfileImage.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), ivProfileImage);
		Button btUser = (Button) v.findViewById(R.id.btUser);
		User user = tweet.getUser();
		btUser.setTag(user);
		tvScreenName.setText(tweet.getUser().getScreenName());
		tvBody.setText(tweet.getBody());
		tvTimeDelta.setText(tweetTimeDelta(tweet.getCreatedAt()));
		
		return v;
	}
	
	public String tweetTimeDelta(Date createdDate) {
		long now = System.currentTimeMillis();
		long createdTime = createdDate.getTime();
		String timeDeltaString = DateUtils.getRelativeTimeSpanString(createdTime, now, DateUtils.MINUTE_IN_MILLIS).toString();
		return timeDeltaString;
	}
	
}
