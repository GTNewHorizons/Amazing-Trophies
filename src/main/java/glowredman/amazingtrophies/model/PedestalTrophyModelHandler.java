package glowredman.amazingtrophies.model;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.util.font.FontRendering;
import com.gtnewhorizon.gtnhlib.util.font.IFontParameters;

import glowredman.amazingtrophies.AmazingTrophies;
import glowredman.amazingtrophies.api.TrophyModelHandler;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class PedestalTrophyModelHandler extends TrophyModelHandler {

    public static final String ID = "pedestal";

    private static final long MILLIS_PER_DAY = 86_400_000L;
    private static final TimeZone TIME_ZONE = TimeZone.getDefault();
    private static final Long2ObjectMap<String> DATE_TEXTS = new Long2ObjectOpenHashMap<>();
    private static final ModelWrapper<?> MODEL_BASE = ModelWrapper
        .get(new ResourceLocation(AmazingTrophies.MODID, "models/trophy_pedestal.obj"));
    private static final ResourceLocation TEXTURE_BASE = new ResourceLocation(
        AmazingTrophies.MODID,
        "textures/blocks/trophy_pedestal.png");
    /**
     * The label (player name + date) of a trophy never changes, so its render data is cached per unique label. Entries
     * are invalidated when the font renderer instance or its metrics change.
     */
    private static final Map<String, LabelSize> LABEL_SIZES = new HashMap<>();
    /**
     * The font metrics are only checked every 500 ms, since they change far less often than the labels are rendered.
     */
    private static final long METRICS_CHECK_INTERVAL = 500L;
    private static long lastMetricsCheck;
    private static long metricsVersion;

    /**
     * Builds the opaque cache key identifying the given label. The key is static, so it can be computed once (e.g. when
     * the tile is placed) and reused for every frame thereafter.
     * 
     * @return the cache key, or {@code null} if the label is empty
     */
    public static String getLabelKey(@Nullable String name, long time) {
        if (name == null || name.isEmpty() || time == 0) {
            return null;
        }
        return name + getDateText(time);
    }

    /**
     * Clears all cached label render data. Called when the font metrics change (e.g. after a texture stitch), since the
     * cached text widths would no longer match the current font.
     */
    public static void clearLabelCache() {
        LABEL_SIZES.clear();
    }

    /**
     * Clears all cached label render data and date texts. Called on world unload, since no label of the old world can
     * ever be reused.
     */
    public static void clearCaches() {
        LABEL_SIZES.clear();
        DATE_TEXTS.clear();
    }

    @Override
    public void render(double x, double y, double z, int rotation, @Nullable String name, long time,
        float partialTickTime) {

        render(x, y, z, rotation, name, time, partialTickTime, getLabelKey(name, time));
    }

    @Override
    public void render(double x, double y, double z, int rotation, @Nullable String name, long time,
        float partialTickTime, @Nullable String labelKey) {

        // model
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TEXTURE_BASE);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(22.5f * rotation, 0.0f, 1.0f, 0.0f);

        MODEL_BASE.renderAll();

        // text
        if (name == null || name.isEmpty() || time == 0) {
            GL11.glPopMatrix();
            return;
        }
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(0.0f, -0.5f, 0.376f);
        // at 100% scale, the text is rendered upside down and 8 blocks high
        // this scales the text down to 80% of a pixel and flips it on the XZ plane
        GL11.glScalef(0.00625f, -0.00625f, 0.00625f);
        GL11.glDepthMask(false);
        // due to the flip, the Y coordinate must be negative. one pixel is 10 high
        LabelSize labelSize = getLabelSize(fontRenderer, labelKey, name, time);
        FontBatch.begin(fontRenderer);
        fontRenderer.drawString(name, -labelSize.nameWidth / 2, -39, 0x000000);
        fontRenderer.drawString(labelSize.timeText, -labelSize.timeWidth / 2, -29, 0x000000);
        FontBatch.end(fontRenderer);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    private static LabelSize getLabelSize(FontRenderer fontRenderer, @Nullable String labelKey, String name,
        long time) {

        String key = labelKey != null ? labelKey : getLabelKey(name, time);
        LabelSize labelSize = LABEL_SIZES.get(key);
        long metricsVersion = getMetricsVersion(fontRenderer);
        if (labelSize == null || labelSize.fontRenderer != fontRenderer || labelSize.metricsVersion != metricsVersion) {
            String timeText = getDateText(time);
            labelSize = new LabelSize(
                timeText,
                FontRendering.getStringWidth(name, fontRenderer),
                FontRendering.getStringWidth(timeText, fontRenderer),
                fontRenderer,
                metricsVersion);
            LABEL_SIZES.put(key, labelSize);
        }
        return labelSize;
    }

    /**
     * Gets a version value that changes whenever the current font metrics change. Font metrics can change without a
     * texture stitch (e.g. Angelica's font configuration screen, custom font reloads), so the version is recomputed
     * from the renderer's live state every {@value #METRICS_CHECK_INTERVAL} ms.
     */
    private static long getMetricsVersion(FontRenderer fontRenderer) {
        long now = System.currentTimeMillis();
        if (now - lastMetricsCheck > METRICS_CHECK_INTERVAL) {
            lastMetricsCheck = now;
            metricsVersion = computeMetricsVersion(fontRenderer);
        }
        return metricsVersion;
    }

    private static long computeMetricsVersion(FontRenderer fontRenderer) {
        long version = 0L;
        if (fontRenderer instanceof IFontParameters fontParameters) {
            version = Float.floatToIntBits(fontParameters.getGlyphScaleX());
            version = version * 31L + Float.floatToIntBits(fontParameters.getGlyphScaleY());
            version = version * 31L + Float.floatToIntBits(fontParameters.getGlyphSpacing());
            version = version * 31L + Float.floatToIntBits(fontParameters.getWhitespaceScale());
            version = version * 31L + Float.floatToIntBits(fontParameters.getShadowOffset());
            // sample widths: 'A'/'a'/'0' cover the default and custom fonts, ' ' the whitespace scale and 'Ω' the
            // unicode font, since custom and unicode fonts can be reloaded in place
            version = version * 31L + Float.floatToIntBits(fontParameters.getCharWidthFine('A'));
            version = version * 31L + Float.floatToIntBits(fontParameters.getCharWidthFine('a'));
            version = version * 31L + Float.floatToIntBits(fontParameters.getCharWidthFine('0'));
            version = version * 31L + Float.floatToIntBits(fontParameters.getCharWidthFine(' '));
            version = version * 31L + Float.floatToIntBits(fontParameters.getCharWidthFine('\u03A9'));
        }
        return version;
    }

    private static final class LabelSize {

        private final String timeText;
        private final int nameWidth;
        private final int timeWidth;
        private final FontRenderer fontRenderer;
        private final long metricsVersion;

        private LabelSize(String timeText, int nameWidth, int timeWidth, FontRenderer fontRenderer,
            long metricsVersion) {

            this.timeText = timeText;
            this.nameWidth = nameWidth;
            this.timeWidth = timeWidth;
            this.fontRenderer = fontRenderer;
            this.metricsVersion = metricsVersion;
        }
    }

    private static String getDateText(long time) {
        long day = Math.floorDiv(time + TIME_ZONE.getOffset(time), MILLIS_PER_DAY);
        String text = DATE_TEXTS.get(day);
        if (text == null) {
            text = String.format("%tF", time);
            DATE_TEXTS.put(day, text);
        }
        return text;
    }
}
