package com.cmpscjg.profiles;

import com.cmpscjg.profiles.files.DataManager;
import com.cmpscjg.profiles.utils.BukkitSerialization;
import com.cmpscjg.profiles.utils.DataHelper;
import com.cryptomorin.xseries.XMaterial;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
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
    public static HashMap<String, Integer> currentSlotMapper = new HashMap<>();
    private static Economy eco = null;

    enum SaveTypeEnum {
        NEW,
        EXISTING,
        AUTO
    }

    public DataManager data;

    public DataHelper dataHelper;

    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            Bukkit.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Save default config.yml
        this.saveDefaultConfig();

        // Get plugin version
        pluginVersion = this.getDescription().getVersion();

        // Initialize all the separate YAML manager classes
        this.data = new DataManager(this);

        // Initialize all the utility classes
        this.dataHelper = new DataHelper(this);

        // Register main class events
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        boolean shouldSaveOnPlayerLeave = this.getConfig().getBoolean("shouldSaveOnPlayerLeave");
        if (shouldSaveOnPlayerLeave) {
            Bukkit.broadcastMessage(color(this.getConfig().getString("prefix") + " &c Since the server has been reloaded, the saveOnPlayerLeave feature will not work as intended. Please manually save your profile before leaving!"));
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        eco = rsp.getProvider();
        return eco != null;
    }

    public static Economy getEconomy() {
        return eco;
    }

    // ------------------------ Utils ------------------------
    public void getMainMenu(CommandSender sender) {
        sender.sendMessage(color("&e*&8------------- &dProfiles: v" + pluginVersion + " &8-------------&e*"));
        sender.sendMessage(color("&d/profiles open &8: &7Open the Profiles main menu"));
        sender.sendMessage(color("&d/profiles save <0 - 1 - 2>: &7Save your progress into a slot"));
        sender.sendMessage(color("&d/profiles load <0 - 1 - 2>: &7Load your progress from an existing save"));
        sender.sendMessage(color("&d/profiles delete <0 - 1 - 2>: &7Delete your progress from a slot"));


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

        // Slots 12, 13, 14 will be save slots
        // If no data exists for that save, show a clearGlassPane. Otherwise, show green
        // Slots 30, 31, 32 will be delete save slots
        for (int i = 0; i < profilesInv.getSize(); i++) {
            switch(i) {
                case 12:
                case 13:
                case 14:
                    int configSlot = slotMapper.get(i);
                    if (this.data.getConfig().contains("data." + uuid + ".slot" + configSlot)) {
                        // TODO: Re-visit this prettify logic.
                        // String dateTimeString = new SimpleDateFormat("dd/MM/yyyy hh.mm aa").format(new Date(Objects.requireNonNull(this.data.getConfig().getString("data." + uuid + ".slot" + configSlot + ".dateSaved"))));
                        double healthLevel = this.data.getConfig().getDouble("data." + uuid + ".slot" + configSlot + ".healthLevel");
                        int hungerLevel = this.data.getConfig().getInt("data." + uuid + ".slot" + configSlot + ".hungerLevel");
                        int xpLevel = this.data.getConfig().getInt("data." + uuid + ".slot" + configSlot + ".experience.xpLevel");
                        double coins = this.data.getConfig().getInt("data." + uuid + ".slot" + configSlot + ".coins");
                        float xpPoints = (float) this.data.getConfig().getDouble("data." + uuid + ".slot" + configSlot + ".experience.xpPoints");
                        String world = this.data.getConfig().getString("data." + uuid + ".slot" + configSlot + ".location.world");
                        int X = (int) this.data.getConfig().getDouble("data." + uuid + ".slot" + configSlot + ".location.X");
                        int Y = (int) this.data.getConfig().getDouble("data." + uuid + ".slot" + configSlot + ".location.Y");
                        int Z = (int) this.data.getConfig().getDouble("data." + uuid + ".slot" + configSlot + ".location.Z");

                        ArrayList<String> savedSlotLore = new ArrayList<>();
                        for (String line : getConfig().getStringList("GUI.savedSlot.lore"))
                            savedSlotLore.add(color(line));

                        for (String line : getConfig().getStringList("GUI.savedSlot.saveDataLore")) {

                            if (line.contains("%health%"))
                                line = line.replace("%health%", String.valueOf(healthLevel));

                            if (line.contains("%hunger%"))
                                line = line.replace("%hunger%", String.valueOf(hungerLevel));

                            if (line.contains("%xp_level%"))
                                line = line.replace("%xp_level%", String.valueOf(xpLevel));

                            if (line.contains("%xp_points%"))
                                line = line.replace("%xp_points%", String.valueOf(xpPoints));
                            if (line.contains("%coins%"))
                                line = line.replace("%coins%", String.valueOf(coins));

                            if (line.contains("%world%"))
                                line = line.replace("%world%", String.valueOf(world));

                            if (line.contains("%X%"))
                                line = line.replace("%X%", String.valueOf(X));

                            if (line.contains("%Y%"))
                                line = line.replace("%Y%", String.valueOf(Y));

                            if (line.contains("%Z%"))
                                line = line.replace("%Z%", String.valueOf(Z));

                            savedSlotLore.add(color(line));
                        }

                    }
                    break;
                case 30:
                case 31:
                case 32:
                    break;
                default:
                    break;
            }
        }
        player.openInventory(profilesInv);
    }

    public String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void saveProfile(int saveSlot, Player player, SaveTypeEnum saveType) {
        UUID uuid = player.getUniqueId();
        String playerName = player.getDisplayName();
        boolean freshStartOnNewSave = this.getConfig().getBoolean("freshStart.freshStartOnNewSave");
        boolean teleportToSpawnOnNewSave = this.getConfig().getBoolean("freshStart.teleportToSpawnOnNewSave");
        List<String> commandsToDispatchOnSave = new ArrayList<>();

        // Check if commands are defined for the provided slot
        if (this.getConfig().contains("commands.onSave.slot" + saveSlot)) {
            commandsToDispatchOnSave = (List<String>) this.getConfig().getList("commands.onSave.slot" + saveSlot);
        }

        // If save is the result of the player clicking an empty save slot, treat the save as a 'freshStart'
        if (saveType == SaveTypeEnum.NEW) {

            // If enabled, clear player inventory, ender chest inventory and other player data.
            if (freshStartOnNewSave) {
                player.setHealth(20.0);
                player.setFoodLevel(20);
                player.setExp(0);
                player.setLevel(0);
                player.setTotalExperience(0);
                player.getInventory().clear();
                ItemStack item = new ItemStack(Material.NETHER_STAR);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("Main Menu");
                meta.addEnchant(Enchantment.DIG_SPEED, 100, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
                player.getInventory().setItem(8,item);
                player.getEnderChest().clear();
                eco.withdrawPlayer(player,eco.getBalance(player));
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
        double coins = Double.valueOf(eco.getBalance(player));
        player.sendMessage(String.valueOf(coins));

        // Need to serialize the player's inventory and ender chest inventory to base64 and store as a string
        String base64PlayerInventory = bukkitSerialization.itemStackArrayToBase64(player.getInventory().getContents());
        String base64PlayerArmor = bukkitSerialization.itemStackArrayToBase64(player.getInventory().getArmorContents());
        String base64EnderChestInventory = bukkitSerialization.itemStackArrayToBase64(player.getEnderChest().getContents());

        String world = Objects.requireNonNull(player.getLocation().getWorld()).getName();
        double X = player.getLocation().getX();
        double Y = player.getLocation().getY();
        double Z = player.getLocation().getZ();

        // Set data to data.yml
        this.data.getConfig().set("data." + uuid + ".slot" + saveSlot + ".playerName", playerName);
        this.data.getConfig().set("data." + uuid + ".slot" + saveSlot + ".dateSaved", dateSaved);
        this.data.getConfig().set("data." + uuid + ".slot" + saveSlot + ".healthLevel", healthLevel);
        this.data.getConfig().set("data." + uuid + ".slot" + saveSlot + ".hungerLevel", hungerLevel);
        this.data.getConfig().set("data." + uuid + ".slot" + saveSlot + ".experience.xpLevel", xpLevel);
        this.data.getConfig().set("data." + uuid + ".slot" + saveSlot + ".experience.xpPoints", xpPoints);
        this.data.getConfig().set("data." + uuid + ".slot" + saveSlot + ".playerInventory", base64PlayerInventory);
        this.data.getConfig().set("data." + uuid + ".slot" + saveSlot + ".playerArmor", base64PlayerArmor);
        this.data.getConfig().set("data." + uuid + ".slot" + saveSlot + ".enderChestInventory", base64EnderChestInventory);
        this.data.getConfig().set("data." + uuid + ".slot" + saveSlot + ".location.world", world);
        this.data.getConfig().set("data." + uuid + ".slot" + saveSlot + ".location.X", X);
        this.data.getConfig().set("data." + uuid + ".slot" + saveSlot + ".location.Y", Y);
        this.data.getConfig().set("data." + uuid + ".slot" + saveSlot + ".location.Z", Z);
        this.data.getConfig().set("data." + uuid + ".slot" + saveSlot + ".coins", coins);

        // Save data to data.yml
        this.data.saveConfig();

        // Store which slot the player saved to. Making it the current slot
        currentSlotMapper.put(player.getDisplayName(), saveSlot);

        // Dispatch any and all commands defined for the save slot
        if (commandsToDispatchOnSave != null && saveType != SaveTypeEnum.AUTO) {
            for (String command : commandsToDispatchOnSave) {
                if (command != null) {
                    command = command.replaceAll("<PLAYER>", player.getDisplayName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }
        }

        // Send a message to the player stating the save was successful
        if (saveType != SaveTypeEnum.AUTO) {
            player.sendMessage(color(this.getConfig().getString("prefix") + this.getConfig().getString("saveProfileMessage").replaceAll("<SLOT_NUMBER>", Integer.toString(saveSlot))));
        }
    }

    public void loadProfile(int saveSlot, Player player) throws IOException {
        UUID uuid = player.getUniqueId();
        List<String> commandsToDispatchOnLoad = new ArrayList<>();
        String saveDoesNotExistOnSlotMessage = this.getConfig().getString("saveDoesNotExistOnSlotMessage");
        saveDoesNotExistOnSlotMessage = saveDoesNotExistOnSlotMessage.replaceAll("<SLOT_NUMBER>", Integer.toString(saveSlot));

        // Check if commands are defined for the provided slot
        if (this.getConfig().contains("commands.onLoad.slot" + saveSlot)) {
            commandsToDispatchOnLoad = (List<String>) this.getConfig().getList("commands.onLoad.slot" + saveSlot);
        }

        // Check if save data does not exist for the provided slot
        if (!this.data.getConfig().contains("data." + uuid + ".slot" + saveSlot)) {
            player.sendMessage(color(this.getConfig().getString("prefix") + saveDoesNotExistOnSlotMessage));
            return;
        }

        // Get data from data.yml
        double healthLevel = this.data.getConfig().getDouble("data." + uuid + ".slot" + saveSlot + ".healthLevel");
        int hungerLevel = this.data.getConfig().getInt("data." + uuid + ".slot" + saveSlot + ".hungerLevel");
        int xpLevel = this.data.getConfig().getInt("data." + uuid + ".slot" + saveSlot + ".experience.xpLevel");
        double coins = this.data.getConfig().getInt("data." + uuid + ".slot" + saveSlot + ".coins");
        float xpPoints = (float) this.data.getConfig().getDouble("data." + uuid + ".slot" + saveSlot + ".experience.xpPoints");
        String base64PlayerInventory = this.data.getConfig().getString("data." + uuid + ".slot" + saveSlot + ".playerInventory");
        String base64PlayerArmor = this.data.getConfig().getString("data." + uuid + ".slot" + saveSlot + ".playerArmor");
        String base64EnderChestInventory = this.data.getConfig().getString("data." + uuid + ".slot" + saveSlot + ".enderChestInventory");
        String world = this.data.getConfig().getString("data." + uuid + ".slot" + saveSlot + ".location.world");
        double X = this.data.getConfig().getDouble("data." + uuid + ".slot" + saveSlot + ".location.X");
        double Y = this.data.getConfig().getDouble("data." + uuid + ".slot" + saveSlot + ".location.Y");
        double Z = this.data.getConfig().getDouble("data." + uuid + ".slot" + saveSlot + ".location.Z");

        // Clean incoming data from config.yml to prevent errors
        // Assign properties to default Minecraft starting values if bad data exists.
        if (healthLevel < 0.0 || healthLevel > 20.0) {
            healthLevel = 20.0;
        }

        if (hungerLevel < 0 || hungerLevel > 20) {
            hungerLevel = 20;
        }

        if (xpLevel < 0 || xpLevel > Integer.MAX_VALUE) {
            xpLevel = 0;
        }

        if (xpPoints < 0.0 || xpPoints > 1.0) {
            xpPoints = 0;
        }

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
        player.setExp(0);
        player.setLevel(0);
        player.giveExp(xpLevel);
        player.setExp(xpPoints);
        eco.withdrawPlayer(player,eco.getBalance(player));
        eco.depositPlayer(player,coins);

        // Teleport player to saved location
        assert world != null;
        World fullWorld = Bukkit.getWorld(world);
        Location savedLocation = new Location(fullWorld, X, Y, Z);
        player.teleport(savedLocation);

        // Dispatch any and all commands defined for the save slot
        if (commandsToDispatchOnLoad != null) {
            for (String command : commandsToDispatchOnLoad) {
                if (command != null) {
                    command = command.replaceAll("<PLAYER>", player.getDisplayName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }
        }

        // Store which slot the player has loaded. Making it the current slot
        currentSlotMapper.put(player.getDisplayName(), saveSlot);

        player.sendMessage(color(this.getConfig().getString("prefix") + this.getConfig().getString("loadProfileMessage").replaceAll("<SLOT_NUMBER>", Integer.toString(saveSlot))));
    }

    public void deleteProfile(int saveSlot, Player player) {
        UUID uuid = player.getUniqueId();
        List<String> commandsToDispatchOnDelete = new ArrayList<>();
        String deleteProfileMessage = this.getConfig().getString("deleteProfileMessage");
        deleteProfileMessage = deleteProfileMessage.replaceAll("<SLOT_NUMBER>", Integer.toString(saveSlot));

        // Check if commands are defined for the provided slot
        if (this.getConfig().contains("commands.onDelete.slot" + saveSlot)) {
            commandsToDispatchOnDelete = (List<String>) this.getConfig().getList("commands.onDelete.slot" + saveSlot);
        }

        // Remove the appropriate slot section from data.yml
        this.data.getConfig().set("data." + uuid + ".slot" + Integer.toString(saveSlot), null);
        this.data.saveConfig();

        // Dispatch any and all commands defined for the save slot
        if (commandsToDispatchOnDelete != null) {
            for (String command : commandsToDispatchOnDelete) {
                if (command != null) {
                    command = command.replaceAll("<PLAYER>", player.getDisplayName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }
        }

        player.sendMessage(color(this.getConfig().getString("prefix") + deleteProfileMessage));
    }

    public void previewInventory(int configSlot, Player player, boolean isLeftClick) throws IOException {
        UUID uuid = player.getUniqueId();

        // Get data from data.yml
        String base64PlayerInventory = this.data.getConfig().getString("data." + uuid + ".slot" + configSlot + ".playerInventory");
        String base64EnderChestInventory = this.data.getConfig().getString("data." + uuid + ".slot" + configSlot + ".enderChestInventory");

        // Set player data
        ItemStack[] playerInventoryContents = bukkitSerialization.itemStackArrayFromBase64(base64PlayerInventory);
        ItemStack[] enderChestInventory = bukkitSerialization.itemStackArrayFromBase64(base64EnderChestInventory);
        ItemStack closeButton = XMaterial.matchXMaterial(getConfig().getString("GUI.closeButton.material")).get().parseItem();
        ItemMeta closeButtonIM = closeButton.getItemMeta();
        assert closeButtonIM != null;
        closeButtonIM.setDisplayName(color(getConfig().getString("GUI.closeButton.name")));
        ArrayList<String> clearGlassPaneLore = new ArrayList<>();
        for (String line : getConfig().getStringList("GUI.closeButton.lore"))
            clearGlassPaneLore.add(color(line));
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
        ItemStack blackGlassPane = XMaterial.matchXMaterial(getConfig().getString("GUI.default.material")).get().parseItem();
        ItemMeta blackGlassPaneIM = blackGlassPane.getItemMeta();
        assert blackGlassPaneIM != null;
        ArrayList<String> defaultLore = new ArrayList<>();
        for (String line : getConfig().getStringList("GUI.default.lore"))
            defaultLore.add(color(line));
        blackGlassPaneIM.setLore(defaultLore);
        blackGlassPaneIM.setDisplayName(color(getConfig().getString("GUI.default.name")));
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
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    inventoryScheduler.put(player.getDisplayName(), clickedArray);
                    openProfilesInventory(player);
                } else {
                    sender.sendMessage(color(this.getConfig().getString("prefix") + this.getConfig().getString("noPermissionMessage")));
                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("profiles.reload")) {
                        this.reloadConfig();
                        sender.sendMessage(color(this.getConfig().getString("prefix") + this.getConfig().getString("reloadConfigMessage")));
                    } else {
                        sender.sendMessage(color(this.getConfig().getString("prefix") + this.getConfig().getString("noPermissionMessage")));
                    }
                } else if (args[0].equalsIgnoreCase("help")) {
                    getMainMenu(sender);
                }
            } else if (args.length == 2) {
                int slotProvided = -1;
                try {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        // Ensure the provided slot is the string-equivalent of 0, 1, 2
                        // TODO: From a user perspective, 1, 2, 3 makes more sense
                        slotProvided = Integer.parseInt(args[1]);
                        if (slotProvided < 0 || slotProvided > 2) {
                            String invalidSlotMessage = this.getConfig().getString("invalidSlotMessage");
                            invalidSlotMessage = invalidSlotMessage.replaceAll("<SLOT_NUMBER>", Integer.toString(slotProvided));
                            player.sendMessage(color(this.getConfig().getString("prefix") + invalidSlotMessage));
                        } else {
                            if (args[0].equalsIgnoreCase("save")) {
                                boolean doesSaveExist = this.data.getConfig().contains("data." + player.getUniqueId() + ".slot" + slotProvided);
                                saveProfile(slotProvided, player, doesSaveExist ? SaveTypeEnum.EXISTING : SaveTypeEnum.NEW);
                            } else if (args[0].equalsIgnoreCase("load")) {
                                loadProfile(slotProvided, player);
                            } else if (args[0].equalsIgnoreCase("delete")) {
                                deleteProfile(slotProvided, player);
                            }
                        }
                    } else {
                        sender.sendMessage(color(this.getConfig().getString("prefix") + this.getConfig().getString("noPermissionMessage")));
                    }
                } catch (NumberFormatException | IOException e) {
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

        // Since we are force closing an inventory, this event will fire constantly.
        // Meaning, we can just invoke the saveProfile method here.
        // TODO: Need to find all inventories that return items on close and do not auto save
        int currentProfileSlot = -1;
        if (!inventoryClosed.getTitle().equalsIgnoreCase("Crafting") &&
                !inventoryClosed.getTitle().equalsIgnoreCase(profilesInvTitle) &&
                currentSlotMapper.containsKey(player.getDisplayName())) {
            currentProfileSlot = currentSlotMapper.get(player.getDisplayName());
            saveProfile(currentProfileSlot, player, SaveTypeEnum.AUTO);
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

        // Profiles inventory
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
                ItemStack emptySlot = XMaterial.matchXMaterial(getConfig().getString("GUI.emptySlot.material")).get().parseItem();
                ItemStack savedSlot = XMaterial.matchXMaterial(getConfig().getString("GUI.savedSlot.material")).get().parseItem();

                if (itemMaterial == emptySlot.getType()) {
                    saveProfile(configSlot, player, SaveTypeEnum.NEW);
                } else if (itemMaterial == savedSlot.getType()) {
                    if (event.getClick().isShiftClick()) {
                        previewInventory(configSlot, player, event.getClick().isLeftClick());
                    } else {
                        if (event.getClick().isLeftClick()) {
                            loadProfile(configSlot, player);
                        }
                        if (event.getClick().isRightClick()) {
                            saveProfile(configSlot, player, SaveTypeEnum.EXISTING);
                        }

                    }
                }
                player.closeInventory();
            } else if (slotClicked == 30 || slotClicked == 31 || slotClicked == 32) {
                // Delete the corresponding Profile slot
                deleteProfile(slotMapper.get(slotClicked), player);

                // Refresh the Profiles inventory to show removed save slot
                inventoryScheduler.remove(player.getDisplayName());
                player.closeInventory();
            }
        }

        // Preview inventory
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
            ItemStack closeButton = XMaterial.matchXMaterial(getConfig().getString("GUI.closeButton.material")).get().parseItem();

            // If they click the close button, close this inventory and re-open the Profiles menu
            if (itemMaterial == closeButton.getType()) {
                player.closeInventory();
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> openProfilesInventory(player), 0);
            }
        }

        // Auto save the Player's Profile when they put an item into an inventory
        // Chest actions - MOVE_TO_OTHER_INVENTORY, PLACE_ALL
        // Inventory titles - Chest, Ender Chest, Furnace, Blast Furnace, Minecart with Chest, Item Hopper
        // Repair & Name, Enchant, Crafting, Large Chest
        // if (clickAction == InventoryAction.MOVE_TO_OTHER_INVENTORY || clickAction == InventoryAction.PLACE_ALL)
    }

    // Clean up HashMap once player leaves the server
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        boolean shouldSaveOnPlayerLeave = this.getConfig().getBoolean("shouldSaveOnPlayerLeave");

        // TODO: This is really hacky.
        // Close any open inventory to kick off auto save.
        player.openInventory(Bukkit.createInventory(null, 18, "Profiles - Inventory"));
        player.closeInventory();

        // Get the current Profile slot that the player last interacted with (either through saving or loading)
        // TODO: This should work as long as the server is not being stopped/reset.
        //       In a future update, we should persist this somehow.
        int currentProfileSlot = -1;
        if (currentSlotMapper.containsKey(player.getDisplayName())) {
            currentProfileSlot = currentSlotMapper.get(player.getDisplayName());
        }

        // If enabled, save the current Profile slot if defined. Then, remove player from HashMap
        if (shouldSaveOnPlayerLeave && currentProfileSlot != -1) {
            saveProfile(currentProfileSlot, player, SaveTypeEnum.AUTO);
            currentSlotMapper.remove(player.getDisplayName());
        }

        // Remove player from HashMap
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

    // Auto save the Player's Profile when they pick up a new item
    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        // Ensure entity is a player before casting it as such
            int currentProfileSlot = -1;
            if (currentSlotMapper.containsKey(player.getDisplayName())) {
                currentProfileSlot = currentSlotMapper.get(player.getDisplayName());
                int finalCurrentProfileSlot = currentProfileSlot;
                Player finalPlayer = player;
                Bukkit.getScheduler().runTaskLater(this, () ->
                        saveProfile(finalCurrentProfileSlot, finalPlayer, SaveTypeEnum.AUTO), 0);
            }
    }

    // Auto save the Player's Profile when they drop an item
    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        int currentProfileSlot = -1;
        if (currentSlotMapper.containsKey(player.getDisplayName())) {
            currentProfileSlot = currentSlotMapper.get(player.getDisplayName());
            saveProfile(currentProfileSlot, player, SaveTypeEnum.AUTO);
        }
    }

    // Auto save the Player's Profile when they break an item
    @EventHandler
    public void onPlayerBreakItem(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();

        int currentProfileSlot = -1;
        if (currentSlotMapper.containsKey(player.getDisplayName())) {
            currentProfileSlot = currentSlotMapper.get(player.getDisplayName());
            int finalCurrentProfileSlot = currentProfileSlot;
            Player finalPlayer = player;
            Bukkit.getScheduler().runTaskLater(this, () ->
                    saveProfile(finalCurrentProfileSlot, finalPlayer, SaveTypeEnum.AUTO), 0);
        }
    }

    // Auto save the Player's Profile when they consume an item
    @EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        int currentProfileSlot = -1;
        if (currentSlotMapper.containsKey(player.getDisplayName())) {
            currentProfileSlot = currentSlotMapper.get(player.getDisplayName());
            int finalCurrentProfileSlot = currentProfileSlot;
            Player finalPlayer = player;
            Bukkit.getScheduler().runTaskLater(this, () ->
                    saveProfile(finalCurrentProfileSlot, finalPlayer, SaveTypeEnum.AUTO), 0);
        }
    }
}
