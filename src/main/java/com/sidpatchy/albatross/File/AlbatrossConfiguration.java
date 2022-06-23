package com.sidpatchy.albatross.File;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to manage plugin files.
 *
 * Intended to bring consistency to config handling and add quality of life features.
 *
 * Heavily based on the CommentedYamlConfiguration from MCShared.
 */
public class AlbatrossConfiguration extends YamlConfiguration {
    private final AlbatrossFileManager fileManager;
    private final JavaPlugin plugin;
    private final String resourceName;
    private final String fileName;
    private File file;
    private int numComments;

    /**
     * Constructs a new AlbatrossConfiguration object representing a config/lang file.
     *
     * @param fileName name/path of file
     * @param plugin plugin utilizing the file manager
     */
    public AlbatrossConfiguration(String fileName, JavaPlugin plugin) {
        this.fileName = fileName;
        this.resourceName = fileName;
        this.plugin = plugin;
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("Invalid file name.");
        }
        fileManager = new AlbatrossFileManager(fileName, plugin);
    }

    /**
     * Constructs a new AlbatrossConfiguration where the resource name is different from the name of the file
     *
     * @param fileName name/path of file
     * @param resourceName name/path of resource
     * @param plugin plugin utilizing the file manager
     */
    public AlbatrossConfiguration(String fileName, String resourceName, JavaPlugin plugin) {
        this.fileName = fileName;
        this.resourceName = fileName;
        this.plugin = plugin;
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("Invalid file name.");
        }
        fileManager = new AlbatrossFileManager(fileName, resourceName, plugin);
    }

    /**
     * Gets the requested list by path.
     *
     * @param path Path of the ConfigurationSection to get.
     * @return list of strings
     */
    @Override
    public ConfigurationSection getConfigurationSection(String path) {
        ConfigurationSection configurationSection = super.getConfigurationSection(path);
        return configurationSection == null ? createSection(path) : configurationSection;
    }

    /**
     * Gets a list of type String.
     *
     * @param path Path of the List to get.
     * @return List of Strings.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> getList(String path) {
        List<String> list = (List<String>) super.getList(path);
        if (list != null) {
            return list;
        }
        return new ArrayList<>();
    }

    /**
     * Sets the value of given key with a comment.
     *
     * @param path name of key to set
     * @param value new value of object at path
     * @param comment new comment at path, ignored if null.
     */
    public void set(String path, Object value, String comment) {
        if (comment != null) {
            // Insert comment as new value in the file; will be converted back to a comment when saved by the
            // FileManager.
            this.set(plugin.getDescription().getName() + "_COMMENT_" + numComments, comment.replace(":", "_COLON_")
                    .replace("|", "_VERT_").replace("-", "_HYPHEN_").replace(" ", "_SPACE_"));
            numComments++;
        }
        this.set(path, value);
    }

    /**
     * Sets the value of a given key with multiple comments (one per line).
     *
     * @param path name of key to set
     * @param value new value of object at path
     * @param comments new comments at path, ignored if null.
     */
    public void set(String path, Object value, String... comments) {
        for (String comment : comments) {
            // Insert comment as new value in the file; will be converted back to a comment when saved.
            this.set(plugin.getDescription().getName() + "_COMMENT_" + numComments, comment.replace(":", "_COLON_")
                    .replace("|", "_VERT_").replace("-", "_HYPHEN_").replace(" ", "_SPACE_"));
            numComments++;
        }
        this.set(path, value);
    }

    /**
     * Loads or reloads the file from disk.
     *
     * @throws IOException
     * @throws InvalidConfigurationException
     */
    public void loadConfiguration() throws IOException, InvalidConfigurationException {
        fileManager.createConfigurationFileIfNotExists();
        map.clear();
        load(new StringReader(fileManager.getConfigurationString()));
        numComments = fileManager.getNumComments();
    }

    /**
     * Saves the config file with any modifications.
     *
     * @throws IOException
     */
    public void saveConfiguration() throws IOException {
        String configurationString = this.saveToString();
        fileManager.saveConfiguration(configurationString);
    }

    /**
     * Backs up the config file. Copies the file and adds ".bak" to the end of the file name.
     *
     * @throws IOException
     */
    public void backupConfiguration() throws IOException {
        fileManager.backup();
    }
}
