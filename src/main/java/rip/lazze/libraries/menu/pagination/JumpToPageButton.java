package rip.lazze.libraries.menu.pagination;

import rip.lazze.libraries.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.lazze.libraries.menu.Button;

import java.util.List;

public class JumpToPageButton extends Button {
    private int page;
    private PaginatedMenu menu;

    public JumpToPageButton(final int page, final PaginatedMenu menu) {
        this.page = page;
        this.menu = menu;
    }

    @Override
    public String getName(final Player player) {
        return "§ePage " + this.page;
    }

    @Override
    public List<String> getDescription(final Player player) {
        return null;
    }

    @Override
    public Material getMaterial(final Player player) {
        return Material.BOOK;
    }

    @Override
    public int getAmount(final Player player) {
        return this.page;
    }

    @Override
    public byte getDamageValue(final Player player) {
        return 0;
    }

    @Override
    public void clicked(final Player player, final int i, final ClickType clickType) {
        this.menu.modPage(player, this.page - this.menu.getPage());
        Button.playNeutral(player);
    }
}
