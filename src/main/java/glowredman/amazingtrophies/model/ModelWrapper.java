package glowredman.amazingtrophies.model;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.ModelFormatException;

import com.gtnewhorizons.angelica.config.AngelicaConfig;
import com.gtnewhorizons.angelica.mixins.interfaces.IModelCustomExt;

import glowredman.amazingtrophies.AmazingTrophies;

public abstract class ModelWrapper<T extends IModelCustom> {

    protected T model;

    public abstract void renderAll();

    public static ModelWrapper<? extends IModelCustom> get(ResourceLocation resource)
        throws IllegalArgumentException, ModelFormatException {
        if (AmazingTrophies.isAngelicaLoaded) {
            return new ModelCustomWrapperExt(resource);
        }
        return new ModelCustomWrapper(resource);
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
            if (AngelicaConfig.enableVBO) {
                this.model.renderAllVBO();
            } else {
                this.model.renderAll();
            }
        }
    }
}
