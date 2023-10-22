package glowredman.amazingtrophies.api.StructureRenderer.Trophies;


import glowredman.amazingtrophies.api.StructureRenderer.Base.BaseRenderTESR;
import glowredman.amazingtrophies.api.StructureRenderer.Structures.BaseModelStructure;

public class BaseTrophyTESR extends BaseRenderTESR {

    @Override
    protected BaseModelStructure getModel(String modelName) {
        return Trophies.getModel(modelName);
    }

}
