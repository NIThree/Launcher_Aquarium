package com.github.NIThree.AquariumLauncher;

import com.github.NIThree.AquariumLauncher.game.MinecraftInfos;
import com.github.NIThree.AquariumLauncher.ui.PanelManager;
import com.github.NIThree.AquariumLauncher.ui.panels.pages.App;
import com.github.NIThree.AquariumLauncher.ui.panels.pages.Login;

import fr.flowarg.flowlogger.ILogger;
import fr.flowarg.flowlogger.Logger;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.NIThree.AquariumLauncher.AutoUpdater.*;


public class Launcher extends Application {
    private static Launcher instance;
    private final ILogger logger;
    private final Path launcherDir = GameDirGenerator.createGameDir("Aquarium", false);
    private final Saver saver;
    private PanelManager panelManager;
    private AuthInfos authInfos = null;

    public Launcher() {
        instance = this;
        this.logger = new Logger("[LauncherAquarium]", this.launcherDir.resolve("Aquarium.log"));
        if (Files.notExists(this.launcherDir))
        {
            try
            {
                Files.createDirectory(this.launcherDir);
            } catch (IOException e)
            {
                this.logger.err("Unable to create launcher folder");
                this.logger.printStackTrace(e);
            }
        }

        saver = new Saver(this.launcherDir.resolve("config.properties"));
        saver.load();
    }

    public static Launcher getInstance() {
        return instance;
    }

    @Override
    public void start(Stage stage) {
        this.logger.info("Starting launcher");
        this.panelManager = new PanelManager(this, stage);
        this.panelManager.init();

        try {
            if (checkForUpdates()) {
               MinecraftInfos.Update = true;
            } else {
                System.out.println("No updates available.");
                this.logger.info("No updates available.");
                MinecraftInfos.Update = false;
            }
        } catch (IOException e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
        }

        if (this.isUserAlreadyLoggedIn()) {
            logger.info("Hello " + authInfos.getUsername());

            this.panelManager.showPanel(new App());
        } else {
            this.panelManager.showPanel(new Login());
        }
    }

    public boolean isUserAlreadyLoggedIn() {
        if (saver.get("msAccessToken") != null && saver.get("msRefreshToken") != null) {
            try {
                MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                MicrosoftAuthResult response = authenticator.loginWithRefreshToken(saver.get("msRefreshToken"));

                saver.set("msAccessToken", response.getAccessToken());
                saver.set("msRefreshToken", response.getRefreshToken());
                saver.save();
                this.setAuthInfos(new AuthInfos(
                        response.getProfile().getName(),
                        response.getAccessToken(),
                        response.getProfile().getId(),
                        response.getXuid(),
                        response.getClientId()
                ));
                return true;
            } catch (MicrosoftAuthenticationException e) {
                saver.remove("msAccessToken");
                saver.remove("msRefreshToken");
                saver.save();
            }
        }

        return false;
    }

    public AuthInfos getAuthInfos() {
        return authInfos;
    }

    public void setAuthInfos(AuthInfos authInfos) {
        this.authInfos = authInfos;
    }

    public ILogger getLogger() {
        return logger;
    }

    public Saver getSaver() {
        return saver;
    }

    public Path getLauncherDir() {
        return launcherDir;
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }

    public void hideWindow() {
        this.panelManager.getStage().hide();
    }
}
