package glowredman.amazingtrophies.api.StructureRenderer.Structures;

import glowredman.amazingtrophies.api.StructureRenderer.Base.Util.BlockInfo;
import net.minecraft.init.Blocks;

public class Model_Default extends BaseModelStructure {

    public Model_Default() {
        charToBlock.put('x', new BlockInfo(Blocks.stone, 0));
        charToBlock.put('z', new BlockInfo(Blocks.glass, 0));
        charToBlock.put('a', new BlockInfo(Blocks.redstone_block, 0));
        charToBlock.put('b', new BlockInfo(Blocks.wool, 0));
        charToBlock.put('c', new BlockInfo(Blocks.coal_block, 0));
        processStructureMap();
    }

    @Override
    public String[][] getStructureString() {
        return structure;
    }

    @SuppressWarnings("SpellCheckingInspection")
    private final String[][] structure = new String[][] {
            { "xzx", "abc"}
    };

}
