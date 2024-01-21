package glowredman.amazingtrophies.model.complex;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.gtnewhorizons.angelica.client.renderer.CapturingTessellator;
import com.gtnewhorizons.angelica.compat.mojang.DefaultVertexFormat;
import com.gtnewhorizons.angelica.compat.mojang.VertexBuffer;
import com.gtnewhorizons.angelica.compat.mojang.VertexFormat;
import com.gtnewhorizons.angelica.config.AngelicaConfig;
import com.gtnewhorizons.angelica.glsm.TessellatorManager;
import com.gtnewhorizons.angelica.glsm.VBOManager;

public class RenderHelperAngelica {

    private static int VBO_ID = -1;

    private static void centreModel(BaseModelStructure model) {

        String[][] testShape = model.getStructureString();

        int x = testShape.length / 2;
        int z = testShape[0][0].length() / 2;

        GL11.glTranslated(-x, -0.5, -1 - z);
    }

    private static VertexBuffer rebuildVBO(BaseModelStructure model) {
        final CustomRenderBlocks renderBlocks = new CustomRenderBlocks(Minecraft.getMinecraft().theWorld);
        renderBlocks.enableAO = false;

        TessellatorManager.startCapturing();
        CapturingTessellator tessellator = (CapturingTessellator) TessellatorManager.get();
        for (int x = 0; x < model.getXLength(); x++) {
            for (int y = 0; y < model.getYLength(); y++) {
                for (int z = 0; z < model.getZLength(); z++) {
                    final Character blockChar = model.getStructureString()[x][z].charAt(y);

                    if (blockChar.equals(' ')) continue;
                    if (model.renderFacesArray[x][z][y].allHidden()) continue;

                    final Pair<Block, Integer> blockInfo = model.getAssociatedBlockInfo(blockChar);

                    renderBlocks.setRenderFacesInfo(model.renderFacesArray[x][z][y]);
                    tessellator.setTranslation(x, z + 1, y + 1);
                    renderBlock(blockInfo.getLeft(), blockInfo.getRight(), renderBlocks);
                }
            }
        }
        if (VBO_ID == -1) {
            VBO_ID = VBOManager.generateDisplayLists(1);
        }
        final VertexBuffer vertexBuffer = TessellatorManager.stopCapturingToVBO(format);
        VBOManager.registerVBO(VBO_ID, vertexBuffer);

        model.vertexBuffer = vertexBuffer;
        return vertexBuffer;
    }

    static final VertexFormat format = DefaultVertexFormat.POSITION_TEXTURE_NORMAL;

    private static void renderModelInternal(BaseModelStructure model) {

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TextureMap.locationBlocksTexture);

        // Build the VBO if needed and store it on the model
        VertexBuffer vertexBuffer = (VertexBuffer) model.vertexBuffer;
        if (model.vertexBuffer == null) {
            vertexBuffer = rebuildVBO(model);
        }

        // Now render the VBO
        vertexBuffer.bind();
        format.setupBufferState(0L);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_LIGHTING);
        // Unclear if this is needed
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        vertexBuffer.draw();

        format.clearBufferState();
        vertexBuffer.unbind();

    }

    private static final float TROPHY_BASE_RATIO = 12.0f / 16.0f; // 12x12 top in 16x16 texture.

    private static void scaleModel(final BaseModelStructure model) {
        final float maxScale = TROPHY_BASE_RATIO / model.maxAxisSize();
        GL11.glScalef(maxScale, maxScale, maxScale);
    }

    public static void renderModel(final BaseModelStructure model) {

        if (model == null) return;

        if (!AngelicaConfig.enableVBO) {
            RenderHelper.renderModel(model);
            return;
        }

        GL11.glPushMatrix();

        scaleModel(model);
        centreModel(model);
        renderModelInternal(model);

        GL11.glPopMatrix();
    }

    public static void renderBlock(Block block, int metadata, CustomRenderBlocks renderBlocks) {
        renderBlocks.renderBlockAsItem(block, metadata, 1.0f);
    }

}
