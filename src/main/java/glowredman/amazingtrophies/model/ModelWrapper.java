package glowredman.amazingtrophies.model;

import net.minecraft.util.ResourceLocation;

import com.gtnewhorizon.gtnhlib.client.model.wavefront.WavefrontVBOBuilder;
import com.gtnewhorizon.gtnhlib.client.renderer.vao.IVertexArrayObject;

public class ModelWrapper {

    public static IVertexArrayObject get(ResourceLocation resource) {
        return WavefrontVBOBuilder.compileToVBO(resource);
    }
}
