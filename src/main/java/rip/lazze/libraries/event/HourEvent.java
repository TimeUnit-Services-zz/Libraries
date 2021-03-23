package rip.lazze.libraries.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HourEvent extends Event {
    private static HandlerList handlerList;

    static {
        HourEvent.handlerList = new HandlerList();
    }

    private int hour;

    public HourEvent(final int hour) {
        this.hour = hour;
    }

    public static HandlerList getHandlerList() {
        return HourEvent.handlerList;
    }

    public HandlerList getHandlers() {
        return HourEvent.handlerList;
    }

    public int getHour() {
        return this.hour;
    }
}
