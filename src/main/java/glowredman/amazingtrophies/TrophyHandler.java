package glowredman.amazingtrophies;

import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import glowredman.amazingtrophies.api.AmazingTrophiesAPI;
import glowredman.amazingtrophies.api.ConditionHandler;
import glowredman.amazingtrophies.api.TrophyModelHandler;
import glowredman.amazingtrophies.api.TrophyProperties;

public class TrophyHandler {

    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_CONDITION = "condition";
    public static final String PROPERTY_TYPE = "type";
    public static final String PROPERTY_MODEL = "model";

    static void parseTrophy(JsonElement element) {
        JsonObject json = element.getAsJsonObject();
        String id = ConfigHandler.getStringProperty(json, PROPERTY_ID);
        try {
            JsonObject conditionJson = json.getAsJsonObject(PROPERTY_CONDITION);
            ConditionHandler conditionHandler = null;
            if (conditionJson != null && !conditionJson.isJsonNull()
                && conditionJson.entrySet()
                    .size() > 0) {
                String type = ConfigHandler.getStringProperty(conditionJson, PROPERTY_TYPE);
                conditionHandler = AmazingTrophiesAPI.getTrophyConditionHandler(type);
                if (conditionHandler == null) {
                    throw new IllegalArgumentException("Referencing unknown condition type: \"" + type + "\"");
                }
            }
            TrophyModelHandler modelHandler = null;
            if (FMLLaunchHandler.side()
                .isClient()) {
                JsonObject modelJson = json.getAsJsonObject(PROPERTY_MODEL);
                if (modelJson != null && !modelJson.isJsonNull()
                    && modelJson.entrySet()
                        .size() > 0) {
                    String modelType = ConfigHandler.getStringProperty(modelJson, PROPERTY_TYPE);
                    Supplier<TrophyModelHandler> modelHandlerSupplier = AmazingTrophiesAPI
                        .getTrophyModelHandlerProvider(modelType);
                    if (modelHandlerSupplier == null) {
                        throw new IllegalArgumentException("Referencing unknown model type: \"" + modelType + "\"");
                    }
                    modelHandler = modelHandlerSupplier.get();
                    modelHandler.parse(id, modelJson);
                }
            }
            TrophyProperties props = new TrophyProperties(modelHandler);

            if (conditionHandler != null) {
                // condition must be parsed last
                conditionHandler.parse(id, conditionJson);
            }

            AmazingTrophiesAPI.registerTrophy(id, props);
        } catch (Exception e) {
            AmazingTrophies.LOGGER.error("Failed to parse trophy \"" + id + "\"!", e);
        }
    }
}
