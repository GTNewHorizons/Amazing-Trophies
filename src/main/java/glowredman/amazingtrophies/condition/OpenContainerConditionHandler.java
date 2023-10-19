package glowredman.amazingtrophies.condition;

import net.minecraft.inventory.Container;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.api.ConditionHandler;

public class OpenContainerConditionHandler extends ConditionHandler {

    public static final String ID = "container.open";
    public static final String PROPERTY_CONTAINERS = "containers";

    private final Multimap<Class<? extends Container>, String> conditions = HashMultimap.create();
    private Container previous;

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) {
        for (Class<? extends Container> clazz : ConfigHandler
            .getSetProperty(json, PROPERTY_CONTAINERS, OpenContainerConditionHandler::parseTarget)) {
            this.conditions.put(clazz, id);
        }
    }

    @Override
    protected boolean isForgeEventHandler() {
        return !this.conditions.isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onOpenContainer(PlayerOpenContainerEvent event) {
        Container current = event.entityPlayer.openContainer;
        if (event.getResult() == Result.DENY || current == this.previous) {
            return;
        }
        for (String id : this.conditions.get(current.getClass())) {
            this.getListener()
                .accept(id, event.entityPlayer);
        }
        this.previous = current;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Container> parseTarget(JsonElement json) {
        String name = json.getAsString();
        Class<?> clazz;
        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Could not find target class!", e);
        }
        if (Container.class.isAssignableFrom(clazz)) {
            return (Class<? extends Container>) clazz;
        } else {
            throw new IllegalArgumentException(name + " is not a subclass of " + Container.class.getName() + "!");
        }
    }

}
