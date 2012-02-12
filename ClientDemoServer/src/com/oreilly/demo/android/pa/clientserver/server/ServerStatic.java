package com.oreilly.demo.android.pa.clientserver.server;

import java.io.File;
import java.io.FileReader;
import java.util.Date;

import org.json.JSONObject;

public final class ServerStatic {	
	public static String CONFIGPATH					= null;
	
	public static final ServletUtil SERVLETUTIL		= new ServletUtil();

	private static Config CONFIG					= new Config();

	public static boolean DEBUG						= getConfig().debug();

	private ServerStatic() {}

	public static final Config getConfig() {
		return CONFIG;
	}

	public static final void setConfig(JSONObject json) {
		CONFIG = new Config(json);
		info("New Config set");
		DEBUG = CONFIG.debug();
		debug("*** DEBUG ON ***");

		info("New Config loaded");
		
		SERVLETUTIL.loadServlets(CONFIG.getServlets());

		info("Loaded Servlets");
	}

	public static final void loadConfig() throws Exception {
		if(CONFIGPATH == null) { // defaults
			JSONObject json = new JSONObject();
			
			setConfig(json);
		} else {
			File config = new File(CONFIGPATH);
			if(!config.exists()) throw new Exception("no file "+CONFIGPATH);
			FileReader in = new FileReader(config);
			int r = -1;
			StringBuilder ref = new StringBuilder();
			while((r = in.read()) != -1) {
				ref.append((char) r);
			}
			in.close();
			setConfig(new JSONObject(ref.toString()));
		}
	}

	public static final void debug(String s) {
		debug(s, null);
	}

	public static final void debug(Throwable t) {
		debug(null, t);
	}

	public static final void debug(String s, Throwable t) {
		if(DEBUG) {
			if(s != null) System.out.println("D: "+new Date()+": "+s);
			if(t != null) t.printStackTrace();
		}
	}

	public static final void info(String s) {
		System.out.println("I: "+new Date()+": "+s);
	}

	public static final void error(String s) {
		error(s, null);
	}

	public static final void error(Throwable t) {
		error(null, t);
	}

	public static final void error(String s, Throwable t) {
		if(s != null) System.out.println("E: "+new Date()+": "+s);
		if(t != null) t.printStackTrace();
	}
}
