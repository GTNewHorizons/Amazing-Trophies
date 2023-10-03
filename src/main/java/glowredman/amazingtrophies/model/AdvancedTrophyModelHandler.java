package glowredman.amazingtrophies.model;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import glowredman.amazingtrophies.AssetHandler;

public class AdvancedTrophyModelHandler extends BaseTrophyModelHandler {

    public static final String ID = "advanced";

    private IModelCustom model;
    private ResourceLocation texture;

    @Override
    public void parse(String id, JsonObject json) throws JsonSyntaxException {
        JsonElement modelPath = json.get("model");
        if (modelPath == null) {
            throw new JsonSyntaxException("Trophy \"" + id + "\" is missing required property \"model\"!");
        }
        JsonElement texturePath = json.get("texture");
        if (texturePath == null) {
            throw new JsonSyntaxException("Trophy \"" + id + "\" is missing required property \"texture\"!");
        }
        try {
            this.model = AdvancedModelLoader
                .loadModel(AssetHandler.getResourceLocation(modelPath.getAsString(), "models/"));
            this.texture = AssetHandler.getResourceLocation(texturePath.getAsString(), "textures/blocks/");
        } catch (ClassCastException | IllegalStateException e) {
            throw new JsonSyntaxException("Malformed JSON!", e);
        }
    }

    @Override
    public void render(double x, double y, double z, int rotation, String name, long time) {
        super.render(x, y, z, rotation, name, time);

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(this.texture);
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(22.5f * rotation, 0.0f, 1.0f, 0.0f);
        this.model.renderAll();
        GL11.glPopMatrix();
    }

}
