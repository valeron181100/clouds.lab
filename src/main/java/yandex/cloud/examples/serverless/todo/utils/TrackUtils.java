package yandex.cloud.examples.serverless.todo.utils;

import java.util.Arrays;

public class TrackUtils {

    public static int getTimeInSeconds(String time) {
        var timeParts = time.split(":");
        if (timeParts.length == 3) {
            return Integer.parseInt(timeParts[0]) * 60 * 60 + Integer.parseInt(timeParts[1]) * 60 + Integer.parseInt(timeParts[2]);
        } else {
            return Integer.parseInt(timeParts[0]) * 60 + Integer.parseInt(timeParts[1]);
        }
    }

}
