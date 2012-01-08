package com.oreilly.demo.pa.ch17.server.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.demo.pa.ch17.dataobjects.User;
import com.oreilly.demo.pa.ch17.server.ServerStatic;


public class DeleteFriendServlet extends BaseServlet {
	private static final long serialVersionUID = -5821144577448449223L;

	@Override
	protected void response(HttpServletRequest req, HttpServletResponse res) {
		if(res == null) return;
		if(ServerStatic.getConfig().getUserData() == null) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentLength(0);
			return;
		}
		
		String authtoken = req.getParameter("token");
		String idstr = req.getParameter("id");
		if(authtoken == null || idstr == null) {
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
			res.setContentLength(0);
			return;
		}
		
		User user = ServerStatic.getConfig().getUserData().getUserByToken(authtoken);
		if(user == null) {
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
			res.setContentLength(0);
			return;
		}
		
		int id = 0;
		try {
			id = Integer.parseInt(idstr);
		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
			res.setContentLength(0);
			return;
		}
		
		user.deleteFriend(id);
		
		try {
			ServerStatic.getConfig().getUserData().saveData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		res.setStatus(HttpServletResponse.SC_OK);
		try {
			byte[] b = "{}".getBytes();
			res.getOutputStream().write(b);
			res.setContentLength(b.length);
		} catch (Throwable t) {
			res.setContentLength(0);
		}
	}

	@Override
	public String getPath() {
		return "/deletefriend/*";
	}

}
