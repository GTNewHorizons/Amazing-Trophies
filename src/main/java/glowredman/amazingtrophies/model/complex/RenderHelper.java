package glowredman.amazingtrophies.model.complex;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;
import com.gtnewhorizon.gtnhlib.client.renderer.vbo.VBOManager;
import com.gtnewhorizon.gtnhlib.client.renderer.vbo.VertexBuffer;
import com.gtnewhorizon.gtnhlib.client.renderer.vertex.DefaultVertexFormat;
import com.gtnewhorizon.gtnhlib.client.renderer.vertex.VertexFormat;
import com.gtnewhorizon.gtnhlib.util.data.BlockMeta;

import glowredman.amazingtrophies.AmazingTrophies;

public class RenderHelper {

    public static final RenderHelper INSTANCE = AmazingTrophies.enableVBO ? new RenderHelperVBO() : new RenderHelper();

    private static final float TROPHY_RATIO_XZ = 12.0f / 16.0f; // 12x12 top in 16x16 texture.
    private static final float TROPHY_RATIO_Y = 11.0f / 16.0f; // 11 of 16 pixels empty above the pedestal

    protected void centreModel(BaseModelStructure model) {
        float x = model.getXLength() * 0.5f;
        float z = model.getYLength() * 0.5f;

        GL11.glTranslatef(0.5f - x, -0.5f, -0.5f - z);
    }

    protected void buildModel(BaseModelStructure model) {

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TextureMap.locationBlocksTexture);

        CustomRenderBlocks renderBlocks = new CustomRenderBlocks(Minecraft.getMinecraft().theWorld);
        renderBlocks.enableAO = false;

        for (int x = 0; x < model.getXLength(); x++) {
            for (int y = 0; y < model.getYLength(); y++) {
                for (int z = 0; z < model.getZLength(); z++) {
                    char blockChar = model.getStructureString()[x][z].charAt(y);

                    if (blockChar == ' ') continue;
                    if (model.renderFacesArray[x][z][y].allHidden()) continue;

                    BlockMeta blockInfo = model.getAssociatedBlockInfo(blockChar);
                    Block block = blockInfo.getBlock();
                    int meta = blockInfo.getBlockMeta();

                    if (!block.renderAsNormalBlock()) {
                        GL11.glPushMatrix();
                        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    }

                    renderBlocks.setRenderFacesInfo(model.renderFacesArray[x][z][y]);
                    if (model.skipHalfOffset.contains(blockChar)) {
                        this.renderBlock(block, meta, renderBlocks, x + 0.5, z + 1.5, y + 1.5);
                    } else {
                        this.renderBlock(block, meta, renderBlocks, x, z + 1, y + 1);
                    }

                    if (!block.renderAsNormalBlock()) {
                        GL11.glPopAttrib();
                        GL11.glPopMatrix();
                    }
                }
            }
        }
    }

    protected void scaleModel(final BaseModelStructure model) {
        final float maxScale = Math.min(
            TROPHY_RATIO_XZ / Math.max(model.getXLength(), model.getYLength()),
            TROPHY_RATIO_Y / model.getZLength());
        GL11.glScalef(maxScale, maxScale, maxScale);
    }

    public void renderModel(final BaseModelStructure model) {

        if (model == null) return;

        GL11.glPushMatrix();

        this.scaleModel(model);
        this.centreModel(model);
        this.buildModel(model);

        GL11.glPopMatrix();
    }

    protected void renderBlock(Block block, int metadata, CustomRenderBlocks renderBlocks, double x, double y,
        double z) {
        GL11.glPushMatrix();

        GL11.glTranslated(x, y, z);
        GL11.glRotated(-90, 0.0, 1.0, 0.0);
        renderBlocks.renderBlockAsItem(block, metadata, 1.0f);

        GL11.glPopMatrix();
    }

    private static class RenderHelperVBO extends RenderHelper {

        private static final VertexFormat format = DefaultVertexFormat.POSITION_TEXTURE_NORMAL;

        private VertexBuffer rebuildVBO(BaseModelStructure model) {
            final CustomRenderBlocks renderBlocks = new CustomRenderBlocks(Minecraft.getMinecraft().theWorld);
            renderBlocks.enableAO = false;

            TessellatorManager.startCapturing();
            Tessellator tessellator = TessellatorManager.get();
            for (int x = 0; x < model.getXLength(); x++) {
                for (int y = 0; y < model.getYLength(); y++) {
                    for (int z = 0; z < model.getZLength(); z++) {
                        final char blockChar = model.getStructureString()[x][z].charAt(y);

                        if (blockChar == ' ') continue;
                        if (model.renderFacesArray[x][z][y].allHidden()) continue;

                        final BlockMeta blockInfo = model.getAssociatedBlockInfo(blockChar);

                        renderBlocks.setRenderFacesInfo(model.renderFacesArray[x][z][y]);

                        if (model.skipHalfOffset.contains(blockChar)) {
                            tessellator.setTranslation(x + 0.5, z + 1.5, y + 1.5);
                        } else {
                            tessellator.setTranslation(x, z + 1, y + 1);
                        }

                        this.renderBlock(blockInfo.getBlock(), blockInfo.getBlockMeta(), renderBlocks);
                    }
                }
            }
            tessellator.setTranslation(0, 0, 0);
            final int vboId = VBOManager.generateDisplayLists(1);
            final VertexBuffer vertexBuffer = TessellatorManager.stopCapturingToVBO(format);
            VBOManager.registerVBO(vboId, vertexBuffer);

            model.vertexBuffer = vertexBuffer;
            return vertexBuffer;
        }

        @Override
        protected void buildModel(BaseModelStructure model) {

            Minecraft.getMinecraft()
                .getTextureManager()
                .bindTexture(TextureMap.locationBlocksTexture);

            // Build the VBO if needed and store it on the model
            VertexBuffer vertexBuffer = (VertexBuffer) model.vertexBuffer;
            if (model.vertexBuffer == null) {
                vertexBuffer = this.rebuildVBO(model);
            }

            // Now render the VBO
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_LIGHTING);
            // Unclear if this is needed
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);

            vertexBuffer.render();

        }

        private void renderBlock(Block block, int metadata, CustomRenderBlocks renderBlocks) {
            renderBlocks.renderBlockAsItem(block, metadata, 1.0f);
        }
    }
}
