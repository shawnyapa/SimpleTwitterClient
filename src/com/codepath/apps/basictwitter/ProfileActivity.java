package com.codepath.apps.basictwitter;

import org.json.JSONObject;

import com.codepath.apps.basictwitter.fragments.UserTimelineFragment;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends FragmentActivity {
	
	ImageView ivProfile;
	TextView tvTagline;
	TextView tvFollowing;
	TextView tvFollowers;
	User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		ivProfile = (ImageView) findViewById(R.id.ivProfile);
		tvTagline = (TextView) findViewById(R.id.tvTagline);
		tvFollowing = (TextView) findViewById(R.id.tvFollowing);
		tvFollowers = (TextView) findViewById(R.id.tvFollowers);
		user = (User) getIntent().getSerializableExtra("User");
		if (user == null) {
			loadProfileInfo(); 
		} else {		
			loadProfileInfoforOtherUsers(user);
			createUserTimelineFragment(user);
		}	
	}
	
	public void createUserTimelineFragment(User user) {
		String userId = String.valueOf(user.getUid());
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// Replace the container with the new fragment
		UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
		userTimelineFragment.setUser(userId);
		ft.replace(R.id.ctUserTimelineFragment, userTimelineFragment);
		// Execute the changes specified
		ft.commit();
	}
	
	public void loadProfileInfo() {
		TwitterApplication.getRestClient().getUserInfo(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject json) {
				User u = User.fromJSON(json);
				getActionBar().setTitle("@" + u.getScreenName());
				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.displayImage(u.getProfileImageUrl(), ivProfile);
				tvTagline.setText(u.getTagline());
				tvFollowing.setText("Following: " + u.getFollowing());
				tvFollowers.setText("Followers: " + u.getFollowers());
				createUserTimelineFragment(u);
			}
		});
	}
	
	public void loadProfileInfoforOtherUsers(User u) {	
			getActionBar().setTitle("@" + u.getScreenName());
			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(u.getProfileImageUrl(), ivProfile);
			tvTagline.setText(u.getTagline());
			tvFollowing.setText("Following: " + u.getFollowing());
			tvFollowers.setText("Followers: " + u.getFollowers());	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onUserProfile(View v) {
		//Toast.makeText(this, "Button Pressed", Toast.LENGTH_SHORT).show();
	}
}
