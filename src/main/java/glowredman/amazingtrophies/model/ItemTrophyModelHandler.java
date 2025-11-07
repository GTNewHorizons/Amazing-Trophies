package glowredman.amazingtrophies.model;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import cpw.mods.fml.common.registry.GameRegistry;
import glowredman.amazingtrophies.ConfigHandler;

public class ItemTrophyModelHandler extends PedestalTrophyModelHandler {

    public static final String ID = "item";
    public static final String PROPERTY_REGISTRY_NAME = "registryName";
    public static final String PROPERTY_META = "meta";
    public static final String PROPERTY_NBT = "nbt";
    public static final String PROPERTY_X_OFFSET = "xOffset";
    public static final String PROPERTY_Y_OFFSET = "yOffset";
    public static final String PROPERTY_Z_OFFSET = "zOffset";
    public static final String PROPERTY_YAW_OFFSET = "yawOffset";
    public static final String PROPERTY_SCALE = "scale";
    private static final Render RENDER = new Render();

    private EntityItem item;
    private double xOffset = 0.0;
    private double yOffset = Double.NaN;
    private double zOffset = 0.0;
    private float yawOffset = 0.0f;
    private float scale = Float.NaN;

    public ItemTrophyModelHandler() {}

    public ItemTrophyModelHandler(ItemStack item) {
        this.setItem(item);
        this.calculateScaleAndYOffset(item.getItem());
    }

    public ItemTrophyModelHandler(ItemStack item, float yawOffset) {
        this(item);
        this.yawOffset = yawOffset;
    }

    @Override
    public void parse(String id, JsonObject json) throws JsonSyntaxException {
        String registryName = ConfigHandler.getStringProperty(json, PROPERTY_REGISTRY_NAME);
        int meta = ConfigHandler.getIntegerProperty(json, PROPERTY_META, 0);
        if (meta < 0 || meta > OreDictionary.WILDCARD_VALUE) {
            throw new IllegalArgumentException("Illegal meta value (" + meta + ")!");
        }
        String nbt = ConfigHandler.getStringProperty(json, PROPERTY_NBT, null);
        ItemStack stack = GameRegistry.makeItemStack(registryName, meta, 0, nbt);
        if (stack == null) {
            throw new IllegalArgumentException("Could not find item " + registryName + "!");
        }
        this.xOffset = ConfigHandler.getDoubleProperty(json, PROPERTY_X_OFFSET, this.xOffset);
        this.yOffset = ConfigHandler.getDoubleProperty(json, PROPERTY_Y_OFFSET, this.yOffset);
        this.zOffset = ConfigHandler.getDoubleProperty(json, PROPERTY_Z_OFFSET, this.zOffset);
        this.yawOffset = ConfigHandler.getFloatProperty(json, PROPERTY_YAW_OFFSET, this.yawOffset);
        this.scale = ConfigHandler.getFloatProperty(json, PROPERTY_SCALE, this.scale);
        this.setItem(stack);
        this.calculateScaleAndYOffset(stack.getItem());
    }

    @Override
    public void render(double x, double y, double z, int rotation, @Nullable String name, long time,
        float partialTickTime) {
        super.render(x, y, z, rotation, name, time, partialTickTime);

        if (RENDER.getFontRendererFromRenderManager() == null) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslated(x + this.xOffset, y + this.yOffset, z + this.zOffset);
        GL11.glRotatef(22.5f * rotation + this.yawOffset, 0.0f, 1.0f, 0.0f);
        GL11.glScalef(this.scale, this.scale, this.scale);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

        synchronized (this.item) {
            try {
                this.item.setWorld(Minecraft.getMinecraft().theWorld);
                RENDER.doRender(this.item, 0.0, 0.0, 0.0, 0.0f, 0.0f);
            } finally {
                this.item.setWorld(null);
            }
        }

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void setItem(ItemStack item) {
        this.item = new EntityItem(null);
        this.item.setEntityItemStack(item);
        this.item.hoverStart = 0.0f;
    }

    private void calculateScaleAndYOffset(Item item) {
        if (Float.isNaN(this.scale)) {
            // RenderItem.doRender() scales blocks by 0.25 and items by 0.5
            this.scale = item instanceof ItemBlock ? 1.375f : 0.6875f;
        }
        if (Double.isNaN(this.yOffset)) {
            // RenderItem.doRender() translates blocks by -0.5 and items by -0.25 in the Y direction
            // in both cases, an additional scaled translation by 0.125 is needed
            this.yOffset = (double) this.scale * 0.125 - 0.1875;
        }
    }

    private static class Render extends RenderItem {

        public Render() {
            this.setRenderManager(RenderManager.instance);
        }

        @Override
        protected void renderDroppedItem(EntityItem entityItem, IIcon icon, int count, float partialTickTime, float r,
            float g, float b, int pass) {
            Tessellator tessellator = Tessellator.instance;

            if (icon == null) {
                TextureManager texturemanager = Minecraft.getMinecraft()
                    .getTextureManager();
                ResourceLocation resourcelocation = texturemanager.getResourceLocation(
                    entityItem.getEntityItem()
                        .getItemSpriteNumber());
                icon = ((TextureMap) texturemanager.getTexture(resourcelocation)).getAtlasSprite("missingno");
            }

            GL11.glPushMatrix();

            float f1 = 0.0625F; // 1/16 (one pixel)
            float f2 = 0.021875F; // ???
            ItemStack itemstack = entityItem.getEntityItem();

            GL11.glTranslatef(-0.5f, -0.25f, 0.5f * (f1 + f2));

            if (itemstack.getItemSpriteNumber() == 0) {
                this.bindTexture(TextureMap.locationBlocksTexture);
            } else {
                this.bindTexture(TextureMap.locationItemsTexture);
            }

            GL11.glColor4f(r, g, b, 1.0F);
            ItemRenderer.renderItemIn2D(
                tessellator,
                icon.getMaxU(),
                icon.getMinV(),
                icon.getMinU(),
                icon.getMaxV(),
                icon.getIconWidth(),
                icon.getIconHeight(),
                f1);

            if (itemstack.hasEffect(pass)) {

                // render holographic effect

                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                this.renderManager.renderEngine.bindTexture(RES_ITEM_GLINT);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                GL11.glColor4f(0.38f, 0.19f, 0.608f, 1.0F);
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPushMatrix();
                GL11.glScalef(0.125F, 0.125F, 0.125F);
                float f3 = (float) (Minecraft.getSystemTime() % 3000L) * 0.0026666666666667f; // *8/3000
                GL11.glTranslatef(f3, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f1);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(0.125F, 0.125F, 0.125F);
                f3 = (float) (Minecraft.getSystemTime() % 4873L) * -0.001641699159f; // *-8/4873
                GL11.glTranslatef(f3, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f1);
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
            }

            GL11.glPopMatrix();
        }
    }
}
