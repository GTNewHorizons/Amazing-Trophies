package glowredman.amazingtrophies.trophy;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import glowredman.amazingtrophies.api.AmazingTrophiesAPI;

public class BlockTrophy extends BlockContainer {

    private final ThreadLocal<TileTrophy> tempTE = new ThreadLocal<>();

    public BlockTrophy() {
        super(Material.wood);
        this.setHardness(1.0f);
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        if (worldIn.getTileEntity(x, y, z) instanceof TileTrophy tileTrophy) {
            this.tempTE.set(tileTrophy);
        }
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileTrophy();
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        TileTrophy tileTrophy = null;
        if (world.getTileEntity(x, y, z) instanceof TileTrophy tile) {
            tileTrophy = tile;
        } else {
            TileTrophy tile = this.tempTE.get();
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
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Blocks.soul_sand.getIcon(side, meta);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        if (world.getTileEntity(x, y, z) instanceof TileTrophy tileTrophy) {
            return tileTrophy.getItemStack();
        }
        return new ItemStack(this);
    }

    @Override
    public int getRenderType() {
        // TODO change render type
        return super.getRenderType();
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
