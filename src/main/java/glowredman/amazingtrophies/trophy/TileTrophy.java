package glowredman.amazingtrophies.trophy;

import static glowredman.amazingtrophies.api.AmazingTrophiesAPI.TAGNAME_ID;
import static glowredman.amazingtrophies.api.AmazingTrophiesAPI.TAGNAME_NAME;
import static glowredman.amazingtrophies.api.AmazingTrophiesAPI.TAGNAME_TIME;
import static glowredman.amazingtrophies.api.AmazingTrophiesAPI.TAGNAME_UUID;

import java.util.Locale;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import glowredman.amazingtrophies.AmazingTrophies;
import glowredman.amazingtrophies.api.AmazingTrophiesAPI;
import glowredman.amazingtrophies.api.TrophyProperties;

public class TileTrophy extends TileEntity {

    private TrophyProperties props;
    private long time;
    private UUID uuid;
    private String name;

    public ItemStack getItemStack() {
        ItemStack stack = new ItemStack(AmazingTrophiesAPI.getTrophyBlock());
        stack.setTagCompound(this.getNBT());
        return stack;
    }

    public NBTTagCompound getNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.copyToNBT(nbt);
        return nbt;
    }

    public void copyToNBT(NBTTagCompound nbt) {
        if (this.props != null) {
            nbt.setString(TAGNAME_ID, this.props.getID());
        }
        if (this.uuid != null) {
            nbt.setString(TAGNAME_UUID, this.uuid.toString());
        }
        if (this.name != null) {
            nbt.setString(TAGNAME_NAME, this.name.toString());
        }
        nbt.setLong(TAGNAME_TIME, this.time);
    }

    public void copyFromNBT(NBTTagCompound nbt) {
        String id = nbt.hasKey(TAGNAME_ID) ? nbt.getString(TAGNAME_ID) : null;
        String name = nbt.getString(TAGNAME_NAME);
        this.name = name.isEmpty() ? null : name;
        this.props = id == null ? null : AmazingTrophiesAPI.getTrophyProperties(id);
        this.time = nbt.getLong(TAGNAME_TIME);
        String uuid = nbt.getString(TAGNAME_UUID);
        try {
            this.uuid = uuid.isEmpty() ? null : UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            if (this.hasWorldObj()) {
                AmazingTrophies.LOGGER.warn(
                    String.format(
                        Locale.ROOT,
                        "Found trophy of type \"%s\" with invalid UUID \"%s\" at x=%d, y=%d, z=%d, dim=%d (%s)!",
                        id,
                        uuid,
                        this.xCoord,
                        this.yCoord,
                        this.zCoord,
                        this.worldObj.provider.dimensionId,
                        this.worldObj.getProviderName()),
                    e);
            } else {
                AmazingTrophies.LOGGER.warn(
                    String.format(Locale.ROOT, "Found trophy of type \"%s\" with invalid UUID \"%s\"!", id, uuid),
                    e);
            }
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, this.blockMetadata, nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.copyFromNBT(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        this.copyToNBT(compound);
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

}
