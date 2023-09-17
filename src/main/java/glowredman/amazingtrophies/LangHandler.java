package glowredman.amazingtrophies;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class LangHandler implements IResourcePack, IResourceManagerReloadListener {

    protected Path langDir;
    protected Path currentLangFile;
    protected Path langFileFallback;
    protected String currentLangCode;

    @SuppressWarnings("unchecked")
    public LangHandler(Path root) {
        // resolve path
        try {
            this.langDir = root.resolve("lang");
        } catch (InvalidPathException e) {
            AmazingTrophies.LOGGER.error("Failed to resolve lang path!", e);
            return;
        }

        // create directory if needed
        try {
            Files.createDirectories(this.langDir);
        } catch (Exception e) {
            AmazingTrophies.LOGGER.error("Failed to create lang directory!", e);
            return;
        }

        // create en_US.lang for convenience if needed
        try {
            Path path = this.langDir.resolve("en_US.lang");
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
        } catch (Exception e) {
            AmazingTrophies.LOGGER.warn("Failed to create fallback lang file!", e);
        }

        // cache active lang file
        this.cacheLangFilePath();
        Minecraft.getMinecraft().defaultResourcePacks.add(this);
        ((IReloadableResourceManager) Minecraft.getMinecraft()
            .getResourceManager()).registerReloadListener(this);
    }

    protected void cacheLangFilePath() {
        this.currentLangCode = Minecraft.getMinecraft()
            .getLanguageManager()
            .getCurrentLanguage()
            .getLanguageCode();
        // cache path for currently selected language if possible
        try {
            Path path = this.langDir.resolve(this.currentLangCode + ".lang");
            this.currentLangFile = Files.exists(path) ? path : null;
        } catch (Exception e) {
            AmazingTrophies.LOGGER.error("Failed to resolve path for " + this.currentLangCode + "!", e);
        }
        if ("en_US".equals(this.currentLangCode)) {
            return;
        }
        // cache path for en_US if possible
        try {
            Path path = this.langDir.resolve("en_US.lang");
            this.langFileFallback = Files.exists(path) ? path : null;
        } catch (Exception e) {
            AmazingTrophies.LOGGER.error("Failed to resolve path for en_US!", e);
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager p_110549_1_) {
        this.cacheLangFilePath();
    }

    @Override
    public InputStream getInputStream(ResourceLocation p_110590_1_) throws IOException {
        return p_110590_1_.getResourcePath()
            .equals("lang/" + this.currentLangCode + ".lang") ? Files.newInputStream(this.currentLangFile)
                : Files.newInputStream(this.langFileFallback);
    }

    @Override
    public boolean resourceExists(ResourceLocation p_110589_1_) {
        String resourcePath = p_110589_1_.getResourcePath();
        return (resourcePath.equals("lang/" + this.currentLangCode + ".lang") && this.currentLangCode != null)
            || (resourcePath.equals("lang/en_US.lang") && this.langFileFallback != null);
    }

    @Override
    public Set<String> getResourceDomains() {
        return Collections.singleton(AmazingTrophies.MODID);
    }

    @Override
    public IMetadataSection getPackMetadata(IMetadataSerializer p_135058_1_, String p_135058_2_) throws IOException {
        return null;
    }

    @Override
    public BufferedImage getPackImage() throws IOException {
        return null;
    }

    @Override
    public String getPackName() {
        return "Amazing Trophies Lang Files";
    }

}
