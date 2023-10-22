package glowredman.amazingtrophies.api.StructureRenderer.Trophies;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import glowredman.amazingtrophies.api.StructureRenderer.Structures.BaseModelStructure;
import glowredman.amazingtrophies.api.StructureRenderer.Structures.Model_DTPF;
import glowredman.amazingtrophies.api.StructureRenderer.Structures.Model_Default;
import glowredman.amazingtrophies.api.StructureRenderer.Structures.Model_NanoForge;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

import java.util.HashMap;
import java.util.Set;

import static glowredman.amazingtrophies.AmazingTrophies.MODID;


public abstract class Trophies {

    public static final Block TrophyBlock = new BaseTrophyBlock("% Trophy");
    public static Item TrophyItem;


    // These two registrations happen at different stages of the minecraft loading process,
    // hence why we have different methods for them.
    public static void registerBlock() {
        GameRegistry.registerBlock(TrophyBlock, BaseTrophyItemBlock.class, "% Trophy");
    }

    public static void registerRenderer() {
        GameRegistry.registerTileEntity(
                BaseTrophyTileEntity.class,
                MODID + ":ModelTrophy");

        TrophyItem = Item.getItemFromBlock(TrophyBlock);

        MinecraftForgeClient.registerItemRenderer(TrophyItem, new BaseTrophyItemRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(BaseTrophyTileEntity.class, new BaseTrophyTESR());

        registerAll();
    }


    private static final HashMap<String, BaseModelStructure> modelMap = new HashMap<>();

    public static void registerModel(final String label, final BaseModelStructure model) {
        modelMap.put(label, model);
    }

    public static Set<String> getModelList() {
        return modelMap.keySet();
    }

    public static BaseModelStructure getModel(final String modelName) {
        return modelMap.getOrDefault(modelName, null);
    }

    private static void registerAll() {
        registerModel("Default", new Model_Default());
        //registerModel("DTPF", new Model_DTPF());
        registerModel("Nano Forge", new Model_NanoForge());
    }
}
