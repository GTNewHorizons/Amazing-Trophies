package glowredman.amazingtrophies.trophy;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

import glowredman.amazingtrophies.api.AmazingTrophiesAPI;
import glowredman.amazingtrophies.api.TrophyModelHandler;
import glowredman.amazingtrophies.api.TrophyProperties;
import glowredman.amazingtrophies.model.PedestalTrophyModelHandler;

public class RendererTrophy extends TileEntitySpecialRenderer implements IItemRenderer {

    private static final double MAX_LABEL_RENDER_DISTANCE_SQUARED = 32.0 * 32.0;
    private static final TrophyModelHandler FALLBACK_MODEL_HANDLER = new PedestalTrophyModelHandler();

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTickTime) {
        TileEntityTrophy tileTrophy = (TileEntityTrophy) tileEntity;
        TrophyProperties props = tileTrophy.getProperties();
        TrophyModelHandler modelHandler = props == null ? FALLBACK_MODEL_HANDLER : props.getModelHandler();
        if (modelHandler == null) {
            return;
        }
        double centerX = x + 0.5;
        double centerY = y + 0.5;
        double centerZ = z + 0.5;
        boolean renderLabel = centerX * centerX + centerY * centerY + centerZ * centerZ
            <= MAX_LABEL_RENDER_DISTANCE_SQUARED;
        modelHandler.render(
            centerX,
            centerY,
            centerZ,
            tileTrophy.getBlockMetadata(),
            renderLabel ? tileTrophy.getPlayerName() : null,
            renderLabel ? tileTrophy.getTime() : 0L,
            partialTickTime,
            renderLabel ? tileTrophy.getLabelKey() : null);
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        TrophyModelHandler modelHandler = FALLBACK_MODEL_HANDLER;
        String name = null;
        long time = 0;

        if (item.hasTagCompound()) {
            NBTTagCompound nbt = item.getTagCompound();
            String id = nbt.getString(AmazingTrophiesAPI.TAGNAME_ID);
            if (!id.isEmpty()) {

                TrophyProperties props = AmazingTrophiesAPI.getTrophyProperties(id);
                if (props != null) {
                    modelHandler = props.getModelHandler();
                    if (modelHandler == null) {
                        return;
                    }
                }
            }
            name = nbt.getString(AmazingTrophiesAPI.TAGNAME_NAME);
            time = nbt.getLong(AmazingTrophiesAPI.TAGNAME_TIME);
        }

        switch (type) {
            case EQUIPPED, EQUIPPED_FIRST_PERSON -> modelHandler.render(0.5, 0.5, 0.5, 12, name, time, 0.0f);
            default -> modelHandler.render(0.0, 0.0, 0.0, 12, name, time, 0.0f);
        }
    }

}
