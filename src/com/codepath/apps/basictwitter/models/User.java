package com.codepath.apps.basictwitter.models;

import java.io.Serializable;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

@Table(name = "Users")
public class User extends Model implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
    public long uid;
    @Column(name="name")
	public String name;
	@Column (name="screenName")
	public String screenName;
	@Column (name="profileImageUrl")
	public String profileImageUrl;

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

}
