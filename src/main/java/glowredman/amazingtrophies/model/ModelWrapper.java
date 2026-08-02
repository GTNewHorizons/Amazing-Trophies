package glowredman.amazingtrophies.model;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.ModelFormatException;

import com.gtnewhorizon.gtnhlib.client.renderer.vbo.IModelCustomExt;

import glowredman.amazingtrophies.AmazingTrophies;

public abstract class ModelWrapper<T extends IModelCustom> {

    private static final Map<ResourceLocation, ModelWrapper<? extends IModelCustom>> MODELS = new HashMap<>();

    protected T model;

    public abstract void renderAll();

    public static ModelWrapper<? extends IModelCustom> get(ResourceLocation resource)
        throws IllegalArgumentException, ModelFormatException {
        ModelWrapper<? extends IModelCustom> model = MODELS.get(resource);
        if (model == null) {
            model = AmazingTrophies.enableVBO ? new ModelCustomWrapperExt(resource) : new ModelCustomWrapper(resource);
            MODELS.put(resource, model);
        }
        return model;
    }

    private static class ModelCustomWrapper extends ModelWrapper<IModelCustom> {

        private ModelCustomWrapper(ResourceLocation resource) throws IllegalArgumentException, ModelFormatException {
            this.model = AdvancedModelLoader.loadModel(resource);
        }

        @Override
        public void renderAll() {
            this.model.renderAll();
        }
    }

    private static class ModelCustomWrapperExt extends ModelWrapper<IModelCustomExt> {

        private ModelCustomWrapperExt(ResourceLocation resource) throws IllegalArgumentException, ModelFormatException {
            this.model = (IModelCustomExt) AdvancedModelLoader.loadModel(resource);
        }

        @Override
        public void renderAll() {
            if (AmazingTrophies.enableVBO) {
                this.model.renderAllVBO();
            } else {
                this.model.renderAll();
            }
        }
    }
}
