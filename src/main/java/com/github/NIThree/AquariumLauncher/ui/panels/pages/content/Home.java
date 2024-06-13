package com.github.NIThree.AquariumLauncher.ui.panels.pages.content;

import com.github.NIThree.AquariumLauncher.Launcher;
import com.github.NIThree.AquariumLauncher.game.MinecraftInfos;
import com.github.NIThree.AquariumLauncher.ui.PanelManager;
import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.DownloadList;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.Step;
import fr.flowarg.flowupdater.download.json.CurseFileInfo;
import fr.flowarg.flowupdater.download.json.Mod;
import fr.flowarg.flowupdater.utils.ModFileDeleter;
import fr.flowarg.flowupdater.versions.AbstractForgeVersion;
import fr.flowarg.flowupdater.versions.ForgeVersionBuilder;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import fr.flowarg.materialdesignfontfx.MaterialDesignIcon;
import fr.flowarg.materialdesignfontfx.MaterialDesignIconView;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.nio.file.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

import static com.github.NIThree.AquariumLauncher.AutoUpdater.*;

public class Home extends ContentPanel {
    private final Saver saver = Launcher.getInstance().getSaver();
    GridPane boxPane = new GridPane();
    GridPane eventContent = new GridPane();
    ProgressBar progressBar = new ProgressBar();
    Label stepLabel = new Label();
    Label fileLabel = new Label();
    boolean isDownloading = false;
    String fileName = "servers.dat";
    String appDataDirectory = System.getenv("APPDATA"); // Dossier %appdata%
    String destinationDirectory = Paths.get(appDataDirectory, ".Aquarium").toString(); // Sous-dossier .Aquarium dans %appdata%


    @Override
    public String getName() {
        return "home";
    }

    @Override
    public String getStylesheetPath() {
        return "css/content/home.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.CENTER);
        rowConstraints.setMinHeight(75);
        rowConstraints.setMaxHeight(75);
        this.layout.getRowConstraints().addAll(new RowConstraints(), rowConstraints);
        boxPane.getStyleClass().add("box-pane");
        setCanTakeAllSize(boxPane);
        boxPane.setPadding(new Insets(20));
        this.layout.add(boxPane, 0, 1);
        this.layout.getStyleClass().add("home-layout");

        progressBar.getStyleClass().add("download-progress");
        stepLabel.getStyleClass().add("download-status");
        fileLabel.getStyleClass().add("download-status");

        progressBar.setTranslateY(-15);
        setCenterH(progressBar);
        setCanTakeAllWidth(progressBar);

        stepLabel.setTranslateY(5);
        setCenterH(stepLabel);
        setCanTakeAllSize(stepLabel);

        fileLabel.setTranslateY(20);
        setCenterH(fileLabel);
        setCanTakeAllSize(fileLabel);

        this.layout.add(eventContent, 0, 0);
        setCenterH(eventContent);
        setCenterV(eventContent);
        setCanTakeAllSize(eventContent);

        if (MinecraftInfos.Update) {
            // Navigation
            Button UpdateBtn = new Button("Nouvelle version Disponible");
            UpdateBtn.getStyleClass().add("update-btn");
            UpdateBtn.setTranslateY(270d);
            UpdateBtn.setTranslateX(735d);
            UpdateBtn.setOnMouseClicked(e -> {
                try {
                    downloadUpdate();
                    runUpdaterScript();
                } catch (IOException ex) {
                    UpdateBtn.setText("Erreur Update");
                    logger.info("Erreur Update");
                    Launcher.getInstance().getLogger().printStackTrace(ex);
                }


            });
            this.layout.add(UpdateBtn, 0, 0);
        }

        this.showPlayButton();
    }

    private void showPlayButton() {
        boxPane.getChildren().clear();
        Button playBtn = new Button("Jouer");
        final var playIcon = new MaterialDesignIconView<>(MaterialDesignIcon.G.GAMEPAD);
        playIcon.getStyleClass().add("play-icon");
        setCanTakeAllSize(playBtn);
        setCenterH(playBtn);
        setCenterV(playBtn);
        playBtn.getStyleClass().add("play-btn");
        playBtn.setGraphic(playIcon);
        playBtn.setOnMouseClicked(e -> this.play());
        boxPane.getChildren().add(playBtn);
    }

    private void play() {
        isDownloading = true;
        boxPane.getChildren().clear();
        setProgress(0, 0);
        boxPane.getChildren().addAll(progressBar, stepLabel, fileLabel);

        new Thread(this::update).start();
    }

    public void update() {
        IProgressCallback callback = new IProgressCallback() {
            private final DecimalFormat decimalFormat = new DecimalFormat("#.#");
            private String stepTxt = "";
            private String percentTxt = "0.0%";

            @Override
            public void step(Step step) {
                Platform.runLater(() -> {
                    stepTxt = StepInfo.valueOf(step.name()).getDetails();
                    setStatus(String.format("%s (%s)", stepTxt, percentTxt));
                });
            }

            @Override
            public void update(DownloadList.DownloadInfo info) {
                Platform.runLater(() -> {
                    percentTxt = decimalFormat.format(info.getDownloadedBytes() * 100.d / info.getTotalToDownloadBytes()) + "%";
                    setStatus(String.format("%s (%s)", stepTxt, percentTxt));
                    setProgress(info.getDownloadedBytes(), info.getTotalToDownloadBytes());
                });
            }

            @Override
            public void onFileDownloaded(Path path) {
                Platform.runLater(() -> {
                    String p = path.toString();
                    fileLabel.setText("..." + p.replace(Launcher.getInstance().getLauncherDir().toFile().getAbsolutePath(), ""));
                });
            }
        };

        try {
            try {
                downloadFile(MinecraftInfos.SERVER_FILE, destinationDirectory, fileName);
                System.out.println("Téléchargement terminé avec succès.");
            } catch (IOException e) {
                System.err.println("Erreur lors du téléchargement : " + e.getMessage());
            }



            final VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder()
                    .withName(MinecraftInfos.GAME_VERSION)
                    .build();

            List<CurseFileInfo> curseMods = CurseFileInfo.getFilesFromJson(MinecraftInfos.CURSE_MODS_LIST_URL);
            //curseMods.add(new CurseFileInfo(256256, 4840340));

            //curseMods.add(new CurseFileInfo(238222, 5101366));

            List<Mod> mods = Mod.getModsFromJson(MinecraftInfos.MODS_LIST_URL);

            /*boolean setOptifine = false;

            if (saver.get("optiFine") != null) {
                setOptifine = Boolean.parseBoolean((saver.get("optiFine")));
            }*/

            final AbstractForgeVersion forge ;

            //if (setOptifine) {
                forge = new ForgeVersionBuilder(MinecraftInfos.FORGE_VERSION_TYPE)
                        .withForgeVersion(MinecraftInfos.FORGE_VERSION)
                        .withCurseMods(curseMods)
                        .withMods(mods)
                        .withFileDeleter(new ModFileDeleter(true))
                        .build();
            /*} else {
                forge = new ForgeVersionBuilder(MinecraftInfos.FORGE_VERSION_TYPE)
                        .withForgeVersion(MinecraftInfos.FORGE_VERSION)
                        .withCurseMods(curseMods)
                        //.withMods(mods)
                        .withFileDeleter(new ModFileDeleter(true))
                        .build();

            }*/

            final FlowUpdater updater = new FlowUpdater.FlowUpdaterBuilder()
                    .withVanillaVersion(vanillaVersion)
                    .withModLoaderVersion(forge)
                    .withLogger(Launcher.getInstance().getLogger())
                    .withProgressCallback(callback)
                    .build();

            updater.update(Launcher.getInstance().getLauncherDir());

            this.startGame(updater.getVanillaVersion().getName());
        } catch (Exception e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
            Platform.runLater(() -> this.panelManager.getStage().show());
        }
    }

    public void startGame(String gameVersion) {
        try {
            NoFramework noFramework = new NoFramework(
                    Launcher.getInstance().getLauncherDir(),
                    Launcher.getInstance().getAuthInfos(),
                    GameFolder.FLOW_UPDATER
            );

            noFramework.getAdditionalArgs().addAll(Arrays.asList("--quickPlayMultiplayer", MinecraftInfos.SERVER_URL));
            noFramework.getAdditionalVmArgs().add(this.getRamArgsFromSaver());


            Process p = noFramework.launch(gameVersion, MinecraftInfos.FORGE_VERSION.split("-")[1], NoFramework.ModLoader.FORGE);

            Platform.runLater(() -> {
                try {
                    p.waitFor();
                    Platform.exit();
                } catch (InterruptedException e) {
                    Launcher.getInstance().getLogger().printStackTrace(e);
                }
            });
        } catch (Exception e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
        }
    }

    public String getRamArgsFromSaver() {
        int val = 1024;
        try {
            if (saver.get("maxRam") != null) {
                val = Integer.parseInt(saver.get("maxRam"));
            } else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException error) {
            saver.set("maxRam", String.valueOf(val));
            saver.save();
        }

        return "-Xms" + val + "M";
    }

    public void setStatus(String status) {
        this.stepLabel.setText(status);
    }

    public void setProgress(double current, double max) {
        this.progressBar.setProgress(current / max);
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public enum StepInfo {
        READ("Lecture du fichier json..."),
        DL_LIBS("Téléchargement des libraries..."),
        DL_ASSETS("Téléchargement des ressources..."),
        EXTRACT_NATIVES("Extraction des natives..."),
        FORGE("Installation de forge..."),
        FABRIC("Installation de fabric..."),
        MODS("Téléchargement des mods..."),
        EXTERNAL_FILES("Téléchargement des fichier externes..."),
        POST_EXECUTIONS("Exécution post-installation..."),
        MOD_LOADER("Installation du mod loader..."),
        INTEGRATION("Intégration des mods..."),
        END("Fini !");

        final String details;

        StepInfo(String details) {
            this.details = details;
        }

        public String getDetails() {
            return details;
        }
    }

    public static void downloadFile(String fileURL, String destinationDirectory, String fileName) throws IOException {
        URL url = new URL(fileURL);
        Path targetDirectory = Paths.get(destinationDirectory);
        Path targetFile = targetDirectory.resolve(fileName);

        // Crée le dossier de destination s'il n'existe pas
        if (!Files.exists(targetDirectory)) {
            Files.createDirectories(targetDirectory);
        }

        // Télécharge le fichier
        try (InputStream in = url.openStream()) {
            Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
