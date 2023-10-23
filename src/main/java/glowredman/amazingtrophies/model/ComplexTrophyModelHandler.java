package glowredman.amazingtrophies.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.common.registry.GameRegistry;
import glowredman.amazingtrophies.AmazingTrophies;
import glowredman.amazingtrophies.api.StructureRenderer.Base.Util.BlockInfo;
import glowredman.amazingtrophies.api.StructureRenderer.Base.Util.RenderHelper;
import glowredman.amazingtrophies.api.StructureRenderer.Structures.Model_TrophyGenerated;
import glowredman.amazingtrophies.api.TrophyModelHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static glowredman.amazingtrophies.api.StructureRenderer.Trophies.Trophies.getModel;
import static glowredman.amazingtrophies.api.StructureRenderer.Trophies.Trophies.registerModel;
import static glowredman.amazingtrophies.trophy.RendererTrophy.currentTrophyProperties;

public class ComplexTrophyModelHandler extends TrophyModelHandler {

    public static final String ID = "complexBase";

    private static final IModelCustom MODEL_BASE = AdvancedModelLoader
        .loadModel(new ResourceLocation(AmazingTrophies.MODID, "models/trophyBase.obj"));
    private static final ResourceLocation TEXTURE_BASE = new ResourceLocation(
        AmazingTrophies.MODID,
        "textures/blocks/trophyBase.png");

    public void parse(String id, JsonObject json) throws JsonSyntaxException {

        // Parse the keys to get BlockInfo map
        HashMap<Character, BlockInfo> blockInfoMap = parseKeysToBlockInfoMap(json);

        // Parse the structure into a 2D array
        String[][] structure = parseStructureToArray(json);

        Model_TrophyGenerated jsonModel = new Model_TrophyGenerated(structure, blockInfoMap);

        registerModel(id, jsonModel);
    }

    private HashMap<Character, BlockInfo> parseKeysToBlockInfoMap(JsonObject model) throws JsonSyntaxException {
        if (!model.has("keys") || !model.has("metadata")) {
            throw new JsonSyntaxException("Both 'keys' and 'metadata' must be present in the model.");
        }

        JsonObject metadata = model.getAsJsonObject("metadata");
        JsonObject keys = model.getAsJsonObject("keys");

        HashMap<Character, BlockInfo> resultMap = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : keys.entrySet()) {
            String keyChar = entry.getKey();
            String blockIdentifier = entry.getValue().getAsString();

            if (metadata.has(keyChar)) {
                int meta = metadata.get(keyChar).getAsInt();
                Block block = getBlock(blockIdentifier);

                if (block != null) {
                    BlockInfo blockInfo = new BlockInfo(block, meta);
                    resultMap.put(keyChar.charAt(0), blockInfo);
                }
            } else {
                throw new JsonSyntaxException("Metadata for key '" + keyChar + "' is missing.");
            }
        }
        return resultMap;
    }

    private String[][] parseStructureToArray(JsonObject model) throws JsonSyntaxException {
        if (!model.has("structure")) {
            throw new JsonSyntaxException("Missing structure array in model");
        }

        JsonArray outerArray = model.getAsJsonArray("structure");
        String[][] structure = new String[outerArray.size()][];

        for (int i = 0; i < outerArray.size(); i++) {
            JsonArray innerArray = outerArray.get(i).getAsJsonArray();
            structure[i] = new String[innerArray.size()];

            for (int j = 0; j < innerArray.size(); j++) {
                structure[i][j] = innerArray.get(j).getAsString();
            }
        }

        return structure;
    }


    private Block getBlock(String blockIdentifier) {
        String[] parts = blockIdentifier.split(":");
        if (parts.length == 2) {
            String modId = parts[0];
            String blockName = parts[1];
            return GameRegistry.findBlock(modId, blockName);
        }
        throw new JsonSyntaxException("Invalid block identifier while generating Trophy : " + blockIdentifier);
    }

    static final double trophyBaseHeight = 0.3125;

    @Override
    public void render(double x, double y, double z, int rotation, @Nullable String userName, long time) {

        GL11.glPushMatrix();

        // Translate to the relative position.
        GL11.glTranslated(x, y, z);

        // Apply the rotation.
        GL11.glRotatef(22.5f * rotation, 0.0f, 1.0f, 0.0f);

        // Render the actual base of the trophy.
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_BASE);
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        MODEL_BASE.renderAll();
        GL11.glPopMatrix();

        // Render custom structure.
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glTranslated(0, trophyBaseHeight, 0);
        RenderHelper.renderModel(Minecraft.getMinecraft().theWorld, getModel(currentTrophyProperties.getID()));
        GL11.glPopAttrib();
        GL11.glPopMatrix();

        GL11.glPopMatrix();
    }


}
