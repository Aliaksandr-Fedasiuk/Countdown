package com.arcus.archery;

public class Countdown {

    private int initTime;
    private int seconds;
    private boolean pause = true;

    public Countdown(String time) {
        this.seconds = Integer.valueOf(time);
        this.initTime = seconds;
    }

    public void reset() {
        setPause(false);
        this.seconds = initTime;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isPause() {
        return pause;
    }

    public boolean isCloseToEnd() {
        return seconds < 10;
    }

    public boolean isFinished() {
        return seconds < 1;
    }

    public Countdown decrement() {
        if (seconds > 0) seconds--;
        return this;
    }

    public String toReadableString() {
        return String.format("%02d", seconds);
    }

}