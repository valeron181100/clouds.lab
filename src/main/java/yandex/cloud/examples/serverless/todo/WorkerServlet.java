package yandex.cloud.examples.serverless.todo;

import lombok.SneakyThrows;
import org.json.JSONObject;
import yandex.cloud.examples.serverless.todo.db.Dao;
import yandex.cloud.examples.serverless.todo.db.RoomDao;
import yandex.cloud.examples.serverless.todo.model.Room;
import yandex.cloud.examples.serverless.todo.queue.QueueConnectionFactory;
import yandex.cloud.examples.serverless.todo.utils.MyStaticContext;
import yandex.cloud.examples.serverless.todo.utils.TrackTimerTask;
import yandex.cloud.examples.serverless.todo.utils.TrackUtils;

import javax.jms.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class WorkerServlet extends HttpServlet {

    private Dao<Room> roomDao = new RoomDao();

    private MyStaticContext staticContext = MyStaticContext.getInstance();

    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var out = resp.getWriter();
        var body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        var bodyJson = new JSONObject(body);
        var roomId = bodyJson.getString("roomId");

        var room = roomDao.findById(roomId);

        if (room == null) {
            resp.sendError(400, "Room not found");
            out.flush();
            return;
        }

        Message message = getLastTrackFromQueue(room);
        TextMessage textMessage = (TextMessage) message;
        var trackObject = new JSONObject(textMessage.getText());

        TrackTimerTask timerTask = new TrackTimerTask(trackObject.getString("url"),
                TrackUtils.getTimeInSeconds(trackObject.getString("time")));

        staticContext.addTimerTask(room.getRoomId(), timerTask);

        staticContext.getTimerTask(room.getRoomId()).start();

        out.print(trackObject);

        out.flush();
    }

    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var out = resp.getWriter();
        var roomId = req.getParameter("roomId");
        var timerTask = staticContext.getTimerTask(roomId);
        var json = new JSONObject();
        json.put("currentTime", timerTask.getTimeSeconds());
        json.put("totalTime", timerTask.getTotalTimeSeconds());
        json.put("url", timerTask.getTrackUrl());
        out.print(json);
        out.flush();
    }

    private Message getLastTrackFromQueue(Room room) throws JMSException {

        var queueConnection = new QueueConnectionFactory().getConnection();

        var session = queueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        System.out.println("IN-FUNCTION: Queue session opened");

        var queue = session.createQueue(room.getQueueId());

        MessageConsumer consumer = session.createConsumer(queue);

        queueConnection.start();

        return consumer.receive(1000);
    }

}
