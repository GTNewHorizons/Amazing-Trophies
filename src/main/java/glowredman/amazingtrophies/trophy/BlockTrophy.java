package glowredman.amazingtrophies.trophy;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import glowredman.amazingtrophies.AmazingTrophies;
import glowredman.amazingtrophies.api.AmazingTrophiesAPI;

public class BlockTrophy extends BlockContainer {

    private final ThreadLocal<TileEntityTrophy> tempTE = new ThreadLocal<>();

    public BlockTrophy() {
        super(MaterialTrophy.INSTANCE);
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
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask,
        List<AxisAlignedBB> list, Entity collider) {

        // Trophy base collision box

        setBlockBounds(0.125F, 0.0625F, 0.125F, 0.875F, 0.3125F, 0.875F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        setBlockBounds(0.0625F, 0F, 0.0625F, 0.9375F, 0.0625F, 0.9375F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBoundsForItemRender();
    }

    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        setBlockBounds(0.0625F, 0F, 0.0625F, 0.9375F, 0.0625F, 0.9375F);
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
