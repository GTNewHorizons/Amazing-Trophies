package glowredman.amazingtrophies.condition;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingFallEvent;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.api.ConditionHandler;
import it.unimi.dsi.fastutil.floats.FloatObjectImmutablePair;
import it.unimi.dsi.fastutil.floats.FloatObjectPair;

public class FallConditionHandler extends ConditionHandler {

    public static final String ID = "fall";
    public static final String PROPERTY_DISTANCE = "distance";

    private final List<FloatObjectPair<String>> conditions = new ArrayList<>();

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) {
        this.conditions
            .add(new FloatObjectImmutablePair<>(ConfigHandler.getFloatProperty(json, PROPERTY_DISTANCE, 0.0f), id));
    }

    @Override
    protected boolean isForgeEventHandler() {
        return !this.conditions.isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onFall(LivingFallEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer player)) {
            return;
        }
        float distance = event.distance;
        for (FloatObjectPair<String> condition : this.conditions) {
            if (distance >= condition.leftFloat()) {
                this.getListener()
                    .accept(condition.right(), player);
            }
        }
    }
}
