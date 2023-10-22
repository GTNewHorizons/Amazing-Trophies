package glowredman.amazingtrophies.api.StructureRenderer.Base;

import glowredman.amazingtrophies.api.StructureRenderer.Base.Util.RenderHelper;
import glowredman.amazingtrophies.api.StructureRenderer.Structures.BaseModelStructure;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class BaseRenderTESR extends TileEntitySpecialRenderer {

    public static final String MODEL_NAME_NBT_TAG = "modelName";

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float timeSinceLastTick) {
        if (!(tile instanceof BaseRenderTileEntity trophyTileEntity)) return;

        RenderHelper.renderModel(tile.getWorldObj(), getModel(trophyTileEntity.modelName));
    }

    protected BaseModelStructure getModel(String modelName) {
        return null;
    }

}
