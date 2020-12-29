package com.lifeknight.challenges.utilities;

import java.util.ArrayList;
import java.util.List;

public class Timer {
    private static final List<Timer> timers = new ArrayList<>();
    private long endTime;
    private long lastPauseTime = 0L;
    private long timeSpentPaused = 0L;
    private long millisecondsRemaining = 0L;
    private boolean running = false;
    private boolean ended = false;
    private ITimerListener onEnd;

    public Timer(int seconds, int minutes, int hours, int days) {
        int secondsLeft = seconds + minutes * 60 + hours * 3600 + days * 86400;
        this.endTime = System.currentTimeMillis() + secondsLeft * 1000;
        timers.add(this);
    }

    public Timer(int seconds, int minutes, int hours) {
        int secondsLeft = seconds + minutes * 60 + hours * 3600;
        this.endTime = System.currentTimeMillis() + secondsLeft * 1000;
        timers.add(this);
    }

    public Timer(int seconds, int minutes) {
        int secondsLeft = seconds + minutes * 60;
        this.endTime = System.currentTimeMillis() + secondsLeft * 1000;
        timers.add(this);
    }

    public Timer(int seconds) {
        this.endTime = System.currentTimeMillis() + seconds * 1000;
        timers.add(this);
    }

    public void onEnd(ITimerListener onEnd) {
        this.onEnd = onEnd;
    }

    public static void onRenderTick() {
        for (Timer timer : timers) {
            timer.checkForEnd();
        }
    }

    public void start() {
        this.running = true;
        this.ended = false;
        if (!timers.contains(this)) timers.add(this);
        this.lastPauseTime = 0L;
        this.timeSpentPaused = 0L;
    }

    private void checkForEnd() {
        if (this.getTotalMilliseconds() <= 0 && this.running) {
            this.running = false;
            this.ended = true;
            timers.remove(this);
            if (this.onEnd != null) this.onEnd.onTimerEnd();
        }
    }

    public void pause() {
        if (this.ended) throw new IllegalArgumentException("Cannot pause stopped timer.");
        this.millisecondsRemaining = this.getTotalMilliseconds();
        this.running = false;
        this.lastPauseTime = System.currentTimeMillis();
    }

    public void resume() {
        if (this.ended) throw new IllegalArgumentException("Cannot resume stopped timer.");
        this.running = true;
        this.timeSpentPaused += System.currentTimeMillis() - this.lastPauseTime;
    }

    public void stop() {
        this.pause();
        timers.remove(this);
        this.ended = true;
        this.millisecondsRemaining = 0L;
    }

    public void reset() {
        this.running = false;
        this.ended = false;
        this.lastPauseTime = 0L;
        this.timeSpentPaused = 0L;
    }

    public long getTotalMilliseconds() {
        return !this.running ? this.millisecondsRemaining : this.endTime - System.currentTimeMillis() + this.timeSpentPaused;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setTimeFromSeconds(long seconds) {
        endTime = System.currentTimeMillis() + seconds * 1000;
    }

    public String getFormattedTime(int count, boolean includeMilliseconds) {
        return Text.formatTimeFromMilliseconds(this.getTotalMilliseconds(), count, includeMilliseconds);
    }

    public String getFormattedTime() {
        return this.getFormattedTime(2, false);
    }

    public String getTextualFormattedTime(boolean includeMilliseconds) {
        long days;
        long hours;
        long minutes;
        long seconds;
        long millisecondsLeft = this.getTotalMilliseconds();
        days = millisecondsLeft / 86400000;
        millisecondsLeft %= 86400000;
        hours = millisecondsLeft / 3600000;
        millisecondsLeft %= 3600000;
        minutes = millisecondsLeft / 60000;
        millisecondsLeft %= 60000;
        seconds = millisecondsLeft / 1000;
        millisecondsLeft %= 1000;

        String result = "";
        if (includeMilliseconds) {
            if (millisecondsLeft > 0) {
                if (millisecondsLeft != 1) {
                    result = millisecondsLeft + " milliseconds";
                } else {
                    result = "1 millisecond";
                }
            }
        }

        if (seconds > 0) {
            if (seconds != 1) {
                if (result.length() != 0) {
                    result = seconds + " seconds, " + result;
                } else {
                    result = seconds + " seconds";
                }
            } else {
                if (result.length() != 0) {
                    result = "1 second, " + result;
                } else {
                    result = "1 second";
                }
            }
        }

        if (minutes > 0) {
            if (minutes != 1) {
                if (result.length() != 0) {
                    result = minutes + " minutes, " + result;
                } else {
                    result = minutes + " minutes";
                }
            } else {
                if (result.length() != 0) {
                    result = "1 minute, " + result;
                } else {
                    result = "1 minute";
                }
            }
        }

        if (hours > 0) {
            if (hours != 1) {
                if (result.length() != 0) {
                    result = hours + " hours, " + result;
                } else {
                    result = hours + " hours";
                }
            } else {
                if (result.length() != 0) {
                    result = "1 hour, " + result;
                } else {
                    result = "1 hour";
                }
            }
        }

        if (days > 0) {
            if (days != 1) {
                if (result.length() != 0) {
                    result = days + " days, " + result;
                } else {
                    result = days + " days";
                }
            } else {
                if (result.length() != 0) {
                    result = "1 day, " + result;
                } else {
                    result = "1 day";
                }
            }
        }

        if (result.contains(",")) {
            char[] asChars = result.toCharArray();

            int lastCommaIndex = result.lastIndexOf(",");
            for (int i = 0; i < asChars.length; i++) {
                if (i == lastCommaIndex) {
                    asChars[i] = '.';
                }
            }

            result = new String(asChars).replace(".", " and");
        }

        return result;
    }

    public long getSeconds() {
        long millisecondsLeft = this.getTotalMilliseconds();
        millisecondsLeft %= 86400000;
        millisecondsLeft %= 3600000;
        millisecondsLeft %= 60000;
        return millisecondsLeft / 1000;
    }

    public long getMinutes() {
        long millisecondsLeft = this.getTotalMilliseconds();
        return millisecondsLeft / 60000;
    }

    public long getHours() {
        long millisecondsLeft = this.getTotalMilliseconds();
        millisecondsLeft %= 86400000;
        return millisecondsLeft / 3600000;
    }

    public long getDays() {
        long millisecondsLeft = this.getTotalMilliseconds();
        return millisecondsLeft / 86400000;
    }

    public interface ITimerListener {
        void onTimerEnd();
    }
}
