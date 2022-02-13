package yandex.cloud.examples.serverless.todo.db;

import com.yandex.ydb.table.query.Params;
import com.yandex.ydb.table.values.PrimitiveValue;
import yandex.cloud.examples.serverless.todo.model.User;
import yandex.cloud.examples.serverless.todo.utils.ThrowingConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDao implements Dao<User> {

    private final EntityManager entityManager = new EntityManager(System.getenv("DATABASE"), System.getenv("ENDPOINT"));

    @Override
    public List<User> findAll() {
        var tasks = new ArrayList<User>();
        entityManager.execute("select * from Users", Params.empty(), ThrowingConsumer.unchecked(result -> {
            var resultSet = result.getResultSet(0);
            while (resultSet.next()) {
                tasks.add(User.fromResultSet(resultSet));
            }
        }));
        return tasks;
    }

    @Override
    public User findById(String userId) {
        final User[] user = {null};
        entityManager.execute(
                "declare $userId as Utf8;" +
                        "select * from Users where UserId = $userId",
                Params.of("$userId", PrimitiveValue.utf8(userId)),
                ThrowingConsumer.unchecked(result -> {
                    var resultSet = result.getResultSet(0);
                    resultSet.next();
                    user[0] = User.fromResultSet(resultSet);
                }));
        return user[0];
    }

    @Override
    public void save(User user) {
        entityManager.execute(
                "declare $userId as Utf8;" +
                        "declare $username as Utf8;" +
                        "insert into Users(UserId, Username, CreatedAt) values ($userId, $username, CurrentUtcDateTime())",
                Params.of("$userId", PrimitiveValue.utf8(user.getUserId()),
                        "$username", PrimitiveValue.utf8(user.getUsername()))
        );
    }

    @Override
    public void deleteById(String userId) {
        entityManager.execute(
                "declare $userId as Utf8;" +
                        "delete from Users where UserId = $userId",
                Params.of("$userId", PrimitiveValue.utf8(userId)));
    }

}
