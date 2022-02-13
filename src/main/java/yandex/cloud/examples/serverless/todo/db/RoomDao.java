package yandex.cloud.examples.serverless.todo.db;

import com.yandex.ydb.table.query.Params;
import com.yandex.ydb.table.values.PrimitiveValue;
import yandex.cloud.examples.serverless.todo.model.Room;
import yandex.cloud.examples.serverless.todo.utils.ThrowingConsumer;

import java.util.ArrayList;
import java.util.List;

public class RoomDao implements Dao<Room>{

    private final EntityManager entityManager = new EntityManager(System.getenv("DATABASE"), System.getenv("ENDPOINT"));

    @Override
    public List<Room> findAll() {
        var rooms = new ArrayList<Room>();
        entityManager.execute("select * from Rooms", Params.empty(), ThrowingConsumer.unchecked(result -> {
            var resultSet = result.getResultSet(0);
            while (resultSet.next()) {
                rooms.add(Room.fromResultSet(resultSet));
            }
        }));
        return rooms;
    }

    @Override
    public Room findById(String roomId) {
        final Room[] room = {null};
        entityManager.execute(
                "declare $roomId as Utf8;" +
                        "select * from Rooms where RoomId = $roomId",
                Params.of("$roomId", PrimitiveValue.utf8(roomId)),
                ThrowingConsumer.unchecked(result -> {
                    var resultSet = result.getResultSet(0);
                    resultSet.next();
                    room[0] = Room.fromResultSet(resultSet);
                }));
        return room[0];
    }

    @Override
    public void save(Room room) {
        entityManager.execute(
                "declare $roomId as Utf8;" +
                        "declare $name as Utf8;" +
                        "declare $queueId as Utf8;" +
                        "insert into Rooms(RoomId, Name, CreatedAt, QueueId) values ($roomId, $name, CurrentUtcDateTime(), $queueId)",
                Params.of("$roomId", PrimitiveValue.utf8(room.getRoomId()),
                        "$name", PrimitiveValue.utf8(room.getName()),
                        "$queueId", PrimitiveValue.utf8(room.getQueueId()))
        );
    }

    @Override
    public void deleteById(String roomId) {
        entityManager.execute(
                "declare $roomId as Utf8;" +
                        "delete from Rooms where RoomId = $roomId",
                Params.of("$roomId", PrimitiveValue.utf8(roomId)));
    }
}
