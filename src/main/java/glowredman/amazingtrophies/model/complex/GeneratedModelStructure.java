package glowredman.amazingtrophies.model.complex;

import java.util.Map;

import net.minecraft.block.Block;

import org.apache.commons.lang3.tuple.Pair;

public class GeneratedModelStructure extends BaseModelStructure {

    private final String[][] structure;

    public GeneratedModelStructure(String[][] structure, Map<Character, Pair<Block, Integer>> blockInfoMap) {
        this.charToBlock = blockInfoMap;
        this.structure = structure;

        reverseInnerArrays(structure);
        processStructureMap();
    }

    @Override
    public String[][] getStructureString() {
        return structure;
    }

}
