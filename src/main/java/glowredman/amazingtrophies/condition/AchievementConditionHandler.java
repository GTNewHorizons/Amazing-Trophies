package glowredman.amazingtrophies.condition;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.AchievementEvent;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import glowredman.amazingtrophies.api.ConditionHandler;

public class AchievementConditionHandler extends ConditionHandler {

    public static final String ID = "achievement";

    private final Multimap<String, String> achievements = HashMultimap.create();

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) throws JsonSyntaxException {
        if (!json.has("id")) {
            throw new JsonSyntaxException("\"" + id + "\" is missing required property \"id\"!");
        }
        try {
            this.achievements.put(
                json.get("id")
                    .getAsString(),
                id);
        } catch (Exception e) {
            throw new JsonSyntaxException("Malformed JSON!", e);
        }
    }

    @Override
    protected boolean isForgeEventHandler() {
        return !this.achievements.isEmpty();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAchievement(AchievementEvent event) {
        if (!(event.entityPlayer instanceof EntityPlayerMP player)) {
            return;
        }
        if (player.func_147099_x() // getStatFile
            .hasAchievementUnlocked(event.achievement)) {
            return;
        }
        for (String trophyID : this.achievements.get(event.achievement.statId)) {
            this.getListener()
                .accept(trophyID, player);
        }
    }

}
