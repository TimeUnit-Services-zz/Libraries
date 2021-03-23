package rip.lazze.libraries.menu.pagination;

import rip.lazze.libraries.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.lazze.libraries.menu.Button;

import java.util.ArrayList;
import java.util.List;

public class PageButton extends Button {
    private int mod;

    private PaginatedMenu menu;

    public PageButton(int mod, PaginatedMenu menu) {
        this.mod = mod;
        this.menu = menu;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        if (clickType == ClickType.RIGHT) {
            (new ViewAllPagesMenu(this.menu)).openMenu(player);
            Button.playNeutral(player);
        } else if (hasNext(player)) {
            this.menu.modPage(player, this.mod);
            Button.playNeutral(player);
        } else {
            Button.playFail(player);
        }
    }

    private boolean hasNext(Player player) {
        int pg = this.menu.getPage() + this.mod;
        return (pg > 0 && this.menu.getPages(player) >= pg);
    }

    @Override
    public String getName(final Player player) {
        if (!this.hasNext(player)) {
            return (this.mod > 0) ? "page" : "page";
        }
        final String str = "(§e" + (this.menu.getPage() + this.mod) + "/§e" + this.menu.getPages(player) + "§a";
        return (this.mod > 0) ? "§a\u27f6" : "§c\u27f5";
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) (hasNext(player) ? 11 : 7);
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.CARPET;
    }
}
