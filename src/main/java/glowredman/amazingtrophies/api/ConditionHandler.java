package glowredman.amazingtrophies.api;

import java.util.function.BiConsumer;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * A condition handler uses events or other means to trigger an action once a specific condition is met.
 * 
 * @author glowredman
 * @apiNote When registered to the {@link AmazingTrophiesAPI}, there exists one instance for each {@link #getListener()
 *          listener}.
 * @implSpec The instance must be able to handle all conditions of the same {@link #getListener() listener}.
 * @implSpec The instance should be able to trigger multiple actions for the same condition.
 *
 */
@ParametersAreNonnullByDefault
public abstract class ConditionHandler {

    private String owner;
    private BiConsumer<String, EntityPlayer> listener;

    /**
     * The ID used to register to the {@link AmazingTrophiesAPI}.
     * 
     * @implSpec The returned string must be {@link String#equals(Object) equal} for all invocations of the method.
     */
    public abstract String getID();

    /**
     * Parses the given JSON object and stores the details internally (this is up to the implementing class).
     * 
     * @param id   the string identifying the parent JSON object
     * @param json the JSON object to parse
     * @throws Exception This method may throw any Exception, e.g. if the JSON object is not conforming with the syntax
     *                   specified by the implementing
     *                   ConditionHandler or if a value can not be found in a registry.
     */
    public abstract void parse(String id, JsonObject json);

    void setOwner(String owner) {
        this.owner = owner;
    }

    void setListener(BiConsumer<String, EntityPlayer> listener) {
        this.listener = listener;
    }

    /**
     * Gets this condition handler's owner (e.g. "achievements" or "trophies").
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     * Gets the action to trigger. The first argument of {@link BiConsumer#accept(Object, Object)} is the ID, as
     * received by {@link #parse(String, JsonObject)}, the second argument is the player to trigger the action on.
     */
    protected BiConsumer<String, EntityPlayer> getListener() {
        return this.listener;
    }

    /**
     * Whether or not this instance should be registered to the {@link FMLCommonHandler#bus() FML event bus}.
     * 
     * @see #registerAsEventHandler()
     */
    protected boolean isFMLEventHandler() {
        return false;
    }

    /**
     * Whether or not this instance should be registered to the {@link MinecraftForge#EVENT_BUS MinecraftForge event
     * bus}.
     * 
     * @see #registerAsEventHandler()
     */
    protected boolean isForgeEventHandler() {
        return false;
    }

    /**
     * Registers this instance to the {@link FMLCommonHandler#bus() FML event bus} and/or the
     * {@link MinecraftForge#EVENT_BUS MinecraftForge event bus} or neither.
     * 
     * @see #isFMLEventHandler()
     * @see #isForgeEventHandler()
     */
    public void registerAsEventHandler() {
        if (this.isFMLEventHandler()) {
            FMLCommonHandler.instance()
                .bus()
                .register(this);
        }
        if (this.isForgeEventHandler()) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }
}
