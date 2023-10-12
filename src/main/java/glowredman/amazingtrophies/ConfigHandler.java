package glowredman.amazingtrophies;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;

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

    public static <T> T getProperty(JsonObject json, String key, String id, Function<JsonElement, T> parser) {
        JsonElement element = json.get(key);
        if (element == null) {
            throw new JsonSyntaxException("\"" + id + "\" is missing required property \"" + key + "\"!");
        }
        return parser.apply(element);
    }

    public static double getDoubleProperty(JsonObject json, String key, String id) {
        return getProperty(json, key, id, JsonElement::getAsDouble);
    }

    public static String getStringProperty(JsonObject json, String key, String id) {
        return getProperty(json, key, id, JsonElement::getAsString);
    }

    public static <T> T getProperty(JsonObject json, String key, String id, Function<JsonElement, T> parser,
        T fallback) {
        JsonElement element = json.get(key);
        return element == null ? fallback : parser.apply(element);
    }

    public static int getIntegerProperty(JsonObject json, String key, String id, int fallback) {
        return getProperty(json, key, id, JsonElement::getAsInt, fallback);
    }

    public static String getStringProperty(JsonObject json, String key, String id, String fallback) {
        return getProperty(json, key, id, JsonElement::getAsString, fallback);
    }

}
