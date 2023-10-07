package glowredman.amazingtrophies.condition;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.oredict.OreDictionary;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.gtnewhorizon.gtnhlib.util.map.ItemStackMap;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import glowredman.amazingtrophies.api.ConditionHandler;

public class DropItemConditionHandler extends ConditionHandler {

    public static final String ID = "item.drop";

    private final Map<ItemStack, Set<String>> items = new ItemStackMap<>(false);
    private final Map<ItemStack, Set<String>> itemsNBT = new ItemStackMap<>(true);

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void parse(String id, JsonObject json) throws JsonSyntaxException {
        JsonElement itemJson = json.get("item");
        if (itemJson == null) {
            throw new JsonSyntaxException("\"" + id + "\" is missing required property \"item\"!");
        }
        JsonElement metaJson = json.get("meta");
        JsonElement nbtJson = json.get("nbt");
        String registryName = null;
        String nbt = null;
        ItemStack stack = null;
        try {
            registryName = itemJson.getAsString();
            int meta = OreDictionary.WILDCARD_VALUE;
            if (metaJson != null && !metaJson.isJsonNull()) {
                meta = metaJson.getAsInt();
            }
            if (nbtJson != null && !nbtJson.isJsonNull()) {
                nbt = nbtJson.getAsString();
            }
            stack = GameRegistry.makeItemStack(registryName, meta, 0, nbt);
        } catch (RuntimeException e) {
            throw new JsonSyntaxException("Malformed condition JSON!", e);
        }
        if (stack == null) {
            throw new JsonSyntaxException("Could not find item " + registryName + " for condition of \"" + id + "\"!");
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
