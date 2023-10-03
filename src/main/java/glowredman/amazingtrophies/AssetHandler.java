package glowredman.amazingtrophies;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.util.Collections;
import java.util.Set;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class AssetHandler implements IResourcePack {

    public static ResourceLocation getResourceLocation(String location, String dir) {
        if (location.indexOf(':') == -1) {
            return new ResourceLocation(AmazingTrophies.MODID, dir + location);
        }
        return new ResourceLocation(location);
    }

    @Override
    public InputStream getInputStream(ResourceLocation p_110590_1_) throws IOException {
        try {
            return Files.newInputStream(AmazingTrophies.CONFIG_DIR.resolve(p_110590_1_.getResourcePath()));
        } catch (InvalidPathException e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean resourceExists(ResourceLocation p_110589_1_) {
        try {
            return Files.exists(AmazingTrophies.CONFIG_DIR.resolve(p_110589_1_.getResourcePath()));
        } catch (InvalidPathException ignored) {
            return false;
        }
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
        return "Amazing Trophies Custom Assets";
    }

}
