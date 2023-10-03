package glowredman.amazingtrophies.trophy;

import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.UsernameCache;

import glowredman.amazingtrophies.AmazingTrophies;
import glowredman.amazingtrophies.api.AmazingTrophiesAPI;

public class ItemBlockTrophy extends ItemBlock {

    public ItemBlockTrophy(Block p_i45328_1_) {
        super(p_i45328_1_);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advancedTooltips) {
        if (!stack.hasTagCompound()) {
            // no nbt data
            return;
        }
        NBTTagCompound nbt = stack.getTagCompound();
        long time = nbt.getLong(AmazingTrophiesAPI.TAGNAME_TIME);
        String rawUUID = nbt.getString(AmazingTrophiesAPI.TAGNAME_UUID);
        if (rawUUID.isEmpty()) {
            // no uuid
            return;
        }
        UUID uuid = null;
        try {
            uuid = UUID.fromString(rawUUID);
        } catch (IllegalArgumentException ignored) {
            tooltip.add(StatCollector.translateToLocal(AmazingTrophies.MODID + ".uuid.invalid"));
            return;
        }
        String name = nbt.getString(AmazingTrophiesAPI.TAGNAME_NAME);
        String cachedName = null;
        if (uuid != null) {
            cachedName = UsernameCache.getLastKnownUsername(uuid);
            if (cachedName != null) {
                name = cachedName;
                nbt.setString(AmazingTrophiesAPI.TAGNAME_NAME, cachedName);
            }
        }
        if (name.isEmpty()) {
            // uuid is not present in UsernameCache and playernamer is not stored in the NBT
            tooltip.add(
                StatCollector.translateToLocalFormatted(
                    this.field_150939_a.getUnlocalizedName() + ".unknownUsername.name.desc"));
            tooltip.add(
                StatCollector.translateToLocalFormatted(
                    this.field_150939_a.getUnlocalizedName() + ".unknownUsername.time.desc",
                    time));
            return;
        }
        tooltip.add(
            StatCollector.translateToLocalFormatted(this.field_150939_a.getUnlocalizedName() + ".name.desc", name));
        tooltip.add(
            StatCollector.translateToLocalFormatted(this.field_150939_a.getUnlocalizedName() + ".time.desc", time));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        String prefix = this.field_150939_a.getUnlocalizedName();
        if (!stack.hasTagCompound()) {
            return prefix;
        }
        String id = stack.getTagCompound()
            .getString(AmazingTrophiesAPI.TAGNAME_ID);
        if (id.isEmpty()) {
            return prefix;
        }
        return prefix + "." + id;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ, int metadata) {
        // Convert the player's rotation to an int (0 - 15) such that, when multiplied by 22.5, it represents the angle
        // by which the model needs to be rotated to face the player.
        metadata = Math.round(-player.rotationYaw / 22.5f) & 15;
        boolean placed = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
        if (placed && stack.hasTagCompound() && world.getTileEntity(x, y, z) instanceof TileEntityTrophy tileTrophy) {
            tileTrophy.copyFromNBT(stack.getTagCompound());
            if (!world.isRemote) {
                world.markBlockForUpdate(x, y, z);
            }
        }
        return placed;
    }

}
