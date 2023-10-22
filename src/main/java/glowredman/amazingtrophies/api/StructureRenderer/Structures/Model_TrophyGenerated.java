package glowredman.amazingtrophies.api.StructureRenderer.Structures;

import glowredman.amazingtrophies.api.StructureRenderer.Base.Util.BlockInfo;

import java.util.HashMap;

public class Model_TrophyGenerated extends BaseModelStructure {

    private final String[][] structure;

    public Model_TrophyGenerated(String[][] structure, HashMap<Character, BlockInfo> blockInfoMap) {
        this.charToBlock = blockInfoMap;
        this.structure = structure;
        processStructureMap();
    }

    @Override
    public String[][] getStructureString() {
        return structure;
    }

}
