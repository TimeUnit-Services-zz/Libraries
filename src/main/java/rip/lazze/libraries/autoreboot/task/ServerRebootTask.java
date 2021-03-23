package rip.lazze.libraries.autoreboot.task;

import rip.lazze.libraries.Library;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import rip.lazze.libraries.kt.util.TimeUtils;

import java.util.concurrent.TimeUnit;

public class ServerRebootTask extends BukkitRunnable {

    private int secondsRemaining;

    private boolean wasWhitelisted;

    public ServerRebootTask(final int timeUnitAmount, final TimeUnit timeUnit) {
        this.secondsRemaining = (int) timeUnit.toSeconds(timeUnitAmount);
        this.wasWhitelisted = Library.getInstance().getServer().hasWhitelist();
    }

    public void run() {
        if (this.secondsRemaining == 300) {
            Library.getInstance().getServer().setWhitelist(true);
        } else if (this.secondsRemaining == 0) {
            Library.getInstance().getServer().setWhitelist(this.wasWhitelisted);
            Library.getInstance().getServer().shutdown();
        }
        switch (this.secondsRemaining) {
            case 5:
            case 10:
            case 15:
            case 30:
            case 60:
            case 120:
            case 180:
            case 240:
            case 300: {
                Library.getInstance().getServer().broadcastMessage(ChatColor.RED + "\u26a0 " + ChatColor.DARK_RED.toString() + ChatColor.STRIKETHROUGH + "------------------------" + ChatColor.RED + " \u26a0");
                Library.getInstance().getServer().broadcastMessage(ChatColor.RED + "Server rebooting in " + TimeUtils.formatIntoDetailedString(this.secondsRemaining) + ".");
                Library.getInstance().getServer().broadcastMessage(ChatColor.RED + "\u26a0 " + ChatColor.DARK_RED.toString() + ChatColor.STRIKETHROUGH + "------------------------" + ChatColor.RED + " \u26a0");
                break;
            }
        }
        --this.secondsRemaining;
    }

    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        Bukkit.setWhitelist(this.wasWhitelisted);
    }

    public int getSecondsRemaining() {
        return this.secondsRemaining;
    }
}
