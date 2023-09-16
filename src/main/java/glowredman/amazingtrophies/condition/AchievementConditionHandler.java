package glowredman.amazingtrophies.condition;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.event.entity.player.AchievementEvent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import glowredman.amazingtrophies.api.ConditionHandler;

public class AchievementConditionHandler extends ConditionHandler {

    public static final String ID = "achievement";

    private final Map<String, String> achievements = new HashMap<>();

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) {
        if (!json.has("id")) {
            throw new JsonParseException(id + "is missing required property \"id\"!");
        }
        this.achievements.put(
            json.get("id")
                .getAsString(),
            id);
    }

    @Override
    public boolean isForgeEventHandler() {
        return true;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAchievement(AchievementEvent event) {
        String trophyID = achievements.get(event.achievement.statId);
        if (trophyID == null) {
            return;
        }
        this.getListener()
            .accept(trophyID, event.entityPlayer);
    }

}
