package glowredman.amazingtrophies.condition;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.api.ConditionHandler;

public class KillConditionHandler extends ConditionHandler {

    public static final String ID = "kill";
    public static final String PROPERTY_TARGETS = "targets";
    public static final String PROPERTY_IS_TARGETS_ALLOW_LIST = "isTargetsAllowList";

    private final Set<KillInfo> conditions = new HashSet<>();

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) {
        Set<Class<? extends EntityLivingBase>> targets = ConfigHandler
            .getSetProperty(json, PROPERTY_TARGETS, ConfigHandler::parseEntityLivingClass, new HashSet<>());
        boolean isTargetsAllowList = ConfigHandler.getBooleanProperty(json, PROPERTY_IS_TARGETS_ALLOW_LIST, false);

        KillInfo newInfo = new KillInfo(targets, isTargetsAllowList);
        for (KillInfo oldInfo : this.conditions) {
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
        if (!(event.source.getEntity() instanceof EntityPlayer player)) {
            return;
        }
        Class<? extends EntityLivingBase> targetClass = event.entityLiving.getClass();
        for (KillInfo condition : this.conditions) {
            condition.trigger(targetClass, player);
        }
    }

    private class KillInfo {

        private final Set<Class<? extends EntityLivingBase>> targets;
        private final boolean isTargetsAllowList;
        private final Set<String> ids = new HashSet<>();

        private KillInfo(Set<Class<? extends EntityLivingBase>> targets, boolean isTargetsAllowList) {
            this.targets = targets;
            this.isTargetsAllowList = isTargetsAllowList;
        }

        private void trigger(Class<? extends EntityLivingBase> target, EntityPlayer player) {
            if (this.targets.contains(target) ^ this.isTargetsAllowList) {
                return;
            }
            for (String id : this.ids) {
                KillConditionHandler.this.getListener()
                    .accept(id, player);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            return obj instanceof KillInfo other && this.targets.equals(other.targets)
                && this.isTargetsAllowList == other.isTargetsAllowList;
        }
    }

}
