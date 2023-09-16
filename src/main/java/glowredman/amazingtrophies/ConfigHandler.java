package glowredman.amazingtrophies;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import glowredman.amazingtrophies.api.AmazingTrophiesAPI;

public class ConfigHandler {

    public static void parseOrCreate(String fileName, Consumer<? super Entry<String, JsonElement>> action) {
        Path path = AmazingTrophiesAPI.CONFIG_DIR.resolve(fileName);
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

}
