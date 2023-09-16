package glowredman.amazingtrophies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import glowredman.amazingtrophies.api.Reference;
@Mod(acceptedMinecraftVersions = "[1.7.10]", dependencies = "required-after:gtnhlib@[0.0.10)", modid = AmazingTrophies.MODID, name = AmazingTrophies.MODNAME, version = AmazingTrophies.VERSION)
public class AmazingTrophies {
    
    public static final String MODID = "amazingtrophies";
    public static final String MODNAME = "Amazing Trophies";
    public static final String VERSION = Reference.VERSION;
    static final Logger LOGGER = LogManager.getLogger(MODNAME);

}
