package glowredman.amazingtrophies.condition;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.google.gson.JsonObject;
import com.gtnewhorizon.gtnhlib.compat.BaublesCompat;
import com.gtnewhorizon.gtnhlib.compat.Mods;
import com.gtnewhorizon.gtnhlib.event.InventoryChangedEvent;
import com.gtnewhorizon.gtnhlib.util.map.ItemStackMap;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import glowredman.amazingtrophies.ConfigHandler;
import glowredman.amazingtrophies.api.ConditionHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;
import it.unimi.dsi.fastutil.ints.IntObjectPair;

public abstract class InventoryChangedHandler extends ConditionHandler {

    public static final String PROPERTY_ITEM = "item";
    public static final String PROPERTY_META = "meta";
    public static final String PROPERTY_COUNT = "count";
    protected static final int MASK_WILDCARD = 0b1;

    protected final Int2ObjectMap<Map<ItemStack, Set<IntObjectPair<String>>>> conditions = new Int2ObjectOpenHashMap<>();

    public InventoryChangedHandler() {
        this.conditions.put(0b0, new ItemStackMap<>(false)); // damage sensitive
        this.conditions.put(MASK_WILDCARD, new ItemStackMap<>(false)); // wildcard
    }

    @Override
    public void parse(String id, JsonObject json) {
        String registryName = ConfigHandler.getStringProperty(json, PROPERTY_ITEM);
        int meta = ConfigHandler.getIntegerProperty(json, PROPERTY_META, OreDictionary.WILDCARD_VALUE);
        if (meta < 0 || meta > OreDictionary.WILDCARD_VALUE) {
            throw new IllegalArgumentException("Illegal meta value (" + meta + ")!");
        }
        int count = ConfigHandler.getIntegerProperty(json, PROPERTY_COUNT, 1);
        ItemStack stack = GameRegistry.makeItemStack(registryName, meta, 0, null);
        if (stack == null) {
            throw new IllegalArgumentException("Could not find item " + registryName + "!");
        }
        Map<ItemStack, Set<IntObjectPair<String>>> map = this.getMap(meta);
        Set<IntObjectPair<String>> ids = map.get(stack);
        if (ids == null) {
            ids = new HashSet<>();
            ids.add(new IntObjectImmutablePair<>(count, id));
            map.put(stack, ids);
            return;
        }
        ids.add(new IntObjectImmutablePair<>(count, id));
    }

    protected Map<ItemStack, Set<IntObjectPair<String>>> getMap(int meta) {
        int mask = 0b00;
        if (meta == OreDictionary.WILDCARD_VALUE) {
            mask |= MASK_WILDCARD;
        }
        return this.conditions.get(mask);
    }

    protected void trigger(ItemStack stack, EntityPlayer player) {
        for (Map<ItemStack, Set<IntObjectPair<String>>> map : this.conditions.values()) {
            for (IntObjectPair<String> p : map.getOrDefault(stack, Collections.emptySet())) {
                if (stack.stackSize >= p.leftInt()) {
                    this.getListener()
                        .accept(p.right(), player);
                }
            }
        }
    }

    @Override
    protected boolean isForgeEventHandler() {
        return this.conditions.values()
            .stream()
            .anyMatch(map -> !map.isEmpty());
    }

    public static class Add extends InventoryChangedHandler {

        public static final String ID = "inventory.add";

        @Override
        public String getID() {
            return ID;
        }

        @SubscribeEvent
        public void onItemAdded(InventoryChangedEvent.ItemAdded event) {
            if (event.entityPlayer instanceof EntityPlayerMP) {
                this.trigger(event.item, event.entityPlayer);
            }
        }
    }

    public static class Remove extends InventoryChangedHandler {

        public static final String ID = "inventory.remove";

        @Override
        public String getID() {
            return ID;
        }

        @SubscribeEvent
        public void onItemRemoved(InventoryChangedEvent.ItemRemoved event) {
            if (event.entityPlayer instanceof EntityPlayerMP) {
                this.trigger(event.item, event.entityPlayer);
            }
        }
    }

    public static class Total extends InventoryChangedHandler {

        public static final String ID = "inventory.total";

        @Override
        public String getID() {
            return ID;
        }

        @SubscribeEvent
        public void onItemAdded(InventoryChangedEvent.ItemAdded event) {
            if (!(event.entityPlayer instanceof EntityPlayerMP player)) {
                return;
            }

            Item item = event.item.getItem();
            int meta = event.item.getItemDamage();
            int numItems = 0;
            int numStacks = 0; // respects meta

            // aggregate inventory contents
            for (ItemStack stack : player.inventory.mainInventory) {
                if (stack == null) {
                    continue;
                }
                if (stack.getItem() == item) {
                    numItems += stack.stackSize;
                    if (stack.getItemDamage() == meta) {
                        numStacks += stack.stackSize;
                    }
                }
            }
            for (ItemStack stack : player.inventory.armorInventory) {
                if (stack == null) {
                    continue;
                }
                if (stack.getItem() == item) {
                    numItems += stack.stackSize;
                    if (stack.getItemDamage() == meta) {
                        numStacks += stack.stackSize;
                    }
                }
            }
            ItemStack heldItem = player.inventory.getItemStack();
            if (heldItem != null && heldItem.getItem() == item) {
                numItems += heldItem.stackSize;
                if (heldItem.getItemDamage() == meta) {
                    numStacks += heldItem.stackSize;
                }
            }
            if (player.inventoryContainer instanceof ContainerPlayer container) {
                for (ItemStack stack : container.craftMatrix.stackList) {
                    if (stack == null) {
                        continue;
                    }
                    if (stack.getItem() == item) {
                        numItems += stack.stackSize;
                        if (stack.getItemDamage() == meta) {
                            numStacks += stack.stackSize;
                        }
                    }
                }
            }
            baubles: if (Mods.BAUBLES) {
                IInventory inv = BaublesCompat.getBaubles(player);
                if (inv == null) {
                    break baubles;
                }
                int size = inv.getInventoryStackLimit();
                for (int i = 0; i < size; i++) {
                    ItemStack stack = inv.getStackInSlot(i);
                    if (stack == null) {
                        continue;
                    }
                    if (stack.getItem() == item) {
                        numItems += stack.stackSize;
                        if (stack.getItemDamage() == meta) {
                            numStacks += stack.stackSize;
                        }
                    }
                }
            }

            // trigger listeners
            for (IntObjectPair<String> p : this.conditions.get(MASK_WILDCARD)
                .getOrDefault(event.item, Collections.emptySet())) {
                if (numItems >= p.leftInt()) {
                    this.getListener()
                        .accept(p.right(), player);
                }
            }
            for (IntObjectPair<String> p : this.conditions.get(0b0)
                .getOrDefault(event.item, Collections.emptySet())) {
                if (numStacks >= p.leftInt()) {
                    this.getListener()
                        .accept(p.right(), player);
                }
            }
        }
    }
}
