package glowredman.amazingtrophies.api;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

/**
 * Subclasses of this are delegates for rendering a trophy in-world, in the inventory and when equipped. There exists no
 * more than one instance per {@link TrophyProperties} object.
 * 
 * @author glowredman
 *
 */
@ParametersAreNonnullByDefault
public abstract class TrophyModelHandler {

    /**
     * Parses the given JSON object and stores the details internally (this is up to the implementing class).
     * 
     * @param id   the string identifying the parent JSON object
     * @param json the JSON object to parse
     * @throws JsonSyntaxException if the JSON object is not conforming with the syntax specified by the implementing
     *                             TrophyModelHandler
     */
    public void parse(String id, JsonObject json) throws JsonSyntaxException {}

    /**
     * Renders the trophy at the give position.
     * 
     * @param x               the x coordinate to render at
     * @param y               the y coordinate to render at
     * @param z               the z coordinate to render at
     * @param rotation        A value in the range of 0 - 15 (both inclusive). Each step represents a rotation by 22.5°.
     * @param name            The name of the player who received the trophy to render. May be {@code null} or empty if
     *                        the
     *                        player is unknown.
     * @param time            the time, in milliseconds, since 1970-01-01 00:00 (UTC)
     * @param partialTickTime How much time has elapsed since the last tick, in ticks (range: 0.0 - 1.0)
     */
    public abstract void render(double x, double y, double z, int rotation, @Nullable String name, long time,
        float partialTickTime);

}
