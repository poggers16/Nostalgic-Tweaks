package mod.adrenix.nostalgic.util.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.mixin.duck.IReequipSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * This utility class uses client only Minecraft code. For safety, the server should not interface with this utility.
 * For a server safe mixin utility use {@link mod.adrenix.nostalgic.util.server.ItemServerUtil}.
 */

public abstract class ItemClientUtil
{
    // Used to enhance the old reequipping logic
    public static ItemStack getLastItem(ItemStack originalItemStack, ItemStack rendererItemStack, ItemStack playerItemStack, IReequipSlot player)
    {
        // Item from main hand turns to air as soon as the player pulls it out. When this happens, the following strings appear in each property respectively.
        boolean isUnequipped = rendererItemStack.toString().equals("0 air") && playerItemStack.toString().equals("1 air");
        if (!ModConfig.Animation.oldItemReequip() || !isUnequipped)
            return originalItemStack;

        return player.NT$getLastItem();
    }

    // Tells the item renderer if we're rendering a flat item.
    private static boolean isRenderingFlat = false;

    // Used to cache the current level pose stack position matrix for re-enabling diffused lighting after flat rendering.
    @Nullable
    public static PoseStack.Pose levelPoseStack;

    // Used to cache the current buffer source during the entity render cycle.
    // This is needed so we can end the batch early to apply flat lighting to vertices.
    public static MultiBufferSource.BufferSource levelBufferSource;

    // Used to check if a model should be rendered in 2D.
    public static boolean isModelFlat(BakedModel model)
    {
        return !model.usesBlockLight();
    }

    // Flattens an item to be as close to 2D as possible via scaling.
    public static void flatten(PoseStack poseStack)
    {
        poseStack.scale(1.0F, 1.0F, 0.001F);
    }

    // Getter for checking if diffused lighting is disabled.
    public static boolean isLightingFlat()
    {
        return isRenderingFlat;
    }

    // Turns off diffused lighting.
    public static void disableDiffusedLighting()
    {
        levelBufferSource.endBatch();
        Lighting.setupForFlatItems();
        isRenderingFlat = true;
    }

    // Turns on diffused lighting.
    public static void enableDiffusedLighting()
    {
        isRenderingFlat = false;
        levelBufferSource.endBatch();

        if (Minecraft.getInstance().level == null || levelPoseStack == null)
            return;

        if (Minecraft.getInstance().level.effects().constantAmbientLight())
            Lighting.setupNetherLevel(levelPoseStack.pose());
        else
            Lighting.setupLevel(levelPoseStack.pose());
    }

    // Used to change the normal based on which quad side we're rendering.
    public static void setNormalQuad(PoseStack.Pose pose, BakedQuad quad)
    {
        pose.normal().setIdentity();
        if (quad.getDirection() == Direction.NORTH)
            pose.normal().mul(-1.0F);
    }
}