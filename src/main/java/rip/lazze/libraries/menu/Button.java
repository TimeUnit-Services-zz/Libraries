package rip.lazze.libraries.menu;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;


public abstract class Button {

    @Deprecated
    public static Button placeholder(final Material material, final byte data, final String... title) {
        return placeholder(material, data, (title == null || title.length == 0) ? " " : Joiner.on(" ").join(title));
    }

    public static Button placeholder(final Material material) {
        return placeholder(material, " ");
    }

    public static Button placeholder(final Material material, final String title) {
        return placeholder(material, (byte) 0, title);
    }

    public static Button placeholder(final Material material, final byte data, final String title) {
        return new Button() {

            @Override
            public String getName(final Player player) {
                return title;
            }

            @Override
            public List<String> getDescription(final Player player) {
                return ImmutableList.of();
            }

            @Override
            public Material getMaterial(final Player player) {
                return material;
            }

            @Override
            public byte getDamageValue(final Player player) {
                return data;
            }
        };
    }

    public static Button fromItem(final ItemStack item) {
        return new Button() {
            @Override
            public ItemStack getButtonItem(final Player player) {
                return item;
            }

            @Override
            public String getName(final Player player) {
                return null;
            }

            @Override
            public List<String> getDescription(final Player player) {
                return null;
            }

            @Override
            public Material getMaterial(final Player player) {
                return null;
            }
        };
    }

    public static void playFail(final Player player) {
        player.playSound(player.getLocation(), Sound.DIG_GRASS, 20.0f, 0.1f);
    }

    public static void playSuccess(final Player player) {
        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20.0f, 15.0f);
    }

    public static void playNeutral(final Player player) {
        player.playSound(player.getLocation(), Sound.CLICK, 20.0f, 1.0f);
    }

    public abstract String getName(final Player p0);

    public abstract List<String> getDescription(final Player p0);

    public abstract Material getMaterial(final Player p0);

    public byte getDamageValue(final Player player) {
        return 0;
    }

    public void clicked(final Player player, final int slot, final ClickType clickType) {
    }

    public boolean shouldCancel(final Player player, final int slot, final ClickType clickType) {
        return true;
    }

    public int getAmount(final Player player) {
        return 1;
    }

    public ItemStack getButtonItem(Player player) {
        ItemStack buttonItem = new ItemStack(getMaterial(player), getAmount(player), getDamageValue(player));
        ItemMeta meta = buttonItem.getItemMeta();
        meta.setDisplayName(getName(player));
        List<String> description = getDescription(player);
        if (description != null)
            meta.setLore(description);
        buttonItem.setItemMeta(meta);
        return buttonItem;
    }
}
