package glowredman.amazingtrophies.condition;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.AchievementEvent;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.api.ConditionHandler;

public class AchievementConditionHandler extends ConditionHandler {

    public static final String ID = "achievement";
    public static final String PROPERTY_ID = "id";

    private final Multimap<String, String> conditions = HashMultimap.create();

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) {
        this.conditions.put(ConfigHandler.getStringProperty(json, PROPERTY_ID, id), id);
    }

    @Override
    protected boolean isForgeEventHandler() {
        return !this.conditions.isEmpty();
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
        for (String trophyID : this.conditions.get(event.achievement.statId)) {
            this.getListener()
                .accept(trophyID, player);
        }
    }

}
