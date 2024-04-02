package com.github.NIThree.AquariumLauncher.ui.panels.pages;

import com.github.NIThree.AquariumLauncher.Launcher;
import com.github.NIThree.AquariumLauncher.game.MinecraftInfos;
import com.github.NIThree.AquariumLauncher.ui.PanelManager;
import com.github.NIThree.AquariumLauncher.ui.panel.Panel;
import com.github.NIThree.AquariumLauncher.ui.panels.pages.content.ContentPanel;
import com.github.NIThree.AquariumLauncher.ui.panels.pages.content.Home;
import com.github.NIThree.AquariumLauncher.ui.panels.pages.content.Settings;
import fr.flowarg.materialdesignfontfx.MaterialDesignIcon;
import fr.flowarg.materialdesignfontfx.MaterialDesignIconView;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class App extends Panel {
    GridPane sidemenu = new GridPane();
    GridPane navContent = new GridPane();

    Node activeLink = null;
    ContentPanel currentPage = null;

    Button homeBtn, settingsBtn, logoutBtn;

    Saver saver = Launcher.getInstance().getSaver();

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getStylesheetPath() {
        return "css/app.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        // Background
        this.layout.getStyleClass().add("app-layout");
        setCanTakeAllSize(this.layout);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHalignment(HPos.LEFT);
        columnConstraints.setMinWidth(300);
        columnConstraints.setMaxWidth(300);
        this.layout.getColumnConstraints().addAll(columnConstraints, new ColumnConstraints());

        // Show Version
        Label versionLabel = new Label(MinecraftInfos.LAUNCHER_VERSION);
        setCanTakeAllSize(versionLabel);
        setCenterV(versionLabel);
        setCenterH(versionLabel);
        versionLabel.setFont(Font.font(versionLabel.getFont().getFamily(), FontWeight.BOLD, FontPosture.REGULAR, 14d));
        versionLabel.getStyleClass().add("version-label");
        versionLabel.setTranslateY(260d);
        versionLabel.setTranslateX(480d);
        versionLabel.setMaxWidth(100d);
        this.layout.add(versionLabel, 1, 0);

        // Nav content
        this.layout.add(navContent, 1, 0);
        setLeft(navContent);
        setCenterH(navContent);
        setCenterV(navContent);
        setCanTakeAllSize(navContent);

        // Side menu
        this.layout.add(sidemenu, 0, 0);
        sidemenu.getStyleClass().add("sidemenu");
        setLeft(sidemenu);
        setCenterH(sidemenu);
        setCenterV(sidemenu);
        setCanTakeAllSize(sidemenu);

        // Pseudo + avatar
        GridPane userPane = new GridPane();
        setCanTakeAllWidth(userPane);
        userPane.setMaxHeight(80);
        userPane.setMinWidth(80);
        userPane.getStyleClass().add("user-pane");

        Label usernameLabel = new Label(Launcher.getInstance().getAuthInfos().getUsername());
        usernameLabel.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 25f));
        setCanTakeAllSize(usernameLabel);
        setCenterV(usernameLabel);
        setLeft(usernameLabel);
        usernameLabel.getStyleClass().add("username-label");
        GridPane.setHalignment(usernameLabel,HPos.CENTER);
        setCanTakeAllWidth(usernameLabel);
        sidemenu.add(usernameLabel,0,0);

        String avatarUrl = "https://minotar.net/armor/body/" + (Launcher.getInstance().getAuthInfos().getUuid() + ".png");
        ImageView avatarView = new ImageView();
        Image avatarImg = new Image(avatarUrl);
        avatarView.setImage(avatarImg);
        avatarView.setPreserveRatio(true);
        avatarView.setFitHeight(300d);
        GridPane.setHalignment(avatarView,HPos.CENTER);
        setCanTakeAllSize(avatarView);
        sidemenu.add(avatarView,0,1);

        // Navigation
        homeBtn = new Button("Accueil");
        homeBtn.getStyleClass().add("sidemenu-nav-btn");
        homeBtn.setGraphic(new MaterialDesignIconView<>(MaterialDesignIcon.H.HOME));
        GridPane.setHalignment(homeBtn,HPos.CENTER);
        setCanTakeAllSize(homeBtn);
        homeBtn.setOnMouseClicked(e -> setPage(new Home(), homeBtn));
        userPane.add(homeBtn,0,0);

        settingsBtn = new Button("Paramètres");
        settingsBtn.getStyleClass().add("sidemenu-nav-btn");
        settingsBtn.setGraphic(new MaterialDesignIconView<>(MaterialDesignIcon.C.COG));
        GridPane.setHalignment(settingsBtn,HPos.CENTER);
        setCanTakeAllSize(settingsBtn);
        settingsBtn.setOnMouseClicked(e -> setPage(new Settings(), settingsBtn));
        userPane.add(settingsBtn,0,1);


        logoutBtn = new Button("Déconnexion");
        logoutBtn.getStyleClass().add("sidemenu-nav-btn");
        logoutBtn.setGraphic(new MaterialDesignIconView<>(MaterialDesignIcon.L.LOGOUT));
        GridPane.setHalignment(logoutBtn,HPos.CENTER);
        setCanTakeAllSize(logoutBtn);
        logoutBtn.setOnMouseClicked(e -> {
            if (currentPage instanceof Home && ((Home) currentPage).isDownloading()) {
                return;
            }
            saver.remove("accessToken");
            saver.remove("clientToken");
            saver.remove("msAccessToken");
            saver.remove("msRefreshToken");
            saver.save();
            Launcher.getInstance().setAuthInfos(null);
            this.panelManager.showPanel(new Login());
        });
        userPane.add(logoutBtn,0,3);

        sidemenu.add(userPane,0,2);

    }

    @Override
    public void onShow() {
        super.onShow();
        setPage(new Home(), homeBtn);
    }

    public void setPage(ContentPanel panel, Node navButton) {
        if (currentPage instanceof Home && ((Home) currentPage).isDownloading()) {
            return;
        }
        if (activeLink != null)
            activeLink.getStyleClass().remove("active");
        activeLink = navButton;
        activeLink.getStyleClass().add("active");

        this.navContent.getChildren().clear();
        if (panel != null) {
            this.navContent.getChildren().add(panel.getLayout());
            currentPage = panel;
            if (panel.getStylesheetPath() != null) {
                this.panelManager.getStage().getScene().getStylesheets().clear();
                this.panelManager.getStage().getScene().getStylesheets().addAll(
                        this.getStylesheetPath(),
                        panel.getStylesheetPath()
                );
            }
            panel.init(this.panelManager);
            panel.onShow();
        }
    }
}
