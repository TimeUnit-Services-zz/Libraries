package rip.lazze.libraries.menu.pagination;

import rip.lazze.libraries.menu.Button;
import rip.lazze.libraries.menu.Menu;
import org.bukkit.entity.Player;
import rip.lazze.libraries.menu.Button;
import rip.lazze.libraries.menu.Menu;

import java.util.HashMap;
import java.util.Map;

public abstract class PaginatedMenu extends Menu {
    private int page = 1;

    @Override
    public String getTitle(Player player) {
        return getPrePaginatedTitle(player) + " - " + this.page + "/" + getPages(player);
    }

    public final void modPage(Player player, int mod) {
        this.page += mod;
        getButtons().clear();
        openMenu(player);
    }

    public final int getPages(Player player) {
        int buttonAmount = getAllPagesButtons(player).size();
        if (buttonAmount == 0)
            return 1;
        return (int) Math.ceil((buttonAmount / getMaxItemsPerPage(player)));
    }

    @Override
    public final Map<Integer, Button> getButtons(Player player) {
        int minIndex = (this.page - 1) * getMaxItemsPerPage(player);
        int maxIndex = this.page * getMaxItemsPerPage(player);
        HashMap<Integer, Button> buttons = new HashMap<>();
        buttons.put(0, new PageButton(-1, this));
        buttons.put(8, new PageButton(1, this));
        for (Map.Entry<Integer, Button> entry : getAllPagesButtons(player).entrySet()) {
            int ind = entry.getKey().intValue();
            if (ind >= minIndex && ind < maxIndex) {
                ind -= getMaxItemsPerPage(player) * (this.page - 1) - 9;
                buttons.put(ind, entry.getValue());
            }
        }
        Map<Integer, Button> global = getGlobalButtons(player);
        if (global != null)
            for (Map.Entry<Integer, Button> gent : global.entrySet())
                buttons.put(gent.getKey(), gent.getValue());
        return buttons;
    }

    public int getMaxItemsPerPage(Player player) {
        return 18;
    }

    public Map<Integer, Button> getGlobalButtons(Player player) {
        return null;
    }

    public abstract String getPrePaginatedTitle(Player paramPlayer);

    public abstract Map<Integer, Button> getAllPagesButtons(Player paramPlayer);

    public int getPage() {
        return this.page;
    }
}
