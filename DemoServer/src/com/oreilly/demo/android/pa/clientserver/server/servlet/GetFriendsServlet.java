package com.oreilly.demo.android.pa.clientserver.server.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.demo.android.pa.clientserver.server.ServerStatic;
import com.oreilly.demo.android.pa.clientserver.server.dataobjects.ListFriends;
import com.oreilly.demo.android.pa.clientserver.server.dataobjects.User;


public class GetFriendsServlet extends BaseServlet {
	private static final long serialVersionUID = 109940736848822895L;

	@Override
	protected void response(HttpServletRequest req, HttpServletResponse res) {
		if(res == null) return;
		if(ServerStatic.getConfig().getUserData() == null) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentLength(0);
			return;
		}
		
		String authtoken = req.getParameter("token");
		User user = ServerStatic.getConfig().getUserData().getUserByToken(authtoken);
		if(user == null) {
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
			res.setContentLength(0);
			return;
		}
		
		String time = req.getParameter("time");
		long lasttime = 0;
		if(time != null) {
			try {
				lasttime = Long.parseLong(time);
			} catch (Exception e) {
				lasttime = 0;
			}
		}
		
		res.setStatus(HttpServletResponse.SC_OK);
		try {
			byte[] b = ListFriends.listFriends(ServerStatic.getConfig().getUserData(), user, lasttime).toString().getBytes();
			res.getOutputStream().write(b);
			res.setContentLength(b.length);
		} catch (Throwable t) {
			res.setContentLength(0);
		}
	}

	@Override
	public String getPath() {
		return "/getfriends/*";
	}

}
