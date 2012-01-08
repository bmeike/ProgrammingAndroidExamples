package com.oreilly.demo.android.pa.clientserver.server.data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.oreilly.demo.android.pa.clientserver.server.dataobjects.User;


public class UsersData {
	private String filepath;
	
	public ArrayList<User> users = new ArrayList<User>();
	public volatile int lastid;
	
	public UsersData(String filepath) {
		this.filepath = filepath;
		try {
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public User addUser(User user) {
		if(user == null) return null;
		if(user.id <= lastid) {
			lastid++;
			user.id = lastid;
		}
		users.add(user);
		return user;
	}
	
	public User getUserByToken(String authtoken) {
		for(User user: users) {
			if(user.authtoken != null && user.authtoken.equals(authtoken)) return user;
		}
		return null;
	}
	
	public User getUser(String username) {
		for(User user: users) {
			if(user.username != null && user.username.equals(username)) return user;
		}
		return null;
	}
	
	public User getUser(long id) {
		for(User user: users) {
			if(user.id == id) return user;
		}
		return null;
	}
	
	public String toString() {
		try {
			JSONObject json = toJSON();
			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized void saveData() throws Exception {
		if(filepath == null) throw new Exception("filepath not set!");
		FileWriter out = new FileWriter(filepath);
		String ob = toString();
		if(ob != null) out.append(ob);
		out.close();
	}
	
	private void loadData() throws Exception {
		if(filepath == null) throw new Exception("filepath not set!");
		File datafile = new File(filepath);
		if(!datafile.exists()) throw new Exception("no file "+filepath);
		FileReader in = new FileReader(datafile);
		int r = -1;
		StringBuilder ref = new StringBuilder();
		while((r = in.read()) != -1) {
			ref.append((char) r);
		}
		in.close();
		
		fromJSON(new JSONObject(ref.toString()));
	}
	
	private void fromJSON(JSONObject json) {
		if(json == null) return;
		JSONArray arr = json.optJSONArray("users");
		if(arr != null) {
			for(int i=0;i<arr.length();i++) {
				if(!arr.isNull(i)) {
					try {
						users.add(User.fromJSON(arr.getJSONObject(i)));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		lastid = json.optInt("lastid");
	}
	
	private JSONObject toJSON() throws Exception {
		JSONObject json = new JSONObject();
		if(!users.isEmpty()) {
			JSONArray arr = new JSONArray();
			for(User user : users) {
				arr.put(User.toJSON(user));
			}
			json.put("users", arr);
		}
		json.put("lastid", lastid);
		return json;
	}
}
