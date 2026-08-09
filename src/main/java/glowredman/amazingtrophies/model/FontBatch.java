package glowredman.amazingtrophies.model;

import net.minecraft.client.gui.FontRenderer;

import com.gtnewhorizons.angelica.client.font.BatchingFontRenderer;
import com.gtnewhorizons.angelica.mixins.interfaces.FontRendererAccessor;

import cpw.mods.fml.common.Loader;

/**
 * Wrapper around Angelica's batched font renderer. Without Angelica, the begin/end calls are no-ops.
 */
public class FontBatch {

    private static final boolean ANGELICA_LOADED = Loader.isModLoaded("angelica");

    private FontBatch() {}

    public static void begin(FontRenderer fontRenderer) {
        BatchingFontRenderer batcher = getBatcher(fontRenderer);
        if (batcher != null) {
            batcher.beginBatch();
        }
    }

    public static void end(FontRenderer fontRenderer) {
        BatchingFontRenderer batcher = getBatcher(fontRenderer);
        if (batcher != null) {
            batcher.endBatch();
        }
    }

    private static BatchingFontRenderer getBatcher(FontRenderer fontRenderer) {
        if (ANGELICA_LOADED && fontRenderer instanceof FontRendererAccessor accessor) {
            return accessor.angelica$getBatcher();
        }
        return null;
    }
}
