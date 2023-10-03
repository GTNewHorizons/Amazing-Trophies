package glowredman.amazingtrophies.api;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * A class holding all details to construct an {@link ItemStack}.
 * 
 * @author glowredman
 *
 */
@ParametersAreNonnullByDefault
public class ItemDefinition {

    private final String registryName;
    private final int meta;
    private final String nbt;

    /**
     * Creates an instance of {@code ItemDefinition} with meta = 0 and no NBT.
     * 
     * @param registryName The item's registry name. Format: {@code modid:itemName}
     */
    public ItemDefinition(String registryName) {
        this(registryName, 0);
    }

    /**
     * Creates an instance of {@code ItemDefinition} with no NBT.
     * 
     * @param registryName The item's registry name. Format: {@code modid:itemName}
     * @param meta         A value between 0 and 32766 (both inclusive). {@link OreDictionary#WILDCARD_VALUE} can be
     *                     used to represent any value.
     */
    public ItemDefinition(String registryName, @Nonnegative int meta) {
        this(registryName, meta, null);
    }

    /**
     * Creates an instance of {@code ItemDefinition} with meta = 0.
     * 
     * @param registryName The item's registry name. Format: {@code modid:itemName}
     * @param nbt          The NBT value in <a href=https://minecraft.wiki/w/NBT_format#SNBT_format>SNBT format</a>. If
     *                     {@code null} or empty, the NBT value will be ignored.
     */
    public ItemDefinition(String registryName, @Nullable String nbt) {
        this(registryName, 0, nbt);
    }

    /**
     * Creates an instance of {@code ItemDefinition}.
     * 
     * @param registryName The item's registry name. Format: {@code modid:itemName}
     * @param meta         A value between 0 and 32766 (both inclusive). {@link OreDictionary#WILDCARD_VALUE} can be
     *                     used to represent any value.
     * @param nbt          The NBT value in <a href=https://minecraft.wiki/w/NBT_format#SNBT_format>SNBT format</a>. If
     *                     {@code null} or empty, the NBT value will be ignored.
     */
    public ItemDefinition(String registryName, @Nonnegative int meta, @Nullable String nbt) {
        this.registryName = registryName;
        this.meta = meta;
        this.nbt = nbt;
    }

    /**
     * Gets the item's registry name. Format: {@code modid:itemName}
     */
    public String getRegistryName() {
        return this.registryName;
    }

    /**
     * Gets the item stack's meta value.
     * 
     * @return A value between 0 and 32767 (both inclusive)
     */
    @Nonnegative
    public int getMeta() {
        return this.meta;
    }

    /**
     * Gets the item stack's NBT value in <a href=https://minecraft.wiki/w/NBT_format#SNBT_format>SNBT format</a>. A
     * {@code null} or empty string may be returned to indicate that the NBT value should be ignored.
     */
    @Nullable
    public String getNbt() {
        return this.nbt;
    }

    /**
     * Constructs a new {@link ItemStack} using the stored properties.
     * 
     * @param stackSize The item stack's size
     * @see GameRegistry#makeItemStack(String, int, int, String)
     */
    public ItemStack getAsStack(int stackSize) {
        ItemStack stack = this.getAsStack();
        stack.stackSize = stackSize;
        return stack;
    }

    /**
     * Constructs a new {@link ItemStack} with stackSize = 1 using the stored properties.
     * 
     * @see GameRegistry#makeItemStack(String, int, int, String)
     */
    public ItemStack getAsStack() {
        // FML completely ignores the stackSize parameter in the method's implementation...
        return GameRegistry.makeItemStack(this.registryName, this.meta, 0, this.nbt);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        // spotless:off
        return obj instanceof ItemDefinition other
                && this.registryName.equals(other.registryName)
                && (this.meta == other.meta || this.meta == OreDictionary.WILDCARD_VALUE || other.meta == OreDictionary.WILDCARD_VALUE)
                && (StringUtils.isNullOrEmpty(this.nbt) || StringUtils.isNullOrEmpty(other.nbt) || this.nbt.equals(other.nbt));
        // spotless:on
    }

    @Override
    public String toString() {
        return String
            .format("ItemDefinition(registryName=\"%s\", meta=%d, nbt=\"%s\")", this.registryName, this.meta, this.nbt);
    }

}
