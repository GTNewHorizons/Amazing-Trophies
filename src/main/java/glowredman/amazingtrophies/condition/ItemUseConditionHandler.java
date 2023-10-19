package glowredman.amazingtrophies.condition;

import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public abstract class ItemUseConditionHandler extends ItemConditionHandler {

    public static class Start extends ItemUseConditionHandler {

        public static final String ID = "item.use.start";

        @Override
        public String getID() {
            return ID;
        }

        @Override
        protected boolean isForgeEventHandler() {
            return !this.conditions.isEmpty();
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onStartUse(PlayerUseItemEvent.Start event) {
            this.trigger(event.item, event.entityPlayer);
        }

    }

    public static class Stop extends ItemUseConditionHandler {

        public static final String ID = "item.use.stop";

        @Override
        public String getID() {
            return ID;
        }

        @Override
        protected boolean isForgeEventHandler() {
            return !this.conditions.isEmpty();
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onStopUse(PlayerUseItemEvent.Stop event) {
            this.trigger(event.item, event.entityPlayer);
        }

    }

    public static class Finish extends ItemUseConditionHandler {

        public static final String ID = "item.use.finish";

        @Override
        public String getID() {
            return ID;
        }

        @Override
        protected boolean isForgeEventHandler() {
            return !this.conditions.isEmpty();
        }

        @SubscribeEvent
        public void onFinishUse(PlayerUseItemEvent.Finish event) {
            this.trigger(event.item, event.entityPlayer);
        }

    }

}
