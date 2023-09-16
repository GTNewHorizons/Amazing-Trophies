package glowredman.amazingtrophies.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraftforge.common.AchievementPage;

// TODO javadoc
public class AchievementProperties {

    // TODO javadoc for all
    public final String page;
    public final int x;
    public final int y;
    public final String parent;
    public final boolean isSpecial;
    public final ItemDefinition icon;
    transient String id;
    private transient boolean registered = false;
    
    // TODO javadoc
    public AchievementProperties(String page, int x, int y, ItemDefinition icon) {
        this(page, x, y, null, icon);
    }

    // TODO javadoc
    public AchievementProperties(String page, int x, int y, String parent, ItemDefinition icon) {
        this(page, x, y, parent, false, icon);
    }

    // TODO javadoc
    public AchievementProperties(String page, int x, int y, boolean isSpecial, ItemDefinition icon) {
        this(page, x, y, null, isSpecial, icon);
    }

    // TODO javadoc
    public AchievementProperties(String page, int x, int y, String parent, boolean isSpecial, ItemDefinition icon) {
        this.page = page;
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.isSpecial = isSpecial;
        this.icon = icon;
    }
    
    public static AchievementProperties parse(JsonObject json, String id) throws JsonSyntaxException {
        JsonElement pageJson = json.get("page");
        if(pageJson == null) {
            throw new JsonSyntaxException("Achievement \"" + id + "\" is missing required property \"page\"!");
        }
        JsonElement xJson = json.get("x");
        if(xJson == null) {
            throw new JsonSyntaxException("Achievement \"" + id + "\" is missing required property \"x\"!");
        }
        JsonElement yJson = json.get("y");
        if(yJson == null) {
            throw new JsonSyntaxException("Achievement \"" + id + "\" is missing required property \"y\"!");
        }
        JsonElement parentJson = json.get("parent");
        JsonElement isSpecialJson = json.get("isSpecial");
        try {
            return new AchievementProperties(pageJson.getAsString(), xJson.getAsInt(), yJson.getAsInt(), parentJson == null || parentJson.isJsonNull() ? null : parentJson.getAsString(), isSpecialJson == null ? false : isSpecialJson.getAsBoolean(), ItemDefinition.parse(json.getAsJsonObject("icon")));
        } catch (ClassCastException | IllegalStateException | JsonSyntaxException e) {
            throw new JsonSyntaxException("Malformed JSON in achievement \"" + id + "\"!", e);
        }
    }
    
    // TODO javadoc
    public String getID() {
        return this.id;
    }
    
    // TODO javadoc
    public void register() {
        if(this.registered) {
            return;
        }
        
        Achievement parent = null;
        String parentID = this.parent;
        if(parentID != null && !parentID.isEmpty()) {
            StatBase stat = StatList.func_151177_a(parentID);
            if(!(stat instanceof Achievement)) {
                AmazingTrophiesAPI.LOGGER.error("Parent achievement {} of {} is invalid or not yet registered!", this.parent, this.id);
                return;
            }
            parent = (Achievement) stat;
        }
        Achievement achievement = new Achievement(this.id, this.id, this.x, this.y, this.icon.getAsStack(), parent);
        if(parent == null) {
            achievement.initIndependentStat();
        }
        if(this.isSpecial) {
            achievement.setSpecial();
        }
        
        AchievementPage page = AchievementPage.getAchievementPage(this.page);
        if(page == null) {
            AmazingTrophiesAPI.LOGGER.error("Achievement page {} of achievement {} is invalid or not yet registered!", this.page, this.id);
            return;
        }
        achievement.registerStat();
        page.getAchievements().add(achievement);
        this.registered = true;
    }
    
    // TODO javadoc
    public boolean isRegistered() {
        return this.registered;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        return obj instanceof AchievementProperties other && this.id.equals(other.id);
    }
    
    @Override
    public String toString() {
        return String.format("AchievementProperties(id=\"%s\")", this.id);
    }

}
