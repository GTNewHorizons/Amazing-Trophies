package glowredman.amazingtrophies.model.complex;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;

import org.apache.commons.lang3.tuple.Pair;

public class GeneratedModelStructure extends BaseModelStructure {

    private final String[][] structure;

    public GeneratedModelStructure(String[][] structure, Map<Character, Pair<Block, Integer>> blockInfoMap) {
        this(structure, blockInfoMap, true, Collections.emptySet());
    }

    public GeneratedModelStructure(String[][] structure, Map<Character, Pair<Block, Integer>> blockInfoMap,
        boolean transpose, Set<Character> skipHalfOffset) {
        this.charToBlock = blockInfoMap;
        this.structure = transpose ? transpose(structure) : structure;
        this.skipHalfOffset = skipHalfOffset;

        reverseInnerArrays(this.structure);
        processStructureMap();
    }

    @Override
    public String[][] getStructureString() {
        return structure;
    }

}
