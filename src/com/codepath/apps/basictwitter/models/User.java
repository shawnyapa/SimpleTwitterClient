package com.codepath.apps.basictwitter.models;

import java.io.Serializable;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

@Table(name = "Users")
public class User extends Model implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    //@Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    @Column(name = "uid", unique = true)    
    public long uid;
    @Column(name="name")
	public String name;
	@Column (name="screenName")
	public String screenName;
	@Column (name="profileImageUrl")
	public String profileImageUrl;
	@Column (name="tagline")
	public String tagline;
	@Column (name="following")
	public String following;
	@Column (name="followers")
	public String followers;
	
	
	
	public User() {
		super();
	}
	
	public static User fromJSON(JSONObject jsonObject) {
		User user = new User();
		try {
			user.name = jsonObject.getString("name");
			user.uid = jsonObject.getLong("id");
			user.screenName = jsonObject.getString("screen_name");
			user.profileImageUrl = jsonObject.getString("profile_image_url");
			user.tagline = jsonObject.getString("description");
			user.followers = jsonObject.getString("followers_count");
			user.following = jsonObject.getString("favourites_count");
			//user.save();	
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		return user;
	}

	public String getName() {
		return name;
	}

	public long getUid() {
		return uid;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	
    public String getTagline() {
		return tagline;
	}

	public String getFollowing() {
		return following;
	}

	public String getFollowers() {
		return followers;
	}

	public static List<User> checkforUserbyUid (long uid) {
    	return new Select()
        .from(User.class)
        .where("uid = ?", uid)
        .execute();			
    }

}
