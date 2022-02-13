package yandex.cloud.examples.serverless.todo.model;

import com.yandex.ydb.table.result.ResultSetReader;
import lombok.Data;

import java.util.UUID;

@Data
public class Room {

    private String roomId;

    private String name;

    private String queueId;

    public Room(String name) {
        this.roomId = UUID.randomUUID().toString();
        this.queueId = UUID.randomUUID().toString();
        this.name = name;
    }

    public Room(String roomId, String name, String queueId) {
        this.roomId = roomId;
        this.name = name;
        this.queueId = queueId;
    }

    public static Room fromResultSet(ResultSetReader resultSet) {
        var roomId = resultSet.getColumn("RoomId").getUtf8();
        var name = resultSet.getColumn("Name").getUtf8();
        var queueId = resultSet.getColumn("QueueId").getUtf8();
        return new Room(roomId, name, queueId);
    }
}
