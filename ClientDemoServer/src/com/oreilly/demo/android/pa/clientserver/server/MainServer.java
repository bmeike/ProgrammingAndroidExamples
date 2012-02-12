package com.oreilly.demo.android.pa.clientserver.server;

import org.eclipse.jetty.server.Server;

public class MainServer {

	public static void main(String[] args) {
		if(args != null && args.length > 0) {
			ServerStatic.CONFIGPATH = args[0];
		}

		try {
			ServerStatic.loadConfig();
			
			while(ServerStatic.getConfig().getServletPort() == -1) Thread.sleep(10);

			final Server server = new Server(ServerStatic.getConfig().getServletPort());
			ServerStatic.info("Servlet port set to "+ServerStatic.getConfig().getServletPort());

			ServerStatic.SERVLETUTIL.setServer(server);
			while(!ServerStatic.SERVLETUTIL.isLoaded()) Thread.sleep(1000);

			server.start();
			server.join();
		} catch (Exception e) {
			ServerStatic.error("Difficultly launching server",e);
		}
	}

}
