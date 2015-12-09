package com.brohoof.minelittlepony.model;

import com.brohoof.minelittlepony.MineLittlePony;
import com.brohoof.minelittlepony.Pony;
import com.brohoof.minelittlepony.PonyData;
import com.brohoof.minelittlepony.renderer.AniParams;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;

public abstract class ModelPony extends ModelPlayer {

    protected float strech = 0.0F;
    protected float scale = 0.0625F;
    public boolean issneak = false;
    public boolean isArmour = false;
    public boolean isVillager;
    public boolean isFlying;
    public boolean isGlow;
    public boolean isSleeping;

    public PonyData metadata = new PonyData();

    public int heldItemLeft;
    public int heldItemRight;
    public boolean aimedBow;

    public ModelPony() {
        super(0, false);
    }

    public void setStrech(float strech) {
        this.strech = strech;
    }

    /**
     * @param var1
     * @param var2
     */
    public void init(float var1, float var2) {}

    /**
     * @param var1
     */
    public void animate(AniParams var1) {}

    /**
     * @param var1
     */
    public void render(AniParams var1) {}

    @Override
    public void render(Entity player, float Move, float Moveswing, float Loop, float Right, float Down, float Scale) {
        if (player instanceof AbstractClientPlayer) {
            setModelVisibilities((AbstractClientPlayer) player);
        }
        if (!doCancelRender()) {
            AniParams ani = new AniParams(Move, Moveswing, Loop, Right, Down);
            this.animate(ani);
            this.render(ani);
        } else {
            super.render(player, Move, Moveswing, Loop, Right, Down, Scale);
        }
    }

    @Override
    public final void renderRightArm() {
        // Use the human model
        PMAPI.human.getModel().renderModelRightArm();
    }

    @Override
    public final void renderLeftArm() {
        // Use the human model
        PMAPI.human.getModel().renderModelLeftArm();
    }

    @Override
    public final void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6, Entity ent) {
        // set the angles for the humans in preparation for arm rendering
        // Comes from RenderPlayer.render[Left|Right]Arm?
        if (PMAPI.human.getModel() != this
                && Thread.currentThread().getStackTrace()[2].getClassName().equals(RenderPlayer.class.getName())) {
            PMAPI.human.getModel().setModelVisibilities((AbstractClientPlayer) ent);
            PMAPI.human.getModel().isSneak = isSneak;
            PMAPI.human.getModel().swingProgress = swingProgress;
            PMAPI.human.getModel().setModelRotationAngles(f1, f2, f3, f4, f5, f6, ent);
            // override default skin
            Pony pony = MineLittlePony.getInstance().getManager().getPonyFromResourceRegistry((AbstractClientPlayer) ent);
            Minecraft.getMinecraft().getTextureManager().bindTexture(pony.getTextureResourceLocation());
        }
        setModelRotationAngles(f1, f2, f3, f4, f5, f6, ent);
    }

    public void renderModelRightArm() {
        super.renderRightArm();
    }

    public void renderModelLeftArm() {
        super.renderLeftArm();
    }

    public void setModelRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6, Entity ent) {
        super.setRotationAngles(f1, f2, f3, f4, f5, f6, ent);
    }

    protected void setModelVisibilities(AbstractClientPlayer clientPlayer) {
        ModelPlayer modelplayer = this;

        if (clientPlayer.isSpectator()) {
            modelplayer.setInvisible(false);
            modelplayer.bipedHead.showModel = true;
            modelplayer.bipedHeadwear.showModel = true;
        } else {
            ItemStack itemstack = clientPlayer.inventory.getCurrentItem();
            modelplayer.setInvisible(true);
            modelplayer.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);
            modelplayer.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
            modelplayer.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
            modelplayer.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
            modelplayer.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
            modelplayer.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
            modelplayer.heldItemLeft = 0;
            modelplayer.aimedBow = false;
            modelplayer.isSneak = clientPlayer.isSneaking();

            if (itemstack == null) {
                modelplayer.heldItemRight = 0;
            } else {
                modelplayer.heldItemRight = 1;

                if (clientPlayer.getItemInUseCount() > 0) {
                    EnumAction enumaction = itemstack.getItemUseAction();

                    if (enumaction == EnumAction.BLOCK) {
                        modelplayer.heldItemRight = 3;
                    } else if (enumaction == EnumAction.BOW) {
                        modelplayer.aimedBow = true;
                    }
                }
            }
        }
    }

    protected boolean doCancelRender() {
        return false;
    }

}