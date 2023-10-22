package glowredman.amazingtrophies.api.StructureRenderer.Trophies;

import glowredman.amazingtrophies.api.StructureRenderer.Base.BaseRenderBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BaseTrophyBlock extends BaseRenderBlock {
    public BaseTrophyBlock(String name) {
        super(name);
        this.setResistance(1.0F);
        this.setHardness(1.5F);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new BaseTrophyTileEntity();
    }

    // Handling dropping of item when block is broken.
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest)
    {
        if (willHarvest) return true; // Delay deletion of the block until after getDrops.
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta)
    {
        super.harvestBlock(world, player, x, y, z, meta);
        world.setBlockToAir(x, y, z);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {

        final ArrayList<ItemStack> arrayList = new ArrayList<>();
        arrayList.add(getPickBlock(null, world, x, y, z, null));

        return arrayList;
    }

}
