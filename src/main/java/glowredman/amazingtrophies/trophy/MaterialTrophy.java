package glowredman.amazingtrophies.trophy;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class MaterialTrophy extends Material {

    public static final Material INSTANCE = new MaterialTrophy();

    public MaterialTrophy() {
        super(MapColor.woodColor);
        this.setAdventureModeExempt();
        this.setImmovableMobility();
    }

    @Override
    public boolean getCanBlockGrass() {
        return false;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

}
