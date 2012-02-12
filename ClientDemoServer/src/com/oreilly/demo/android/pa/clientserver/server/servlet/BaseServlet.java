package com.oreilly.demo.android.pa.clientserver.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.demo.android.pa.clientserver.server.ServerStatic;


public abstract class BaseServlet extends HttpServlet implements ISHttpServletType {
	private static final long serialVersionUID = 5785749997571010650L;

	public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException {
		doResponse(req, res);
	}

	public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException {
		doResponse(req, res);
	}

	protected void doResponse(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException {
		try {
			response(req, res);
		} catch (Throwable t) {
			ServerStatic.debug(t);
			
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected String getUrl(HttpServletRequest req) {
		StringBuffer reqUrl = req.getRequestURL();
		String queryString = req.getQueryString();  
		if (queryString != null) {
			reqUrl.append("?");
			reqUrl.append(queryString);
		}
		return reqUrl.toString();
	}
	
	abstract protected void response(HttpServletRequest req, HttpServletResponse res);
}
