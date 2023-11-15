package glowredman.amazingtrophies.api;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraftforge.common.AchievementPage;

/**
 * A class to store all properties of a custom {@link Achievement}.
 * 
 * @author glowredman
 */
@ParametersAreNonnullByDefault
public class AchievementProperties {

    private final String page;
    private final int x;
    private final int y;
    private final String parent;
    private final boolean isSpecial;
    private final ItemDefinition icon;

    private String id;
    private boolean registered = false;

    private AchievementProperties(@Nullable String page, int x, int y, @Nullable String parent, boolean isSpecial,
        ItemDefinition icon) {
        this.page = page;
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.isSpecial = isSpecial;
        this.icon = icon;
    }

    /**
     * The Achievement page to display this custom achievement on
     */
    public String getPage() {
        return this.page;
    }

    /**
     * The achievement's horizontal coordinate
     */
    public int getX() {
        return this.x;
    }

    /**
     * The achievement's vertical coordinate
     */
    public int getY() {
        return this.y;
    }

    /**
     * The stat ID of the achievement which needs to be completed before this one can be completed. If {@code null},
     * this achievement can always be completed.
     */
    @Nullable
    public String getParent() {
        return this.parent;
    }

    /**
     * Special achievements have a different background in the achievements screen and are mentioned in dark purple in
     * the chat
     */
    public boolean isSpecial() {
        return this.isSpecial;
    }

    /**
     * The item that is displayed in the achievements screen and ingame when the player gains it
     */
    public ItemDefinition getIcon() {
        return this.icon;
    }

    /**
     * The ID used to register this instance to the {@link AmazingTrophiesAPI}
     */
    public String getID() {
        return this.id;
    }

    void setID(String id) {
        this.id = id;
    }

    /**
     * Whether or not an {@link Achievement} with properties defined by this instance has been registered to the
     * {@link StatList} and {@link AchievementList}.
     * 
     * @see Achievement#registerStat()
     */
    public boolean isRegistered() {
        return this.registered;
    }

    /**
     * Creates an {@link Achievement} object with the properties describe by this {@code AchievementProperties}
     * instance. The achievement is added to its {@link AchievementPage} and registered. If a parent stat is specified
     * which is
     * either not (yet) registered or not an achievement or the achievement page could not be found, nothing happens and
     * an error is logged.
     */
    public void register() {
        if (this.registered) {
            return;
        }

        Achievement parent = null;
        String parentID = this.parent;
        if (parentID != null && !parentID.isEmpty()) {
            StatBase stat = StatList.func_151177_a(parentID); // getOneShotStat
            if (!(stat instanceof Achievement)) {
                AmazingTrophiesAPI.LOGGER
                    .error("Parent achievement {} of {} is invalid or not yet registered!", this.parent, this.id);
                return;
            }
            parent = (Achievement) stat;
        }
        Achievement achievement = new Achievement(this.id, this.id, this.x, this.y, this.icon.getAsStack(), parent);
        if (parent == null) {
            achievement.initIndependentStat();
        }
        if (this.isSpecial) {
            achievement.setSpecial();
        }

        if (this.page == null) {
            achievement.registerStat();
            this.registered = true;
            return;
        }

        AchievementPage page = AchievementPage.getAchievementPage(this.page);
        if (page == null) {
            AmazingTrophiesAPI.LOGGER
                .error("Achievement page {} of achievement {} is invalid or not yet registered!", this.page, this.id);
            return;
        }
        achievement.registerStat();
        page.getAchievements()
            .add(achievement);
        this.registered = true;
    }

    /**
     * Two {@code AchievementProperties} objects are equal if and only if their {@link #id ids} are
     * {@link String#equals(Object) equal}.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        return obj instanceof AchievementProperties other && this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return String.format("AchievementProperties(id=\"%s\")", this.id);
    }

    /**
     * A builder class for {@link AchievementProperties} instances.
     * 
     * @author glowredman
     */
    @ParametersAreNonnullByDefault
    public static class Builder {

        private String page;
        private final int x;
        private final int y;
        private String parent;
        private boolean isSpecial;
        private final ItemDefinition icon;

        /**
         * Creates an builder instance for {@code AchievementProperties}.
         * 
         * @param x    The achievement's horizontal coordinate
         * @param y    The achievement's vertical coordinate
         * @param icon The item that is displayed in the achievements screen and ingame when the player gains it
         */
        public Builder(int x, int y, ItemDefinition icon) {
            this.x = x;
            this.y = y;
            this.icon = icon;
        }

        /**
         * Sets the Achievement page to display this custom achievement on. If {@code null}, the achievement will be
         * displayed on the default Minecraft page.
         * 
         * @return This {@code Builder} instance to allow chaining of methods.
         */
        public Builder setPage(@Nullable String page) {
            this.page = page;
            return this;
        }

        /**
         * The stat ID of the achievement which needs to be completed before this one can be completed. If {@code null},
         * this achievement can always be completed.
         * 
         * @return This {@code Builder} instance to allow chaining of methods.
         */
        public Builder setParent(@Nullable String parent) {
            this.parent = parent;
            return this;
        }

        /**
         * Special achievements have a different background in the achievements screen and are mentioned in dark purple
         * in the chat. This method enables that behavior.
         * 
         * @return This {@code Builder} instance to allow chaining of methods.
         * @see #setSpecial(boolean)
         */
        public Builder setSpecial() {
            this.isSpecial = true;
            return this;
        }

        /**
         * Special achievements have a different background in the achievements screen and are mentioned in dark purple
         * in the chat. This method enables or disables that behavior depending on the input argument.
         * 
         * @return This {@code Builder} instance to allow chaining of methods.
         * @see #setSpecial()
         */
        public Builder setSpecial(boolean isSpecial) {
            this.isSpecial = isSpecial;
            return this;
        }

        /**
         * Constructs an instance of {@link AchievementProperties} with properties specified by this builder instance.
         */
        public AchievementProperties build() {
            return new AchievementProperties(this.page, this.x, this.y, this.parent, this.isSpecial, this.icon);
        }

    }

}
