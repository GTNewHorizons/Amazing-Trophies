package glowredman.amazingtrophies.condition;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.api.ConditionHandler;
import it.unimi.dsi.fastutil.doubles.DoubleObjectImmutablePair;
import it.unimi.dsi.fastutil.doubles.DoubleObjectPair;

public class ThrowEnderpearlConditionHandler extends ConditionHandler {

    public static final String ID = "enderpearl";
    public static final String PROPERTY_DISTANCE = "distance";

    private final List<DoubleObjectPair<String>> conditions = new ArrayList<>();

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) {
        double dist = ConfigHandler.getDoubleProperty(json, PROPERTY_DISTANCE, 0.0);
        this.conditions.add(new DoubleObjectImmutablePair<>(dist * dist, id));
    }

    @Override
    protected boolean isForgeEventHandler() {
        return !this.conditions.isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEnderTeleport(EnderTeleportEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer player)) {
            return;
        }
        double distSq = player.getDistanceSq(event.targetX, event.targetY, event.targetZ);
        for (DoubleObjectPair<String> condition : this.conditions) {
            if (distSq >= condition.leftDouble()) {
                this.getListener()
                    .accept(condition.right(), player);
            }
        }
    }
}
