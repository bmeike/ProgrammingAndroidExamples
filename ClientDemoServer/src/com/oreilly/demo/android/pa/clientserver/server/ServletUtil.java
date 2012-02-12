package com.oreilly.demo.android.pa.clientserver.server;

import java.util.Set;
import java.util.HashSet;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.oreilly.demo.android.pa.clientserver.server.servlet.ISHttpServletType;


public final class ServletUtil {
	public static final String DEFCONTEXTPATH	= "/";

	private Server server;
	private String contextpath			= DEFCONTEXTPATH;
	private boolean loaded				= false;
	private Set<String> servscache			= null;
	
	public ServletUtil() { }

	public void setServer(Server s) throws Exception {
		if(server != null) throw new Exception("Server Already Set.  This is a One Time Deal");
		server = s;
		ServerStatic.debug("Server set");
		if(!loaded && servscache != null) {
			loadServlets(servscache);
		}
	}

	public void setContextPath(String s) {
		if(s == null) contextpath = DEFCONTEXTPATH;
		else contextpath = s;
		ServerStatic.debug("Contextpath set : "+contextpath);
	}

	public boolean isLoaded() { return loaded; }

	public boolean loadServlets(Set<String> servs) {
		if(loaded || servs == null || servs.isEmpty()) return false;
		if(server == null) {
			servscache = servs;
			return false;
		}
		servscache = null;
		int numloaded = 0;
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath(contextpath);
	
		HashSet<String> paths = new HashSet<String>();

		// load servlets
		for(String s: servs) {
			try {
				Servlet servlet = (Servlet) Class.forName(s).newInstance();
				String path = servlet.getClass().getName();
				if(servlet instanceof ISHttpServletType) path = ((ISHttpServletType) servlet).getPath();

				if(paths.contains(path)) throw new Exception(path+" already exists");
				paths.add(path);

				context.addServlet(new ServletHolder(servlet),path);
				numloaded++;
				ServerStatic.info("Added to "+path+" :: "+s);
			} catch (Throwable e) {
				ServerStatic.error("Failed to load "+s, e);
			}
		}

		server.setHandler(context);
		ServerStatic.info("Set ServletContextHandler : Load Complete ["+numloaded+"/"+servs.size()+"]");
		loaded = true;
		return true;
	}
}
