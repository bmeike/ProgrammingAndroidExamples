package com.oreilly.demo.android.pa.clientserver.server.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.oreilly.demo.android.pa.clientserver.server.ServerStatic;
import com.oreilly.demo.android.pa.clientserver.server.dataobjects.User;


public class AddUserServlet extends BaseServlet {
	private static final long serialVersionUID = 3314034629918477254L;

	@Override
	protected void response(HttpServletRequest req, HttpServletResponse res) {
		if(res == null) return;
		if(ServerStatic.getConfig().getUserData() == null) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setContentLength(0);
			return;
		}
		
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		
		if(username == null || password == null) {
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
			res.setContentLength(0);
			return;
		}
		
		User user = ServerStatic.getConfig().getUserData().getUser(username);
		if(user != null) {
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
			res.setContentLength(0);
			return;
		}
		
		user = new User();
		user.username = username;
		user.password = password;
		user.name = req.getParameter("name");
		user.phone = req.getParameter("phone");
		
		ServerStatic.getConfig().getUserData().addUser(user);
		
		try {
			ServerStatic.getConfig().getUserData().saveData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		res.setStatus(HttpServletResponse.SC_OK);
		try {
			JSONObject json = new JSONObject();
			json.put("id", user.id);
			byte[] b = json.toString().getBytes();
			res.getOutputStream().write(b);
			res.setContentLength(b.length);
		} catch (Throwable t) {
			res.setContentLength(0);
		}
	}

	@Override
	public String getPath() {
		return "/adduser/*";
	}
}
