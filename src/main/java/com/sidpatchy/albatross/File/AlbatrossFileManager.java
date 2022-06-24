package com.sidpatchy.albatross.File;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Class to manage plugin files
 * Heavily based on the FileManager from MCShared.
 */
public class AlbatrossFileManager {
    private final JavaPlugin plugin;
    private final String resourceName;
    private final String fileName;
    private File file;
    private int numComments;

    /**
     * Constructs an instance of the AlbatrossFileManager
     *
     * @param fileName name/path of file
     * @param plugin plugin utilizing the file manager
     */
    public AlbatrossFileManager(String fileName, JavaPlugin plugin) {
        this.plugin = plugin;
        this.resourceName = fileName;
        this.fileName = fileName;
        if (fileName.startsWith("/")) {
            file = new File(plugin.getDataFolder() + fileName.replace("/", File.separator));
        } else {
            file = new File(plugin.getDataFolder() + File.separator + fileName.replace("/", File.separator));
        }
    }

    /**
     * Constructs an instance of the Albatross file manager where the resource name is different
     * from the name of the file
     * @param fileName name/path of file
     * @param resourceName name/path of resource
     * @param plugin plugin utilizing the file manager
     */
    public AlbatrossFileManager(String fileName, String resourceName, JavaPlugin plugin) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.resourceName = resourceName;
        if (fileName.startsWith("/")) {
            file = new File(plugin.getDataFolder() + fileName.replace("/", File.separator));
        } else {
            file = new File(plugin.getDataFolder() + File.separator + fileName.replace("/", File.separator));
        }
    }

    /**
     * Backs up the file adding the extension ".bak"
     * @throws IOException
     */
    public void backup() throws IOException {
        File backupFile = new File(plugin.getDataFolder(), file.getName() + ".bak");

        if (file.lastModified() > backupFile.lastModified() && file.exists()) {
            try (FileInputStream inputStream = new FileInputStream(file);
                    FileOutputStream outputStream = new FileOutputStream(backupFile);) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
        }
    }

    /**
     * Returns configurations string with comments reworked to be key-value pairs allowing them to be maintained while
     * using the Bukkit YamlConfiguration class.
     *
     * @return string with reworked comments.
     * @throws IOException
     */
    protected String getConfigurationString() throws IOException {
        StringBuilder configurationString = new StringBuilder();
        try (FileInputStream fileInputStream = new FileInputStream(file);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8))) {
            numComments = 0;
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                if (currentLine.startsWith("#")) {
                    // Rework comment line so it becomes a standard key-value pair in the configuration file.
                    // This workaround allows the comment to be saved when using Bukkit's YamlConfiguration class.
                    configurationString.append(currentLine.replace(":", "_COLON_").replace("|", "_VERT_")
                            .replace("-", "_HYPHEN_").replace(" ", "_SPACE_")
                            .replaceFirst("#", plugin.getDescription().getName() + "_COMMENT_" + numComments + ": "));
                    numComments++;
                } else {
                    configurationString.append(currentLine);
                }
                configurationString.append("\n");
            }
            return configurationString.toString();
        }
    }

    /**
     * Should only be called after calling {@link #getConfigurationString()}
     *
     * @return total number of comments in the file.
     */
    protected int getNumComments() { return numComments; }

    protected void saveConfiguration(String configurationString) throws IOException {
        String configuration = getConfigurationStringWithRegeneratedComments(getConfigurationString());

        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
            bufferedWriter.write(configuration);
            bufferedWriter.flush();
        }
    }

    /**
     * Creates file on disk if it doesn't already exist.
     *
     * @throws IOException
     */
    protected void createConfigurationFileIfNotExists() throws IOException {
        file.getParentFile().mkdirs();
        if (file.createNewFile()) {
            try (OutputStream outputStream = new FileOutputStream(file)) {
                InputStream resource = plugin.getResource(resourceName);

                if (resource != null) {
                    int length;
                    byte[] buffer = new byte[1024];
                    while ((length = resource.read(buffer)) > 0) {
                        outputStream.write(buffer, 0 , length);
                    }
                }
            }
            plugin.getLogger().info("Successfully created " + file.getName() + " file.");
        }
    }

    private String getConfigurationStringWithRegeneratedComments(String configurationString) {
        boolean previousLineComment = false;
        String[] lines = configurationString.split("\n");
        StringBuilder configStringRegened = new StringBuilder();

        for (String line : lines) {
            if (line.startsWith(plugin.getDescription().getName() + "_COMMENT")) {
                // Rework comment line so it is converted back to a normal comment.
                String comment = ("#" + line.substring(line.indexOf(": ") + 2)).replace("_COLON_", ":")
                        .replace("_HYPHEN_", "-").replace("_VERT_", "|").replace("_SPACE_", " ");
                // No empty line between consecutive comment lines or between a comment and its corresponding
                // parameters; empty line between parameter and new comment.
                if (previousLineComment) {
                    configStringRegened.append(comment + "\n");
                }
                else {
                    configStringRegened.append("\n" + comment + "\n");
                }
                previousLineComment = true;
            }
            else {
                configStringRegened.append(line + "\n");
                previousLineComment = false;
            }
        }
        return configStringRegened.toString();
    }
}
