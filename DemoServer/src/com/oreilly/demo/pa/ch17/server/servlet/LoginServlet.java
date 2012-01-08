package com.oreilly.demo.pa.ch17.server.servlet;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.oreilly.demo.pa.ch17.dataobjects.User;
import com.oreilly.demo.pa.ch17.server.ServerStatic;


public class LoginServlet extends BaseServlet {
	private static final long serialVersionUID = -746353134261617187L;

	@Override
	protected void response(HttpServletRequest req, HttpServletResponse res) {
		if(res == null) return;
		if(ServerStatic.getConfig().getUserData() == null) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			sendError(res,"Server configured badly");
			return;
		}
		
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		
		if(username == null || password == null) {
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
			sendError(res,"No username or password");
			return;
		}
		
		User user = ServerStatic.getConfig().getUserData().getUser(username);
		if(user == null) {
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
			sendError(res,"No such username");
			return;
		}
		
		if(user.password == null || !user.password.equals(password)) {
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
			sendError(res,"Bad Password");
			return;
		}
		
		user.authtoken = req.getParameter("oldtoken") != null ? (user.authtoken != null ? user.authtoken : generateAuth()) : generateAuth();
		
		res.setStatus(HttpServletResponse.SC_OK);
		try {
			JSONObject json = new JSONObject();
			json.put("token", user.authtoken);
			byte[] b = json.toString().getBytes();
			res.getOutputStream().write(b);
			res.setContentLength(b.length);
		} catch (Throwable t) {
			res.setContentLength(0);
		}
	}
	
	private void sendError(HttpServletResponse res, String error) {
		try {
			JSONObject json = new JSONObject();
			json.put("error", error);
			byte[] b = json.toString().getBytes();
			res.getOutputStream().write(b);
			res.setContentLength(b.length);
		} catch (Throwable t) {
			res.setContentLength(0);
		}
	}

	@Override
	public String getPath() {
		return "/login/*";
	}

	private static String generateAuth() {
		return UUID.randomUUID().toString();
	}
}
