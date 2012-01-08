package com.oreilly.demo.android.pa.clientserver.server.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.oreilly.demo.android.pa.clientserver.server.ServerStatic;
import com.oreilly.demo.android.pa.clientserver.server.dataobjects.User;


public class AddFriendServlet extends BaseServlet {
    private static final long serialVersionUID = 8146202127266520911L;

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
        String userstr = req.getParameter("user");

        if(authtoken == null || (idstr == null && userstr == null)) {
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

        User friend = null;

        if(idstr != null) {
            int id = 0;
            try {
                id = Integer.parseInt(idstr);
            } catch (Exception e) {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                res.setContentLength(0);
                return;
            }

            friend = ServerStatic.getConfig().getUserData().getUser(id);
        } else {
            try {
                friend = User.fromJSON(new JSONObject(userstr));
            } catch (Exception e) {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                res.setContentLength(0);
                return;
            }

            friend.id = 0;

            friend = ServerStatic.getConfig().getUserData().addUser(friend);
        }

        if(friend == null) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.setContentLength(0);
            return;
        }

        user.addFriend(friend.id);

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
        return "/addfriend/*";
    }

}
