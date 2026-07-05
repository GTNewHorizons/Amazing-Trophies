package glowredman.amazingtrophies.condition;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.world.ExplosionEvent;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.api.ConditionHandler;
import it.unimi.dsi.fastutil.floats.FloatObjectImmutablePair;
import it.unimi.dsi.fastutil.floats.FloatObjectPair;

public class ExplosionConditionHandler extends ConditionHandler {

    public static final String ID = "explosion";
    public static final String PROPERTY_SIZE = "size";

    private final List<FloatObjectPair<String>> conditions = new ArrayList<>();

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) {
        this.conditions
            .add(new FloatObjectImmutablePair<>(ConfigHandler.getFloatProperty(json, PROPERTY_SIZE, 0.0f), id));
    }

    @Override
    protected boolean isForgeEventHandler() {
        return !this.conditions.isEmpty();
    }

    @SubscribeEvent
    public void onExplosion(ExplosionEvent.Detonate event) {
        if (!(event.explosion.exploder instanceof EntityPlayer player)) {
            return;
        }
        float size = event.explosion.explosionSize;
        for (FloatObjectPair<String> condition : this.conditions) {
            if (size >= condition.leftFloat()) {
                this.getListener()
                    .accept(condition.right(), player);
            }
        }
    }
}
