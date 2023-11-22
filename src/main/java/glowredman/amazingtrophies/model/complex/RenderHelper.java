package glowredman.amazingtrophies.model.complex;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

public class RenderHelper {

    private static void centreModel(BaseModelStructure model) {

        String[][] testShape = model.getStructureString();

        int x = testShape.length / 2;
        int z = testShape[0][0].length() / 2;

        GL11.glTranslated(-x, -0.5, -1 - z);
    }

    private static void buildModel(BaseModelStructure model) {

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TextureMap.locationBlocksTexture);

        CustomRenderBlocks renderBlocks = new CustomRenderBlocks(Minecraft.getMinecraft().theWorld);
        renderBlocks.enableAO = false;

        for (int x = 0; x < model.getXLength(); x++) {
            for (int y = 0; y < model.getYLength(); y++) {
                for (int z = 0; z < model.getZLength(); z++) {
                    Character blockChar = model.getStructureString()[x][z].charAt(y);

                    if (blockChar.equals(' ')) continue;
                    if (model.renderFacesArray[x][z][y].allHidden()) continue;

                    Pair<Block, Integer> blockInfo = model.getAssociatedBlockInfo(blockChar);

                    renderBlocks.setRenderFacesInfo(model.renderFacesArray[x][z][y]);
                    renderBlock(blockInfo.getLeft(), blockInfo.getRight(), renderBlocks, x, z + 1, y + 1);
                }
            }
        }
    }

    private static final float TROPHY_BASE_RATIO = 12.0f / 16.0f; // 12x12 top in 16x16 texture.

    private static void scaleModel(final BaseModelStructure model) {
        final float maxScale = TROPHY_BASE_RATIO / model.maxAxisSize();
        GL11.glScalef(maxScale, maxScale, maxScale);
    }

    public static void renderModel(final BaseModelStructure model) {

        if (model == null) return;

        GL11.glPushMatrix();

        scaleModel(model);
        centreModel(model);
        buildModel(model);

        GL11.glPopMatrix();
    }

    public static void renderBlock(Block block, int metadata, CustomRenderBlocks renderBlocks, int x, int y, int z) {
        GL11.glPushMatrix();

        GL11.glTranslated(x, y, z);
        GL11.glRotated(-90, 0.0, 1.0, 0.0);
        renderBlocks.renderBlockAsItem(block, metadata, 1.0f);

        GL11.glPopMatrix();
    }

}
