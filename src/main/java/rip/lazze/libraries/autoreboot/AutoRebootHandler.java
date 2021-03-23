package rip.lazze.libraries.autoreboot;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rip.lazze.libraries.Library;
import rip.lazze.libraries.autoreboot.listeners.AutoRebootListener;
import rip.lazze.libraries.autoreboot.task.ServerRebootTask;
import rip.lazze.libraries.utilities.commands.BricksCommandHandler;

public final class AutoRebootHandler {
    private static List<Integer> rebootTimes;

    public static void init() {
        Preconditions.checkState(!initiated);
        initiated = true;
        BricksCommandHandler.registerPackage(Library.getInstance(), "dev.nulledcode.bricks.autoreboot.commands");
        rebootTimes = ImmutableList.copyOf(Library.getInstance().getConfig().getIntegerList("AutoRebootTimes"));
        Library.getInstance().getServer().getPluginManager().registerEvents(new AutoRebootListener(), Library.getInstance());
    }

    @Deprecated
    public static void rebootServer(int seconds) {
        rebootServer(seconds, TimeUnit.SECONDS);
    }

    public static void rebootServer(int timeUnitAmount, TimeUnit timeUnit) {
        if (serverRebootTask != null)
            throw new IllegalStateException("Reboot already in progress");
        (serverRebootTask = new ServerRebootTask(timeUnitAmount, timeUnit)).runTaskTimer(Library.getInstance(), 20L, 20L);
    }

    public static boolean isRebooting() {
        return (serverRebootTask != null);
    }

    public static int getRebootSecondsRemaining() {
        if (serverRebootTask == null)
            return -1;
        return serverRebootTask.getSecondsRemaining();
    }

    public static void cancelReboot() {
        if (serverRebootTask != null) {
            serverRebootTask.cancel();
            serverRebootTask = null;
        }
    }

    public static List<Integer> getRebootTimes() {
        return rebootTimes;
    }

    public static boolean isInitiated() {
        return initiated;
    }

    private static boolean initiated = false;

    private static ServerRebootTask serverRebootTask = null;
}

