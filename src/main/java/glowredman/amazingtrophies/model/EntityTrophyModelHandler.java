package glowredman.amazingtrophies.model;

import java.util.Arrays;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import glowredman.amazingtrophies.ConfigHandler;
import it.unimi.dsi.fastutil.floats.FloatConsumer;

public class EntityTrophyModelHandler extends PedestalTrophyModelHandler {

    public static final String ID = "entity";
    public static final String PROPERTY_ENTITY = "entity";
    public static final String PROPERTY_Y_OFFSET = "yOffset";
    public static final String PROPERTY_YAW_OFFSET = "yawOffset";
    public static final String PROPERTY_SCALE = "scale";
    public static final String PROPERTY_NBT = "nbt";

    private Entity entity;
    private Render render;
    private FloatConsumer yawHandler;
    // -0.1875 = -3/16 -> moves the model down to the top of the pedestal
    private double yOffset = -0.1875;
    private float yawOffset = 180.0f;
    // 0.34375 = 11/32 -> scales a two block high model down to fit in the space over the pedestal
    private float scale = 0.34375f;
    private boolean setsBossStatus;

    public EntityTrophyModelHandler() {}

    public EntityTrophyModelHandler(Entity entity) {
        this.entity = entity;
        this.render = RenderManager.instance.getEntityRenderObject(entity);
        this.setsBossStatus = this.entity instanceof IBossDisplayData;
        this.setYawHandler();
    }

    public EntityTrophyModelHandler(Entity entity, float yOffset, float yawOffset, float scale) {
        this(entity);
        this.yOffset = yOffset;
        this.yawOffset = yawOffset;
        this.scale = scale;
    }

    @Override
    public void parse(String id, JsonObject json) throws JsonSyntaxException {
        JsonElement entityJson = json.get(PROPERTY_ENTITY);
        if (entityJson == null) {
            throw new JsonSyntaxException("Required property \"" + PROPERTY_ENTITY + "\" is missing!");
        }
        Class<? extends Entity> clazz = ConfigHandler.parseEntityClass(entityJson);
        try {
            this.entity = clazz.getConstructor(World.class)
                .newInstance((World) null);
        } catch (Exception e) {
            throw new IllegalStateException("Could not create a new instance of " + clazz.getName() + "!", e);
        }
        this.render = RenderManager.instance.getEntityRenderObject(entity);
        if (this.render == null) {
            throw new NullPointerException("Could not find render object for " + clazz.getName() + "!");
        }
        this.yOffset = ConfigHandler.getDoubleProperty(json, PROPERTY_Y_OFFSET, this.yOffset);
        this.yawOffset = ConfigHandler.getFloatProperty(json, PROPERTY_YAW_OFFSET, this.yawOffset);
        this.scale = ConfigHandler.getFloatProperty(json, PROPERTY_SCALE, this.scale);
        NBTBase nbt = null;
        try {
            nbt = JsonToNBT.func_150315_a(ConfigHandler.getStringProperty(json, PROPERTY_NBT, "{}"));
        } catch (NBTException e) {
            throw new IllegalArgumentException("Could not parse NBT", e);
        }
        if (nbt instanceof NBTTagCompound compound && !compound.func_150296_c() // getKeySet
            .isEmpty()) {
            this.entity.readFromNBT(compound);
        }
        this.setsBossStatus = this.entity instanceof IBossDisplayData;
        this.setYawHandler();
    }

    @Override
    public void render(double x, double y, double z, int rotation, @Nullable String name, long time,
        float partialTickTime) {
        super.render(x, y, z, rotation, name, time, partialTickTime);

        if (this.render.getFontRendererFromRenderManager() == null) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslated(x, y + this.yOffset, z);
        GL11.glScalef(this.scale, this.scale, this.scale);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

        float rotationDeg = this.yawOffset - 22.5f * rotation;
        synchronized (this.entity) {
            this.yawHandler.accept(rotationDeg);
            this.entity.setWorld(Minecraft.getMinecraft().theWorld);

            if (this.setsBossStatus) {
                // boss entities usually call BossStatus.setBossStatus in their Render.doRender method so we need to
                // cache and reset the BossStatus fields
                String bossName = BossStatus.bossName;
                boolean hasColorModifier = BossStatus.hasColorModifier;
                float healthScale = BossStatus.healthScale;
                int statusBarTime = BossStatus.statusBarTime;
                this.render.doRender(this.entity, 0.0, 0.0, 0.0, rotationDeg, partialTickTime);
                BossStatus.bossName = bossName;
                BossStatus.hasColorModifier = hasColorModifier;
                BossStatus.healthScale = healthScale;
                BossStatus.statusBarTime = statusBarTime;
            } else {
                this.render.doRender(this.entity, 0.0, 0.0, 0.0, rotationDeg, partialTickTime);
            }
        }

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void setYawHandler() {
        this.yawHandler = rotation -> {
            this.entity.rotationYaw = rotation;
            this.entity.prevRotationYaw = rotation;
        };
        if (this.entity instanceof EntityLivingBase) {
            this.yawHandler = this.yawHandler.andThen(rotation -> {
                EntityLivingBase living = (EntityLivingBase) this.entity;
                living.renderYawOffset = rotation;
                living.prevRenderYawOffset = rotation;
                living.rotationYawHead = rotation;
                living.prevRotationYawHead = rotation;
            });
            if (this.entity instanceof EntityDragon) {
                this.yawHandler = this.yawHandler.andThen(
                    rotation -> Arrays
                        .fill(((EntityDragon) this.entity).ringBuffer, new double[] { rotation - 180.0, 0.0, 0.0 }));
            }
        }
    }
}
