package glowredman.amazingtrophies.model.complex;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import cpw.mods.fml.common.registry.GameData;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.model.PedestalTrophyModelHandler;

public class ComplexTrophyModelHandler extends PedestalTrophyModelHandler {

    public static final String ID = "complex";
    public static final String PROPERTY_KEYS = "keys";
    public static final String PROPERTY_METADATA = "metadata";
    public static final String PROPERTY_STRUCTURE = "structure";
    private static final double TROPHY_PEDESTAL_HEIGHT = 5.0 / 16.0;

    private BaseModelStructure model;

    public ComplexTrophyModelHandler() {}

    public ComplexTrophyModelHandler(BaseModelStructure model) {
        this.model = model;
    }

    public void parse(String id, JsonObject json) throws JsonSyntaxException {

        // Parse the keys to get BlockInfo map
        Map<Character, Pair<Block, Integer>> blockInfoMap = parseKeysToBlockInfoMap(json);

        // Parse the structure into a 2D array
        String[][] structure = parseStructureToArray(json, blockInfoMap.keySet());

        this.model = new GeneratedModelStructure(structure, blockInfoMap);
    }

    private Map<Character, Pair<Block, Integer>> parseKeysToBlockInfoMap(JsonObject json) {
        JsonObject metadata = json.getAsJsonObject(PROPERTY_METADATA);
        JsonObject keys = ConfigHandler.getObjectProperty(json, PROPERTY_KEYS);

        Map<Character, Pair<Block, Integer>> resultMap = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : keys.entrySet()) {
            String keyChar = entry.getKey();
            String registryName = entry.getValue()
                .getAsString();

            Block block = GameData.getBlockRegistry()
                .getRaw(registryName);
            if (block == null) {
                throw new IllegalArgumentException("Could not find block " + registryName + "!");
            }

            if (metadata == null) {
                resultMap.put(keyChar.charAt(0), Pair.of(block, 0));
            } else {
                int meta = ConfigHandler.getIntegerProperty(metadata, keyChar, 0);
                if (meta < 0 || meta > 15) {
                    throw new IllegalArgumentException("Illegal meta value (" + meta + ")!");
                }
                resultMap.put(keyChar.charAt(0), Pair.of(block, meta));
            }
        }
        return resultMap;
    }

    private String[][] parseStructureToArray(JsonObject json, Set<Character> blockKeys) {
        if (!json.has(PROPERTY_STRUCTURE)) {
            throw new JsonSyntaxException("Required property \"" + PROPERTY_STRUCTURE + "\" is missing!");
        }

        JsonArray outerArray = json.getAsJsonArray(PROPERTY_STRUCTURE);
        String[][] structure = new String[outerArray.size()][];
        int sizeY = 0;
        int sizeZ = 0;

        for (int i = 0; i < outerArray.size(); i++) {
            JsonArray innerArray = outerArray.get(i)
                .getAsJsonArray();

            if (sizeZ > 0) {
                if (sizeZ != innerArray.size()) {
                    throw new IllegalArgumentException("Inconsistent structure length (inner array sizes)!");
                }
            } else {
                sizeZ = innerArray.size();
            }

            structure[i] = new String[innerArray.size()];

            for (int j = 0; j < innerArray.size(); j++) {
                String line = innerArray.get(j)
                    .getAsString();

                if (sizeY > 0) {
                    if (sizeY != line.length()) {
                        throw new IllegalArgumentException("Inconsistent structure length (line lengths)!");
                    }
                } else {
                    sizeY = line.length();
                }

                for (int k = 0; k < line.length(); k++) {
                    char c = line.charAt(k);
                    if (!blockKeys.contains(c)) {
                        throw new IllegalArgumentException(
                            "Structure key '" + c
                                + "' (array="
                                + i
                                + ", line="
                                + j
                                + ", char="
                                + k
                                + ") is not not defined!");
                    }
                }

                structure[i][j] = line;
            }
        }

        return structure;
    }

    @Override
    public void render(double x, double y, double z, int rotation, @Nullable String name, long time,
        float partialTickTime) {
        // render pedestal
        super.render(x, y, z, rotation, name, time, partialTickTime);

        // Render custom structure.
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glTranslated(x, y - 0.5 + TROPHY_PEDESTAL_HEIGHT, z);
        GL11.glRotatef(-90, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(22.5f * rotation, 0.0f, 1.0f, 0.0f);
        RenderHelper.renderModel(model);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
