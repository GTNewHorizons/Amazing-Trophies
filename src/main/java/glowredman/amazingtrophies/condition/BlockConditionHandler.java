package glowredman.amazingtrophies.condition;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameData;
import glowredman.amazingtrophies.api.ConditionHandler;

public abstract class BlockConditionHandler extends ConditionHandler {

    protected final Multimap<Pair<Block, Integer>, String> blocks = HashMultimap.create();

    @Override
    public void parse(String id, JsonObject json) throws JsonSyntaxException {
        JsonElement blockJson = json.get("block");
        if (blockJson == null) {
            throw new JsonSyntaxException("\"" + id + "\" is missing required property \"block\"!");
        }
        JsonElement metaJson = json.get("meta");
        int meta = -1;
        String registryName = null;
        try {
            if (metaJson != null && !metaJson.isJsonNull()) {
                meta = metaJson.getAsInt();
            }
            registryName = blockJson.getAsString();
        } catch (ClassCastException | IllegalStateException e) {
            throw new JsonSyntaxException("Malformed condition JSON!", e);
        }
        Block block = GameData.getBlockRegistry()
            .getRaw(registryName);
        if (block == null) {
            throw new JsonSyntaxException("Could not find block " + registryName + " for condition of \"" + id + "\"!");
        }
        this.blocks.put(Pair.of(block, meta), id);
    }

    @Override
    protected boolean isForgeEventHandler() {
        return !this.blocks.isEmpty();
    }

    protected void handleEvent(EntityPlayer player, Block block, int meta) {
        if (player == null || player instanceof FakePlayer) {
            return;
        }
        for (String id : this.blocks.get(Pair.of(block, meta))) {
            this.getListener()
                .accept(id, player);
        }
        for (String id : this.blocks.get(Pair.of(block, -1))) {
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
