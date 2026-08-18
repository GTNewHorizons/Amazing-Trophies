package glowredman.amazingtrophies;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import glowredman.amazingtrophies.api.ItemDefinition;
import glowredman.amazingtrophies.integration.MaterialLibStacks;

public class ConfigHandler {

    public static final String PROPERTY_REGISTRY_NAME = "registryName";
    public static final String PROPERTY_META = "meta";
    public static final String PROPERTY_NBT = "nbt";
    private static final String MATERIALLIB_MODID = "materiallib";
    private static final String MATERIALLIB_PREFIX = "ml:";
    private static final JsonParser PARSER = new JsonParser();
    private static final Comparator<Path> COMPARATOR = new Comparator<>() {

        @Override
        public int compare(Path o1, Path o2) {
            return this.compare(
                AmazingTrophies.CONFIG_DIR.relativize(o1)
                    .toString(),
                AmazingTrophies.CONFIG_DIR.relativize(o2)
                    .toString());
        }

        private int compare(String o1, String o2) {
            int index1 = o1.indexOf('/');
            int index2 = o2.indexOf('/');
            if (index1 == -1) {
                index1 = o1.indexOf('\\');
            }
            if (index2 == -1) {
                index2 = o2.indexOf('\\');
            }

            if (index1 == -1) {
                if (index2 == -1) {
                    // both paths do not contain dirs
                    return o1.compareToIgnoreCase(o2);
                }
                // path 1 does not contain a dir, path 2 does
                return 1;
            }
            if (index2 == -1) {
                // path 1 contains a dir, path 2 does not
                return -1;
            }
            // both paths contain a dir
            String dir1 = o1.substring(0, index1);
            String dir2 = o2.substring(0, index2);
            int i = dir1.compareToIgnoreCase(dir2);
            if (i == 0) {
                // both paths start with the same dir
                return this.compare(o1.substring(index1 + 1), o2.substring(index2 + 1));
            }
            // the paths start with different dirs
            return i;
        }
    };

    private static int materialLibResolved;
    private static int materialLibInvalid;

    static void parseOrCreate(String directoryName, Consumer<JsonElement> action) {
        Path dir = AmazingTrophies.CONFIG_DIR.resolve(directoryName);
        try {
            Files.createDirectories(dir);
        } catch (Exception e) {
            AmazingTrophies.LOGGER.error("Failed to create directory " + dir + "!", e);
            return;
        }
        try (Stream<Path> files = Files.walk(dir)) {
            files.filter(
                path -> Files.isRegularFile(path) && StringUtils.endsWithIgnoreCase(
                    path.getFileName()
                        .toString(),
                    ".json"))
                .sorted(COMPARATOR)
                .forEachOrdered(path -> parseFile(path, action));
        } catch (Exception e) {
            AmazingTrophies.LOGGER.error("Failed to list files in " + dir + "!", e);
        }
    }

    private static void parseFile(Path path, Consumer<JsonElement> action) {
        try (JsonReader reader = new JsonReader(Files.newBufferedReader(path))) {
            action.accept(PARSER.parse(reader));
        } catch (Exception e) {
            AmazingTrophies.LOGGER.error("Failed to parse " + AmazingTrophies.CONFIG_DIR.relativize(path) + "!", e);
        }
    }

    /**
     * Constructs the {@link ItemStack} a config entry names.
     * <p>
     * A registry name of the form {@code ml:<Material>:<shape>} is a MaterialLib reference (see
     * {@link MaterialLibStacks}) and takes the resolved damage in place of {@code meta}.
     *
     * @return {@code null} if the item does not exist
     * @see GameRegistry#makeItemStack(String, int, int, String)
     */
    public static ItemStack makeItemStack(String registryName, int meta, String nbt) {
        if (registryName.startsWith(MATERIALLIB_PREFIX)) {
            ItemDefinition definition = resolveMaterialLibDefinition(registryName, nbt);
            return definition == null ? null : definition.getAsStack();
        }
        // FML completely ignores the stackSize parameter in the method's implementation...
        return GameRegistry.makeItemStack(registryName, meta, 0, nbt);
    }

    /**
     * Logs how many MaterialLib references the config files carried, then forgets them.
     */
    static void logMaterialLibSummary() {
        if (materialLibResolved + materialLibInvalid == 0) {
            return;
        }
        AmazingTrophies.LOGGER.info(
            "{}: resolved {} MaterialLib entries ({} invalid)",
            AmazingTrophies.MODNAME,
            materialLibResolved,
            materialLibInvalid);
        materialLibResolved = 0;
        materialLibInvalid = 0;
    }

    /**
     * Restates a MaterialLib reference as a definition of the resolved item, so {@link ItemDefinition} never has to
     * know about the {@code ml:} form.
     *
     * @return {@code null} if the reference does not resolve
     */
    private static ItemDefinition resolveMaterialLibDefinition(String registryName, String nbt) {
        ItemStack stack = lookupMaterialLibStack(registryName);
        UniqueIdentifier id = stack == null ? null : GameRegistry.findUniqueIdentifierFor(stack.getItem());
        countMaterialLibEntry(id != null);
        if (id == null) {
            return null;
        }
        return new ItemDefinition(id.toString(), stack.getItemDamage(), nbt);
    }

    private static ItemStack lookupMaterialLibStack(String registryName) {
        if (Loader.isModLoaded(MATERIALLIB_MODID)) {
            return MaterialLibStacks.resolve(registryName);
        }
        AmazingTrophies.LOGGER.error("Cannot resolve item {}: MaterialLib is not installed!", registryName);
        return null;
    }

    private static void countMaterialLibEntry(boolean resolved) {
        if (resolved) {
            materialLibResolved++;
        } else {
            materialLibInvalid++;
        }
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends Entity> parseEntityClass(JsonElement json) {
        String name = json.getAsString();
        // entity names may also be used to identify the target entity
        Class<?> clazz = EntityList.stringToClassMapping.get(name);
        if (clazz != null) {
            return (Class<? extends Entity>) clazz;
        }
        // not a valid entity name, try parsing as class name
        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Could not find target class!", e);
        }
        if (Entity.class.isAssignableFrom(clazz)) {
            return (Class<? extends Entity>) clazz;
        } else {
            throw new IllegalArgumentException(name + " is not a subclass of " + Entity.class.getName() + "!");
        }
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends EntityLivingBase> parseEntityLivingClass(JsonElement element) {
        String className = element.getAsString();
        // entity names may also be used to identify the target entity
        Class<?> clazz = EntityList.stringToClassMapping.get(className);
        if (clazz == null) {
            // not a valid entity name, try parsing as class name
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Could not find target class!", e);
            }
        }
        if (EntityLivingBase.class.isAssignableFrom(clazz)) {
            return (Class<? extends EntityLivingBase>) clazz;
        } else {
            throw new IllegalArgumentException(
                className + " is not a subclass of " + EntityLivingBase.class.getName() + "!");
        }
    }

    public static <T> T getProperty(JsonObject json, String key, Function<JsonElement, T> parser) {
        JsonElement element = json.get(key);
        if (element == null || element.isJsonNull()) {
            throw new JsonSyntaxException("Required property \"" + key + "\" is missing!");
        }
        return parser.apply(element);
    }

    public static int getIntegerProperty(JsonObject json, String key) {
        return getProperty(json, key, JsonElement::getAsInt);
    }

    /**
     * Reads an {@link ItemDefinition} from the named property.
     * <p>
     * A registry name of the form {@code ml:<Material>:<shape>} names the resolved item and takes its damage in place
     * of {@code meta}. An unresolvable reference is kept verbatim.
     */
    public static ItemDefinition getItemProperty(JsonObject json, String key, int defaultMeta) {
        JsonObject definitionJson = json.getAsJsonObject(key);
        String registryName = getStringProperty(definitionJson, PROPERTY_REGISTRY_NAME);
        int meta = getIntegerProperty(definitionJson, PROPERTY_META, defaultMeta);
        String nbt = getStringProperty(definitionJson, PROPERTY_NBT, null);
        if (registryName.startsWith(MATERIALLIB_PREFIX)) {
            ItemDefinition resolved = resolveMaterialLibDefinition(registryName, nbt);
            if (resolved != null) {
                return resolved;
            }
        }
        return new ItemDefinition(registryName, meta, nbt);
    }

    public static JsonObject getObjectProperty(JsonObject json, String key) {
        return getProperty(json, key, JsonElement::getAsJsonObject);
    }

    public static <T> Set<T> getSetProperty(JsonObject json, String key, Function<JsonElement, T> parser) {
        return getProperty(json, key, jsonElement -> {
            Set<T> set = new HashSet<>();
            if (jsonElement.isJsonPrimitive()) {
                set.add(parser.apply(jsonElement));
                return set;
            }
            JsonArray array = jsonElement.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                set.add(parser.apply(array.get(i)));
            }
            return set;
        });
    }

    public static String getStringProperty(JsonObject json, String key) {
        return getProperty(json, key, JsonElement::getAsString);
    }

    public static <T> T getProperty(JsonObject json, String key, Function<JsonElement, T> parser, T fallback) {
        JsonElement element = json.get(key);
        if (element == null) {
            return fallback;
        }
        if (element.isJsonNull()) {
            return null;
        }
        return parser.apply(element);
    }

    public static boolean getBooleanProperty(JsonObject json, String key, boolean fallback) {
        return getProperty(json, key, JsonElement::getAsBoolean, fallback);
    }

    public static double getDoubleProperty(JsonObject json, String key, double fallback) {
        return getProperty(json, key, JsonElement::getAsDouble, fallback);
    }

    public static float getFloatProperty(JsonObject json, String key, float fallback) {
        return getProperty(json, key, JsonElement::getAsFloat, fallback);
    }

    public static int getIntegerProperty(JsonObject json, String key, int fallback) {
        return getProperty(json, key, JsonElement::getAsInt, fallback);
    }

    public static <T> Set<T> getSetProperty(JsonObject json, String key, Function<JsonElement, T> parser,
        Set<T> fallback) {
        return getProperty(json, key, jsonElement -> {
            Set<T> set = new HashSet<>();
            if (jsonElement.isJsonPrimitive()) {
                set.add(parser.apply(jsonElement));
                return set;
            }
            JsonArray array = jsonElement.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                set.add(parser.apply(array.get(i)));
            }
            return set;
        }, fallback);
    }

    public static String getStringProperty(JsonObject json, String key, String fallback) {
        return getProperty(json, key, JsonElement::getAsString, fallback);
    }

}
