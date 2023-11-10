package glowredman.amazingtrophies.trophy;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import glowredman.amazingtrophies.AmazingTrophies;
import glowredman.amazingtrophies.api.AmazingTrophiesAPI;

public class BlockTrophy extends BlockContainer {

    private final ThreadLocal<TileEntityTrophy> tempTE = new ThreadLocal<>();

    public BlockTrophy() {
        super(Material.wood);
        this.setBlockName(AmazingTrophies.MODID + ".trophy");
        this.setBlockTextureName(AmazingTrophies.MODID + ":trophy_pedestal");
        this.setHardness(1.0f);
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        if (worldIn.getTileEntity(x, y, z) instanceof TileEntityTrophy tileTrophy) {
            this.tempTE.set(tileTrophy);
        }
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityTrophy();
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        TileEntityTrophy tileTrophy = null;
        if (world.getTileEntity(x, y, z) instanceof TileEntityTrophy tile) {
            tileTrophy = tile;
        } else {
            TileEntityTrophy tile = this.tempTE.get();
            if (tile != null) {
                tileTrophy = tile;
            }
        }
        if (tileTrophy != null) {
            drops.add(tileTrophy.getItemStack());
        }
        return drops;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        if (world.getTileEntity(x, y, z) instanceof TileEntityTrophy tileTrophy) {
            return tileTrophy.getItemStack();
        }
        return new ItemStack(this);
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (String id : AmazingTrophiesAPI.getTrophyIDs()) {
            list.add(AmazingTrophiesAPI.getTrophyWithNBT(id, null));
        }
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
}
