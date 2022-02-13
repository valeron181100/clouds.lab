package yandex.cloud.examples.serverless.todo.utils;

import java.util.Timer;
import java.util.TimerTask;

public class TrackTimerTask extends TimerTask {

    private Timer timer;

    private String trackUrl;
    private int totalTimeSeconds;
    private int timeSeconds;

    public TrackTimerTask(String trackUrl, int timeSeconds) {
        this.timer = new Timer();
        this.trackUrl = trackUrl;
        this.timeSeconds = timeSeconds;
        this.totalTimeSeconds = timeSeconds;
    }

    @Override
    public void run() {
        if (this.timeSeconds > 0)
            this.decTimeSeconds();
        else {
            this.timer.cancel();
            this.timer.purge();
        }
    }

    public void start() {
        this.timer.scheduleAtFixedRate(this, 0, 1000);
    }

    public void decTimeSeconds() {
        this.timeSeconds -= 1;
    }

    public int getTimeSeconds() {
        return timeSeconds;
    }

    public String getTrackUrl() {
        return trackUrl;
    }

    public int getTotalTimeSeconds() {
        return totalTimeSeconds;
    }
}
