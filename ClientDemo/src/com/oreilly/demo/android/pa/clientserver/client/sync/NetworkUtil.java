package com.oreilly.demo.android.pa.clientserver.client.sync;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

public final class NetworkUtil {	
	private NetworkUtil() {}
	
	public static final int OK				= 0;
	public static final int ERR				= 1;
	
	public static final String LOGIN		= "/login?";
	public static final String GETFRIENDS	= "/getfriends?";
	public static final String ADDFRIEND	= "/addfriend?";
	public static final String DELETEFRIEND	= "/deletefriend?";
	
	public static final String USERNAME		= "username=";
	public static final String PASSWORD		= "password=";
	public static final String OLDTOKEN		= "&oldtoken=true";
	public static final String TOKEN		= "token=";
	public static final String TIME			= "time=";
	public static final String ID			= "id=";
	public static final String USER			= "user=";
	
	public static String hosturl;
	
	private static ExecutorService pool 	= Executors.newFixedThreadPool(20);
	
	// -- API CALLS TO SERVER -- //
	public static void login(String baseurl, String username, String password, Handler handler) {
		login(baseurl, username, password, false, handler);
	}
	
	public static JSONObject login(String baseurl, String username, String password, boolean oldtoken, Handler handler) {
		String url = baseurl+LOGIN+(username != null ? USERNAME+username+"&" : "")+(password != null ? PASSWORD+password : "")+(oldtoken ? OLDTOKEN : "");
		if(!oldtoken) {
			getJSONAsync(url, handler);
		} else {
			try {
				return getJSON(url);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static JSONObject getFriends(String baseurl, String token, Handler handler) {
		return getFriends(baseurl, token, 0, handler);
	}
	
	public static JSONObject getFriends(String baseurl, String token, long time, Handler handler) {
		if(handler != null) getJSONAsync(baseurl+GETFRIENDS+TOKEN+token+"&"+TIME+time, handler);
		else {
			try { 
				return getJSON(baseurl+GETFRIENDS+TOKEN+token+"&"+TIME+time);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
			
		return null;
	}
	
	public static void addFriendUser(String baseurl, String token, String userstr, Handler handler) {
		addFriend(baseurl, token, null, userstr, handler);
	}
	
	public static void addFriend(String baseurl, String token, String friendid, Handler handler) {
		addFriend(baseurl, token, friendid, null, handler);
	}
	
	public static void addFriend(String baseurl, String token, String friendid, String userstr, Handler handler) {
		getJSONAsync(baseurl+ADDFRIEND+TOKEN+token+(friendid != null ? "&"+ID+friendid : (userstr != null ? "&"+USER+userstr : "")), handler);
	}
	
	public static void deleteFriend(String baseurl, String token, String friendid, Handler handler) {
		getJSONAsync(baseurl+DELETEFRIEND+TOKEN+token+(friendid != null ? "&"+ID+friendid : ""), handler);
	}
	// -- END API CALLS TO SERVER -- //
	
	/**
	 * async http method that gets JSONObject response and passes it to the handler
	 * 
	 * @param url - String url to connect to
	 * @param handler - handler to recieve the response
	 */
	public static void getJSONAsync(final String url, final Handler handler) {
		pool.execute(new Runnable() {

			@Override
			public void run() {
				try {
					JSONObject resp = getJSON(url);
					if(handler != null) {
						Message.obtain(handler, OK, resp).sendToTarget();
					} else System.out.println(resp.toString());
				} catch (Exception t) {
					if(handler != null) Message.obtain(handler, ERR, t).sendToTarget();
					else t.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Gets response as a JSONObject.
	 * 
	 * @param url - String url to connect to
	 * @return JSONObject - the response as a JSONObject
	 * @throws Exception
	 */
	public static JSONObject getJSON(String url) throws Exception {
		String text = getString(url);
		return new JSONObject(text);
	}
	
	/**
	 * Gets String Response and passes it to handler
	 * 
	 * @param url - String url to connect to
	 * @param handler - handler to recieve the response
	 */
	public static void getStringASync(final String url, final Handler handler) {
		pool.execute(new Runnable() {

			@Override
			public void run() {
				try {
					String resp = getString(url);
					if(handler != null) {
						Message.obtain(handler, OK, resp).sendToTarget();
					} else System.out.println(resp);
				} catch (Exception t) {
					if(handler != null) Message.obtain(handler, ERR, t).sendToTarget();
					else t.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Gets the String response
	 * 
	 * @param url - String url to connect to
	 * @return String - the response as a string
	 * @throws Exception
	 */
	public static String getString(String url) throws Exception {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
		
		HttpClient client = new DefaultHttpClient(httpParameters);
		HttpGet get = new HttpGet(url);
		
		HttpResponse response = client.execute(get);
		
		int statuscode = response.getStatusLine().getStatusCode();
		
		InputStream is = response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String line = null;
		StringBuffer buffer = new StringBuffer();
		while((line = reader.readLine()) != null) {
			buffer.append(line);
			buffer.append("\n");
		}
		reader.close();
		
		client.getConnectionManager().shutdown();
		String resp = buffer.toString();
		
		if(statuscode != HttpStatus.SC_OK) throw new Exception(resp);
		
		return resp;
	}
}
