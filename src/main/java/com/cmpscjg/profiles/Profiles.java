package com.cmpscjg.profiles;

import com.cmpscjg.profiles.utils.BukkitSerialization;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public final class Profiles extends JavaPlugin implements Listener {

    private String pluginVersion = "";
    private final Integer profilesInvSize = 45;
    public final ArrayList<Integer> clickedArray = new ArrayList<>(Collections.singletonList(-1));
    public static HashMap<String, ArrayList<Integer>> inventoryScheduler = new HashMap<>();
    public static BukkitSerialization bukkitSerialization = new BukkitSerialization();
    public static HashMap<Integer, Integer> slotMapper = new HashMap<Integer, Integer>() {{
        put(12, 0);
        put(13, 1);
        put(14, 2);
        put(30, 0);
        put(31, 1);
        put(32, 2);
    }};

    @Override
    public void onEnable() {

        // Save default config.yml
        this.saveDefaultConfig();

        // Get plugin version
        pluginVersion = this.getDescription().getVersion();

        // Register main class events
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    // ------------------------ Utils ------------------------
    public void getMainMenu(CommandSender sender) {
        sender.sendMessage(color("&e*&8------------- &dProfiles: v" + pluginVersion + " &8-------------&e*"));
        sender.sendMessage(color("&d/profiles open &8: &7Open the Profiles main menu"));

        if (sender.hasPermission("profiles.reload")) {
            sender.sendMessage(color("&d/profiles reload &8: &7Reload the Profiles configuration"));
        }
    }

    public String getServerPropertiesLevelName() {
        File serverPropertiesFile = new File("server.properties");
        Properties serverProperties = new Properties();
        String levelName = "";
        try {
            FileInputStream inputStream = new FileInputStream(serverPropertiesFile);
            serverProperties.load(inputStream);
            levelName = serverProperties.getProperty("level-name");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return levelName;
    }

    public void openProfilesInventory(Player player) {
        UUID uuid = player.getUniqueId();
        String profilesInvTitle = color(this.getConfig().getString("inventoryTitle"));

        Inventory profilesInv = Bukkit.createInventory(null, profilesInvSize, profilesInvTitle);
        ItemStack clearGlassPane = new ItemStack(Material.GLASS_PANE);
        ItemMeta clearGlassPaneIM = clearGlassPane.getItemMeta();
        assert clearGlassPaneIM != null;
        clearGlassPaneIM.setDisplayName(color(this.getConfig().getString("prefix")));
        ArrayList<String> clearGlassPaneLore = new ArrayList<>();
        clearGlassPaneLore.add(color("&fClick to start a new save in this save slot."));
        clearGlassPaneIM.setLore(clearGlassPaneLore);
        clearGlassPane.setItemMeta(clearGlassPaneIM);

        ItemStack blackGlassPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta blackGlassPaneIM = blackGlassPane.getItemMeta();
        assert blackGlassPaneIM != null;
        blackGlassPaneIM.setDisplayName(color(this.getConfig().getString("prefix")));
        blackGlassPane.setItemMeta(blackGlassPaneIM);

        ItemStack limeGlassPane = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta limeGlassPaneIM = limeGlassPane.getItemMeta();
        assert limeGlassPaneIM != null;
        limeGlassPaneIM.setDisplayName(color(this.getConfig().getString("prefix")));

        ItemStack redGlassPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta redGlassPaneIM = redGlassPane.getItemMeta();
        assert redGlassPaneIM != null;
        redGlassPaneIM.setDisplayName(color(this.getConfig().getString("prefix")));
        ArrayList<String> redGlassPaneLore = new ArrayList<>();
        redGlassPaneLore.add(color("&cClick to delete the save slot above."));
        redGlassPaneIM.setLore(redGlassPaneLore);
        redGlassPane.setItemMeta(redGlassPaneIM);

        // Slots 12, 13, 14 will be save slots
        // If no data exists for that save, show a clearGlassPane. Otherwise, show green
        // Slots 30, 31, 32 will be delete save slots
        for (int i = 0; i < profilesInv.getSize(); i++) {
            switch(i) {
                case 12:
                case 13:
                case 14:
                    int configSlot = slotMapper.get(i);
                    if (this.getConfig().contains("data." + uuid + ".slot" + configSlot)) {
                        // TODO: Re-visit this prettify logic.
                        // String dateTimeString = new SimpleDateFormat("dd/MM/yyyy hh.mm aa").format(new Date(Objects.requireNonNull(this.getConfig().getString("data." + uuid + ".slot" + configSlot + ".dateSaved"))));
                        double healthLevel = this.getConfig().getDouble("data." + uuid + ".slot" + configSlot + ".healthLevel");
                        int hungerLevel = this.getConfig().getInt("data." + uuid + ".slot" + configSlot + ".hungerLevel");
                        int xpLevel = this.getConfig().getInt("data." + uuid + ".slot" + configSlot + ".experience.xpLevel");
                        float xpPoints = (float) this.getConfig().getDouble("data." + uuid + ".slot" + configSlot + ".experience.xpPoints");
                        String world = this.getConfig().getString("data." + uuid + ".slot" + configSlot + ".location.world");
                        int X = (int) this.getConfig().getDouble("data." + uuid + ".slot" + configSlot + ".location.X");
                        int Y = (int) this.getConfig().getDouble("data." + uuid + ".slot" + configSlot + ".location.Y");
                        int Z = (int) this.getConfig().getDouble("data." + uuid + ".slot" + configSlot + ".location.Z");

                        ArrayList<String> limeGlassPaneLore = new ArrayList<>();
                        limeGlassPaneLore.add(color("&cLeft-Click to load this save slot."));
                        limeGlassPaneLore.add(color("&cRight-Click to overwrite this save slot."));
                        limeGlassPaneLore.add(color("&e*&8-----------&e*"));
                        limeGlassPaneLore.add(color("&cShift Left-Click to see inventory."));
                        limeGlassPaneLore.add(color("&cShift Right-Click to see ender chest."));
                        limeGlassPaneLore.add(color("&e*&8-----------&e*"));
                        // limeGlassPaneLore.add(color("&7Date saved: " + "&6" + dateTimeString));
                        limeGlassPaneLore.add(color("&7Health: " + "&6" + healthLevel));
                        limeGlassPaneLore.add(color("&7Hunger: " + "&6" + hungerLevel));
                        limeGlassPaneLore.add(color("&7XP Level: " + "&6" + xpLevel));
                        limeGlassPaneLore.add(color("&7XP Points: " + "&6" + xpPoints));
                        limeGlassPaneLore.add(color("&7Location: " + "&6" + world + "<" + X + " " + Y + " " + Z + ">"));
                        limeGlassPaneIM.setLore(limeGlassPaneLore);
                        limeGlassPane.setItemMeta(limeGlassPaneIM);

                        profilesInv.setItem(i, limeGlassPane);
                    } else {
                        profilesInv.setItem(i, clearGlassPane);
                    }
                    break;
                case 30:
                case 31:
                case 32:
                    profilesInv.setItem(i, redGlassPane);
                    break;
                default:
                    profilesInv.setItem(i, blackGlassPane);
                    break;
            }
        }
        player.openInventory(profilesInv);
    }

    public String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void saveProfile(int saveSlot, Player player, boolean isBrandNewSave) {
        UUID uuid = player.getUniqueId();
        String playerName = player.getDisplayName();
        boolean freshStartOnNewSave = this.getConfig().getBoolean("freshStart.freshStartOnNewSave");
        boolean teleportToSpawnOnNewSave = this.getConfig().getBoolean("freshStart.teleportToSpawnOnNewSave");

        // If save is the result of the player clicking an empty save slot, treat the save as a 'freshStart'
        if (isBrandNewSave) {

            // If enabled, clear player inventory, ender chest inventory and other player data.
            if (freshStartOnNewSave) {
                player.setHealth(20.0);
                player.setFoodLevel(20);
                player.setTotalExperience(0);
                player.getInventory().clear();
                player.getEnderChest().clear();
            }

            // If enabled, teleport the player to the spawn location of the main world
            if (teleportToSpawnOnNewSave) {
                String serverPropertiesLevelName = getServerPropertiesLevelName();
                World defaultWorld = Bukkit.getServer().getWorld(serverPropertiesLevelName);
                assert defaultWorld != null;
                Location spawnPoint = defaultWorld.getSpawnLocation();
                player.teleport(spawnPoint);
            }
        }

        String dateSaved = new Date().toString();
        double healthLevel = player.getHealth();
        int hungerLevel = player.getFoodLevel();
        int xpLevel = player.getTotalExperience();
        float xpPoints = player.getExp();

        // Need to serialize the player's inventory and ender chest inventory to base64 and store as a string
        String base64PlayerInventory = bukkitSerialization.itemStackArrayToBase64(player.getInventory().getContents());
        String base64PlayerArmor = bukkitSerialization.itemStackArrayToBase64(player.getInventory().getArmorContents());
        String base64EnderChestInventory = bukkitSerialization.itemStackArrayToBase64(player.getEnderChest().getContents());

        String world = Objects.requireNonNull(player.getLocation().getWorld()).getName();
        double X = player.getLocation().getX();
        double Y = player.getLocation().getY();
        double Z = player.getLocation().getZ();

        // Set data to config.yml
        this.getConfig().set("data." + uuid + ".slot" + saveSlot + ".playerName", playerName);
        this.getConfig().set("data." + uuid + ".slot" + saveSlot + ".dateSaved", dateSaved);
        this.getConfig().set("data." + uuid + ".slot" + saveSlot + ".healthLevel", healthLevel);
        this.getConfig().set("data." + uuid + ".slot" + saveSlot + ".hungerLevel", hungerLevel);
        this.getConfig().set("data." + uuid + ".slot" + saveSlot + ".experience.xpLevel", xpLevel);
        this.getConfig().set("data." + uuid + ".slot" + saveSlot + ".experience.xpPoints", xpPoints);
        this.getConfig().set("data." + uuid + ".slot" + saveSlot + ".playerInventory", base64PlayerInventory);
        this.getConfig().set("data." + uuid + ".slot" + saveSlot + ".playerArmor", base64PlayerArmor);
        this.getConfig().set("data." + uuid + ".slot" + saveSlot + ".enderChestInventory", base64EnderChestInventory);
        this.getConfig().set("data." + uuid + ".slot" + saveSlot + ".location.world", world);
        this.getConfig().set("data." + uuid + ".slot" + saveSlot + ".location.X", X);
        this.getConfig().set("data." + uuid + ".slot" + saveSlot + ".location.Y", Y);
        this.getConfig().set("data." + uuid + ".slot" + saveSlot + ".location.Z", Z);

        // Save data to config.yml
        this.saveConfig();

        // Send a message to the player stating the save was successful
        player.sendMessage(color(this.getConfig().getString("prefix") + this.getConfig().getString("saveProfileMessage")));
    }

    public void loadProfile(int saveSlot, Player player) throws IOException {
        UUID uuid = player.getUniqueId();

        // Get data to config.yml
        double healthLevel = this.getConfig().getDouble("data." + uuid + ".slot" + saveSlot + ".healthLevel");
        int hungerLevel = this.getConfig().getInt("data." + uuid + ".slot" + saveSlot + ".hungerLevel");
        int xpLevel = this.getConfig().getInt("data." + uuid + ".slot" + saveSlot + ".experience.xpLevel");
        float xpPoints = (float) this.getConfig().getDouble("data." + uuid + ".slot" + saveSlot + ".experience.xpPoints");
        String base64PlayerInventory = this.getConfig().getString("data." + uuid + ".slot" + saveSlot + ".playerInventory");
        String base64PlayerArmor = this.getConfig().getString("data." + uuid + ".slot" + saveSlot + ".playerArmor");
        String base64EnderChestInventory = this.getConfig().getString("data." + uuid + ".slot" + saveSlot + ".enderChestInventory");
        String world = this.getConfig().getString("data." + uuid + ".slot" + saveSlot + ".location.world");
        double X = this.getConfig().getDouble("data." + uuid + ".slot" + saveSlot + ".location.X");
        double Y = this.getConfig().getDouble("data." + uuid + ".slot" + saveSlot + ".location.Y");
        double Z = this.getConfig().getDouble("data." + uuid + ".slot" + saveSlot + ".location.Z");

        // Set player data
        ItemStack[] playerInventory = bukkitSerialization.itemStackArrayFromBase64(base64PlayerInventory);
        ItemStack[] playerArmor = bukkitSerialization.itemStackArrayFromBase64(base64PlayerArmor);
        ItemStack[] enderChestInventory = bukkitSerialization.itemStackArrayFromBase64(base64EnderChestInventory);
        player.getInventory().clear();
        player.getInventory().setContents(playerInventory);
        player.getInventory().setArmorContents(playerArmor);
        player.getEnderChest().setContents(enderChestInventory);
        player.setHealth(healthLevel);
        player.setFoodLevel(hungerLevel);
        player.setLevel(xpLevel);
        player.setExp(xpPoints);

        // Teleport player to saved location
        assert world != null;
        World fullWorld = Bukkit.getWorld(world);
        Location savedLocation = new Location(fullWorld, X, Y, Z);
        player.teleport(savedLocation);

        player.sendMessage(color(this.getConfig().getString("prefix") + this.getConfig().getString("loadProfileMessage")));
    }

    public void previewInventory(int configSlot, Player player, boolean isLeftClick) throws IOException {
        UUID uuid = player.getUniqueId();

        // Get data to config.yml
        String base64PlayerInventory = this.getConfig().getString("data." + uuid + ".slot" + configSlot + ".playerInventory");
        String base64EnderChestInventory = this.getConfig().getString("data." + uuid + ".slot" + configSlot + ".enderChestInventory");

        // Set player data
        ItemStack[] playerInventoryContents = bukkitSerialization.itemStackArrayFromBase64(base64PlayerInventory);
        ItemStack[] enderChestInventory = bukkitSerialization.itemStackArrayFromBase64(base64EnderChestInventory);
        ItemStack closeButton = new ItemStack(Material.BARRIER, 1);
        ItemMeta closeButtonIM = closeButton.getItemMeta();
        assert closeButtonIM != null;
        closeButtonIM.setDisplayName(color(this.getConfig().getString("prefix")));
        ArrayList<String> clearGlassPaneLore = new ArrayList<>();
        clearGlassPaneLore.add(color("&fClick to return the Profiles - main menu."));
        closeButtonIM.setLore(clearGlassPaneLore);
        closeButton.setItemMeta(closeButtonIM);

        // Show inventory as two separate pages. One for inventory and ender chest
        String previewPlayerInvTitle = color(this.getConfig().getString("previewPlayerTitle"));
        String previewEnderchestInvTitle = color(this.getConfig().getString("previewEnderchestTitle"));
        Inventory previewPlayerInv = Bukkit.createInventory(null, profilesInvSize, previewPlayerInvTitle);
        previewPlayerInv.setContents(playerInventoryContents);
        previewPlayerInv.setItem(44, closeButton);
        Inventory previewEnderchestInv = Bukkit.createInventory(null, profilesInvSize, previewEnderchestInvTitle);
        previewEnderchestInv.setContents(enderChestInventory);
        previewEnderchestInv.setItem(44, closeButton);

        // Set empty inventory slots to stained glass item
        ItemStack blackGlassPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta blackGlassPaneIM = blackGlassPane.getItemMeta();
        assert blackGlassPaneIM != null;
        blackGlassPaneIM.setDisplayName(color(this.getConfig().getString("prefix")));
        blackGlassPane.setItemMeta(blackGlassPaneIM);

        int slotIndex = 0;
        for (ItemStack item : previewPlayerInv.getContents()) {
            if (item == null) {
                previewPlayerInv.setItem(slotIndex, blackGlassPane);
            }
            slotIndex++;
        }
        slotIndex = 0;
        for (ItemStack item : previewEnderchestInv.getContents()) {
            if (item == null) {
                previewEnderchestInv.setItem(slotIndex, blackGlassPane);
            }
            slotIndex++;
        }

        // Close the Profiles inventory and remove the player from the inventory scheduler
        if (inventoryScheduler.containsKey(player.getDisplayName())) {
            ArrayList<Integer> scheduleIdArray = inventoryScheduler.get(player.getDisplayName());
            for (Integer integer : scheduleIdArray) {
                Bukkit.getServer().getScheduler().cancelTask(integer);
            }
        }

        inventoryScheduler.put(player.getDisplayName(), clickedArray);

        player.closeInventory();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> player.openInventory(isLeftClick ? previewPlayerInv : previewEnderchestInv), 0);
    }

    // ------------------------ Commands ------------------------
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("profiles")) {
            if (args.length == 0) {
                getMainMenu(sender);
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("profiles.reload")) {
                        this.reloadConfig();
                        sender.sendMessage(color(this.getConfig().getString("prefix") + this.getConfig().getString("reloadConfigMessage")));
                    } else {
                        sender.sendMessage(color(this.getConfig().getString("prefix") + this.getConfig().getString("noPermissionMessage")));
                    }
                } else if (args[0].equalsIgnoreCase("open")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        inventoryScheduler.put(player.getDisplayName(), clickedArray);
                        openProfilesInventory(player);
                    } else {
                        sender.sendMessage(color(this.getConfig().getString("prefix") + this.getConfig().getString("noPermissionMessage")));
                    }
                }
            }
        }
        return true;
    }

    // ------------------------ Events ------------------------

    // When player joins the server, open the 'Profiles' load menu
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Set player gamemode to spectator to prevent damage
        player.setGameMode(GameMode.SPECTATOR);

        // Open the Profiles inventory
        Bukkit.getScheduler().runTaskLater(this, () -> openProfilesInventory(player), 0);
    }

    // Prevent player from closing Profiles inventory
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        String profilesInvTitle = color(this.getConfig().getString("inventoryTitle"));
        InventoryView inventoryClosed = event.getView();
        Player player = (Player) event.getPlayer();
        boolean hasClosedViaClick =
                inventoryScheduler.containsKey(player.getDisplayName()) &&
                        inventoryScheduler.get(player.getDisplayName()).get(0) == -1;

        if (inventoryClosed.getTitle().equalsIgnoreCase(profilesInvTitle) && !hasClosedViaClick) {

            // Need to use synchronous Bukkit scheduler to open inventory again in 0 ticks
            int schedulerId = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> openProfilesInventory(player), 0);

            // Place player name and scheduler id into HashMap to stop scheduler later
            ArrayList<Integer> schedulerIdArray = new ArrayList<>();
            if (inventoryScheduler.containsKey(player.getDisplayName())) {
                schedulerIdArray = inventoryScheduler.get(player.getDisplayName());
            }
            schedulerIdArray.add(schedulerId);
            inventoryScheduler.put(player.getDisplayName(), schedulerIdArray);
        }
    }

    // Prevent player from clicking and removing items from Profiles inventory
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) throws IOException {
        String profilesInvTitle = color(this.getConfig().getString("inventoryTitle"));
        String previewPlayerInvTitle = color(this.getConfig().getString("previewPlayerTitle"));
        String previewEnderchestInvTitle = color(this.getConfig().getString("previewEnderchestTitle"));
        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        int slotClicked = event.getRawSlot();
        InventoryView inventoryClicked = event.getView();

        if (inventoryClicked.getTitle().equalsIgnoreCase(profilesInvTitle)) {
            event.setCancelled(true);

            // Check if clicked slot is outside of the 'Profiles' inventory
            if (slotClicked >= profilesInvSize || slotClicked == -999) {
                return;
            }

            // Check if clicked slot is empty
            if (inventoryClicked.getItem(slotClicked) == null) {
                return;
            }

            int configSlot = slotMapper.getOrDefault(slotClicked, -1);
            Material itemMaterial = Objects.requireNonNull(inventoryClicked.getItem(slotClicked)).getType();

            // Slots 12, 13, 14 will be save slots
            // If no data exists for that save, show a clearGlassPane. Otherwise, show green
            // Slots 30, 31, 32 will be delete save slots
            if (slotClicked == 12 || slotClicked == 13 || slotClicked == 14) {
                player.setGameMode(GameMode.SURVIVAL);

                // Close the Profiles inventory and remove the player from the inventory scheduler
                if (inventoryScheduler.containsKey(player.getDisplayName())) {
                    ArrayList<Integer> scheduleIdArray = inventoryScheduler.get(player.getDisplayName());
                    for (Integer integer : scheduleIdArray) {
                        Bukkit.getServer().getScheduler().cancelTask(integer);
                    }
                }

                inventoryScheduler.put(player.getDisplayName(), clickedArray);

                // If the item is a normal glass pane, we will save the profile.
                // If the item is a lime glass pane, there is existing save data.
                // If the click is a shift-click, we will preview the inventories.
                // If the click is not a shift-click, we will save/load the profile.
                if (itemMaterial == Material.GLASS_PANE) {
                    saveProfile(configSlot, player, true);
                } else if (itemMaterial == Material.LIME_STAINED_GLASS_PANE) {
                    if (event.getClick().isShiftClick()) {
                        previewInventory(configSlot, player, event.getClick().isLeftClick());
                    } else {
                        if (event.getClick().isLeftClick()) {
                            loadProfile(configSlot, player);
                        }
                        if (event.getClick().isRightClick()) {
                            saveProfile(configSlot, player, false);
                        }

                    }
                }
                player.closeInventory();
            } else if (slotClicked == 30 || slotClicked == 31 || slotClicked == 32) {
                // Remove the appropriate slot section from config.yml
                this.getConfig().set("data." + uuid + ".slot" + configSlot, null);
                this.saveConfig();

                // Refresh the Profiles inventory to show removed save slot
                inventoryScheduler.remove(player.getDisplayName());
                player.closeInventory();
            }
        }

        if (inventoryClicked.getTitle().equalsIgnoreCase(previewPlayerInvTitle) || inventoryClicked.getTitle().equalsIgnoreCase(previewEnderchestInvTitle)) {
            event.setCancelled(true);
            // Check if clicked slot is outside of the 'Profiles' inventory
            if (slotClicked >= profilesInvSize || slotClicked == -999) {
                return;
            }

            // Check if clicked slot is empty
            if (inventoryClicked.getItem(slotClicked) == null) {
                return;
            }

            Material itemMaterial = Objects.requireNonNull(inventoryClicked.getItem(slotClicked)).getType();

            // If they click the close button, close this inventory and re-open the Profiles menu
            if (itemMaterial == Material.BARRIER) {
                player.closeInventory();
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> openProfilesInventory(player), 0);
            }
        }
    }

    // Clean up HashMap once player leaves the server
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        inventoryScheduler.remove(player.getDisplayName());
    }

    // Prevent player from moving while the Profiles inventory is open
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        String profilesInvTitle = color(this.getConfig().getString("inventoryTitle"));
        Player player = event.getPlayer();
        InventoryView inventoryOpened = player.getOpenInventory();

        if (inventoryOpened.getTitle().equalsIgnoreCase(profilesInvTitle)) {
            event.setCancelled(true);
        }
    }
}
