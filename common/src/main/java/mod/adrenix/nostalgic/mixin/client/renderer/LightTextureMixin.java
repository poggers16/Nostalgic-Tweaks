package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.math.Vector3f;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.WorldClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightTexture.class)
public abstract class LightTextureMixin
{
    /* Shadows */

    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private NativeImage lightPixels;
    @Shadow @Final private DynamicTexture lightTexture;
    @Shadow private boolean updateLightTexture;

    /* Injections */

    /**
     * Disables the light flickering from light emitting sources.
     * Controlled by the old light flicker tweak.
     */
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void NT$onTick(CallbackInfo callback)
    {
        if (ModConfig.Candy.disableLightFlicker())
        {
            this.updateLightTexture = true;
            callback.cancel();
        }
    }

    /**
     * Simulates the old lighting engine by bringing back old light colors and abrupt skylight transitioning.
     * Controlled by old light rendering and old light colors tweaks.
     */
    @Inject(method = "updateLightTexture", at = @At("HEAD"), cancellable = true)
    private void NT$onUpdateLightTexture(float partialTicks, CallbackInfo callback)
    {
        ClientLevel level = this.minecraft.level;

        if (level == null || this.minecraft.player == null || !this.updateLightTexture)
            return;

        if (ModConfig.Candy.oldClassicLight())
        {
            this.minecraft.getProfiler().push("lightTex");
            this.updateLightTexture = false;

            for (int y = 0; y < 16; y++)
            {
                for (int x = 0; x < 16; x++)
                {
                    if (x == 15 || y == 15)
                        this.lightPixels.setPixelRGBA(x, y, 255 << 24 | 255 << 16 | 255 << 8 | 255);
                    else
                        this.lightPixels.setPixelRGBA(x, y, 255 << 24 | 153 << 16 | 153 << 8 | 153);
                }
            }

            this.lightTexture.upload();
            this.minecraft.getProfiler().pop();

            callback.cancel();
            return;
        }

        if (!ModConfig.Candy.oldLightRendering() || !ModConfig.Candy.oldLightColor())
            return;

        this.minecraft.getProfiler().push("lightTex");
        this.updateLightTexture = false;

        double gammaSetting = this.minecraft.options.gamma;
        float darkenAmount = this.minecraft.gameRenderer.getDarkenWorldAmount(partialTicks);
        float waterVision = this.minecraft.player.getWaterVision();
        float potionEffect = this.minecraft.player.hasEffect(MobEffects.NIGHT_VISION) ?
            GameRenderer.getNightVisionScale(this.minecraft.player, partialTicks) :
            (waterVision > 0.0F && this.minecraft.player.hasEffect(MobEffects.CONDUIT_POWER) ? waterVision : 0.0F)
        ;

        boolean isGammaDisabled = ModConfig.Candy.disableBrightness();
        boolean isFlashPresent = level.getSkyFlashTime() > 0 && !this.minecraft.options.hideLightningFlashes;
        boolean isWorldDarkening = darkenAmount > 0;

        float skyLightSubtracted = WorldClientUtil.getSkylightSubtracted(level);

        if (isFlashPresent)
            skyLightSubtracted = 1;
        else if (isWorldDarkening)
        {
            skyLightSubtracted += Math.ceil(3 * darkenAmount);
            skyLightSubtracted = Mth.clamp(skyLightSubtracted, 1.0F, 15.0F);
        }

        for (int y = 0; y < 16; y++)
        {
            for (int x = 0; x < 16; x++)
            {
                float fromBlockLight = WorldClientUtil.getOldBrightness(x);
                float fromSkyLight = WorldClientUtil.getOldBrightness((int) Math.max(y - skyLightSubtracted, 0));

                if (level.dimension() == Level.END)
                    fromSkyLight = 0.22F + fromSkyLight * 0.75F;

                if (potionEffect > 0.0F)
                {
                    float shiftBrightness = potionEffect * 0.7F;
                    float adjustBlockColor = ((1.0F - fromBlockLight - 0.5F) * (shiftBrightness * fromBlockLight)) + (0.5F * potionEffect);
                    float skyAdjust = ((1.0F - fromSkyLight - 0.5F) * (shiftBrightness * fromSkyLight)) + (0.5F * potionEffect);

                    fromBlockLight += adjustBlockColor;
                    fromSkyLight += skyAdjust;
                }

                double gamma = isGammaDisabled ? 0.0D : gammaSetting;
                float blockLight = Mth.clamp(fromBlockLight * 255.0F * ((float) gamma + 1.0F), 0.0F, 255.0F);
                float skyLight = Mth.clamp(fromSkyLight * 255.0F * ((float) gamma + 1.0F), 0.0F, 255.0F);
                float light = fromBlockLight > fromSkyLight ? blockLight : skyLight;

                this.lightPixels.setPixelRGBA(x, y, 255 << 24 | (int) light << 16 | (int) light << 8 | (int) light);
            }
        }

        this.lightTexture.upload();
        this.minecraft.getProfiler().pop();

        callback.cancel();
    }

    /**
     * Maintains old light colors when old light rendering is disabled.
     * Controlled by the old light colors tweak.
     */
    @Redirect
    (
        method = "updateLightTexture",
        slice = @Slice(from = @At(value = "INVOKE", target = "Lcom/mojang/math/Vector3f;map(Lit/unimi/dsi/fastutil/floats/Float2FloatFunction;)V")),
        at = @At(value = "INVOKE", target = "Lcom/mojang/math/Vector3f;mul(F)V")
    )
    private void NT$onFinalizeColor(Vector3f rgb, float multiplier)
    {
        if (ModConfig.Candy.oldLightColor())
        {
            float average = (rgb.x() + rgb.y() + rgb.z()) / 3.0F;
            rgb.set(average, average, average);
        }

        rgb.mul(multiplier);
    }
}