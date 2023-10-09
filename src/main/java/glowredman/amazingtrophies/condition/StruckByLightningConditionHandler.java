package glowredman.amazingtrophies.condition;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import glowredman.amazingtrophies.api.ConditionHandler;

public class StruckByLightningConditionHandler extends ConditionHandler {

    public static final String ID = "lightning";

    private final Set<String> ids = new HashSet<>();

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) {
        this.ids.add(id);
    }

    @Override
    protected boolean isForgeEventHandler() {
        return !this.ids.isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onStruckByLightning(EntityStruckByLightningEvent event) {
        if (!(event.entity instanceof EntityPlayer player)) {
            return;
        }
        for (String id : this.ids) {
            this.getListener()
                .accept(id, player);
        }
    }

}
