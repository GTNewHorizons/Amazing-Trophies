package glowredman.amazingtrophies.condition;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.api.ConditionHandler;

public abstract class AttackConditionHandler extends ConditionHandler {

    public static final String PROPERTY_DAMAGE = "damage";
    public static final String PROPERTY_SOURCES = "sources";
    public static final String PROPERTY_IS_SOURCES_ALLOW_LIST = "isSourcesAllowList";
    public static final String PROPERTY_TARGETS = "targets";
    public static final String PROPERTY_IS_TARGETS_ALLOW_LIST = "isTargetsAllowList";

    protected final Set<DamageInfo> conditions = new HashSet<>();

    @Override
    public void parse(String id, JsonObject json) {
        // spotless:off
        float damage = ConfigHandler.getFloatProperty(json, PROPERTY_DAMAGE, 0.0f);
        Set<String> sources = ConfigHandler.getSetProperty(json, PROPERTY_SOURCES, JsonElement::getAsString, new HashSet<>());
        boolean isSourcesAllowList = ConfigHandler.getBooleanProperty(json, PROPERTY_IS_SOURCES_ALLOW_LIST, false);
        Set<Class<? extends EntityLivingBase>> targets = ConfigHandler.getSetProperty(json, PROPERTY_TARGETS, ConfigHandler::parseEntityLivingClass, new HashSet<>());
        boolean isTargetsAllowList = ConfigHandler.getBooleanProperty(json, PROPERTY_IS_TARGETS_ALLOW_LIST, false);
        // spotless:on

        DamageInfo newInfo = new DamageInfo(damage, sources, isSourcesAllowList, targets, isTargetsAllowList);
        for (DamageInfo oldInfo : this.conditions) {
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
    public void onAttack(LivingAttackEvent event) {
        EntityPlayer player = this.getPlayer(event);
        if (player == null) {
            return;
        }

        String damageType = event.source.getDamageType();
        Class<? extends EntityLivingBase> targetClass = event.entityLiving.getClass();
        float damage = event.ammount;

        for (DamageInfo condition : this.conditions) {
            condition.trigger(damageType, targetClass, damage, player);
        }
    }

    protected abstract EntityPlayer getPlayer(LivingAttackEvent event);

    public static class Entity extends AttackConditionHandler {

        public static final String ID = "attack.entity";

        @Override
        public String getID() {
            return ID;
        }

        @Override
        protected EntityPlayer getPlayer(LivingAttackEvent event) {
            if (event.source instanceof EntityDamageSource entitySource
                && entitySource.getEntity() instanceof EntityPlayer player) {
                return player;
            }
            return null;
        }
    }

    public static class Player extends AttackConditionHandler {

        public static final String ID = "attack.player";

        @Override
        public String getID() {
            return ID;
        }

        @Override
        protected EntityPlayer getPlayer(LivingAttackEvent event) {
            if (event.entityLiving instanceof EntityPlayer player) {
                return player;
            }
            return null;
        }
    }

    private class DamageInfo {

        private final float minDamage;
        private final Set<String> sources;
        private final boolean isSourcesAllowList;
        private final Set<Class<? extends EntityLivingBase>> targets;
        private final boolean isTargetsAllowList;
        private final Set<String> ids = new HashSet<>();

        private DamageInfo(float minDamage, Set<String> sources, boolean isSourcesAllowList,
            Set<Class<? extends EntityLivingBase>> targets, boolean isTargetsAllowList) {
            this.minDamage = minDamage;
            this.sources = sources;
            this.isSourcesAllowList = isSourcesAllowList;
            this.targets = targets;
            this.isTargetsAllowList = isTargetsAllowList;
        }

        private void trigger(String damageType, Class<? extends EntityLivingBase> targetClass, float damage,
            EntityPlayer player) {
            if (damage < this.minDamage || (this.sources.contains(damageType) ^ this.isSourcesAllowList)
                || (this.targets.contains(targetClass) ^ this.isTargetsAllowList)) {
                return;
            }
            for (String id : this.ids) {
                AttackConditionHandler.this.getListener()
                    .accept(id, player);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            return obj instanceof DamageInfo other && this.minDamage == other.minDamage
                && this.sources.equals(other.sources)
                && this.isSourcesAllowList == other.isSourcesAllowList
                && this.targets.equals(other.targets)
                && this.isTargetsAllowList == other.isTargetsAllowList;
        }

    }

}
