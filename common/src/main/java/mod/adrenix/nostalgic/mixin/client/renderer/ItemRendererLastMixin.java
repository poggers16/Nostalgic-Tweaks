package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.vertex.*;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import mod.adrenix.nostalgic.util.common.ModCommonUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * To help with mod compatibility, the mixins defined in this class will be applied last.
 * Mixin injections here will occur after all other mods have finished their required modifications.
 */

@Mixin(value = ItemRenderer.class, priority = ModCommonUtil.APPLY_LAST)
public abstract class ItemRendererLastMixin
{
    /* Shadows */

    @Shadow public abstract void render(ItemStack itemStack, ItemTransforms.TransformType transformType, boolean leftHand, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, BakedModel model);

    /* Injections */

    /**
     * Used to change the normal matrix on the pose stack depending on the quad we're rendering.
     * Controlled by flat rendering state in the mixin injector helper class.
     */
    @ModifyVariable(method = "renderQuadList", at = @At("LOAD"))
    private BakedQuad NT$onRenderQuad(BakedQuad quad, PoseStack poseStack)
    {
        if (ModClientUtil.Item.isLightingFlat())
            ModClientUtil.Item.setNormalQuad(poseStack.last(), quad);
        return quad;
    }
}