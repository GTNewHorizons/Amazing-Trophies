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
    public static final String PROPERTY_DAMAGE_TYPES = "damageTypes";
    public static final String PROPERTY_IS_DAMAGE_TYPES_ALLOW_LIST = "isDamageTypesAllowList";
    public static final String PROPERTY_ENTITIES = "entities";
    public static final String PROPERTY_IS_ENTITIES_ALLOW_LIST = "isEntitiesAllowList";

    protected final Set<DamageInfo> conditions = new HashSet<>();

    @Override
    public void parse(String id, JsonObject json) {
        // spotless:off
        float damage = ConfigHandler.getFloatProperty(json, PROPERTY_DAMAGE, 0.0f);
        Set<String> damageTypes = ConfigHandler.getSetProperty(json, PROPERTY_DAMAGE_TYPES, JsonElement::getAsString, new HashSet<>());
        boolean isDamageTypesAllowList = ConfigHandler.getBooleanProperty(json, PROPERTY_IS_DAMAGE_TYPES_ALLOW_LIST, false);
        Set<Class<? extends EntityLivingBase>> entities = ConfigHandler.getSetProperty(json, PROPERTY_ENTITIES, ConfigHandler::parseEntityLivingClass, new HashSet<>());
        boolean isEntitiesAllowList = ConfigHandler.getBooleanProperty(json, PROPERTY_IS_ENTITIES_ALLOW_LIST, false);
        // spotless:on

        DamageInfo newInfo = new DamageInfo(damage, damageTypes, isDamageTypesAllowList, entities, isEntitiesAllowList);
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
        Class<? extends EntityLivingBase> entityClass = this.getEntityClass(event);
        if (player == null || entityClass == null) {
            return;
        }

        String damageType = event.source.getDamageType();
        float damage = event.ammount;

        for (DamageInfo condition : this.conditions) {
            condition.trigger(damageType, entityClass, damage, player);
        }
    }

    protected abstract EntityPlayer getPlayer(LivingAttackEvent event);

    protected abstract Class<? extends EntityLivingBase> getEntityClass(LivingAttackEvent event);

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

        @Override
        protected Class<? extends EntityLivingBase> getEntityClass(LivingAttackEvent event) {
            return event.entityLiving.getClass();
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

        @Override
        protected Class<? extends EntityLivingBase> getEntityClass(LivingAttackEvent event) {
            if (event.source instanceof EntityDamageSource entitySource
                && entitySource.getEntity() instanceof EntityLivingBase entity) {
                return entity.getClass();
            }
            return null;
        }
    }

    private class DamageInfo {

        private final float minDamage;
        private final Set<String> damageTypes;
        private final boolean isDamageTypesAllowList;
        private final Set<Class<? extends EntityLivingBase>> entities;
        private final boolean isEntitiesAllowList;
        private final Set<String> ids = new HashSet<>();

        private DamageInfo(float minDamage, Set<String> damageTypes, boolean isDamageTypesAllowList,
            Set<Class<? extends EntityLivingBase>> entities, boolean isEntitiesAllowList) {
            this.minDamage = minDamage;
            this.damageTypes = damageTypes;
            this.isDamageTypesAllowList = isDamageTypesAllowList;
            this.entities = entities;
            this.isEntitiesAllowList = isEntitiesAllowList;
        }

        private void trigger(String damageType, Class<? extends EntityLivingBase> targetClass, float damage,
            EntityPlayer player) {
            if (damage < this.minDamage || (this.damageTypes.contains(damageType) ^ this.isDamageTypesAllowList)
                || (this.entities.contains(targetClass) ^ this.isEntitiesAllowList)) {
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
                && this.damageTypes.equals(other.damageTypes)
                && this.isDamageTypesAllowList == other.isDamageTypesAllowList
                && this.entities.equals(other.entities)
                && this.isEntitiesAllowList == other.isEntitiesAllowList;
        }

    }

}
