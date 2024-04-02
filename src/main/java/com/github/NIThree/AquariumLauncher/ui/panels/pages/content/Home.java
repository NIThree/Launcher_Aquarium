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
import fr.flowarg.flowupdater.download.json.OptiFineInfo;
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

import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Home extends ContentPanel {
    private final Saver saver = Launcher.getInstance().getSaver();
    GridPane boxPane = new GridPane();
    GridPane eventContent = new GridPane();
    ProgressBar progressBar = new ProgressBar();
    Label stepLabel = new Label();
    Label fileLabel = new Label();
    boolean isDownloading = false;

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
            final VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder()
                    .withName(MinecraftInfos.GAME_VERSION)
                    .build();

            List<CurseFileInfo> curseMods = CurseFileInfo.getFilesFromJson(MinecraftInfos.CURSE_MODS_LIST_URL);
            /*List<CurseFileInfo> curseMods = new ArrayList<>();
            // Alex's Mobs - alexsmobs-1.22.5
            curseMods.add(new CurseFileInfo(426558, 4618074));
            // Aquaculture 2 - Aquaculture-1.20.1-2.5.0.jar
            curseMods.add(new CurseFileInfo(60028, 4608454));
            // Architectury API (Fabric/Forge/NeoForge) - [Forge 1.20(.1)] v9.1.12
            curseMods.add(new CurseFileInfo(419699, 4663010));
            // Artifacts - artifacts-1.20.1-7.0.2.jar
            curseMods.add(new CurseFileInfo(312353, 4647528));
            // Bagus Lib - Baguslib-1.20.1-3.4.0.jar
            curseMods.add(new CurseFileInfo(866533, 4680060));
            // Biomes O' Plenty - Biomes O' Plenty 1.20.1-18.0.0.595
            curseMods.add(new CurseFileInfo(220318, 4683058));
            // Citadel - citadel-2.4.2-1.20.1
            curseMods.add(new CurseFileInfo(331936, 4613231));
            // Cloth Config API (Fabric/Forge/NeoForge) - [Forge 1.20(.1)] v11.1.106
            curseMods.add(new CurseFileInfo(348521, 4633444));
            // Corn Delight[Forge] - corn_delight-1.0.3-1.20.1.jar
            curseMods.add(new CurseFileInfo(577805, 4640390));
            // Corpse - [1.20.1] Corpse 1.20.1-1.0.5
            curseMods.add(new CurseFileInfo(316582, 4678972));
            // Create - Create 1.20.1 v0.5.1e
            curseMods.add(new CurseFileInfo(328085, 4762216));
            // Cristel Lib - cristellib-forge-1.1.0.jar
            curseMods.add(new CurseFileInfo(856996, 4582743));
            // Curios API (Forge/NeoForge) - curios-forge-5.2.0-beta.3+1.20.1.jar
            curseMods.add(new CurseFileInfo(309927, 4583413));
            // [Let's Do] API - API - 1.2.6 - FORGE - 1.20.x
            curseMods.add(new CurseFileInfo(864599, 4678895));
            // Doggy Talents Next - DoggyTalentsNext-1.20.1-1.16.2.jar
            curseMods.add(new CurseFileInfo(694492, 4748385));
            // When Dungeons Arise - Forge! - DungeonsArise-1.20.1-2.1.56.1-beta.jar
            curseMods.add(new CurseFileInfo(442508, 4607317));
            // Farmer's Delight - Farmer's Delight 1.2.3 - 1.20.1
            curseMods.add(new CurseFileInfo(398521, 4679319));
            // Hunter'sReturn - hunterillager-1.20.1-10.1.1.jar
            curseMods.add(new CurseFileInfo(318857, 4642810));
            // Immersive Paintings [Fabric/Forge] - [Forge 1.20.1] Immersive Paintings - 0.6.3
            curseMods.add(new CurseFileInfo(639584, 4679310));
            // Incendium - Incendium 1.20.1 v5.3.1
            curseMods.add(new CurseFileInfo(591388, 4655668));
            // JourneyMap - journeymap-1.20.1-5.9.12-forge
            curseMods.add(new CurseFileInfo(32274, 4655325));
            // L_Ender 's Cataclysm - L_Ender's Cataclysm 1.29 - 1.20.1
            curseMods.add(new CurseFileInfo(551586, 4687114));
            // [Let's Do] Vinery - Vinery - 1.4.3 - FORGE - 1.20.1
            curseMods.add(new CurseFileInfo(704465, 4680504));
            // Moonlight Lib - moonlight-1.20-2.5.15-forge.jar
            curseMods.add(new CurseFileInfo(499980, 4618451));
            // Mysterious Mountain Lib - mysterious_mountain_lib-1.2.7-1.20.jar
            curseMods.add(new CurseFileInfo(368098, 4621552));
            // Nullscape - Nullscape 1.20.1 v1.2.1
            curseMods.add(new CurseFileInfo(570354, 4630978));
            // Sophisticated Backpacks - sophisticatedbackpacks-1.20.1-3.18.56.890.jar
            curseMods.add(new CurseFileInfo(422301, 4637294));
            // Sophisticated Core - sophisticatedcore-1.20.1-0.5.83.395.jar
            curseMods.add(new CurseFileInfo(618298, 4668695));
            // Structory - Structory 1.20.1 v1.3.2
            curseMods.add(new CurseFileInfo(636540, 4630983));
            // TerraBlender (Forge) - TerraBlender Forge 1.20.1-3.0.0.165
            curseMods.add(new CurseFileInfo(563928, 4608122));
            // Terralith - Terralith 1.20.1 v2.4.3
            curseMods.add(new CurseFileInfo(513688, 4657064));
            // Towns and Towers - (1.19.3/4+1.20.x) Towns and Towers v1.11 (Fabric/Forge/Quilt)
            curseMods.add(new CurseFileInfo(626761, 4593117));
            // Underground Villages - UndergroundVillages-1.20.1-2.0.0.jar
            curseMods.add(new CurseFileInfo(606989, 4614916));
            // YUNG's API (Forge) - [1.20] YUNG's API v4.0.1 (Forge)
            curseMods.add(new CurseFileInfo(421850, 4741396));
            // YUNG's Better Desert Temples (Forge) - [1.20] YUNG's Better Desert Temples v3.0.1 (Forge)
            curseMods.add(new CurseFileInfo(631016, 4741450));
            // YUNG's Better Dungeons (Forge) - [1.20] YUNG's Better Dungeons v4.0.1 (Forge)
            curseMods.add(new CurseFileInfo(510089, 4741446));
            // YUNG's Better Mineshafts (Forge) - [1.20] YUNG's Better Mineshafts v4.0.1 (Forge)
            curseMods.add(new CurseFileInfo(389665, 4741419));
            // YUNG's Better Ocean Monuments (Forge) - [1.20] YUNG's Better Ocean Monuments v3.0.1 (Forge)
            curseMods.add(new CurseFileInfo(689238, 4741465));
            // YUNG's Better Strongholds (Forge) - [1.20] YUNG's Better Strongholds v4.0.1 (Forge)
            curseMods.add(new CurseFileInfo(465575, 4741439));
            // YUNG's Better Witch Huts (Forge) - [1.20] YUNG's Better Witch Huts v3.0.1 (Forge)
            curseMods.add(new CurseFileInfo(631401, 4741480));

            //
            //curseMods.add(new CurseFileInfo(419699, 4663010));*/

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
}
