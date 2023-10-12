package glowredman.amazingtrophies.condition;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.oredict.OreDictionary;

import com.google.gson.JsonObject;
import com.gtnewhorizon.gtnhlib.util.map.ItemStackMap;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.api.ConditionHandler;

public class DropItemConditionHandler extends ConditionHandler {

    public static final String ID = "item.drop";
    public static final String PROPERTY_ITEM = "item";
    public static final String PROPERTY_META = "meta";
    public static final String PROPERTY_NBT = "nbt";

    private final Map<ItemStack, Set<String>> items = new ItemStackMap<>(false);
    private final Map<ItemStack, Set<String>> itemsNBT = new ItemStackMap<>(true);

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) {
        String registryName = ConfigHandler.getStringProperty(json, PROPERTY_ITEM, id);
        int meta = ConfigHandler.getIntegerProperty(json, PROPERTY_META, id, OreDictionary.WILDCARD_VALUE);
        String nbt = ConfigHandler.getStringProperty(json, PROPERTY_NBT, id, null);
        ItemStack stack = GameRegistry.makeItemStack(registryName, meta, 0, nbt);
        if (stack == null) {
            throw new IllegalArgumentException(
                "Could not find item " + registryName + " for condition of \"" + id + "\"!");
        }
        (nbt == null ? this.items : this.itemsNBT).getOrDefault(stack, new HashSet<>())
            .add(id);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onItemDrop(ItemTossEvent event) {
        for (String id : this.items.getOrDefault(event.entityItem.getEntityItem(), new HashSet<>())) {
            this.getListener()
                .accept(id, event.player);
        }
        for (String id : this.itemsNBT.getOrDefault(event.entityItem.getEntityItem(), new HashSet<>())) {
            this.getListener()
                .accept(id, event.player);
        }
    }

}
