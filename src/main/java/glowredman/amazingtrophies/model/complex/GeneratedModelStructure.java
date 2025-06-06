package glowredman.amazingtrophies.model.complex;

import java.util.Set;

import com.gtnewhorizon.gtnhlib.util.data.BlockMeta;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;

public class GeneratedModelStructure extends BaseModelStructure {

    private final String[][] structure;

    public GeneratedModelStructure(String[][] structure, Char2ObjectMap<BlockMeta> blockInfoMap) {
        this(structure, blockInfoMap, true, CharSet.of());
    }

    public GeneratedModelStructure(String[][] structure, Char2ObjectMap<BlockMeta> blockInfoMap, boolean transpose,
        Set<Character> skipHalfOffset) {
        this(structure, blockInfoMap, transpose, new CharOpenHashSet(skipHalfOffset));
    }

    public GeneratedModelStructure(String[][] structure, Char2ObjectMap<BlockMeta> blockInfoMap, boolean transpose,
        CharSet skipHalfOffset) {
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
