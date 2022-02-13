package yandex.cloud.examples.serverless.todo;

import com.jsoniter.output.JsonStream;
import org.json.JSONObject;
import yandex.cloud.examples.serverless.todo.db.Dao;
import yandex.cloud.examples.serverless.todo.db.UserDao;
import yandex.cloud.examples.serverless.todo.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class AuthServlet extends HttpServlet {

    private final Dao<User> userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var username = req.getParameter("username");
        Objects.requireNonNull(username, "Parameter 'username' missing");
        var user = new User(username);
        userDao.save(user);

        var keyJson = new JSONObject();

        keyJson.put("assignedKey", user.getUserId());

        var out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        out.print(keyJson.toString());
        out.flush();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String[] pathParts = pathInfo.split("/");
        String userId = pathParts[2];

        Objects.requireNonNull(userId, "Parameter 'userId' missing");

        var user = userDao.findById(userId);

        Objects.requireNonNull(userId, "User not found");

        var userJsonStr = JsonStream.serialize(user);

        var out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        out.print(userJsonStr);
        out.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String[] pathParts = pathInfo.split("/");
        String userId = pathParts[2];

        Objects.requireNonNull(userId, "Parameter 'userId' missing");

        var user = userDao.findById(userId);

        Objects.requireNonNull(userId, "User not found");

        userDao.deleteById(user.getUserId());
    }
}
