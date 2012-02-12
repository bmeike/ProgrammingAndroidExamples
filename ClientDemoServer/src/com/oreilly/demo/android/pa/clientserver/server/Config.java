package com.oreilly.demo.android.pa.clientserver.server;

import java.util.Set;
import java.util.HashSet;

import org.json.JSONObject;
import org.json.JSONArray;

import com.oreilly.demo.android.pa.clientserver.server.data.UsersData;


public final class Config {
	public static final String DEBUGKEY			= "debug";
	public static final String HTTPSERVLETS		= "httpservlets";
	public static final String HTTPSERVLETPORT	= "httpservletport";
	public static final String USERDATA			= "userdata";

	private boolean DEBUG						= false;
	private HashSet<String> SERVLETS			= new HashSet<String>();
	private int SERVLETPORT						= -1;
	private UsersData userdata					= null;

	public Config() { }

	public Config(JSONObject json) {
		SERVLETPORT = json.optInt(HTTPSERVLETPORT, 8080);

		DEBUG = json.optBoolean(DEBUGKEY, false);

		SERVLETS = new HashSet<String>();
		JSONArray servarr = json.optJSONArray(HTTPSERVLETS);
		if(servarr != null && servarr.length() > 0) {
			for(int i=0;i<servarr.length();i++) {
				if(!servarr.isNull(i)) try { SERVLETS.add(servarr.getString(i)); } catch (Exception e) {}
			}
		}
		
		if(json.has(USERDATA)) {
			ServerStatic.info("Loading UserData: "+json.optString(USERDATA));
			userdata = new UsersData(json.optString(USERDATA));
			if(userdata != null) ServerStatic.info("Loaded UserData: "+json.optString(USERDATA));
			else ServerStatic.error("FAILED to load UserData: "+json.optString(USERDATA));
		}
	}

	public boolean debug() { return DEBUG; }

	public Set<String> getServlets() { return SERVLETS; }

	public int getServletPort() { return SERVLETPORT; }
	
	public UsersData getUserData() { return userdata; }
}
