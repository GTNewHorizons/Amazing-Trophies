package glowredman.amazingtrophies.condition;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.api.ConditionHandler;

public class EntityInteractConditionHandler extends ConditionHandler {

    public static final String ID = "interact.entity";
    public static final String PROPERTY_TARGETS = "targets";

    private final Multimap<Class<? extends Entity>, String> conditions = HashMultimap.create();

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) {
        for (Class<? extends Entity> clazz : ConfigHandler
            .getSetProperty(json, PROPERTY_TARGETS, ConfigHandler::parseEntityClass)) {
            this.conditions.put(clazz, id);
        }
    }

    @Override
    protected boolean isForgeEventHandler() {
        return !this.conditions.isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityInteract(EntityInteractEvent event) {
        for (String id : this.conditions.get(event.target.getClass())) {
            this.getListener()
                .accept(id, event.entityPlayer);
        }
    }

}
