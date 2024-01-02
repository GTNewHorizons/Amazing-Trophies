package glowredman.amazingtrophies.model.complex;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class CustomRenderBlocks extends RenderBlocks {

    // I made this, so we can turn on and off what faces should be rendered. Usually we would check if a block exists in
    // an adjacent block but since these are all holograms they don't exist.

    private RenderFacesInfo renderFacesInfo;

    public CustomRenderBlocks(IBlockAccess world) {
        super(world);
    }

    public void setRenderFacesInfo(RenderFacesInfo renderFacesInfo) {
        this.renderFacesInfo = renderFacesInfo;
    }

    @Override
    public void renderFaceYNeg(Block block, double x, double y, double z, IIcon icon) {
        if (this.renderFacesInfo.isYNeg()) {
            super.renderFaceYNeg(block, 0, 0, 0, icon);
        }
    }

    @Override
    public void renderFaceYPos(Block block, double x, double y, double z, IIcon icon) {
        if (this.renderFacesInfo.isYPos()) {
            super.renderFaceYPos(block, 0, 0, 0, icon);
        }
    }

    @Override
    public void renderFaceZNeg(Block block, double x, double y, double z, IIcon icon) {
        if (this.renderFacesInfo.isZNeg()) {
            super.renderFaceZNeg(block, 0, 0, 0, icon);
        }
    }

    @Override
    public void renderFaceZPos(Block block, double x, double y, double z, IIcon icon) {
        if (this.renderFacesInfo.isZPos()) {
            super.renderFaceZPos(block, 0, 0, 0, icon);
        }
    }

    @Override
    public void renderFaceXNeg(Block block, double x, double y, double z, IIcon icon) {
        if (this.renderFacesInfo.isXNeg()) {
            super.renderFaceXNeg(block, 0, 0, 0, icon);
        }
    }

    @Override
    public void renderFaceXPos(Block block, double x, double y, double z, IIcon icon) {
        if (this.renderFacesInfo.isXPos()) {
            super.renderFaceXPos(block, 0, 0, 0, icon);
        }
    }
}
