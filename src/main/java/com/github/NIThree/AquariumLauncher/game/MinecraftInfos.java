package com.github.NIThree.AquariumLauncher.game;

import fr.flowarg.flowupdater.download.json.CurseModPackInfo;
import fr.flowarg.flowupdater.versions.ForgeVersionBuilder;
import fr.flowarg.flowupdater.versions.ForgeVersionType;
import fr.flowarg.openlauncherlib.NewForgeVersionDiscriminator;
import fr.theshark34.openlauncherlib.minecraft.GameType;
public class MinecraftInfos {
    public static  final String SERVER_NAME = "Aquarium";

    public static final String GAME_VERSION = "1.20.1";
    public static final ForgeVersionType FORGE_VERSION_TYPE = ForgeVersionType.NEW;
    public static final String FORGE_VERSION = "1.20.1-47.1.46";
    public static final String OPTIFINE_VERSION = "1.16.5_HD_U_G8";

    public static final GameType OLL_GAME_TYPE = GameType.V1_13_HIGHER_FORGE;
    public static final NewForgeVersionDiscriminator OLL_FORGE_DISCRIMINATOR = new NewForgeVersionDiscriminator(
            "47.1.46",
            MinecraftInfos.GAME_VERSION,
            "20230612.114412"
    );

    public static final String CURSE_MODS_LIST_URL = "http://aquarium.ndemare.fr:2010/files/mods_list.json";
    public static final String MODS_LIST_URL = "http://aquarium.ndemare.fr:2010/files/mods_list.json";
}
