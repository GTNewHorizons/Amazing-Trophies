package glowredman.amazingtrophies.api.StructureRenderer.Trophies;

import glowredman.amazingtrophies.api.StructureRenderer.Base.BaseRenderTileEntity;
import net.minecraft.nbt.NBTTagCompound;


public class BaseTrophyTileEntity extends BaseRenderTileEntity {

    private double scaleFactor;
    private String getScaleFactor() {
        return Double.toString(scaleFactor);
    }
    private void setScaleFactor(String string) {
        try {
            scaleFactor = Double.parseDouble(string);
        } catch (Exception ignored) { }
    }
    double xAngle;
    private String getxAngle() {
        return Double.toString(xAngle);
    }
    private void setxAngle(String string) {
        try {
            xAngle = Double.parseDouble(string);
        } catch (Exception ignored) { }
    }

    double yAngle;
    private String getyAngle() {
        return Double.toString(yAngle);
    }
    private void setyAngle(String string) {
        try {
            yAngle = Double.parseDouble(string);
        } catch (Exception ignored) { }
    }

    double zAngle;
    private String getzAngle() {
        return Double.toString(zAngle);
    }
    private void setzAngle(String string) {
        try {
            zAngle = Double.parseDouble(string);
        } catch (Exception ignored) { }
    }

    static final String NBT_TAG_SCALE_FACTOR = "scaleFactor";
    static final String NBT_TAG_X_ANGLE = "xAngle";
    static final String NBT_TAG_Y_ANGLE = "yAngle";
    static final String NBT_TAG_Z_ANGLE = "zAngle";

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        //nbt.setDouble(NBT_TAG_SCALE_FACTOR, scaleFactor);
        nbt.setDouble(NBT_TAG_X_ANGLE, xAngle);
        nbt.setDouble(NBT_TAG_Y_ANGLE, yAngle);
        nbt.setDouble(NBT_TAG_Z_ANGLE, zAngle);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        //this.scaleFactor = nbt.getDouble(NBT_TAG_SCALE_FACTOR);
        this.xAngle = nbt.getDouble(NBT_TAG_X_ANGLE);
        this.yAngle = nbt.getDouble(NBT_TAG_Y_ANGLE);
        this.zAngle = nbt.getDouble(NBT_TAG_Z_ANGLE);
    }

}
