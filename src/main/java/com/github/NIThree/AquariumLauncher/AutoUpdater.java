package com.github.NIThree.AquariumLauncher;

import com.github.NIThree.AquariumLauncher.game.MinecraftInfos;
import fr.flowarg.flowlogger.ILogger;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AutoUpdater {

    private static final String UPDATE_INFO_URL = "http://aquarium.ndemare.fr/version.json";
    private static final String CURRENT_VERSION = MinecraftInfos.LAUNCHER_VERSION;
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final String UPDATE_ZIP = "Aquarium_Launcher.zip";
    protected static final ILogger logger = Launcher.getInstance().getLogger();

    public static void main(String[] args) {

        try {
            if (checkForUpdates()) {
                downloadUpdate();
                applyUpdate();
            } else {
                System.out.println("No updates available.");
                logger.info("No updates available.");
            }
        } catch (IOException e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
        }
    }

    public static boolean checkForUpdates() throws IOException {
        URL url = new URL(UPDATE_INFO_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (InputStream inputStream = connection.getInputStream()) {
            JSONObject json = new JSONObject(new JSONTokener(inputStream));
            String latestVersion = json.getString("latestVersion");
            return !CURRENT_VERSION.equals(latestVersion);
        }
    }

    public static void downloadUpdate() throws IOException {
        URL url = new URL(getUpdateUrl());
        Path targetPath = Paths.get(TEMP_DIR, UPDATE_ZIP);
        try (InputStream inputStream = url.openStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
        System.out.println("Update downloaded to " + targetPath);
        logger.info("Update downloaded to " + targetPath);
    }

    private static String getUpdateUrl() throws IOException {
        URL url = new URL(UPDATE_INFO_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (InputStream inputStream = connection.getInputStream()) {
            JSONObject json = new JSONObject(new JSONTokener(inputStream));
            return json.getString("updateUrl");
        }
    }

    public static void runUpdaterScript() throws IOException {
        Path updateZipPath = Paths.get(TEMP_DIR, UPDATE_ZIP);
        Path appDirectory = Paths.get(System.getProperty("user.dir"));
        Path updateScriptPath = appDirectory.resolve("update.bat");

        // Move the update.zip to the application directory
        Files.move(updateZipPath, appDirectory.resolve(UPDATE_ZIP), StandardCopyOption.REPLACE_EXISTING);

        // Create the update.bat script
        String scriptContent = "@echo off\n"
                + "echo Waiting for the application to close...\n"
                + "timeout /t 5 /nobreak > nul\n\n"
                + "echo Updating application...\n"
                + ":: Change directory to the application's directory\n"
                + "cd /d %~dp0\n\n"
                + ":: Extract the Aquarium_Launcher.zip\n"
                + "powershell -Command \"Expand-Archive -Path Aquarium_Launcher.zip -DestinationPath . -Force\"\n\n"
                + "echo Update complete. Restarting application...\n"
                + ":: Start the application\n"
                + "start \"\" \"Aquarium Launcher.exe\"\n";
                //+ "exit\n";

        Files.write(updateScriptPath, scriptContent.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // Run the update.bat script
        new ProcessBuilder("cmd", "/c", "start", "cmd", "/k", updateScriptPath.toString()).start();


        // Exit the application
        System.exit(0);
    }

    public static void applyUpdate() throws IOException {
        Path updateZipPath = Paths.get(TEMP_DIR, UPDATE_ZIP);
        Path appDirectory = Paths.get(System.getProperty("user.dir"));

        // Extract the ZIP file
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(updateZipPath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path entryPath = appDirectory.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.copy(zis, entryPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
        System.out.println("Update applied. Restarting application...");
        logger.info("Update applied. Restarting application...");
        restartApplication();
    }

    private static void restartApplication() throws IOException {
        String javaBin = System.getProperty("java.home") + "/bin/java";
        String jarPath = Paths.get(System.getProperty("user.dir"), "Aquarium Launcher.exe").toString();

        ProcessBuilder builder = new ProcessBuilder(javaBin, "-jar", jarPath);
        builder.start();
        System.exit(0);
    }
}
