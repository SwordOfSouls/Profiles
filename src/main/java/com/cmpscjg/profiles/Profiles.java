package com.cmpscjg.profiles;

import com.cmpscjg.profiles.utils.BukkitSerialization;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

public final class Profiles extends JavaPlugin implements Listener {

    private FileConfiguration profileData = null;
    private File profileFile = null;
    private String pluginVersion = "";

    public final ArrayList<Integer> clickedArray = new ArrayList<Integer>(Arrays.asList(new Integer[] {-1}));

    public static HashMap<String, ArrayList<Integer>> inventoryScheduler = new HashMap<String, ArrayList<Integer>>();

    public static BukkitSerialization bukkitSerialization = new BukkitSerialization();

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
        
        if (sender.hasPermission("Profiles.reload")) {
            sender.sendMessage(color("&d/profiles reload &8: &7Reload the Profiles configuration"));
        }
    }

    public void openProfilesInventory(Player player) {
        UUID uuid = player.getUniqueId();
        int profilesInvSize = 45;
        String profilesInvTitle = color(this.getConfig().getString("inventoryTitle"));

        Inventory profilesInv = Bukkit.createInventory(null, profilesInvSize, profilesInvTitle);
        ItemStack clearGlassPane = new ItemStack(Material.GLASS_PANE);
        ItemMeta clearGlassPaneIM = clearGlassPane.getItemMeta();
        clearGlassPaneIM.setDisplayName(color(this.getConfig().getString("prefix")));
        ArrayList<String> clearGlassPaneLore = new ArrayList<String>();
        clearGlassPaneLore.add(color("&fClick to start a new save in this save slot."));
        clearGlassPaneIM.setLore(clearGlassPaneLore);
        clearGlassPane.setItemMeta(clearGlassPaneIM);

        ItemStack blackGlassPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta blackGlassPaneIM = blackGlassPane.getItemMeta();
        blackGlassPaneIM.setDisplayName(color(this.getConfig().getString("prefix")));
        blackGlassPane.setItemMeta(blackGlassPaneIM);

        ItemStack limeGlassPane = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta limeGlassPaneIM = limeGlassPane.getItemMeta();
        limeGlassPaneIM.setDisplayName(color(this.getConfig().getString("prefix")));

        ItemStack redGlassPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta redGlassPaneIM = redGlassPane.getItemMeta();
        redGlassPaneIM.setDisplayName(color(this.getConfig().getString("prefix")));
        ArrayList<String> redGlassPaneLore = new ArrayList<String>();
        redGlassPaneLore.add(color("&cClick to delete the save slot above."));
        redGlassPaneIM.setLore(redGlassPaneLore);
        redGlassPane.setItemMeta(redGlassPaneIM);

        // Slots 12, 13, 14 will be save slots
        // If no data exists for that save, show a clearGlassPane. Otherwise, show green
        // Slots 30, 31, 32 will be delete save slots
        for (int i = 0; i < profilesInv.getSize(); i++) {
            switch(i) {
                case 12:
                    if (this.getConfig().contains("data." + uuid + ".slot0")) {
                        String dateTimeString = new SimpleDateFormat("dd/MM/yyyy hh.mm aa").format(new Date(this.getConfig().getString("data." + uuid + ".slot0" + ".dateSaved")));
                        double healthLevel = this.getConfig().getDouble("data." + uuid + ".slot0" + ".healthLevel");
                        int hungerLevel = this.getConfig().getInt("data." + uuid + ".slot0" + ".hungerLevel");
                        int xpLevel = this.getConfig().getInt("data." + uuid + ".slot0" + ".experience.xpLevel");
                        float xpPoints = (float) this.getConfig().getDouble("data." + uuid + ".slot0" + ".experience.xpPoints");
                        String base64PlayerInventory = this.getConfig().getString("data." + uuid + ".slot0" + ".playerInventory");
                        String base64EnderChestInventory = this.getConfig().getString("data." + uuid + ".slot0" + ".enderChestInventory");
                        String world = this.getConfig().getString("data." + uuid + ".slot0" + ".location.world");
                        int X = (int) this.getConfig().getDouble("data." + uuid + ".slot0" + ".location.X");
                        int Y = (int) this.getConfig().getDouble("data." + uuid + ".slot0" + ".location.Y");
                        int Z = (int) this.getConfig().getDouble("data." + uuid + ".slot0" + ".location.Z");

                        ArrayList<String> limeGlassPaneLore = new ArrayList<String>();
                        limeGlassPaneLore.add(color("&cLeft-Click to load this save slot."));
                        limeGlassPaneLore.add(color("&cRight-Click to overwrite this save slot."));
                        limeGlassPaneLore.add(color("&e*&8-----------&e*"));
                        limeGlassPaneLore.add(color("&7Date saved: " + "&6" + dateTimeString));
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
                case 13:
                    if (this.getConfig().contains("data." + uuid + ".slot1")) {
                        String dateTimeString = new SimpleDateFormat("dd/MM/yyyy hh.mm aa").format(new Date(this.getConfig().getString("data." + uuid + ".slot1" + ".dateSaved")));
                        double healthLevel = this.getConfig().getDouble("data." + uuid + ".slot1" + ".healthLevel");
                        int hungerLevel = this.getConfig().getInt("data." + uuid + ".slot1" + ".hungerLevel");
                        int xpLevel = this.getConfig().getInt("data." + uuid + ".slot1" + ".experience.xpLevel");
                        float xpPoints = (float) this.getConfig().getDouble("data." + uuid + ".slot1" + ".experience.xpPoints");
                        String base64PlayerInventory = this.getConfig().getString("data." + uuid + ".slot1" + ".playerInventory");
                        String base64EnderChestInventory = this.getConfig().getString("data." + uuid + ".slot1" + ".enderChestInventory");
                        String world = this.getConfig().getString("data." + uuid + ".slot1" + ".location.world");
                        int X = (int) this.getConfig().getDouble("data." + uuid + ".slot1" + ".location.X");
                        int Y = (int) this.getConfig().getDouble("data." + uuid + ".slot1" + ".location.Y");
                        int Z = (int) this.getConfig().getDouble("data." + uuid + ".slot1" + ".location.Z");

                        ArrayList<String> limeGlassPaneLore = new ArrayList<String>();
                        limeGlassPaneLore.add(color("&cLeft-Click to load this save slot."));
                        limeGlassPaneLore.add(color("&cRight-Click to overwrite this save slot."));
                        limeGlassPaneLore.add(color("&e*&8-----------&e*"));
                        limeGlassPaneLore.add(color("&7Date saved: " + "&6" + dateTimeString));
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
                case 14:
                    if (this.getConfig().contains("data." + uuid + ".slot2")) {
                        String dateTimeString = new SimpleDateFormat("dd/MM/yyyy hh.mm aa").format(new Date(this.getConfig().getString("data." + uuid + ".slot2" + ".dateSaved")));
                        double healthLevel = this.getConfig().getDouble("data." + uuid + ".slot2" + ".healthLevel");
                        int hungerLevel = this.getConfig().getInt("data." + uuid + ".slot2" + ".hungerLevel");
                        int xpLevel = this.getConfig().getInt("data." + uuid + ".slot2" + ".experience.xpLevel");
                        float xpPoints = (float) this.getConfig().getDouble("data." + uuid + ".slot2" + ".experience.xpPoints");
                        String base64PlayerInventory = this.getConfig().getString("data." + uuid + ".slot2" + ".playerInventory");
                        String base64EnderChestInventory = this.getConfig().getString("data." + uuid + ".slot2" + ".enderChestInventory");
                        String world = this.getConfig().getString("data." + uuid + ".slot2" + ".location.world");
                        int X = (int) this.getConfig().getDouble("data." + uuid + ".slot2" + ".location.X");
                        int Y = (int) this.getConfig().getDouble("data." + uuid + ".slot2" + ".location.Y");
                        int Z = (int) this.getConfig().getDouble("data." + uuid + ".slot2" + ".location.Z");

                        ArrayList<String> limeGlassPaneLore = new ArrayList<String>();
                        limeGlassPaneLore.add(color("&cLeft-Click to load this save slot."));
                        limeGlassPaneLore.add(color("&cRight-Click to overwrite this save slot."));
                        limeGlassPaneLore.add(color("&e*&8-----------&e*"));
                        limeGlassPaneLore.add(color("&7Date saved: " + "&6" + dateTimeString));
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
                    profilesInv.setItem(i, redGlassPane);
                    break;
                case 31:
                    profilesInv.setItem(i, redGlassPane);
                    break;
                case 32:
                    profilesInv.setItem(i, redGlassPane);
                    break;
                default:
                    profilesInv.setItem(i, blackGlassPane);
                    break;
            }
        }
        player.openInventory(profilesInv);
        return;
    }

    public String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void saveProfile(int saveSlot, Player player) {
        UUID uuid = player.getUniqueId();
        String playerName = player.getDisplayName();

        String dateSaved = new Date().toString();
        double healthLevel = player.getHealth();
        int hungerLevel = player.getFoodLevel();
        int xpLevel = player.getTotalExperience();
        float xpPoints = player.getExp();

        // Need to serialize the player's inventory and ender chest inventory to base64 and store as a string
        String base64PlayerInventory = bukkitSerialization.itemStackArrayToBase64(player.getInventory().getContents());
        String base64PlayerArmor = bukkitSerialization.itemStackArrayToBase64(player.getInventory().getArmorContents());
        String base64EnderChestInventory = bukkitSerialization.itemStackArrayToBase64(player.getEnderChest().getContents());

        String world = player.getLocation().getWorld().getName();
        double X = player.getLocation().getX();
        double Y = player.getLocation().getY();
        double Z = player.getLocation().getZ();

        // Set data to config.yml
        this.getConfig().set("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".playerName", playerName);
        this.getConfig().set("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".dateSaved", dateSaved);
        this.getConfig().set("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".healthLevel", healthLevel);
        this.getConfig().set("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".hungerLevel", hungerLevel);
        this.getConfig().set("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".experience.xpLevel", xpLevel);
        this.getConfig().set("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".experience.xpPoints", xpPoints);
        this.getConfig().set("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".playerInventory", base64PlayerInventory);
        this.getConfig().set("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".playerArmor", base64PlayerArmor);
        this.getConfig().set("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".enderChestInventory", base64EnderChestInventory);
        this.getConfig().set("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".location.world", world);
        this.getConfig().set("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".location.X", X);
        this.getConfig().set("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".location.Y", Y);
        this.getConfig().set("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".location.Z", Z);

        // Save data to config.yml
        this.saveConfig();

        // Send a message to the player stating the save was successful
        player.sendMessage(color(this.getConfig().getString("prefix") + this.getConfig().getString("saveProfileMessage")));
        return;
    }

    public void loadProfile(int saveSlot, Player player) throws IOException {
        UUID uuid = player.getUniqueId();

        // Get data to config.yml
        double healthLevel = this.getConfig().getDouble("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".healthLevel");
        int hungerLevel = this.getConfig().getInt("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".hungerLevel");
        int xpLevel = this.getConfig().getInt("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".experience.xpLevel");
        float xpPoints = (float) this.getConfig().getDouble("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".experience.xpPoints");
        String base64PlayerInventory = this.getConfig().getString("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".playerInventory");
        String base64PlayerArmor = this.getConfig().getString("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".playerArmor");
        String base64EnderChestInventory = this.getConfig().getString("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".enderChestInventory");
        String world = this.getConfig().getString("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".location.world");
        double X = this.getConfig().getDouble("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".location.X");
        double Y = this.getConfig().getDouble("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".location.Y");
        double Z = this.getConfig().getDouble("data." + uuid + ".slot" + Integer.toString(saveSlot) + ".location.Z");

        // Set player data
        ItemStack[] playerInventory = bukkitSerialization.itemStackArrayFromBase64(base64PlayerInventory);
        ItemStack[] playerArmor = bukkitSerialization.itemStackArrayFromBase64(base64PlayerArmor);
        ItemStack[] enderChestInventory = bukkitSerialization.itemStackArrayFromBase64(base64EnderChestInventory);

        for (ItemStack item : playerInventory) {
            this.getLogger().info("DEBUG - inventory: " + item);
        }

        for (ItemStack armor : playerArmor) {
            this.getLogger().info("DEBUG - armor " + armor);
        }

        for (ItemStack ender : enderChestInventory) {
            this.getLogger().info("DEBUG - ender " + ender);
        }

        player.getInventory().clear();
        player.getInventory().setContents(playerInventory);
        player.getInventory().setArmorContents(playerArmor);
        player.getEnderChest().setContents(enderChestInventory);
        player.updateInventory();
        player.setHealth(healthLevel);
        player.setFoodLevel(hungerLevel);
        player.setLevel(xpLevel);
        player.setExp(xpPoints);

        // Teleport player to saved location
        World fullWorld = Bukkit.getWorld(world);
        Location savedLocation = new Location(fullWorld, X, Y, Z);
        player.teleport(savedLocation);

        player.sendMessage(color(this.getConfig().getString("prefix") + this.getConfig().getString("loadProfileMessage")));
        return;
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
        openProfilesInventory(player);
    }

    // Prevent player from closing Profiles inventory
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        String profilesInvTitle = color(this.getConfig().getString("inventoryTitle"));
        InventoryView inventoryClosed = event.getView();
        Player player = (Player) event.getPlayer();
        Boolean hasClosedViaClick =
                inventoryScheduler.containsKey(player.getDisplayName()) &&
                        inventoryScheduler.get(player.getDisplayName()).get(0) == -1;

        if (inventoryClosed.getTitle().equalsIgnoreCase(profilesInvTitle) && !hasClosedViaClick) {

            // Need to use synchronous Bukkit scheduler to open inventory again in 0 ticks
            int schedulerId = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    openProfilesInventory(player);
                }
            }, 0);

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
        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        int slotClicked = event.getRawSlot();
        InventoryView inventoryClicked = event.getView();

        if (inventoryClicked.getTitle().equalsIgnoreCase(profilesInvTitle)) {
            event.setCancelled(true);

            // Slots 12, 13, 14 will be save slots
            // If no data exists for that save, show a clearGlassPane. Otherwise, show green
            // Slots 30, 31, 32 will be delete save slots
            if (slotClicked == 12) {
                player.setGameMode(GameMode.SURVIVAL);

                // Close the Profiles inventory and remove the player from the inventory scheduler
                if (inventoryScheduler.containsKey(player.getDisplayName())) {
                    ArrayList<Integer> scheduleIdArray = inventoryScheduler.get(player.getDisplayName());
                    for (int i = 0; i < scheduleIdArray.size(); i++) {
                        Bukkit.getServer().getScheduler().cancelTask(scheduleIdArray.get(i));
                    }
                }

                inventoryScheduler.put(player.getDisplayName(), clickedArray);
                Material itemMaterial = inventoryClicked.getItem(slotClicked).getType();

                // If the user right-clicks, we will save the profile. If they left-click, we will load.
                // If the item is a normal glass pane, we will save the profile.
                if (event.getClick().isRightClick() && itemMaterial == Material.LIME_STAINED_GLASS_PANE) {
                    saveProfile(0, player);
                }
                if (event.getClick().isLeftClick() && itemMaterial == Material.LIME_STAINED_GLASS_PANE) {
                    loadProfile(0, player);
                }

                if (itemMaterial == Material.GLASS_PANE) {
                    saveProfile(0, player);
                }
                player.closeInventory();
            } else if (slotClicked == 13) {
                player.setGameMode(GameMode.SURVIVAL);

                // Close the Profiles inventory and remove the player from the inventory scheduler
                if (inventoryScheduler.containsKey(player.getDisplayName())) {
                    ArrayList<Integer> scheduleIdArray = inventoryScheduler.get(player.getDisplayName());
                    for (int i = 0; i < scheduleIdArray.size(); i++) {
                        Bukkit.getServer().getScheduler().cancelTask(scheduleIdArray.get(i));
                    }
                }

                inventoryScheduler.put(player.getDisplayName(), clickedArray);
                Material itemMaterial = inventoryClicked.getItem(slotClicked).getType();
                // If the user right-clicks, we will save the profile. If they left-click, we will load.
                // If the item is a normal glass pane, we will save the profile.
                if (event.getClick().isRightClick() && itemMaterial == Material.LIME_STAINED_GLASS_PANE) {
                    saveProfile(1, player);
                }
                if (event.getClick().isLeftClick() && itemMaterial == Material.LIME_STAINED_GLASS_PANE) {
                    loadProfile(1, player);
                }

                if (itemMaterial == Material.GLASS_PANE) {
                    saveProfile(1, player);
                }
                player.closeInventory();
            } else if (slotClicked == 14) {
                player.setGameMode(GameMode.SURVIVAL);

                // Close the Profiles inventory and remove the player from the inventory scheduler
                if (inventoryScheduler.containsKey(player.getDisplayName())) {
                    ArrayList<Integer> scheduleIdArray = inventoryScheduler.get(player.getDisplayName());
                    for (int i = 0; i < scheduleIdArray.size(); i++) {
                        Bukkit.getServer().getScheduler().cancelTask(scheduleIdArray.get(i));
                    }
                }

                inventoryScheduler.put(player.getDisplayName(), clickedArray);
                Material itemMaterial = inventoryClicked.getItem(slotClicked).getType();
                // If the user right-clicks, we will save the profile. If they left-click, we will load.
                // If the item is a normal glass pane, we will save the profile.
                if (event.getClick().isRightClick() && itemMaterial == Material.LIME_STAINED_GLASS_PANE) {
                    saveProfile(2, player);
                }
                if (event.getClick().isLeftClick() && itemMaterial == Material.LIME_STAINED_GLASS_PANE) {
                    loadProfile(2, player);
                }

                if (itemMaterial == Material.GLASS_PANE) {
                    saveProfile(2, player);
                }
                player.closeInventory();
            } else if (slotClicked == 30) {
                // Remove slot0 section from config.yml
                this.getConfig().set("data." + uuid + ".slot0", null);
                this.saveConfig();

                // Refresh the Profiles inventory to show removed save slot
                inventoryScheduler.remove(player.getDisplayName());
                player.closeInventory();
            } else if (slotClicked == 31) {
                // Remove slot1 section from config.yml
                this.getConfig().set("data." + uuid + ".slot1", null);
                this.saveConfig();

                // Refresh the Profiles inventory to show removed save slot
                inventoryScheduler.remove(player.getDisplayName());
                player.closeInventory();
            } else if (slotClicked == 32) {
                // Remove slot2 section from config.yml
                this.getConfig().set("data." + uuid + ".slot2", null);
                this.saveConfig();

                // Refresh the Profiles inventory to show removed save slot
                inventoryScheduler.remove(player.getDisplayName());
                player.closeInventory();
            }
        }
    }

    // Ensure player sees the Profiles inventory on join by removing from Inventory Scheduler

    // Clean up HashMap once player leaves the server
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        inventoryScheduler.remove(player.getDisplayName());

        Boolean shouldSaveOnQuit = this.getConfig().getBoolean("shouldSaveOnQuit");
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
