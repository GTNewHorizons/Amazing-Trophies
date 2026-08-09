package glowredman.amazingtrophies;

import java.nio.file.Files;
import java.nio.file.Path;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import glowredman.amazingtrophies.api.AmazingTrophiesAPI;
import glowredman.amazingtrophies.model.BasicTrophyModelHandler;
import glowredman.amazingtrophies.model.EntityTrophyModelHandler;
import glowredman.amazingtrophies.model.ItemTrophyModelHandler;
import glowredman.amazingtrophies.model.PedestalTrophyModelHandler;
import glowredman.amazingtrophies.model.complex.ComplexTrophyModelHandler;
import glowredman.amazingtrophies.trophy.RendererTrophy;
import glowredman.amazingtrophies.trophy.TileEntityTrophy;

public class ClientHandler {

    @SuppressWarnings("unchecked")
    static void setupAssetHandler() {
        FMLClientHandler.instance()
            .getClient().defaultResourcePacks.add(new AssetHandler());
        createAssetDirsAndFiles();
    }

    static void registerTrophyModelHandlers() {
        // spotless:off
        AmazingTrophiesAPI.registerTrophyModelHandlerProvider(PedestalTrophyModelHandler.ID, PedestalTrophyModelHandler::new);
        AmazingTrophiesAPI.registerTrophyModelHandlerProvider(BasicTrophyModelHandler.ID, BasicTrophyModelHandler::new);
        AmazingTrophiesAPI.registerTrophyModelHandlerProvider(EntityTrophyModelHandler.ID, EntityTrophyModelHandler::new);
        AmazingTrophiesAPI.registerTrophyModelHandlerProvider(ItemTrophyModelHandler.ID, ItemTrophyModelHandler::new);
        AmazingTrophiesAPI.registerTrophyModelHandlerProvider(ComplexTrophyModelHandler.ID, ComplexTrophyModelHandler::new);
        // spotless:on
    }

    static void setupTrophyRenderer() {
        RendererTrophy trophyRenderer = new RendererTrophy();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTrophy.class, trophyRenderer);
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(AmazingTrophiesAPI.getTrophyBlock()), trophyRenderer);
        MinecraftForge.EVENT_BUS.register(new LabelCacheInvalidationHandler());
    }

    public static final class LabelCacheInvalidationHandler {

        @SubscribeEvent
        public void onTextureStitchPost(TextureStitchEvent.Post event) {
            PedestalTrophyModelHandler.clearLabelCache();
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
