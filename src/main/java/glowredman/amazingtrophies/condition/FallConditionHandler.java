package glowredman.amazingtrophies.condition;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingFallEvent;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.api.ConditionHandler;

public class FallConditionHandler extends ConditionHandler {

    public static final String ID = "fall";
    public static final String PROPERTY_DISTANCE = "distance";

    private final List<Pair<Float, String>> conditions = new ArrayList<>();

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) {
        this.conditions.add(Pair.of(ConfigHandler.getFloatProperty(json, PROPERTY_DISTANCE, 0.0f), id));
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
        for (Pair<Float, String> condition : this.conditions) {
            if (distance >= condition.getLeft()) {
                this.getListener()
                    .accept(condition.getRight(), player);
            }
        }
    }

}
