package yandex.cloud.examples.serverless.todo;

import lombok.SneakyThrows;
import org.json.JSONObject;
import yandex.cloud.examples.serverless.todo.db.Dao;
import yandex.cloud.examples.serverless.todo.db.RoomDao;
import yandex.cloud.examples.serverless.todo.model.Room;
import yandex.cloud.examples.serverless.todo.queue.QueueConnectionFactory;
import yandex.cloud.examples.serverless.todo.utils.TrackBrowser;

import javax.jms.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class SearchServlet extends HttpServlet {

    private Dao<Room> roomDao = new RoomDao();

    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("IN-FUNCTION: Searching started");
        var out = resp.getWriter();
        var body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        var bodyJson = new JSONObject(body);
        var query = bodyJson.getString("query");
        var roomId = bodyJson.getString("roomId");
        var room = roomDao.findById(roomId);

        if (room == null) {
            resp.sendError(400, "Room not found");
            out.flush();
            return;
        }

        var trackObject = new JSONObject(new TrackBrowser().browse(query));

        System.out.println("IN-FUNCTION: Track object = " + trackObject);

        var queueConnection = new QueueConnectionFactory().getConnection();

        var session = queueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        System.out.println("IN-FUNCTION: Queue session opened");

        var queue = session.createQueue(room.getQueueId());

        var producer = session.createProducer(queue);

        var message = session.createTextMessage(trackObject.toString());

        producer.send(message);

        System.out.println("IN-FUNCTION: Track has been added in the queue");

        out.print(trackObject);

        out.flush();
    }

}
