package com.sidpatchy.albatross;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class Albatross extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getLogger().info("Enabling Albatross...");

        int pluginID = 13540;
        Metrics metrics = new Metrics(this, pluginID);

        this.getLogger().info("Albatross enabled.");
    }

    @Override
    public void onDisable() {
        this.getServer().getLogger().info("Albatross disabled.");
    }
}
