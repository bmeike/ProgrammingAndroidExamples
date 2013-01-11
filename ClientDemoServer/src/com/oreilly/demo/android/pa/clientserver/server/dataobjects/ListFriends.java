package com.oreilly.demo.android.pa.clientserver.server.dataobjects;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import com.oreilly.demo.android.pa.clientserver.server.data.UsersData;


public class ListFriends {	
	public ArrayList<User> friends;
	public ArrayList<Change> history;
	public long time;
	
	public ListFriends() {
		this.friends = new ArrayList<User>();
		this.history = new ArrayList<Change>();
		this.time = System.currentTimeMillis();
	}
	
	@Override
	public String toString() {
		return toJSON(this).toString();
	}
	
	public static ListFriends fromJSON(JSONObject json) {
		if(json == null) return null;
		ListFriends lf = new ListFriends();
		if(json.has("time")) {
			lf.time = json.optLong("time", System.currentTimeMillis());
		}
		if(json.has("friends")) {
			JSONArray friends = json.optJSONArray("friends");
			if(friends != null && friends.length() > 0) {
				for(int i=0;i<friends.length();i++) {
					if(!friends.isNull(i)) {
						try {
							User friend = User.fromJSON(friends.optJSONObject(i));
							if(friend != null) lf.friends.add(friend);
						} catch (Exception e) { e.printStackTrace(); }
					}
				}
			}
		}
		if(json.has("history")) {
			JSONArray history = json.optJSONArray("history");
			if(history != null && history.length() > 0) {
				for(int i=0;i<history.length();i++) {
					if(!history.isNull(i)) {
						try {
							Change change = Change.fromJSON(history.optJSONObject(i));
							if(change != null) lf.history.add(change);
						} catch (Exception e) { e.printStackTrace(); }
					}
				}
				Collections.sort(lf.history);
			}
		}
		
		return lf;
	}
	
	public static JSONObject toJSON(ListFriends lf) {
		if(lf == null) return null;
		JSONObject json = new JSONObject();
		JSONArray friends = new JSONArray();
		for(User friend: lf.friends) {
			friends.put(User.toJSON(friend));
		}
		JSONArray history = new JSONArray();
		for(Change change: lf.history) {
			history.put(Change.toJSON(change));
		}
		try { json.put("friends", friends); } catch (Exception e) {}
		try { json.put("history", history); } catch (Exception e) {}
		try { json.put("time", lf.time); } catch (Exception e) {}
		return json;
	}
	
	public static ListFriends listFriends(UsersData data, User user, long lasttime) {
		ListFriends lf = new ListFriends();
		for(long i: user.friends) {
			User friend = data.getUser(i);
			if(friend != null) {
				User friendcopy = new User();
				friendcopy.id = friend.id;
				friendcopy.name = friend.name;
				friendcopy.phone = friend.phone;
				lf.friends.add(friendcopy);
			}
		}
		for(Change c: user.history) {
			if(c.time > lasttime) lf.history.add(c);
		}
		Collections.sort(lf.history);
		return lf;
	}
}
