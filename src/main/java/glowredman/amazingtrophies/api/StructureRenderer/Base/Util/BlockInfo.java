package glowredman.amazingtrophies.api.StructureRenderer.Base.Util;
import net.minecraft.block.Block;

public class BlockInfo {

    public final int metadata;
    public final Block block;

    public BlockInfo(Block block, int metadata) {
        this.block = block;
        this.metadata = metadata;
    }
}
