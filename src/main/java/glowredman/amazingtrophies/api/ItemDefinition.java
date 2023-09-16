package glowredman.amazingtrophies.api;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;

// TODO javadoc
public class ItemDefinition {

    // TODO javadoc for all
    public final String registryName;
    public final int meta;
    public final String nbt;

    // TODO javadoc
    public ItemDefinition(String registryName) {
        this(registryName, 0);
    }

    // TODO javadoc
    public ItemDefinition(String registryName, int meta) {
        this(registryName, meta, null);
    }

    // TODO javadoc
    public ItemDefinition(String registryName, int meta, String nbt) {
        this.registryName = registryName;
        this.meta = meta;
        this.nbt = nbt;
    }
    
    // TODO javadoc
    public static ItemDefinition parse(JsonObject json) throws JsonSyntaxException {
        JsonElement registryNameJson = json.get("registryName");
        if(registryNameJson == null) {
            throw new JsonSyntaxException("Required property \"registryName\" is missing!");
        }
        JsonElement metaJson = json.get("meta");
        JsonElement nbtJson = json.get("nbt");
        try {
            return new ItemDefinition(registryNameJson.getAsString(), metaJson == null ? 0 : metaJson.getAsInt(), nbtJson == null || nbtJson.isJsonNull() ? null : nbtJson.getAsString());
        } catch (ClassCastException | IllegalStateException e) {
            throw new JsonSyntaxException("Malformed JSON!", e);
        }
    }

    @Nonnull
    public Set<String> checkMissingRequiredProperties() {
        Set<String> missing = new HashSet<>();
        if(this.registryName == null) missing.add("registryName");
        return missing;
    }
    
    // TODO add javadoc
    @Nullable
    public ItemStack getAsStack(int stackSize) {
        ItemStack stack = this.getAsStack();
        stack.stackSize = stackSize;
        return stack;
    }
    
    // TODO add javadoc
    @Nullable
    public ItemStack getAsStack() {
        // FML completely ignores the stackSize parameter in the method's implementation...
        return GameRegistry.makeItemStack(this.registryName, this.meta, 0, this.nbt);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        return obj instanceof ItemDefinition other
                && this.registryName.equals(other.registryName)
                && (this.meta == other.meta || this.meta == WILDCARD_VALUE || other.meta == WILDCARD_VALUE)
                && (this.nbt.equals(other.nbt) || StringUtils.isNullOrEmpty(this.nbt) || StringUtils.isNullOrEmpty(other.nbt));
    }
    
    @Override
    public String toString() {
        return String.format("ItemDefinition(registryName=\"%s\", meta=%d, nbt=\"%s\")", this.registryName, this.meta, this.nbt);
    }

}
