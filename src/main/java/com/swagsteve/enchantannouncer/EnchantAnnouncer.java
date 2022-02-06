package com.swagsteve.enchantannouncer;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public final class EnchantAnnouncer extends JavaPlugin implements Listener {

    //Instance
    private static EnchantAnnouncer instance;
    public static EnchantAnnouncer getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {

        //Instance
        instance = this;

        //Enable MSG
        System.out.println("[EA] Enabled!");

        //Config
        getConfig().options().copyDefaults();
        this.getConfig().addDefault("Messages.AnnounceMessage", "&a&l%player% Just Enchanted Their %itemname% With &r&c&l%enchants%");
        this.getConfig().addDefault("Messages.DontAnnounceAdminEnchanting", false);
        saveDefaultConfig();

        //Events
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        //Commands
        this.getCommand("ea-reload").setExecutor(new ReloadCommand());

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnchant(EnchantItemEvent e) {

        Player p = e.getEnchanter();

        String itemNametemp = e.getItem().getType().name().replace("_", " ").toLowerCase();
        String ItemName = WordUtils.capitalize(itemNametemp);

        if (!(p.isOp() && getConfig().getBoolean("Messages.DontAnnounceAdminEnchanting", false))) {

            List<String> readableList = e.getEnchantsToAdd()
                    .entrySet()
                    .stream()
                    .map(entry -> {
                        String vanillaKey = entry.getKey().getKey().getKey();
                        String readableName = WordUtils.capitalize(vanillaKey.replaceAll("_", " "));
                        return readableName + " " + entry.getValue();
                    })

                    .collect(Collectors.toList());

            String announceString = getConfig()
                    .getString("Messages.AnnounceMessage")
                    .replace("%itemname%", ItemName)
                    .replace("%player%", p.getName())
                    .replace("%enchants%", readableList.toString());

            for (Player player : Bukkit.getOnlinePlayers()) {

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', announceString));

            }
        }
    }

    @Override
    public void onDisable() {

        //Disable MSG
        System.out.println("[EA] Disabled!");
        saveConfig();

    }
}