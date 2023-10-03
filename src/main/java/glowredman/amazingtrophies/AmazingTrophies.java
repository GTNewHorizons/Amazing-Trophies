package glowredman.amazingtrophies;

import java.nio.file.Files;
import java.nio.file.Path;

import net.minecraft.block.Block;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import glowredman.amazingtrophies.api.AchievementProperties;
import glowredman.amazingtrophies.api.AmazingTrophiesAPI;
import glowredman.amazingtrophies.api.ConditionHandler;
import glowredman.amazingtrophies.api.Reference;
import glowredman.amazingtrophies.condition.AchievementConditionHandler;
import glowredman.amazingtrophies.trophy.BlockTrophy;
import glowredman.amazingtrophies.trophy.ItemBlockTrophy;
import glowredman.amazingtrophies.trophy.TileEntityTrophy;

@Mod(
    acceptedMinecraftVersions = "[1.7.10]",
    // dependencies = "required-after:gtnhlib@[0.0.10)",
    modid = AmazingTrophies.MODID,
    name = AmazingTrophies.MODNAME,
    version = AmazingTrophies.VERSION)
public class AmazingTrophies {

    public static final String MODID = "amazingtrophies";
    public static final String MODNAME = "Amazing Trophies";
    public static final String VERSION = Reference.VERSION;
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);
    public static final Path CONFIG_DIR = getConfigDir();

    @EventHandler
    public static void construct(FMLConstructionEvent event) {
        if (event.getSide()
            .isClient()) {
            ClientHandler.setupAssetHandler();
        }
    }

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        AmazingTrophiesAPI.registerConditionHandler(AchievementConditionHandler::new);

        Block blockTrophy = new BlockTrophy();
        AmazingTrophiesAPI.setTrophyBlock(blockTrophy);
        GameRegistry.registerBlock(blockTrophy, ItemBlockTrophy.class, "trophy");
        GameRegistry.registerTileEntity(TileEntityTrophy.class, MODID + ".trophy");

        if (event.getSide()
            .isClient()) {
            ClientHandler.registerTrophyModelHandlers();
            ClientHandler.setupTrophyRenderer();
        }

        ConfigHandler.parseOrCreate("achievements.json", AchievementHandler::parseAchievement);
        ConfigHandler.parseOrCreate("trophies.json", TrophyHandler::parseTrophy);
    }

    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
        AchievementHandler.registerMissingPages();
        AmazingTrophiesAPI.getAchievements()
            .forEach(AchievementProperties::register);
        AmazingTrophiesAPI.getAchievementConditionHandlers()
            .forEach(ConditionHandler::registerAsEventHandler);
        AmazingTrophiesAPI.getTrophyConditionHandlers()
            .forEach(ConditionHandler::registerAsEventHandler);
    }

    private static Path getConfigDir() {
        try {
            Path path = Loader.instance()
                .getConfigDir()
                .toPath()
                .resolve(MODID);
            Files.createDirectories(path);
            return path;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create config directory!", e);
        }
    }

}
