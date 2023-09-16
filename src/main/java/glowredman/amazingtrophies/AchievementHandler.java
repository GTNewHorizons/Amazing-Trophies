package glowredman.amazingtrophies;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import glowredman.amazingtrophies.api.AchievementProperties;
import glowredman.amazingtrophies.api.AmazingTrophiesAPI;
import net.minecraftforge.common.AchievementPage;

public class AchievementHandler {
    
    private static final Collection<String> PAGES = new HashSet<>();
    
    static void parseAchievement(Entry<String, JsonElement> entry) {
        String id = entry.getKey();
        if(id == null) {
            AmazingTrophies.LOGGER.error("Found null-key in achievements.json! The value will be ignored.");
            return;
        }
        try {
            JsonObject json = (JsonObject) entry.getValue();
            JsonObject conditionJson = json.getAsJsonObject("condition");
            if(conditionJson == null) {
                throw new JsonSyntaxException("Achievement \"" + id + "\" is missing required property \"condition\"!");
            }
            JsonElement typeJson = conditionJson.get("type");
            if(typeJson == null) {
                throw new JsonSyntaxException("Achievement \"" + id + "\" is missing required property \"condition/type\"!");
            }
            AchievementProperties props = AchievementProperties.parse(json, id);

            // condition must be parsed last
            AmazingTrophiesAPI.getAchievementConditionHandler(typeJson.getAsString()).parse(id, conditionJson);
            
            // JSON is valid
            PAGES.add(props.page);
            AmazingTrophiesAPI.addAchievement(id, props);
        } catch (Exception e) {
            AmazingTrophies.LOGGER.error("An error occured while parsing achievement \"" + id + "\"!", e);
        }
    }
    
    static void registerMissingPages() {
        for(String name : PAGES) {
            try {
                AchievementPage.registerAchievementPage(new AchievementPage(name));
            } catch (Exception ignored) {}
        }
        
        PAGES.clear();
    }

}
