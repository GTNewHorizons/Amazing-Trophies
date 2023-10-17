package glowredman.amazingtrophies.condition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
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
    private static final int MASK_WILDCARD = 0b01;
    private static final int MASK_NBT = 0b10;

    protected final Map<Integer, Map<ItemStack, Set<String>>> conditions = new HashMap<>();

    }

    @Override
    public String getID() {
        return ID;
    public ItemConditionHandler() {
        this.conditions.put(0b00, new ItemStackMap<>(false)); // damage + nbt insensitive
        this.conditions.put(MASK_WILDCARD, new ItemStackMap<>(false)); // wildcard + nbt insensitive
        this.conditions.put(MASK_NBT, new ItemStackMap<>(true)); // damage + nbt sensitive
        this.conditions.put(MASK_WILDCARD | MASK_NBT, new ItemStackMap<>(true)); // wildcard + nbt sensitive
    }

    @Override
    public void parse(String id, JsonObject json) {
        String registryName = ConfigHandler.getStringProperty(json, PROPERTY_ITEM, id);
        int meta = ConfigHandler.getIntegerProperty(json, PROPERTY_META, OreDictionary.WILDCARD_VALUE);
        if (meta < 0 || meta > OreDictionary.WILDCARD_VALUE) {
            throw new IllegalArgumentException("Illegal meta value (" + meta + ")!");
        }
        String nbt = ConfigHandler.getStringProperty(json, PROPERTY_NBT, null);
        ItemStack stack = GameRegistry.makeItemStack(registryName, meta, 0, nbt);
        if (stack == null) {
            throw new IllegalArgumentException(
                "Could not find item " + registryName + " for condition of \"" + id + "\"!");
        }
        Map<ItemStack, Set<String>> map = this.getMap(meta, nbt);
        Set<String> ids = map.get(stack);
        if (ids == null) {
            ids = new HashSet<>();
            ids.add(id);
            map.put(stack, ids);
            return;
        }
        ids.add(id);
    }

    private Map<ItemStack, Set<String>> getMap(int meta, String nbt) {
        int mask = 0b00;
        if (meta == OreDictionary.WILDCARD_VALUE) {
            mask |= MASK_WILDCARD;
        }
        if (!StringUtils.isNullOrEmpty(nbt)) {
            mask |= MASK_NBT;
        }
    }

    @Override
    protected boolean isForgeEventHandler() {
        return this.items.values()
            .stream()
            .anyMatch(map -> !map.isEmpty());
        return this.conditions.get(mask);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onItemDrop(ItemTossEvent event) {
            for (String id : map.getOrDefault(event.entityItem.getEntityItem(), new HashSet<>())) {
        for (Map<ItemStack, Set<String>> map : this.conditions.values()) {
                this.getListener()
                    .accept(id, event.player);
            }
        }
    }

}
