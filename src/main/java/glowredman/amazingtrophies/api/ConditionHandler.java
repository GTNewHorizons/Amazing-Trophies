package glowredman.amazingtrophies.api;

import java.util.function.BiConsumer;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.FMLCommonHandler;

// TODO javadoc
@ParametersAreNonnullByDefault
public abstract class ConditionHandler {

    private BiConsumer<String, EntityPlayer> listener;

    // TODO javadoc
    public ConditionHandler() {
        if (this.isFMLEventHandler()) {
            FMLCommonHandler.instance()
                .bus()
                .register(this);
        }
        if (this.isForgeEventHandler()) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    // TODO javadoc
    public abstract String getID();

    // TODO javadoc
    public abstract void parse(String id, JsonObject json);

    void setListener(BiConsumer<String, EntityPlayer> listener) {
        this.listener = listener;
    }

    // TODO javadoc
    protected BiConsumer<String, EntityPlayer> getListener() {
        return this.listener;
    }

    // TODO javadoc
    public boolean isFMLEventHandler() {
        return false;
    }

    // TODO javadoc
    public boolean isForgeEventHandler() {
        return false;
    }

}
