package glowredman.amazingtrophies.model.complex;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.gtnewhorizon.gtnhlib.util.data.BlockMeta;

import cpw.mods.fml.common.registry.GameData;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.model.PedestalTrophyModelHandler;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharSet;

public class ComplexTrophyModelHandler extends PedestalTrophyModelHandler {

    public static final String ID = "complex";
    public static final String PROPERTY_KEYS = "keys";
    public static final String PROPERTY_METADATA = "metadata";
    public static final String PROPERTY_STRUCTURE = "structure";
    public static final String PROPERTY_TRANSPOSE = "transpose";
    public static final String PROPERTY_SKIP_HALF_OFFSET = "skipHalfOffset";
    private static final double TROPHY_PEDESTAL_HEIGHT = 5.0 / 16.0;

    private BaseModelStructure model;

    public ComplexTrophyModelHandler() {}

    public ComplexTrophyModelHandler(BaseModelStructure model) {
        this.model = model;
    }

    public void parse(String id, JsonObject json) throws JsonSyntaxException {

        // Parse the keys to get BlockInfo map
        Char2ObjectMap<BlockMeta> blockInfoMap = parseKeysToBlockInfoMap(json);

        // Parse the structure into a 2D array
        String[][] structure = parseStructureToArray(json, blockInfoMap.keySet());

        this.model = new GeneratedModelStructure(
            structure,
            blockInfoMap,
            ConfigHandler.getBooleanProperty(json, PROPERTY_TRANSPOSE, false),
            ConfigHandler
                .getSetProperty(json, PROPERTY_SKIP_HALF_OFFSET, JsonElement::getAsCharacter, Collections.emptySet()));
    }

    private Char2ObjectMap<BlockMeta> parseKeysToBlockInfoMap(JsonObject json) {
        JsonObject metadata = json.getAsJsonObject(PROPERTY_METADATA);
        JsonObject keys = ConfigHandler.getObjectProperty(json, PROPERTY_KEYS);

        Char2ObjectMap<BlockMeta> resultMap = new Char2ObjectOpenHashMap<>();

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
                resultMap.put(keyChar.charAt(0), new BlockMeta(block, 0));
            } else {
                int meta = ConfigHandler.getIntegerProperty(metadata, keyChar, 0);
                if (meta < 0 || meta > OreDictionary.WILDCARD_VALUE) {
                    throw new IllegalArgumentException("Illegal meta value (" + meta + ")!");
                }
                resultMap.put(keyChar.charAt(0), new BlockMeta(block, meta));
            }
        }
        return resultMap;
    }

    private String[][] parseStructureToArray(JsonObject json, CharSet blockKeys) {
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
                    if (!Character.isSpaceChar(c) && !blockKeys.contains(c)) {
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

        RenderHelper.INSTANCE.renderModel(model);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
