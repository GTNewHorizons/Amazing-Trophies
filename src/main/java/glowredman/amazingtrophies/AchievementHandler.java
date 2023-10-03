package glowredman.amazingtrophies;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;

import net.minecraftforge.common.AchievementPage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import glowredman.amazingtrophies.api.AchievementProperties;
import glowredman.amazingtrophies.api.AmazingTrophiesAPI;
import glowredman.amazingtrophies.api.ConditionHandler;
import glowredman.amazingtrophies.api.ItemDefinition;

public class AchievementHandler {

    private static final Collection<String> PAGES = new HashSet<>();

    static void parseAchievement(Entry<String, JsonElement> entry) {
        String id = entry.getKey();
        if (id == null) {
            AmazingTrophies.LOGGER.error("Found null-key in achievements.json! The value will be ignored.");
            return;
        }
        try {
            JsonObject json = (JsonObject) entry.getValue();
            JsonObject conditionJson = json.getAsJsonObject("condition");
            ConditionHandler conditionHandler = null;
            if (conditionJson != null && !conditionJson.isJsonNull()) {
                JsonElement typeJson = conditionJson == null ? null : conditionJson.get("type");
                if (typeJson == null) {
                    throw new JsonSyntaxException("Required property \"condition/type\" is missing!");
                }
                conditionHandler = AmazingTrophiesAPI.getAchievementConditionHandler(typeJson.getAsString());
                if (conditionHandler == null) {
                    throw new IllegalArgumentException(
                        "Referencing unknown condition type: \"" + typeJson.getAsString() + "\"");
                }
            }
            JsonElement pageJson = json.get("page");
            if (pageJson == null) {
                throw new JsonSyntaxException("Required property \"page\" is missing!");
            }
            JsonElement xJson = json.get("x");
            if (xJson == null) {
                throw new JsonSyntaxException("Required property \"x\" is missing!");
            }
            JsonElement yJson = json.get("y");
            if (yJson == null) {
                throw new JsonSyntaxException("Required property \"y\" is missing!");
            }
            JsonElement parentJson = json.get("parent");
            JsonElement isSpecialJson = json.get("isSpecial");
            AchievementProperties props = new AchievementProperties(
                pageJson.getAsString(),
                xJson.getAsInt(),
                yJson.getAsInt(),
                parentJson == null || parentJson.isJsonNull() ? null : parentJson.getAsString(),
                isSpecialJson == null ? false : isSpecialJson.getAsBoolean(),
                parseItemDefinition(json.getAsJsonObject("icon")));

            if (conditionHandler != null) {
                // condition must be parsed last
                conditionHandler.parse(id, conditionJson);
            }

            // JSON is valid
            PAGES.add(props.getPage());
            AmazingTrophiesAPI.registerAchievement(id, props);
        } catch (Exception e) {
            AmazingTrophies.LOGGER.error("An error occured while parsing achievement \"" + id + "\"!", e);
        }
    }

    private static ItemDefinition parseItemDefinition(JsonObject json) throws JsonSyntaxException {
        JsonElement registryNameJson = json.get("registryName");
        if (registryNameJson == null) {
            throw new JsonSyntaxException("Required property \"icon/registryName\" is missing!");
        }
        JsonElement metaJson = json.get("meta");
        JsonElement nbtJson = json.get("nbt");
        return new ItemDefinition(
            registryNameJson.getAsString(),
            metaJson == null ? 0 : metaJson.getAsInt(),
            nbtJson == null || nbtJson.isJsonNull() ? null : nbtJson.getAsString());
    }

    static void registerMissingPages() {
        for (String name : PAGES) {
            try {
                AchievementPage.registerAchievementPage(new AchievementPage(name));
            } catch (Exception ignored) {}
        }
        PAGES.clear();
    }

}
