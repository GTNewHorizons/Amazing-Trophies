package glowredman.amazingtrophies;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

import org.apache.commons.io.FileUtils;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Loader;
import glowredman.amazingtrophies.api.AmazingTrophiesAPI;
import glowredman.amazingtrophies.model.BasicTrophyModelHandler;
import glowredman.amazingtrophies.model.EntityTrophyModelHandler;
import glowredman.amazingtrophies.model.ItemTrophyModelHandler;
import glowredman.amazingtrophies.model.PedestalTrophyModelHandler;
import glowredman.amazingtrophies.trophy.RendererTrophy;
import glowredman.amazingtrophies.trophy.TileEntityTrophy;

public class ClientHandler {

    @SuppressWarnings("unchecked")
    static void setupAssetHandler() {
        if (Loader.isModLoaded("txloader")) {
            moveAssetDirsAndFiles();
        } else {
            FMLClientHandler.instance()
                .getClient().defaultResourcePacks.add(new AssetHandler());
            createAssetDirsAndFiles();
        }
    }

    static void registerTrophyModelHandlers() {
        // spotless:off
        AmazingTrophiesAPI.registerTrophyModelHandlerProvider(PedestalTrophyModelHandler.ID, PedestalTrophyModelHandler::new);
        AmazingTrophiesAPI.registerTrophyModelHandlerProvider(BasicTrophyModelHandler.ID, BasicTrophyModelHandler::new);
        AmazingTrophiesAPI.registerTrophyModelHandlerProvider(EntityTrophyModelHandler.ID, EntityTrophyModelHandler::new);
        AmazingTrophiesAPI.registerTrophyModelHandlerProvider(ItemTrophyModelHandler.ID, ItemTrophyModelHandler::new);
        // spotless:on
    }

    static void setupTrophyRenderer() {
        RendererTrophy trophyRenderer = new RendererTrophy();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTrophy.class, trophyRenderer);
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(AmazingTrophiesAPI.getTrophyBlock()), trophyRenderer);
    }

    private static void moveAssetDirsAndFiles() {
        try {
            File dest = new File(
                Loader.instance()
                    .getConfigDir(),
                "txloader" + File.separator + "load" + File.separator + AmazingTrophies.MODID);
            dest.mkdirs();
            Files.list(AmazingTrophies.CONFIG_DIR)
                .filter(Files::isDirectory)
                .forEach(dir -> {
                    try {
                        FileUtils.moveDirectory(dir.toFile(), dest);
                    } catch (Exception e) {
                        AmazingTrophies.LOGGER.error("Failed to move " + dir + " to " + dest, e);
                    }
                });
        } catch (Exception e) {
            AmazingTrophies.LOGGER.error("Failed to get subpaths of " + AmazingTrophies.CONFIG_DIR, e);
        }
    }

    private static void createAssetDirsAndFiles() {
        try {
            Path langDir = AmazingTrophies.CONFIG_DIR.resolve("lang");
            Path langFile = langDir.resolve("en_US.lang");
            Files.createDirectories(langDir);
            Files.createDirectories(AmazingTrophies.CONFIG_DIR.resolve("models"));
            Files.createDirectories(
                AmazingTrophies.CONFIG_DIR.resolve("textures")
                    .resolve("blocks"));
            if (Files.notExists(langFile)) {
                Files.createFile(langFile);
            }
        } catch (Exception e) {
            AmazingTrophies.LOGGER.warn("Could not create default dirs/files for custom assets.", e);
        }
    }

}
