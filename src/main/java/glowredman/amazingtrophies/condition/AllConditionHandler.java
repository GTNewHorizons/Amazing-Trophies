package glowredman.amazingtrophies.condition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.JsonSerializableSet;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import glowredman.amazingtrophies.AchievementHandler;
import glowredman.amazingtrophies.AmazingTrophies;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.TrophyHandler;
import glowredman.amazingtrophies.api.AmazingTrophiesAPI;
import glowredman.amazingtrophies.api.ConditionHandler;

public class AllConditionHandler extends ConditionHandler {

    public static final String ID = "all";
    public static final String ID_DELIMITER = "/";
    public static final String PROPERTY_CONDITIONS = "conditions";
    public static final String PROPERTY_CHILD_ID = "childID";

    private String propertyType;
    private final Map<String, ConditionHandler> conditionHandlers = new HashMap<>();
    private final Map<String, Set<String>> requiredConditions = new HashMap<>();
    private final Map<String, StatBase> stats = new HashMap<>();

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) {
        if (id.contains(ID_DELIMITER)) {
            throw new IllegalArgumentException(
                "ID \"" + id
                    + "\" contains \""
                    + ID_DELIMITER
                    + "\", this is not allowed for conditions of type \""
                    + this.getID()
                    + "\"!");
        }

        Set<String> conditions = new HashSet<>();

        for (JsonObject childJSON : ConfigHandler
            .getSetProperty(json, PROPERTY_CONDITIONS, JsonElement::getAsJsonObject)) {
            String childID = ConfigHandler.getStringProperty(childJSON, PROPERTY_CHILD_ID);
            if (conditions.contains(childID)) {
                throw new IllegalArgumentException("Duplicate child_id: \"" + childID + "\"");
            }
            String type = ConfigHandler.getStringProperty(childJSON, this.propertyType);
            if (AnyConditionHandler.PROPERTY_CONDITIONS.equals(type)) {
                throw new IllegalArgumentException(
                    "Conditions of type \"" + AnyConditionHandler.PROPERTY_CONDITIONS + "\" are not supported!");
            }
            ConditionHandler conditionHandler = this.getConditionHandler(type);
            if (conditionHandler == null) {
                throw new IllegalArgumentException("Referencing unknown condition type: \"" + type + "\"");
            }
            conditionHandler.parse(id + ID_DELIMITER + childID, childJSON);
            conditions.add(childID);
        }

        String statID = "stat." + AmazingTrophies.MODID + "." + id;
        this.stats.put(
            id,
            new StatBase(statID, new ChatComponentTranslation(statID)).func_150953_b(JsonSerializableSet.class)
                .initIndependentStat()
                .registerStat());
        this.requiredConditions.put(id, conditions);
    }

    @SuppressWarnings("unchecked")
    private void onTrigger(String compoundID, EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }
        String[] idSplit = compoundID.split(ID_DELIMITER, 2);
        String id = idSplit[0];
        String childID = idSplit[1];
        StatBase stat = this.stats.get(id);
        if (stat == null) {
            // an exception occurred in parse() such that some child conditions parsed successfully and other didn't
            return;
        }
        Set<String> required = this.requiredConditions.get(id);
        StatisticsFile statFile = ((EntityPlayerMP) player).func_147099_x(); // getStatFile
        Set<String> completed = (Set<String>) statFile.func_150870_b(stat);

        if (completed == null) {
            completed = (Set<String>) statFile.func_150872_a(stat, new JsonSerializableSet());
        }

        completed.add(childID);

        if (completed.size() < required.size()) {
            return;
        }

        required = new HashSet<>(required);

        for (String condition : completed) {
            Iterator<String> iter = required.iterator();
            while (iter.hasNext()) {
                if (StringUtils.equals(iter.next(), condition)) {
                    iter.remove();
                }
            }

            if (required.isEmpty()) {
                break;
            }
        }

        if (required.isEmpty()) {
            this.getListener()
                .accept(id, player);
            completed.clear(); // reset when all conditions are met
        }
    }

    private ConditionHandler getConditionHandler(String id) {
        return this.conditionHandlers.get(id);
    }

    @Override
    public void preParsing() {
        for (String id : AmazingTrophiesAPI.getConditionHandlerProviderIDs()) {
            ConditionHandler conditionHandler = AmazingTrophiesAPI.getConditionHandlerProvider(id)
                .get();
            conditionHandler.setListener(this::onTrigger);
            this.conditionHandlers.put(id, conditionHandler);
        }

        this.propertyType = switch (this.getOwner()) {
            case AmazingTrophiesAPI.OWNER_ACHIEVEMENTS -> AchievementHandler.PROPERTY_TYPE;
            case AmazingTrophiesAPI.OWNER_TROPHIES -> TrophyHandler.PROPERTY_TYPE;
            default -> {
                AmazingTrophies.LOGGER.error("Unexpected Owner: {}", this.getOwner(), new IllegalStateException());
                yield null;
            }
        };
    }

    @Override
    public void registerAsEventHandler() {
        this.conditionHandlers.values()
            .forEach(ConditionHandler::registerAsEventHandler);
    }
}
