package rip.lazze.libraries.menu;

import rip.lazze.libraries.Library;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ButtonListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onButtonPress(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());
        if (openMenu != null) {
            if (e.getSlot() != e.getRawSlot()) {
                if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) {
                    e.setCancelled(true);
                    if (openMenu.isNoncancellingInventory() && e.getCurrentItem() != null) {
                        player.getOpenInventory().getTopInventory().addItem(e.getCurrentItem());
                        e.setCurrentItem(null);
                    }
                }
                return;
            }
            if (openMenu.getButtons().containsKey(e.getSlot())) {
                Button button = openMenu.getButtons().get(e.getSlot());
                boolean cancel = button.shouldCancel(player, e.getSlot(), e.getClick());
                if (!cancel && (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT)) {
                    e.setCancelled(true);
                    if (e.getCurrentItem() != null)
                        player.getInventory().addItem(e.getCurrentItem());
                } else {
                    e.setCancelled(cancel);
                }
                button.clicked(player, e.getSlot(), e.getClick());
                if (Menu.currentlyOpenedMenus.containsKey(player.getName())) {
                    Menu newMenu = Menu.currentlyOpenedMenus.get(player.getName());
                    if (newMenu == openMenu && newMenu.isUpdateAfterClick())
                        newMenu.openMenu(player);
                }
                if (e.isCancelled())
                    Bukkit.getScheduler().runTaskLater(Library.getInstance(), player::updateInventory, 1L);
            } else if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) {
                e.setCancelled(true);
                if (openMenu.isNoncancellingInventory() && e.getCurrentItem() != null) {
                    player.getOpenInventory().getTopInventory().addItem(e.getCurrentItem());
                    e.setCurrentItem(null);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());
        if (openMenu != null) {
            openMenu.onClose(player);
            Menu.cancelCheck(player);
            Menu.currentlyOpenedMenus.remove(player.getName());
        }
    }
}
