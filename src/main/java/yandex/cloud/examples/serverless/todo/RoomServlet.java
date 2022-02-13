package yandex.cloud.examples.serverless.todo;

import com.jsoniter.output.JsonStream;
import lombok.SneakyThrows;
import org.json.JSONObject;
import yandex.cloud.examples.serverless.todo.db.Dao;
import yandex.cloud.examples.serverless.todo.db.RoomDao;
import yandex.cloud.examples.serverless.todo.model.Room;
import yandex.cloud.examples.serverless.todo.queue.QueueConnectionFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

public class RoomServlet extends HttpServlet {

    private final Dao<Room> roomDao = new RoomDao();

    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        var body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        JSONObject bodyJson = new JSONObject(body);
        var room = new Room(bodyJson.getString("name"));
        roomDao.save(room);

        var queueClient = new QueueConnectionFactory().getConnection().getWrappedAmazonSQSClient();

        queueClient.createQueue(room.getQueueId());

        var roomJson = JsonStream.serialize(room);

        var out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        out.print(roomJson);
        out.flush();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String[] pathParts = pathInfo.split("/");
        var out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (pathParts.length <= 2) {
            var rooms = roomDao.findAll();
            out.print(JsonStream.serialize(rooms));
        } else {
            String roomId = pathParts[2];

            var room = roomDao.findById(roomId);

            Objects.requireNonNull(room, "Room not found");

            out.print(JsonStream.serialize(room));
        }
        out.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String[] pathParts = pathInfo.split("/");
        String roomId = pathParts[2];

        Objects.requireNonNull(roomId, "Parameter 'roomId' missing");

        var room = roomDao.findById(roomId);

        Objects.requireNonNull(room, "Room not found");

        roomDao.deleteById(room.getRoomId());
    }

}
