package rip.lazze.libraries.menu.pagination;

import rip.lazze.libraries.menu.Button;
import rip.lazze.libraries.menu.Menu;
import rip.lazze.libraries.menu.buttons.BackButton;
import lombok.NonNull;
import org.bukkit.entity.Player;
import rip.lazze.libraries.menu.Button;
import rip.lazze.libraries.menu.Menu;
import rip.lazze.libraries.menu.buttons.BackButton;

import java.util.HashMap;
import java.util.Map;

public class ViewAllPagesMenu extends Menu {
    @NonNull
    PaginatedMenu menu;

    public ViewAllPagesMenu(@NonNull PaginatedMenu menu) {
        if (menu == null)
            throw new NullPointerException("menu");
        this.menu = menu;
    }

    @Override
    public String getTitle(Player player) {
        return "Jump to page";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        buttons.put(0, new BackButton(this.menu));
        int index = 10;
        for (int i = 1; i <= this.menu.getPages(player); i++) {
            buttons.put(index++, new JumpToPageButton(i, this.menu));
            if ((index - 8) % 9 == 0)
                index += 2;
        }
        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @NonNull
    public PaginatedMenu getMenu() {
        return this.menu;
    }
}
