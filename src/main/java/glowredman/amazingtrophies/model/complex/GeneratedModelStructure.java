package glowredman.amazingtrophies.model.complex;

import java.util.Map;

import net.minecraft.block.Block;

import org.apache.commons.lang3.tuple.Pair;

public class GeneratedModelStructure extends BaseModelStructure {

    private final String[][] structure;

    public GeneratedModelStructure(String[][] structure, Map<Character, Pair<Block, Integer>> blockInfoMap) {
        this(structure, blockInfoMap, true);
    }

    public GeneratedModelStructure(String[][] structure, Map<Character, Pair<Block, Integer>> blockInfoMap,
        boolean transpose) {
        this.charToBlock = blockInfoMap;
        this.structure = transpose ? transpose(structure) : structure;

        reverseInnerArrays(this.structure);
        processStructureMap();
    }

    @Override
    public String[][] getStructureString() {
        return structure;
    }

}
