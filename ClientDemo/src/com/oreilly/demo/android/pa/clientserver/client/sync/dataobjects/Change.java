package com.oreilly.demo.android.pa.clientserver.client.sync.dataobjects;

import org.json.JSONObject;

public class Change implements Comparable<Object> {
	public static enum ChangeType {
		ADD, DELETE
	}
	
	public long time;
	public ChangeType type;
	public long who = -1;
	
	public Change() {
		time = System.currentTimeMillis();
	}
	
	public String toString() {
		return toJSON(this).toString();
	}
	
	public static JSONObject toJSON(Change change) {
		if(change == null) return null;
		JSONObject json = new JSONObject();
		try { json.put("time", change.time); } catch (Exception e) {}
		try { json.put("type", change.type.ordinal()); } catch (Exception e) {}
		try { json.put("who", change.who); } catch (Exception e) {}
		return json;
	}
	
	public static Change fromJSON(JSONObject json) {
		if(json == null) return null;
		Change change = new Change();
		change.time = json.optLong("time", System.currentTimeMillis());
		int type = json.optInt("type");
		if(type == 0) change.type = ChangeType.ADD;
		else if(type == 1) change.type = ChangeType.DELETE;
		change.who = json.optLong("who");
		return change;
	}

	@Override
	public int compareTo(Object arg0) {
		if(arg0 == null || !(arg0 instanceof Change)) return 1;
		long val = ((Change) arg0).time;
		return val == time ? 0 : (time > val ? 1 : -1);
	}
}
