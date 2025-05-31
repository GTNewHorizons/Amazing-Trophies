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
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import glowredman.amazingtrophies.api.AchievementProperties;
import glowredman.amazingtrophies.api.AmazingTrophiesAPI;
import glowredman.amazingtrophies.api.ConditionHandler;
import glowredman.amazingtrophies.api.Reference;
import glowredman.amazingtrophies.condition.AchievementConditionHandler;
import glowredman.amazingtrophies.condition.AttackConditionHandler;
import glowredman.amazingtrophies.condition.BlockConditionHandler;
import glowredman.amazingtrophies.condition.DeathConditionHandler;
import glowredman.amazingtrophies.condition.EntityInteractConditionHandler;
import glowredman.amazingtrophies.condition.ExplosionConditionHandler;
import glowredman.amazingtrophies.condition.FallConditionHandler;
import glowredman.amazingtrophies.condition.HealConditionHandler;
import glowredman.amazingtrophies.condition.ItemConditionHandler;
import glowredman.amazingtrophies.condition.ItemUseConditionHandler;
import glowredman.amazingtrophies.condition.JoinWorldConditionHandler;
import glowredman.amazingtrophies.condition.KillConditionHandler;
import glowredman.amazingtrophies.condition.OpenContainerConditionHandler;
import glowredman.amazingtrophies.condition.PickupXPConditionHandler;
import glowredman.amazingtrophies.condition.StruckByLightningConditionHandler;
import glowredman.amazingtrophies.condition.ThrowEnderpearlConditionHandler;
import glowredman.amazingtrophies.trophy.BlockTrophy;
import glowredman.amazingtrophies.trophy.ItemBlockTrophy;
import glowredman.amazingtrophies.trophy.TileEntityTrophy;

@Mod(
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:gtnhlib@[0.4.0,);after:angelica@[1.0.0-beta4,)",
    modid = AmazingTrophies.MODID,
    name = AmazingTrophies.MODNAME,
    version = AmazingTrophies.VERSION)
public class AmazingTrophies {

    public static final String MODID = "amazingtrophies";
    public static final String MODNAME = "Amazing Trophies";
    public static final String VERSION = Reference.VERSION;
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);
    public static final Path CONFIG_DIR = getConfigDir();
    public static final boolean enableVBO = !Boolean.getBoolean("amazingtrophies.disableVBO");

    @EventHandler
    public static void construct(FMLConstructionEvent event) {
        if (event.getSide()
            .isClient()) {
            ClientHandler.setupAssetHandler();
        }
    }

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        registerConditionHandlers();

        Block blockTrophy = new BlockTrophy();
        AmazingTrophiesAPI.setTrophyBlock(blockTrophy);
        GameRegistry.registerBlock(blockTrophy, ItemBlockTrophy.class, "trophy");
        GameRegistry.registerTileEntity(TileEntityTrophy.class, MODID + ".trophy");

        if (event.getSide()
            .isClient()) {
            ClientHandler.registerTrophyModelHandlers();
            ClientHandler.setupTrophyRenderer();
        }
    }

    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
        ConfigHandler.parseOrCreate("achievements", AchievementHandler::parseAchievement);
        ConfigHandler.parseOrCreate("trophies", TrophyHandler::parseTrophy);
        AchievementHandler.registerMissingPages();
        AmazingTrophiesAPI.getAchievements()
            .forEach(AchievementProperties::register);
        AmazingTrophiesAPI.getAchievementConditionHandlers()
            .forEach(ConditionHandler::registerAsEventHandler);
        AmazingTrophiesAPI.getTrophyConditionHandlers()
            .forEach(ConditionHandler::registerAsEventHandler);
    }

    @EventHandler
    public static void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandTrophy());
    }

    private static void registerConditionHandlers() {
        AmazingTrophiesAPI.registerConditionHandler(AchievementConditionHandler::new);
        AmazingTrophiesAPI.registerConditionHandler(AttackConditionHandler.Entity::new);
        AmazingTrophiesAPI.registerConditionHandler(AttackConditionHandler.Player::new);
        AmazingTrophiesAPI.registerConditionHandler(BlockConditionHandler.Break::new);
        AmazingTrophiesAPI.registerConditionHandler(BlockConditionHandler.Place::new);
        AmazingTrophiesAPI.registerConditionHandler(DeathConditionHandler::new);
        AmazingTrophiesAPI.registerConditionHandler(ExplosionConditionHandler::new);
        AmazingTrophiesAPI.registerConditionHandler(EntityInteractConditionHandler::new);
        AmazingTrophiesAPI.registerConditionHandler(FallConditionHandler::new);
        AmazingTrophiesAPI.registerConditionHandler(HealConditionHandler::new);
        AmazingTrophiesAPI.registerConditionHandler(ItemConditionHandler.Craft::new);
        AmazingTrophiesAPI.registerConditionHandler(ItemConditionHandler.Drop::new);
        AmazingTrophiesAPI.registerConditionHandler(ItemConditionHandler.Pickup::new);
        AmazingTrophiesAPI.registerConditionHandler(ItemConditionHandler.Smelt::new);
        AmazingTrophiesAPI.registerConditionHandler(ItemUseConditionHandler.Finish::new);
        AmazingTrophiesAPI.registerConditionHandler(ItemUseConditionHandler.Start::new);
        AmazingTrophiesAPI.registerConditionHandler(ItemUseConditionHandler.Stop::new);
        AmazingTrophiesAPI.registerConditionHandler(JoinWorldConditionHandler::new);
        AmazingTrophiesAPI.registerConditionHandler(KillConditionHandler::new);
        AmazingTrophiesAPI.registerConditionHandler(OpenContainerConditionHandler::new);
        AmazingTrophiesAPI.registerConditionHandler(PickupXPConditionHandler::new);
        AmazingTrophiesAPI.registerConditionHandler(StruckByLightningConditionHandler::new);
        AmazingTrophiesAPI.registerConditionHandler(ThrowEnderpearlConditionHandler::new);
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
