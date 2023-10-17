package glowredman.amazingtrophies.condition;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.world.ExplosionEvent;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.api.ConditionHandler;

public class ExplosionConditionHandler extends ConditionHandler {

    public static final String ID = "explosion";
    public static final String PROPERTY_SIZE = "size";

    private final List<Pair<Float, String>> conditions = new ArrayList<>();

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) {
        this.conditions.add(Pair.of(ConfigHandler.getFloatProperty(json, PROPERTY_SIZE, 0.0f), id));
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
        for (Pair<Float, String> condition : this.conditions) {
            if (size >= condition.getLeft()) {
                this.getListener()
                    .accept(condition.getRight(), player);
            }
        }
    }

}
