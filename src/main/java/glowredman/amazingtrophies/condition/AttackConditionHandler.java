package glowredman.amazingtrophies.condition;

import java.util.function.BiConsumer;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public abstract class AttackConditionHandler extends DamageConditionHandler {

    protected abstract EntityPlayer getPlayer(LivingAttackEvent event);

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAttack(LivingAttackEvent event) {
        EntityPlayer player = this.getPlayer(event);
        if (player == null) {
            return;
        }

        String damageType = event.source.getDamageType();
        Class<? extends EntityLivingBase> targetClass = event.entityLiving.getClass();
        float damage = event.ammount;
        BiConsumer<String, EntityPlayer> listener = this.getListener();

        for (DamageInfo condition : this.conditions) {
            condition.trigger(damageType, targetClass, damage, id -> listener.accept(id, player));
        }
    }

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

}
