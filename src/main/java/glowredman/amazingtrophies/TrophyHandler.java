package glowredman.amazingtrophies;

import java.util.Map.Entry;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import glowredman.amazingtrophies.api.AmazingTrophiesAPI;
import glowredman.amazingtrophies.api.ConditionHandler;
import glowredman.amazingtrophies.api.TrophyModelHandler;
import glowredman.amazingtrophies.api.TrophyProperties;

public class TrophyHandler {

    static void parseTrophy(Entry<String, JsonElement> entry) {
        String id = entry.getKey();
        if (id == null) {
            AmazingTrophies.LOGGER.error("Found null-key in trophies.json! The value will be ignored.");
            return;
        }
        try {
            JsonObject json = (JsonObject) entry.getValue();
            JsonObject conditionJson = json.getAsJsonObject("condition");
            ConditionHandler conditionHandler = null;
            if (conditionJson != null && !conditionJson.isJsonNull()
                && conditionJson.entrySet()
                    .size() > 0) {
                JsonElement typeJson = conditionJson == null ? null : conditionJson.get("type");
                if (typeJson == null) {
                    throw new JsonSyntaxException("Required property \"condition/type\" is missing!");
                }
                conditionHandler = AmazingTrophiesAPI.getTrophyConditionHandler(typeJson.getAsString());
                if (conditionHandler == null) {
                    throw new IllegalArgumentException(
                        "Referencing unknown condition type: \"" + typeJson.getAsString() + "\"");
                }
            }
            TrophyModelHandler modelHandler = null;
            if (FMLLaunchHandler.side()
                .isClient()) {
                JsonObject modelJson = json.getAsJsonObject("model");
                if (modelJson != null && !modelJson.isJsonNull()
                    && modelJson.entrySet()
                        .size() > 0) {
                    JsonElement modelTypeJson = modelJson.get("type");
                    if (modelTypeJson == null) {
                        throw new JsonSyntaxException("Required property \"model/type\" is missing!");
                    }
                    Supplier<TrophyModelHandler> modelHandlerSupplier = AmazingTrophiesAPI
                        .getTrophyModelHandlerProvider(modelTypeJson.getAsString());
                    if (modelHandlerSupplier == null) {
                        throw new IllegalArgumentException(
                            "Referencing unknown model type: \"" + modelTypeJson.getAsString() + "\"");
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
            AmazingTrophies.LOGGER.error("An error occured while parsing trophy \"" + id + "\"!", e);
        }
    }
}
