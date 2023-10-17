package glowredman.amazingtrophies.condition;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameData;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.api.ConditionHandler;

public abstract class BlockConditionHandler extends ConditionHandler {

    public static final String PROPERTY_BLOCK = "block";
    public static final String PROPERTY_META = "meta";

    protected final Multimap<Pair<Block, Integer>, String> conditions = HashMultimap.create();

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
        this.conditions.put(Pair.of(block, meta), id);
    }

    @Override
    protected boolean isForgeEventHandler() {
        return !this.conditions.isEmpty();
    }

    protected void handleEvent(EntityPlayer player, Block block, int meta) {
        if (player == null || player instanceof FakePlayer) {
            return;
        }
        for (String id : this.conditions.get(Pair.of(block, meta))) {
            this.getListener()
                .accept(id, player);
        }
        for (String id : this.conditions.get(Pair.of(block, OreDictionary.WILDCARD_VALUE))) {
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
}
