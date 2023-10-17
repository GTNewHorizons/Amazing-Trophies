package glowredman.amazingtrophies.condition;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.api.ConditionHandler;

public class PickupXPConditionHandler extends ConditionHandler {

    public static final String ID = "xp";
    public static final String PROPERTY_AMOUNT = "amount";

    private final List<Pair<Float, String>> conditions = new ArrayList<>();

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) {
        this.conditions.add(Pair.of(ConfigHandler.getFloatProperty(json, PROPERTY_AMOUNT, 0.0f), id));
    }

    @Override
    protected boolean isForgeEventHandler() {
        return !this.conditions.isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onExplosion(PlayerPickupXpEvent event) {
        float amount = event.orb.xpValue;
        for (Pair<Float, String> condition : this.conditions) {
            if (amount >= condition.getLeft()) {
                this.getListener()
                    .accept(condition.getRight(), event.entityPlayer);
            }
        }
    }

}
