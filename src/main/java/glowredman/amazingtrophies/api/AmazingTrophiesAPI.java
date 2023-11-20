package glowredman.amazingtrophies.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Amazing Throphies API. This class holds all registries and other references.
 * 
 * @author glowredman
 */
@ParametersAreNonnullByDefault
public class AmazingTrophiesAPI {

    /**
     * The NBT key used to identify the trophie's ID. Can be used for {@link ItemStack ItemStacks} and {@link TileEntity
     * TileEntities}.
     */
    public static final String TAGNAME_ID = "trophyID";

    /**
     * The NBT key used to identify the point in time at which the trophy was awarded. Can be used for {@link ItemStack
     * ItemStacks} and {@link TileEntity TileEntities}.
     */
    public static final String TAGNAME_TIME = "time";

    /**
     * The NBT key used to identify the {@link UUID} of the player who got the trophy. Can be used for {@link ItemStack
     * ItemStacks} and {@link TileEntity TileEntities}.
     */
    public static final String TAGNAME_UUID = "uuid";

    /**
     * The NBT key used to identify the name of the player who got the trophy. Can be used for {@link ItemStack
     * ItemStacks} and {@link TileEntity TileEntities}.
     */
    public static final String TAGNAME_NAME = "name";

    static final Logger LOGGER = LogManager.getLogger("Amazing Trophies API");

    private static final Map<String, AchievementProperties> ACHIEVEMENTS = new LinkedHashMap<>();
    private static final Map<String, TrophyProperties> TROPHIES = new LinkedHashMap<>();
    private static final Map<String, ConditionHandler> ACHIEVEMENT_CONDITION_HANDLERS = new HashMap<>();
    private static final Map<String, ConditionHandler> TROPHY_CONDITION_HANDLERS = new HashMap<>();
    private static final Map<String, Supplier<TrophyModelHandler>> TROPHY_MODEL_HANDLER_PROVIDERS = new HashMap<>();
    private static Block blockTrophy;

    /**
     * Gets the {@link AchievementProperties} associated with the given ID. Returns {@code null} if no
     * AchievementProperties has been registered for the given ID.
     */
    @Nullable
    public static AchievementProperties getAchievementProperties(@Nullable String id) {
        return ACHIEVEMENTS.get(id);
    }

    /**
     * Gets a {@link Set} of all custom achievement IDs.
     */
    public static Set<String> getAchievementIDs() {
        return ACHIEVEMENTS.keySet();
    }

    /**
     * Gets a {@link Collection} of all {@link AchievementProperties}.
     */
    public static Collection<AchievementProperties> getAchievements() {
        return ACHIEVEMENTS.values();
    }

    /**
     * Gets the {@link TrophyProperties} associated with the given ID. Returns {@code null} if no TrophyProperties has
     * been registered for the given ID.
     */
    @Nullable
    public static TrophyProperties getTrophyProperties(@Nullable String id) {
        return TROPHIES.get(id);
    }

    /**
     * Gets a {@link Set} of all trophy IDs.
     */
    public static Set<String> getTrophyIDs() {
        return TROPHIES.keySet();
    }

    /**
     * Gets a {@link Collection} of all {@link TrophyProperties}.
     */
    public static Collection<TrophyProperties> getTrophies() {
        return TROPHIES.values();
    }

    /**
     * Gets the AchievementConditionHandler associated with the given ID. Returns {@code null} if no
     * AchievementConditionHandler has been registered for the given ID.
     */
    @Nullable
    public static ConditionHandler getAchievementConditionHandler(@Nullable String id) {
        return ACHIEVEMENT_CONDITION_HANDLERS.get(id);
    }

    /**
     * Gets a {@link Set} of all achievement {@link ConditionHandler} IDs.
     */
    public static Set<String> getAchievementConditionHandlerIDs() {
        return ACHIEVEMENT_CONDITION_HANDLERS.keySet();
    }

    /**
     * Gets a {@link Collection} of all achievement {@link ConditionHandler ConditionHandlers}.
     */
    public static Collection<ConditionHandler> getAchievementConditionHandlers() {
        return ACHIEVEMENT_CONDITION_HANDLERS.values();
    }

    /**
     * Gets the TrophyConditionHandler associated with the given ID. Returns {@code null} if no TrophyConditionHandler
     * has been registered for the given ID.
     */
    @Nullable
    public static ConditionHandler getTrophyConditionHandler(@Nullable String id) {
        return TROPHY_CONDITION_HANDLERS.get(id);
    }

    /**
     * Gets a {@link Set} of all trophy {@link ConditionHandler} IDs.
     */
    public static Set<String> getTrophyConditionHandlerIDs() {
        return TROPHY_CONDITION_HANDLERS.keySet();
    }

    /**
     * Gets a {@link Collection} of all trophy {@link ConditionHandler ConditionHandlers}.
     */
    public static Collection<ConditionHandler> getTrophyConditionHandlers() {
        return TROPHY_CONDITION_HANDLERS.values();
    }

    /**
     * Gets the {@link TrophyModelHandler} {@link Supplier} associated with the given ID. Returns {@code null} if no
     * TrophyModelHandler Supplier has been registered for the given ID.
     */
    @Nullable
    public static Supplier<TrophyModelHandler> getTrophyModelHandlerProvider(@Nullable String id) {
        return TROPHY_MODEL_HANDLER_PROVIDERS.get(id);
    }

    /**
     * Gets a {@link Set} of all {@link TrophyModelHandler} IDs.
     */
    public static Set<String> getTrophyModelHandlerProviderIDs() {
        return TROPHY_MODEL_HANDLER_PROVIDERS.keySet();
    }

    /**
     * Gets a {@link Collection} of all {@link TrophyModelHandler} {@link Supplier Suppliers}.
     */
    public static Collection<Supplier<TrophyModelHandler>> getTrophyModelHandlerProviders() {
        return TROPHY_MODEL_HANDLER_PROVIDERS.values();
    }

    /**
     * The trophy block. May be {@code null} if nothing has been assigned yet.
     */
    @Nullable
    public static Block getTrophyBlock() {
        return blockTrophy;
    }

    /**
     * Creates a new {@link ItemStack} of the given trophy. Unless {@code player} is null, this moment in time will be
     * saved in the stack's NBT.
     * 
     * @param trophyID The ID associated with the {@link TrophyProperties} defining the trophy
     * @param player   The player whose name and {@link UUID} should be saved in the stack's NBT data
     * @see System#currentTimeMillis()
     */
    public static ItemStack getTrophyWithNBT(String trophyID, @Nullable EntityPlayer player) {
        ItemStack stack = new ItemStack(blockTrophy);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString(TAGNAME_ID, trophyID);
        if (player != null) {
            GameProfile gameProfile = player.getGameProfile();
            nbt.setLong(TAGNAME_TIME, System.currentTimeMillis());
            nbt.setString(
                TAGNAME_UUID,
                gameProfile.getId()
                    .toString());
            nbt.setString(TAGNAME_NAME, gameProfile.getName());
        }
        stack.setTagCompound(nbt);
        return stack;
    }

    /**
     * Gives the trophy to the player. If the player's inventory is full, it is dropped at the player's position.
     * 
     * @param trophyID The trophie's ID
     * @param player   The player to receive the trophy
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
     * <p>
     * Set the block to be used for all trophies. If a block has already been assigned, a message is logged and
     * {@code block} is not assigned.
     * </p>
     * <p>
     * <i>Note: The block is not registered automatically to the {@link GameRegistry} by this API!</i>
     * </p>
     * 
     * @param block The block to be used for all trophies.
     * @return {@code true} if {@code block} was assigned successfully, {@code false} otherwise
     */
    public static boolean setTrophyBlock(Block block) {
        if (blockTrophy == null) {
            blockTrophy = block;
            return true;
        }
        LOGGER.error(
            "Could not set trophy block to {}, already set to {}!",
            block.getClass()
                .getName(),
            blockTrophy.getClass()
                .getName());
        return false;
    }

    /**
     * <p>
     * Registers a custom achievement.
     * </p>
     * <p>
     * <i>Note: The achievement is not registered automatically to the {@link StatList} nor to the
     * {@link AchievementList} by this API! {@link AchievementProperties#register()} needs to be called at an
     * appropriate time.</i>
     * </p>
     * 
     * @param id    A unique, non-empty string to identify this custom achievement
     * @param props The properties of the custom achievement
     */
    public static void registerAchievement(String id, AchievementProperties props) {
        if (ACHIEVEMENTS.containsKey(id)) {
            LOGGER.error("Achievement with id {} already exists! {} will not be registered.", id, props);
            return;
        }
        props.setID(id);
        ACHIEVEMENTS.put(id, props);
    }

    /**
     * Registers a trophy.
     * 
     * @param id    A unique, non-empty string to identify this trophy
     * @param props The properties of the trophy
     */
    public static void registerTrophy(String id, TrophyProperties props) {
        if (TROPHIES.containsKey(id)) {
            LOGGER.error("Trophy with id {} already exists! {} will not be registered.", id, props);
            return;
        }
        props.setID(id);
        TROPHIES.put(id, props);
    }

    /**
     * Registers a {@link ConditionHandler} to be used for custom achievements and trophies.
     * 
     * @param handlerSupplier A {@link Supplier} which provides a new instance of the ConditionHandler to register each
     *                        time {@link Supplier#get()} is called. All instances provided by this Supplier must have
     *                        the same functionality and {@link ConditionHandler#getID() id}.
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

        achievementHandler.setOwner("achievements");
        achievementHandler.setListener((achievementID, player) -> {
            StatBase stat = StatList.func_151177_a(achievementID); // getOneShotStat
            if (stat == null || !stat.isAchievement()) {
                return;
            }
            player.triggerAchievement(stat);
        });
        ACHIEVEMENT_CONDITION_HANDLERS.put(id, achievementHandler);

        ConditionHandler trophyHandler = handlerSupplier.get();
        trophyHandler.setOwner("trophies");
        trophyHandler.setListener(AmazingTrophiesAPI::awardTrophy);
        TROPHY_CONDITION_HANDLERS.put(id, trophyHandler);
    }

    /**
     * <p>
     * Registers a {@link TrophyModelHandler}.
     * </p>
     * <p>
     * <i>Note: Trophy model handlers must only be registered on the client-side.</i>
     * </p>
     * 
     * @param id       A unique, non-empty string to identify this trophy model handler
     * @param provider A {@link Supplier} which provides a new instance of the TrophyModelHandler to register each time
     *                 {@link Supplier#get()} is called
     */
    public static void registerTrophyModelHandlerProvider(String id, Supplier<TrophyModelHandler> provider) {
        if (TROPHY_MODEL_HANDLER_PROVIDERS.containsKey(id)) {
            LOGGER.error("Trophy model handler with id {} already exists! {} will not be registered.", id, provider);
            return;
        }
        TROPHY_MODEL_HANDLER_PROVIDERS.put(id, provider);
    }
}
