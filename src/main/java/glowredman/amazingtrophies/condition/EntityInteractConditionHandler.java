package glowredman.amazingtrophies.condition;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
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
            .getSetProperty(json, PROPERTY_TARGETS, EntityInteractConditionHandler::parseTarget)) {
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

    @SuppressWarnings("unchecked")
    private static Class<? extends Entity> parseTarget(JsonElement json) {
        String name = json.getAsString();
        // entity names may also be used to identify the target entity
        Class<?> clazz = EntityList.stringToClassMapping.get(name);
        if (clazz != null) {
            return (Class<? extends Entity>) clazz;
        }
        // not a valid entity name, try parsing as class name
        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Could not find target class!", e);
        }
        if (Entity.class.isAssignableFrom(clazz)) {
            return (Class<? extends Entity>) clazz;
        } else {
            throw new IllegalArgumentException(name + " is not a subclass of " + Entity.class.getName() + "!");
        }
    }

}
