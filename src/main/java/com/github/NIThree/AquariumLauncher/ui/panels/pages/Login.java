package com.github.NIThree.AquariumLauncher.ui.panels.pages;

import com.github.NIThree.AquariumLauncher.Launcher;
import com.github.NIThree.AquariumLauncher.game.MinecraftInfos;
import com.github.NIThree.AquariumLauncher.ui.PanelManager;
import com.github.NIThree.AquariumLauncher.ui.panel.Panel;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Login extends Panel {
    Saver saver = Launcher.getInstance().getSaver();
    Button msLoginBtn = new Button();

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getStylesheetPath() {
        return "css/login.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        // Background
        this.layout.getStyleClass().add("login-layout");
        setCanTakeAllSize(this.layout);

        // Background image
        GridPane bgImage = new GridPane();
        setCanTakeAllSize(bgImage);
        bgImage.getStyleClass().add("bg-image");
        this.layout.add(bgImage, 0, 0);

        GridPane Logo = new GridPane();
        setCanTakeAllSize(Logo);
        setCenterH(Logo);
        setCanTakeAllSize(Logo);
        setTop(Logo);
        Logo.setTranslateY(-30d);
        Logo.getStyleClass().add("logo-image");
        this.layout.getChildren().add(Logo);


        // Login with label
        Label loginWithLabel = new Label("Se connecter avec:".toUpperCase());
        setCanTakeAllSize(loginWithLabel);
        setCenterV(loginWithLabel);
        setCenterH(loginWithLabel);
        loginWithLabel.setFont(Font.font(loginWithLabel.getFont().getFamily(), FontWeight.BOLD, FontPosture.REGULAR, 14d));
        loginWithLabel.getStyleClass().add("login-with-label");
        loginWithLabel.setTranslateY(200d);
        loginWithLabel.setMaxWidth(280d);

        // Microsoft login button
        ImageView view = new ImageView(new Image("images/microsoft.png"));
        view.setPreserveRatio(true);
        view.setFitHeight(30d);
        setCanTakeAllSize(msLoginBtn);
        setCenterH(msLoginBtn);
        setCenterV(msLoginBtn);
        msLoginBtn.getStyleClass().add("ms-login-btn");
        msLoginBtn.setMaxWidth(300);
        msLoginBtn.setTranslateY(235d);
        msLoginBtn.setGraphic(view);
        msLoginBtn.setOnMouseClicked(e -> this.authenticateMS());

        this.layout.getChildren().addAll(loginWithLabel, msLoginBtn);

        // Show Version
        Label versionLabel = new Label(MinecraftInfos.LAUNCHER_VERSION);
        setCanTakeAllSize(versionLabel);
        setCenterV(versionLabel);
        setCenterH(versionLabel);
        versionLabel.setFont(Font.font(versionLabel.getFont().getFamily(), FontWeight.BOLD, FontPosture.REGULAR, 14d));
        versionLabel.getStyleClass().add("version-label");
        versionLabel.setTranslateY(320d);
        versionLabel.setTranslateX(630d);
        versionLabel.setMaxWidth(100d);
        this.layout.getChildren().add(versionLabel);
    }

    public void authenticateMS() {
        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
        authenticator.loginWithAsyncWebview().whenComplete((response, error) -> {
            if (error != null) {
                Launcher.getInstance().getLogger().err(error.toString());
                Platform.runLater(()-> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setContentText(error.getMessage());
                    alert.show();
                });

                return;
            }

            saver.set("msAccessToken", response.getAccessToken());
            saver.set("msRefreshToken", response.getRefreshToken());
            saver.save();
            Launcher.getInstance().setAuthInfos(new AuthInfos(
                    response.getProfile().getName(),
                    response.getAccessToken(),
                    response.getProfile().getId(),
                    response.getXuid(),
                    response.getClientId()
            ));

            Launcher.getInstance().getLogger().info("Hello " + response.getProfile().getName());

            Platform.runLater(() -> panelManager.showPanel(new App()));
        });
    }
}
