package glowredman.amazingtrophies.model;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import glowredman.amazingtrophies.AssetHandler;
import glowredman.amazingtrophies.ConfigHandler;

public class BasicTrophyModelHandler extends PedestalTrophyModelHandler {

    public static final String ID = "basic";
    public static final String PROPERTY_MODEL = "model";
    public static final String PROPERTY_TEXTURE = "texture";

    private ModelWrapper<?> model;
    private ResourceLocation texture;

    public BasicTrophyModelHandler() {}

    public BasicTrophyModelHandler(ModelWrapper<?> model, ResourceLocation texture) {
        this.model = model;
        this.texture = texture;
    }

    @Override
    public void parse(String id, JsonObject json) throws JsonSyntaxException {
        this.model = ModelWrapper
            .get(AssetHandler.getResourceLocation(ConfigHandler.getStringProperty(json, PROPERTY_MODEL), "models/"));
        this.texture = AssetHandler
            .getResourceLocation(ConfigHandler.getStringProperty(json, PROPERTY_TEXTURE), "textures/blocks/");
    }

    @Override
    public void render(double x, double y, double z, int rotation, @Nullable String name, long time,
        float partialTickTime) {
        super.render(x, y, z, rotation, name, time, partialTickTime);

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(this.texture);
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(22.5f * rotation, 0.0f, 1.0f, 0.0f);
        this.model.renderAll();
        GL11.glPopMatrix();
    }

}
