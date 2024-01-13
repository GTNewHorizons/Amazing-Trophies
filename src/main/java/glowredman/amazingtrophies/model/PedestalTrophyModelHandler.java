package glowredman.amazingtrophies.model;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import glowredman.amazingtrophies.AmazingTrophies;
import glowredman.amazingtrophies.api.TrophyModelHandler;

public class PedestalTrophyModelHandler extends TrophyModelHandler {

    public static final String ID = "pedestal";

    private static final ModelWrapper<?> MODEL_BASE = ModelWrapper
        .get(new ResourceLocation(AmazingTrophies.MODID, "models/trophy_pedestal.obj"));
    private static final ResourceLocation TEXTURE_BASE = new ResourceLocation(
        AmazingTrophies.MODID,
        "textures/blocks/trophy_pedestal.png");

    @Override
    public void render(double x, double y, double z, int rotation, @Nullable String name, long time,
        float partialTickTime) {

        // model
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TEXTURE_BASE);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(22.5f * rotation, 0.0f, 1.0f, 0.0f);

        MODEL_BASE.renderAll();

        // text
        if (name == null || name.isEmpty() || time == 0) {
            GL11.glPopMatrix();
            return;
        }
        String timeText = String.format("%tF", time);
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(0.0f, -0.5f, 0.376f);
        // at 100% scale, the text is rendered upside down and 8 blocks high
        // this scales the text down to 80% of a pixel and flips it on the XZ plane
        GL11.glScalef(0.00625f, -0.00625f, 0.00625f);
        GL11.glDepthMask(false);
        // due to the flip, the Y coordinate must be negative. one pixel is 10 high
        fontRenderer.drawString(name, -fontRenderer.getStringWidth(name) / 2, -39, 0x000000);
        fontRenderer.drawString(timeText, -fontRenderer.getStringWidth(timeText) / 2, -29, 0x000000);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }
}
