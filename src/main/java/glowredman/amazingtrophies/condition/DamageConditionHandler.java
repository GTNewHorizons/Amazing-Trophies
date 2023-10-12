package glowredman.amazingtrophies.condition;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.api.ConditionHandler;

public abstract class DamageConditionHandler extends ConditionHandler {

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
        Set<Class<? extends EntityLivingBase>> targets = ConfigHandler.getSetProperty(json, PROPERTY_TARGETS, DamageConditionHandler::parseTarget, new HashSet<>());
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

    @SuppressWarnings("unchecked")
    private static Class<? extends EntityLivingBase> parseTarget(JsonElement element) {
        String className = element.getAsString();
        // entity names may also be used to identify the target entity
        Class<?> clazz = EntityList.stringToClassMapping.get(className);
        if (clazz == null) {
            // not a valid entity name, try parsing as class name
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Could not find target class!", e);
            }
        }
        if (EntityLivingBase.class.isAssignableFrom(clazz)) {
            return (Class<? extends EntityLivingBase>) clazz;
        } else {
            throw new IllegalArgumentException(
                className + " is not a subclass of " + EntityLivingBase.class.getName() + "!");
        }
    }

    protected class DamageInfo {

        protected final float minDamage;
        protected final Set<String> sources;
        protected final boolean isSourcesAllowList;
        protected final Set<Class<? extends EntityLivingBase>> targets;
        protected final boolean isTargetsAllowList;
        protected final Set<String> ids = new HashSet<>();

        protected DamageInfo(float minDamage, Set<String> sources, boolean isSourcesAllowList,
            Set<Class<? extends EntityLivingBase>> targets, boolean isTargetsAllowList) {
            this.minDamage = minDamage;
            this.sources = sources;
            this.isSourcesAllowList = isSourcesAllowList;
            this.targets = targets;
            this.isTargetsAllowList = isTargetsAllowList;
        }

        protected void trigger(String damageType, Class<? extends EntityLivingBase> targetClass, float damage,
            Consumer<String> listener) {
            if (damage < this.minDamage || (this.sources.contains(damageType) ^ this.isSourcesAllowList)
                || (this.targets.contains(targetClass) ^ this.isTargetsAllowList)) {
                return;
            }
            this.ids.forEach(listener::accept);
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
