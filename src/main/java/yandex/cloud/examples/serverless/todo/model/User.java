package yandex.cloud.examples.serverless.todo.model;

import com.yandex.ydb.table.result.ResultSetReader;
import lombok.Data;

import java.util.UUID;

@Data
public class User {

    private String userId;
    private String username;

    public User(String username) {
        this.userId = UUID.randomUUID().toString();
        this.username = username;
    }

    public User(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public static User fromResultSet(ResultSetReader resultSet) {
        var userId = resultSet.getColumn("UserId").getUtf8();
        var username = resultSet.getColumn("Username").getUtf8();
        return new User(userId, username);
    }

}
