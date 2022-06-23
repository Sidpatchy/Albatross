package com.sidpatchy.albatross.Update;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class SpigotUpdateChecker {

    private final JavaPlugin plugin;
    private final int spigotResourceID;
    private final String spigotResourceURL;

    /**
     * Constructs a new instance of the SpigotUpdateChecker.
     *
     * @param spigotResourceURL link to Spigot listing where user can download the plugin.
     * @param spigotResourceID the spigot resource ID
     * @param plugin the plugin using the update checker.
     */
    public SpigotUpdateChecker(String spigotResourceURL, int spigotResourceID, JavaPlugin plugin) {
        this.spigotResourceURL = spigotResourceURL;
        this.spigotResourceID = spigotResourceID;
        this.plugin = plugin;
    }


    private void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + spigotResourceID).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                plugin.getLogger().info("Unable to check for updates: " + exception.getMessage());
            }
        });
    }

    public void checkForUpdates() {
        plugin.getLogger().info("Checking for updates...");
        getVersion(version -> {
            if (plugin.getDescription().getVersion().equalsIgnoreCase(version)) {
                plugin.getLogger().info("You are already running the latest version.");
            }
            else {
                plugin.getLogger().info("There is an update available! Download it at " + spigotResourceURL);
            }

        });
    }
}
