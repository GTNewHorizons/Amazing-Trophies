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
import glowredman.amazingtrophies.AmazingTrophies;
import glowredman.amazingtrophies.api.ConditionHandler;

public class JoinWorldConditionHandler extends ConditionHandler {

    public static final String ID = "dimension.join";

    private final Multimap<Integer, String> byID = HashMultimap.create();
    private final Multimap<Class<? extends WorldProvider>, String> byProvider = HashMultimap.create();

    @Override
    public String getID() {
        return ID;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void parse(String id, JsonObject json) throws JsonSyntaxException {
        JsonElement idJson = json.get("id");
        JsonElement providerJson = json.get("provider");
        if (idJson != null && !idJson.isJsonNull() && providerJson != null && !providerJson.isJsonNull()) {
            throw new JsonSyntaxException(
                "\"" + id + "\" is overdefined! Properties \"id\" and \"provider\" exclude each other.");
        }
        if (idJson != null && !idJson.isJsonNull()) {
            try {
                this.byID.put(idJson.getAsInt(), id);
            } catch (ClassCastException | IllegalStateException e) {
                throw new JsonSyntaxException("Malformed condition JSON!", e);
            }
            return;
        } else if (providerJson != null && !providerJson.isJsonNull()) {
            try {
                Class<?> clazz = Class.forName(providerJson.getAsString());
                if (WorldProvider.class.isAssignableFrom(clazz)) {
                    this.byProvider.put((Class<? extends WorldProvider>) clazz, id);
                } else {
                    AmazingTrophies.LOGGER.warn(
                        "Could not parse condition of \"{}\": provider {} is not a subclass of {}!",
                        id,
                        clazz.getName(),
                        WorldProvider.class);
                }
            } catch (ClassCastException | IllegalStateException e) {
                throw new JsonSyntaxException("Malformed condition JSON!", e);
            } catch (ClassNotFoundException e) {
                AmazingTrophies.LOGGER
                    .error("Could not parse condition of \"" + id + "\": provider class could not be found!", e);
            }
            return;
        }
        throw new JsonSyntaxException("\"" + id + "\" is missing required property \"id\" or \"provider\"!");
    }

    @Override
    protected boolean isForgeEventHandler() {
        return !this.byID.isEmpty() || !this.byProvider.isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if (!(event.entity instanceof EntityPlayer player)) {
            return;
        }
        WorldProvider provider = event.world.provider;
        for (String id : this.byID.get(provider.dimensionId)) {
            this.getListener()
                .accept(id, player);
        }
        for (String id : this.byProvider.get(provider.getClass())) {
            this.getListener()
                .accept(id, player);
        }
    }

}
