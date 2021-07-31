package model.timedEvents;

import java.time.LocalDateTime;

public abstract class TimedEvent {

    protected final LocalDateTime time;

    protected TimedEvent(LocalDateTime time) {
        this.time = time;
    }

    public abstract void execute();

    public abstract boolean isValid();

    public LocalDateTime getTime() {
        return time;
    }
}
