package glowredman.amazingtrophies.integration;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import com.ruling_0.materiallib.api.StackResolver;

import glowredman.amazingtrophies.AmazingTrophies;

/// Resolves the `ml:<Material>:<shape>` registry names MaterialLib-aware config files carry. Such a name identifies
/// its item by material and shape instead of by registry name and metadata, which MaterialLib assigns afresh each
/// session.
///
/// This is the only class holding MaterialLib types, so the rest of Amazing Trophies loads without MaterialLib
/// installed. Call only when `materiallib` is loaded.
public final class MaterialLibStacks {

    private MaterialLibStacks() {}

    /// The stack the registry name resolves to, or null when it is malformed or names nothing MaterialLib registers.
    @Nullable
    public static ItemStack resolve(String registryName) {
        String[] parts = registryName.split(":");
        if (parts.length != 3 || parts[1].isEmpty() || parts[2].isEmpty()) {
            AmazingTrophies.LOGGER
                .error("Malformed MaterialLib item name \"{}\", expected ml:<Material>:<shape>", registryName);
            return null;
        }
        return StackResolver.getStack(parts[1], parts[2], 1);
    }
}
