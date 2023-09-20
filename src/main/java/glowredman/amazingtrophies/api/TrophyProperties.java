package glowredman.amazingtrophies.api;

import javax.annotation.ParametersAreNonnullByDefault;

import glowredman.amazingtrophies.trophy.BlockTrophy;

@ParametersAreNonnullByDefault
public class TrophyProperties {

    // TODO javadoc for all
    String id;
    BlockTrophy block;

    public String getID() {
        return this.id;
    }

}
