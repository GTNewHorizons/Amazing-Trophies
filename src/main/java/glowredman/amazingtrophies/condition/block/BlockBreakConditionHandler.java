package glowredman.amazingtrophies.condition.block;

import java.util.function.BiConsumer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import org.apache.commons.lang3.tuple.Pair;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class BlockBreakConditionHandler extends BlockConditionHandler {

    public static final String ID = "block.break";

    @Override
    public String getID() {
        return ID;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockBreak(BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        if (player == null || player instanceof FakePlayer) {
            return;
        }
        BiConsumer<String, EntityPlayer> listener = this.getListener();
        for (String id : this.blocks.get(Pair.of(event.block, event.blockMetadata))) {
            listener.accept(id, player);
        }
        for (String id : this.blocks.get(Pair.of(event.block, -1))) {
            listener.accept(id, player);
        }
    }

}
