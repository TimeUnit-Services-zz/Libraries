package rip.lazze.libraries.utilities.commands;

import org.bukkit.ChatColor;

public class CommandConfiguration {
    private String noPermissionMessage;

    public CommandConfiguration() {
    }

    public CommandConfiguration setNoPermissionMessage(String noPermissionMessage) {
        this.noPermissionMessage = ChatColor.translateAlternateColorCodes('&', noPermissionMessage);
        return this;
    }

    public String getNoPermissionMessage() {
        return this.noPermissionMessage;
    }
}
