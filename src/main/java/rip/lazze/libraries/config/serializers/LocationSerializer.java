package rip.lazze.libraries.config.serializers;

import rip.lazze.libraries.config.AbstractSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import rip.lazze.libraries.config.AbstractSerializer;

public class LocationSerializer extends AbstractSerializer<Location> {
    @Override
    public String toString(final Location data) {
        return data.getWorld().getName() + "|" + data.getBlockX() + "|" + data.getBlockY() + "|" + data.getBlockZ();
    }

    @Override
    public Location fromString(final String data) {
        final String[] parts = data.split("\\|");
        return new Location(Bukkit.getWorld(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
    }
}
