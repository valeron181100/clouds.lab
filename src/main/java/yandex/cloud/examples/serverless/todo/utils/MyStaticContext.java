package yandex.cloud.examples.serverless.todo.utils;

import java.util.HashMap;
import java.util.Hashtable;

public class MyStaticContext {

    private static MyStaticContext instance;

    private Hashtable<String, TrackTimerTask> timerTaskMap;


    private MyStaticContext() {
        this.timerTaskMap = new Hashtable<>();
    }

    public static MyStaticContext getInstance() {
        if (instance == null)
            instance = new MyStaticContext();
        return instance;
    }

    public void addTimerTask(String roomId, TrackTimerTask task) {
        this.timerTaskMap.put(roomId, task);
    }

    public TrackTimerTask getTimerTask(String roomId) {
        return this.timerTaskMap.get(roomId);
    }
}
