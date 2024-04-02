package com.github.NIThree.AquariumLauncher.game;

import com.github.NIThree.AquariumLauncher.Launcher;
import fr.flowarg.flowupdater.download.json.CurseFileInfo;
import fr.flowarg.flowupdater.versions.ForgeVersionType;

import java.util.ArrayList;
import java.util.List;

public class MinecraftInfos {
    public static final String GAME_VERSION = "1.20.1";
    public static final ForgeVersionType FORGE_VERSION_TYPE = ForgeVersionType.NEW;
    public static final String FORGE_VERSION = "1.20.1-47.1.46";

    public static final String CURSE_MODS_LIST_URL = "http://aquarium.ndemare.fr/mods_list.json";
    public static final String MODS_LIST_URL = "http://aquarium.ndemare.fr/mods_list.json";
    public static final String SERVER_URL = "46.105.41.246:20006";
    public static final String SERVER_PORT = "20006";
    public static final String SERVER_FILE = Launcher.getInstance().getLauncherDir() + "/server.dat";

    public static final String LAUNCHER_VERSION = "v0.0.1";
}
