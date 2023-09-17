package glowredman.amazingtrophies.api;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Loader;

// TODO javadoc
@ParametersAreNonnullByDefault
public class AmazingTrophiesAPI {

    static final Logger LOGGER = LogManager.getLogger("Amazing Trophies API");

    // TODO javadoc
    public static final Path CONFIG_DIR = getConfigDir();

    private static final Map<String, AchievementProperties> ACHIEVEMENTS = new LinkedHashMap<>();
    private static final Map<String, ConditionHandler> ACHIEVEMENT_CONDITION_HANDLERS = new HashMap<>();
    private static final Map<String, ConditionHandler> TROPHY_CONDITION_HANDLERS = new HashMap<>();

    // TODO add javadoc
    public static ItemStack getTrophyWithNBT(String trophyID, EntityPlayer player) {
        // TODO impl
        return new ItemStack(Blocks.fire);
    }

    /**
     * Gives the trophy to the player. If the player's inventory is full, it is dropped at the player's position.
     * 
     * @param trophyID The trophy's ID
     * @param player   The player to receive the trophy
     * @author glowredman
     */
    public static void awardTrophy(String trophyID, EntityPlayer player) {
        ItemStack trophy = getTrophyWithNBT(trophyID, player);
        if (player.inventory.addItemStackToInventory(trophy)) {
            // The trophy has been successfully added to the player's inventory
            return;
        }
        // Drop the trophy if the player's inventory is full
        World world = player.worldObj;
        world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, trophy));
    }

    /**
     * Register a {@link ConditionHandler} to be used for custom achievements and trophies.
     * 
     * @param handlerSupplier A {@link Supplier} which provides a new instance of the ConditionHandler to register each
     *                        time {@link Supplier#get()} is called. All instances provided by this Supplier must have
     *                        the same functionality and {@link ConditionHandler#getID() id}.
     * @author glowredman
     */
    public static void registerConditionHandler(Supplier<ConditionHandler> handlerSupplier) {
        ConditionHandler achievementHandler = handlerSupplier.get();
        String id = achievementHandler.getID();
        if (ACHIEVEMENT_CONDITION_HANDLERS.containsKey(id)) {
            LOGGER.error(
                "Condition Handler with id {} already exists! {} will not be registered.",
                id,
                achievementHandler.getClass()
                    .getName());
            return;
        }

        achievementHandler.setListener((achievementID, player) -> {
            StatBase stat = StatList.func_151177_a(achievementID);
            if (stat == null || !stat.isAchievement()) {
                return;
            }
            player.triggerAchievement(stat);
        });
        ACHIEVEMENT_CONDITION_HANDLERS.put(id, achievementHandler);

        ConditionHandler trophyHandler = handlerSupplier.get();
        trophyHandler.setListener(AmazingTrophiesAPI::awardTrophy);
        TROPHY_CONDITION_HANDLERS.put(id, trophyHandler);
    }

    // TODO javadoc
    public static ConditionHandler getAchievementConditionHandler(String id) {
        return ACHIEVEMENT_CONDITION_HANDLERS.get(id);
    }

    // TODO javadoc
    public static ConditionHandler getTrophyConditionHandler(String id) {
        return TROPHY_CONDITION_HANDLERS.get(id);
    }

    // TODO javadoc
    public static void addAchievement(String id, AchievementProperties props) {
        props.id = id;
        ACHIEVEMENTS.put(id, props);
    }

    // TODO javadoc
    public static void registerAchievements() {
        ACHIEVEMENTS.values()
            .forEach(AchievementProperties::register);
    }

    private static Path getConfigDir() {
        try {
            Path path = Loader.instance()
                .getConfigDir()
                .toPath()
                .resolve("amazingtrophies");
            Files.createDirectories(path);
            return path;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create config directory!", e);
        }
    }
}
