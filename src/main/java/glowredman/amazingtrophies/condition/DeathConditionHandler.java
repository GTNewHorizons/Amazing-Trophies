package glowredman.amazingtrophies.condition;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.api.ConditionHandler;

public class DeathConditionHandler extends ConditionHandler {

    public static final String ID = "death";
    public static final String PROPERTY_SOURCES = "sources";
    public static final String PROPERTY_IS_SOURCES_ALLOW_LIST = "isSourcesAllowList";

    private final Set<DeathInfo> conditions = new HashSet<>();

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) {
        Set<String> sources = ConfigHandler
            .getSetProperty(json, PROPERTY_SOURCES, JsonElement::getAsString, new HashSet<>());
        boolean isSourcesAllowList = ConfigHandler.getBooleanProperty(json, PROPERTY_IS_SOURCES_ALLOW_LIST, false);

        DeathInfo newInfo = new DeathInfo(sources, isSourcesAllowList);
        for (DeathInfo oldInfo : this.conditions) {
            if (newInfo.equals(oldInfo)) {
                oldInfo.ids.add(id);
                return;
            }
        }
        newInfo.ids.add(id);
        this.conditions.add(newInfo);
    }

    @Override
    protected boolean isForgeEventHandler() {
        return !this.conditions.isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDeath(LivingDeathEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer player)) {
            return;
        }
        String damageType = event.source.getDamageType();
        for (DeathInfo condition : this.conditions) {
            condition.trigger(damageType, player);
        }
    }

    private class DeathInfo {

        private final Set<String> sources;
        private final boolean isSourcesAllowList;
        private final Set<String> ids = new HashSet<>();

        private DeathInfo(Set<String> sources, boolean isSourcesAllowList) {
            this.sources = sources;
            this.isSourcesAllowList = isSourcesAllowList;
        }

        private void trigger(String damageType, EntityPlayer player) {
            if (this.sources.contains(damageType) ^ this.isSourcesAllowList) {
                return;
            }
            for (String id : this.ids) {
                DeathConditionHandler.this.getListener()
                    .accept(id, player);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            return obj instanceof DeathInfo other && this.sources.equals(other.sources)
                && this.isSourcesAllowList == other.isSourcesAllowList;
        }
    }

}
