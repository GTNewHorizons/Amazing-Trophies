package glowredman.amazingtrophies;

import net.minecraft.block.Block;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import glowredman.amazingtrophies.api.AmazingTrophiesAPI;
import glowredman.amazingtrophies.api.Reference;
import glowredman.amazingtrophies.condition.AchievementConditionHandler;
import glowredman.amazingtrophies.trophy.BlockTrophy;
import glowredman.amazingtrophies.trophy.ItemBlockTrophy;
import glowredman.amazingtrophies.trophy.TileTrophy;

@Mod(
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:gtnhlib@[0.0.10)",
    modid = AmazingTrophies.MODID,
    name = AmazingTrophies.MODNAME,
    version = AmazingTrophies.VERSION)
public class AmazingTrophies {

    public static final String MODID = "amazingtrophies";
    public static final String MODNAME = "Amazing Trophies";
    public static final String VERSION = Reference.VERSION;
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        AmazingTrophiesAPI.registerConditionHandler(AchievementConditionHandler::new);

        Block blockTrophy = new BlockTrophy().setBlockName(MODID + ".trophy");
        AmazingTrophiesAPI.setTrophyBlock(blockTrophy);
        GameRegistry.registerBlock(blockTrophy, ItemBlockTrophy.class, "trophy");
        GameRegistry.registerTileEntity(TileTrophy.class, MODID + ".trophy");

        ConfigHandler.parseOrCreate("achievements.json", AchievementHandler::parseAchievement);
        // TODO trophy parser
    }

    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
        AchievementHandler.registerMissingPages();
        AmazingTrophiesAPI.registerAchievements();

        if (FMLCommonHandler.instance()
            .getSide()
            .isClient()) {
            new LangHandler(AmazingTrophiesAPI.CONFIG_DIR);
        }
    }

}
