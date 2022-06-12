package mod.adrenix.nostalgic.mixin.client.model;

import mod.adrenix.nostalgic.client.config.MixinConfig;
import mod.adrenix.nostalgic.util.MixinUtil;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimationUtils.class)
public abstract class AnimationUtilsMixin
{
    /**
     * Prevents zombie arm bobbing.
     * Controlled by the zombie arm bobbing tweak.
     */
    @Inject
    (
        method = "animateZombieArms",
        at = @At
        (
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/model/AnimationUtils;bobArms(Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;F)V"
        )
    )
    private static void NT$onBobZombieArms(ModelPart leftArm, ModelPart rightArm, boolean isAggressive, float attackTime, float ageInTicks, CallbackInfo callback)
    {
        if (MixinConfig.Animation.oldZombieArms())
            MixinUtil.Animation.setStaticArms(rightArm, leftArm);
    }
}