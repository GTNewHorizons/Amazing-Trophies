package glowredman.amazingtrophies.api;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A class to store all properties of a trophy.
 * 
 * @author glowredman
 *
 */
@ParametersAreNonnullByDefault
public class TrophyProperties {

    private final TrophyModelHandler modelHandler;
    private String id;

    /**
     * Creates an instance of {@code TrophyProperties}.
     * 
     * @param modelHandler The trophy model handler used to render this trophy. Must be {@code null} on the server. May
     *                     be {@code null} on the client if no model should be rendered.
     */
    public TrophyProperties(@Nullable TrophyModelHandler modelHandler) {
        this.modelHandler = modelHandler;
    }

    /**
     * Gets the trophy model handler associated with this trophy.
     * 
     * @return {@code null} if there is no model to render or if this called on the server
     */
    @Nullable
    public TrophyModelHandler getModelHandler() {
        return this.modelHandler;
    }

    /**
     * The ID used to register this instance to the {@link AmazingTrophiesAPI}.
     */
    public String getID() {
        return this.id;
    }

    void setID(String id) {
        this.id = id;
    }

    /**
     * Two {@code TrophyProperties} objects are equal if and only if their {@link #id ids} are
     * {@link String#equals(Object) equal}.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        return obj instanceof TrophyProperties other && this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return String.format("TrophyProperties(id=\"%s\")", this.id);
    }

}
