package glowredman.amazingtrophies;

import java.util.Collection;
import java.util.HashSet;

import net.minecraftforge.common.AchievementPage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import glowredman.amazingtrophies.api.AchievementProperties;
import glowredman.amazingtrophies.api.AmazingTrophiesAPI;
import glowredman.amazingtrophies.api.ConditionHandler;

public class AchievementHandler {

    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_CONDITION = "condition";
    public static final String PROPERTY_TYPE = "type";
    public static final String PROPERTY_PAGE = "page";
    public static final String PROPERTY_X = "x";
    public static final String PROPERTY_Y = "y";
    public static final String PROPERTY_PARENT = "parent";
    public static final String PROPERTY_IS_SPECIAL = "isSpecial";
    public static final String PROPERTY_ICON = "icon";
    private static final Collection<String> PAGES = new HashSet<>();

    static void parseAchievement(JsonElement element) {
        JsonObject json = element.getAsJsonObject();
        String id = ConfigHandler.getStringProperty(json, PROPERTY_ID);
        try {
            JsonObject conditionJson = json.getAsJsonObject(PROPERTY_CONDITION);
            ConditionHandler conditionHandler = null;
            if (conditionJson != null && !conditionJson.isJsonNull()
                && conditionJson.entrySet()
                    .size() > 0) {
                String type = ConfigHandler.getStringProperty(conditionJson, PROPERTY_TYPE);
                conditionHandler = AmazingTrophiesAPI.getAchievementConditionHandler(type);
                if (conditionHandler == null) {
                    throw new IllegalArgumentException("Referencing unknown condition type: \"" + type + "\"");
                }
            }
            AchievementProperties props = new AchievementProperties.Builder(
                ConfigHandler.getIntegerProperty(json, PROPERTY_X),
                ConfigHandler.getIntegerProperty(json, PROPERTY_Y),
                ConfigHandler.getItemProperty(json, PROPERTY_ICON, 0))
                    .setPage(ConfigHandler.getStringProperty(json, PROPERTY_PAGE, null))
                    .setParent(ConfigHandler.getStringProperty(json, PROPERTY_PARENT, null))
                    .setSpecial(ConfigHandler.getBooleanProperty(json, PROPERTY_IS_SPECIAL, false))
                    .build();

            if (conditionHandler != null) {
                // condition must be parsed last
                conditionHandler.parse(id, conditionJson);
            }

            // JSON is valid
            PAGES.add(props.getPage());
            AmazingTrophiesAPI.registerAchievement(id, props);
        } catch (Exception e) {
            AmazingTrophies.LOGGER.error("Failed to parse achievement \"" + id + "\"!", e);
        }
    }

    static void registerMissingPages() {
        PAGES.remove(null);
        for (String name : PAGES) {
            try {
                AchievementPage.registerAchievementPage(new AchievementPage(name));
            } catch (Exception ignored) {}
        }
        PAGES.clear();
    }

}
