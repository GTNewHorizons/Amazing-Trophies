package glowredman.amazingtrophies.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import glowredman.amazingtrophies.AmazingTrophies;
import glowredman.amazingtrophies.api.TrophyModelHandler;

public class BaseTrophyModelHandler extends TrophyModelHandler {

    public static final String ID = "base";

    private static final IModelCustom MODEL_BASE = AdvancedModelLoader
        .loadModel(new ResourceLocation(AmazingTrophies.MODID, "models/trophyBase.obj"));
    private static final ResourceLocation TEXTURE_BASE = new ResourceLocation(
        AmazingTrophies.MODID,
        "textures/blocks/trophyBase.png");

    @Override
    public void render(double x, double y, double z, int rotation, String name, long time) {

        // model
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TEXTURE_BASE);
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(22.5f * rotation, 0.0f, 1.0f, 0.0f);
        MODEL_BASE.renderAll();

        // text
        if (name == null || name.isEmpty() || time == 0) {
            GL11.glPopMatrix();
            return;
        }
        String timeText = String.format("%1$tF %1$tT", time);
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(0.0f, -0.5f, 0.376f);
        GL11.glScalef(0.00625f, -0.00625f, 0.00625f);
        GL11.glDepthMask(false);
        fontRenderer.drawString(name, -fontRenderer.getStringWidth(name) / 2, -39, 0x000000);
        fontRenderer.drawString(timeText, -fontRenderer.getStringWidth(timeText) / 2, -29, 0x000000);
        GL11.glDepthMask(true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
}
