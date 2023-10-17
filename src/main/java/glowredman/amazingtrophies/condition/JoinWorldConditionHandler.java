package glowredman.amazingtrophies.condition;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import glowredman.amazingtrophies.api.ConditionHandler;

public class JoinWorldConditionHandler extends ConditionHandler {

    public static final String ID = "dimension.join";
    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_PROVIDER = "provider";

    private final Multimap<Integer, String> conditionsID = HashMultimap.create();
    private final Multimap<Class<? extends WorldProvider>, String> conditionsProvider = HashMultimap.create();

    @Override
    public String getID() {
        return ID;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void parse(String id, JsonObject json) {
        JsonElement idJson = json.get(PROPERTY_ID);
        JsonElement providerJson = json.get(PROPERTY_PROVIDER);
        if (idJson != null && !idJson.isJsonNull() && providerJson != null && !providerJson.isJsonNull()) {
            throw new JsonSyntaxException(
                "Condition is overdefined! Properties \"" + PROPERTY_ID
                    + "\" and \""
                    + PROPERTY_PROVIDER
                    + "\" exclude each other.");
        }
        if (idJson != null && !idJson.isJsonNull()) {
            this.conditionsID.put(idJson.getAsInt(), id);
            return;
        } else if (providerJson != null && !providerJson.isJsonNull()) {
            try {
                Class<?> clazz = Class.forName(providerJson.getAsString());
                if (WorldProvider.class.isAssignableFrom(clazz)) {
                    this.conditionsProvider.put((Class<? extends WorldProvider>) clazz, id);
                    return;
                }
                throw new IllegalArgumentException(
                    "Could not parse condition of \"" + id
                        + "\": provider "
                        + clazz.getName()
                        + " is not a subclass of "
                        + WorldProvider.class.getName()
                        + "!");
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(
                    "Could not parse condition of \"" + id + "\": provider class could not be found!",
                    e);
            }
        }
        throw new JsonSyntaxException(
            "Condition is missing required property \"" + PROPERTY_ID + "\" or \"" + PROPERTY_PROVIDER + "\"!");
    }

    @Override
    protected boolean isForgeEventHandler() {
        return !this.conditionsID.isEmpty() || !this.conditionsProvider.isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if (!(event.entity instanceof EntityPlayer player)) {
            return;
        }
        WorldProvider provider = event.world.provider;
        for (String id : this.conditionsID.get(provider.dimensionId)) {
            this.getListener()
                .accept(id, player);
        }
        for (String id : this.conditionsProvider.get(provider.getClass())) {
            this.getListener()
                .accept(id, player);
        }
    }

}
