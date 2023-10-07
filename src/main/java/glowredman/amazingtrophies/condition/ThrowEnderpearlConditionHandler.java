package glowredman.amazingtrophies.condition;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import glowredman.amazingtrophies.api.ConditionHandler;

public class ThrowEnderpearlConditionHandler extends ConditionHandler {

    public static final String ID = "enderpearl";

    private final List<Pair<Double, String>> distances = new ArrayList<>();

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) throws JsonSyntaxException {
        JsonElement distJson = json.get("distance");
        if (distJson == null) {
            throw new JsonSyntaxException("\"" + id + "\" is missing required property \"distance\"!");
        }
        double dist = 0.0;
        try {
            dist = distJson.getAsDouble();
        } catch (ClassCastException | IllegalStateException e) {
            throw new JsonSyntaxException("Malformed condition JSON!", e);
        }
        this.distances.add(Pair.of(dist * dist, id));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEnderTeleport(EnderTeleportEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer player)) {
            return;
        }
        double distSq = player.getDistanceSq(event.targetX, event.targetY, event.targetZ);
        for (Pair<Double, String> p : this.distances) {
            if (p.getLeft() >= distSq) {
                this.getListener()
                    .accept(p.getRight(), player);
            }
        }
    }

}
