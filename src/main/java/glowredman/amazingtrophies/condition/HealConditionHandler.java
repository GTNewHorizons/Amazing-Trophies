package glowredman.amazingtrophies.condition;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingHealEvent;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.api.ConditionHandler;
import it.unimi.dsi.fastutil.floats.FloatObjectImmutablePair;
import it.unimi.dsi.fastutil.floats.FloatObjectPair;

public class HealConditionHandler extends ConditionHandler {

    public static final String ID = "heal";
    public static final String PROPERTY_AMOUNT = "amount";

    private final List<FloatObjectPair<String>> conditions = new ArrayList<>();

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) {
        this.conditions
            .add(new FloatObjectImmutablePair<>(ConfigHandler.getFloatProperty(json, PROPERTY_AMOUNT, 0.0f), id));
    }

    @Override
    protected boolean isForgeEventHandler() {
        return !this.conditions.isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onHeal(LivingHealEvent event) {
        // TODO: Allow healing of other entities, similar to KillConditionHandler
        if (!(event.entityLiving instanceof EntityPlayer player)) {
            return;
        }
        float amount = event.amount;
        for (FloatObjectPair<String> condition : this.conditions) {
            if (amount >= condition.leftFloat()) {
                this.getListener()
                    .accept(condition.right(), player);
            }
        }
    }

}
