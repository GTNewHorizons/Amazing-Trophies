package glowredman.amazingtrophies.condition;

import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import glowredman.amazingtrophies.AchievementHandler;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.TrophyHandler;
import glowredman.amazingtrophies.api.AmazingTrophiesAPI;
import glowredman.amazingtrophies.api.ConditionHandler;

public class AnyConditionHandler extends ConditionHandler {

    public static final String ID = "any";
    public static final String PROPERTY_CONDITIONS = "conditions";

    private Function<JsonObject, ConditionHandler> conditionHandlerProvider;

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) {
        for (JsonObject childJSON : ConfigHandler
            .getSetProperty(json, PROPERTY_CONDITIONS, JsonElement::getAsJsonObject)) {
            this.getConditionHandler(childJSON)
                .parse(id, childJSON);
        }
    }

    private ConditionHandler getConditionHandler(JsonObject json) {
        if (this.conditionHandlerProvider == null) {
            this.conditionHandlerProvider = switch (this.getOwner()) {
                case AmazingTrophiesAPI.OWNER_ACHIEVEMENTS -> j -> {
                    String type = ConfigHandler.getStringProperty(j, AchievementHandler.PROPERTY_TYPE);
                    ConditionHandler conditionHandler = AmazingTrophiesAPI.getAchievementConditionHandler(type);
                    if (conditionHandler == null) {
                        throw new IllegalArgumentException("Referencing unknown condition type: \"" + type + "\"");
                    }
                    return conditionHandler;
                };
                case AmazingTrophiesAPI.OWNER_TROPHIES -> j -> {
                    String type = ConfigHandler.getStringProperty(j, TrophyHandler.PROPERTY_TYPE);
                    ConditionHandler conditionHandler = AmazingTrophiesAPI.getTrophyConditionHandler(type);
                    if (conditionHandler == null) {
                        throw new IllegalArgumentException("Referencing unknown condition type: \"" + type + "\"");
                    }
                    return conditionHandler;
                };
                default -> throw new IllegalStateException("Unexpected owner: " + this.getOwner());
            };
        }

        return this.conditionHandlerProvider.apply(json);
    }
}
