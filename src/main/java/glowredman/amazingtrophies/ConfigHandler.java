package glowredman.amazingtrophies;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

public class ConfigHandler {

    public static void parseOrCreate(String fileName, Consumer<? super Entry<String, JsonElement>> action) {
        Path path = AmazingTrophies.CONFIG_DIR.resolve(fileName);
        if (Files.exists(path)) {
            try (JsonReader reader = new JsonReader(Files.newBufferedReader(path))) {
                new JsonParser().parse(reader)
                    .getAsJsonObject()
                    .entrySet()
                    .forEach(action);
            } catch (Exception e) {
                AmazingTrophies.LOGGER.error("Failed to parse " + fileName + "!", e);
            }
        } else {
            try {
                Files.write(path, Arrays.asList("{}"));
            } catch (Exception e) {
                AmazingTrophies.LOGGER.error("Failed to create " + fileName + "!", e);
            }
        }
    }

    public static <T> T getProperty(JsonObject json, String key, Function<JsonElement, T> parser) {
        JsonElement element = json.get(key);
        if (element == null || element.isJsonNull()) {
            throw new JsonSyntaxException("Required property \"" + key + "\" is missing!");
        }
        return parser.apply(element);
    }

    public static double getDoubleProperty(JsonObject json, String key) {
        return getProperty(json, key, JsonElement::getAsDouble);
    }

    public static String getStringProperty(JsonObject json, String key) {
        return getProperty(json, key, JsonElement::getAsString);
    }

    public static <T> T getProperty(JsonObject json, String key, Function<JsonElement, T> parser, T fallback) {
        JsonElement element = json.get(key);
        return element == null ? fallback : parser.apply(element);
    }

    public static boolean getBooleanProperty(JsonObject json, String key, boolean fallback) {
        return getProperty(json, key, JsonElement::getAsBoolean, fallback);
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
