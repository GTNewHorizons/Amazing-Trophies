package glowredman.amazingtrophies.condition;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.gtnewhorizon.gtnhlib.util.data.BlockMeta;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameData;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.api.ConditionHandler;

public abstract class BlockConditionHandler extends ConditionHandler {

    public static final String PROPERTY_BLOCK = "block";
    public static final String PROPERTY_META = "meta";

    protected final Multimap<BlockMeta, String> conditions = HashMultimap.create();

    @Override
    public void parse(String id, JsonObject json) {
        int meta = ConfigHandler.getIntegerProperty(json, PROPERTY_META, OreDictionary.WILDCARD_VALUE);
        if (meta < 0 || meta > OreDictionary.WILDCARD_VALUE) {
            throw new IllegalArgumentException("Illegal meta value (" + meta + ")!");
        }
        String registryName = ConfigHandler.getStringProperty(json, PROPERTY_BLOCK, id);
        Block block = GameData.getBlockRegistry()
            .getRaw(registryName);
        if (block == null) {
            throw new IllegalArgumentException(
                "Could not find block " + registryName + " for condition of \"" + id + "\"!");
        }
        this.conditions.put(new BlockMeta(block, meta), id);
    }

    @Override
    protected boolean isForgeEventHandler() {
        return !this.conditions.isEmpty();
    }

    protected void handleEvent(EntityPlayer player, Block block, int meta) {
        if (player == null || player instanceof FakePlayer) {
            return;
        }
        for (String id : this.conditions.get(new BlockMeta(block, meta))) {
            this.getListener()
                .accept(id, player);
        }
        for (String id : this.conditions.get(new BlockMeta(block, OreDictionary.WILDCARD_VALUE))) {
            this.getListener()
                .accept(id, player);
        }
    }

    public static class Break extends BlockConditionHandler {

        public static final String ID = "block.break";

        @Override
        public String getID() {
            return ID;
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onBlockBreak(BreakEvent event) {
            this.handleEvent(event.getPlayer(), event.block, event.blockMetadata);
        }
    }

    public static class Place extends BlockConditionHandler {

        public static final String ID = "block.place";

        @Override
        public String getID() {
            return ID;
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onBlockPlace(PlaceEvent event) {
            this.handleEvent(event.player, event.block, event.blockMetadata);
        }
    }

    public static class Interact extends BlockConditionHandler {

        public static final String ID = "block.interact";

        @Override
        public String getID() {
            return ID;
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onInteract(PlayerInteractEvent event) {
            if (event.action != Action.RIGHT_CLICK_BLOCK) {
                return;
            }
            Block block = event.world.getBlock(event.x, event.y, event.z);
            int meta = event.world.getBlockMetadata(event.x, event.y, event.z);
            this.handleEvent(event.entityPlayer, block, meta);
        }
    }
}
