package rip.lazze.libraries.menu;

import com.google.common.base.Preconditions;
import rip.lazze.libraries.Library;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Menu {
    public static Map<String, Menu> currentlyOpenedMenus = new HashMap<>();
    public static Map<String, BukkitRunnable> checkTasks = new HashMap<>();
    private static Method openInventoryMethod;

    static {
        Library.getInstance().getServer().getPluginManager().registerEvents(new ButtonListener(), Library.getInstance());
    }

    private ConcurrentHashMap<Integer, Button> buttons;
    private boolean autoUpdate;
    private boolean updateAfterClick;
    private boolean placeholder;
    private boolean noncancellingInventory;
    private String staticTitle;

    public Menu() {
        this.buttons = new ConcurrentHashMap<>();
        this.autoUpdate = false;
        this.updateAfterClick = true;
        this.placeholder = false;
        this.noncancellingInventory = false;
        this.staticTitle = null;
    }

    public Menu(String staticTitle) {
        this.buttons = new ConcurrentHashMap<>();
        this.autoUpdate = false;
        this.updateAfterClick = true;
        this.placeholder = false;
        this.noncancellingInventory = false;
        this.staticTitle = null;
        this.staticTitle = Preconditions.checkNotNull(staticTitle);
    }

    private static Method getOpenInventoryMethod() {
        if (openInventoryMethod == null)
            try {
                (openInventoryMethod = CraftHumanEntity.class.getDeclaredMethod("openCustomInventory", Inventory.class, EntityPlayer.class, int.class)).setAccessible(true);
            } catch (NoSuchMethodException ex) {
                ex.printStackTrace();
            }
        return openInventoryMethod;
    }

    public static void cancelCheck(Player player) {
        if (checkTasks.containsKey(player.getName()))
            ((BukkitRunnable) checkTasks.remove(player.getName())).cancel();
    }

    private Inventory createInventory(Player player) {
        Map<Integer, Button> invButtons = getButtons(player);
        Inventory inv = Bukkit.createInventory(player, size(invButtons), getTitle(player));
        for (Map.Entry<Integer, Button> buttonEntry : invButtons.entrySet()) {
            this.buttons.put(buttonEntry.getKey(), buttonEntry.getValue());
            inv.setItem(buttonEntry.getKey(), ((Button) buttonEntry.getValue()).getButtonItem(player));
        }
        if (isPlaceholder()) {
            Button placeholder = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15);
            for (int index = 0; index < size(invButtons); index++) {
                if (invButtons.get(index) == null) {
                    this.buttons.put(index, placeholder);
                    inv.setItem(index, placeholder.getButtonItem(player));
                }
            }
        }
        return inv;
    }

    public void openMenu(Player player) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        Inventory inv = createInventory(player);
        try {
            getOpenInventoryMethod().invoke(player, inv, ep, 0);
            update(player);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void update(final Player player) {
        cancelCheck(player);
        currentlyOpenedMenus.put(player.getName(), this);
        onOpen(player);
        BukkitRunnable runnable = new BukkitRunnable() {
            public void run() {
                if (!player.isOnline()) {
                    Menu.cancelCheck(player);
                    Menu.currentlyOpenedMenus.remove(player.getName());
                }
                if (Menu.this.isAutoUpdate())
                    player.getOpenInventory().getTopInventory().setContents(Menu.this.createInventory(player).getContents());
            }
        };
        runnable.runTaskTimer(Library.getInstance(), 10L, 10L);
        checkTasks.put(player.getName(), runnable);
    }

    public int size(Map<Integer, Button> buttons) {
        int highest = 0;
        for (Iterator<Integer> iterator = buttons.keySet().iterator(); iterator.hasNext(); ) {
            int buttonValue = iterator.next();
            if (buttonValue > highest)
                highest = buttonValue;
        }
        return (int) (Math.ceil((highest + 1) / 9.0D) * 9.0D);
    }

    public int getSlot(int x, int y) {
        return 9 * y + x;
    }

    public String getTitle(Player player) {
        return this.staticTitle;
    }

    public void onOpen(Player player) {
    }

    public void onClose(Player player) {
    }

    public ConcurrentHashMap<Integer, Button> getButtons() {
        return this.buttons;
    }

    public boolean isAutoUpdate() {
        return this.autoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public boolean isUpdateAfterClick() {
        return this.updateAfterClick;
    }

    public void setUpdateAfterClick(boolean updateAfterClick) {
        this.updateAfterClick = updateAfterClick;
    }

    public boolean isPlaceholder() {
        return this.placeholder;
    }

    public void setPlaceholder(boolean placeholder) {
        this.placeholder = placeholder;
    }

    public boolean isNoncancellingInventory() {
        return this.noncancellingInventory;
    }

    public void setNoncancellingInventory(boolean noncancellingInventory) {
        this.noncancellingInventory = noncancellingInventory;
    }

    public abstract Map<Integer, Button> getButtons(Player paramPlayer);
}
