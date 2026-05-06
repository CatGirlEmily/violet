package violet.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import violet.misc.Utils;

import java.nio.file.Files;
import java.nio.file.Path;

import static violet.Main.LOGGER;

public class Config {
    private static final Path folderPath = FabricLoader.getInstance().getConfigDir().resolve("violet");
    private static final Path filePath = folderPath.resolve("config.json");
    private static JsonObject data = new JsonObject();
    private static int hash = 0;

    public static Path getFolderPath() {
        return folderPath;
    }

    public static void load() {
        if (Files.exists(filePath)) {
            try {
                data = JsonParser.parseString(Files.readString(filePath)).getAsJsonObject();
            } catch (Exception exception) {
                LOGGER.error("Unable to load violet config file!", exception);
            }
        } else {
            save();
        }
        computeHash();
    }

    public static void save() {
        try {
            Utils.atomicWrite(filePath, data);
        } catch (Exception exception) {
            LOGGER.error("Unable to save violet config file!", exception);
        }
    }

    public static void saveAsync() {
        Thread.startVirtualThread(Config::save);
    }

    public static int getHash() {
        return hash;
    }

    public static void computeHash() {
        hash = data.hashCode();
    }

    public static JsonObject get() {
        return data;
    }

    public static boolean isNew() {
        return !Files.exists(filePath);
    }
}
