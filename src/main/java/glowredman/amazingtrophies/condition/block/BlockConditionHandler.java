package glowredman.amazingtrophies.condition.block;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;

import net.minecraft.block.Block;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import cpw.mods.fml.common.registry.GameData;
import glowredman.amazingtrophies.AmazingTrophies;
import glowredman.amazingtrophies.api.ConditionHandler;

public abstract class BlockConditionHandler extends ConditionHandler {

    protected final Multimap<Pair<Block, Integer>, String> blocks = HashMultimap.create();

    private final Multimap<Pair<String, Integer>, String> blocksRaw = HashMultimap.create();

    @Override
    public void parse(String id, JsonObject json) throws JsonSyntaxException {
        JsonElement blockJson = json.get("block");
        if (blockJson == null) {
            throw new JsonSyntaxException("\"" + id + "\" is missing required property \"block\"!");
        }
        JsonElement metaJson = json.get("meta");
        try {
            int meta = metaJson == null ? -1 : metaJson.getAsInt();
            this.blocksRaw.put(Pair.of(blockJson.getAsString(), meta), id);
        } catch (ClassCastException | IllegalStateException e) {
            throw new JsonSyntaxException("Malformed condition JSON!", e);
        }
    }

    @Override
    public void registerAsEventHandler() {
        this.convertBlockMap();
        super.registerAsEventHandler();
    }

    private void convertBlockMap() {
        Set<Pair<String, String>> failed = new HashSet<>();

        for (Entry<Pair<String, Integer>, String> entry : this.blocksRaw.entries()) {
            // extract values
            Pair<String, Integer> blockMetaPair = entry.getKey();
            String id = entry.getValue();
            String registryName = blockMetaPair.getLeft();

            // get block object related to the registry name. GameRegistry.findBlock() is not used here because it
            // requires modID and blockName as separate arguments, only to combine them and call the same code we do now
            Block block = GameData.getBlockRegistry()
                .getRaw(registryName);

            if (block == null) {
                // the block does not exist. add to failed list for logging and continue
                failed.add(Pair.of(id, registryName));
                continue;
            }

            this.blocks.put(Pair.of(block, blockMetaPair.getRight()), id);
        }

        if (!failed.isEmpty()) {
            StringJoiner joiner = new StringJoiner(", ");
            failed.stream()
                .map(p -> p.getLeft() + " (" + p.getRight() + ")")
                .forEach(joiner::add);
            AmazingTrophies.LOGGER
                .warn("Could not find block objects specified by the following {}: {}", this.getOwner(), joiner);
        }

        this.blocksRaw.clear();
    }

    @Override
    protected boolean isForgeEventHandler() {
        return !this.blocks.isEmpty();
    }

}
