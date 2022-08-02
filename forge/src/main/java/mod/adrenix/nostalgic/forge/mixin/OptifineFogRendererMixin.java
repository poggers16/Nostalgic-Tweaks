package mod.adrenix.nostalgic.forge.mixin;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin allows for the mod to tap into optifine changes, if that mod is installed.
 * Otherwise, we should use Forge's ViewportEvent.RenderFog event.
 */
@Mixin(FogRenderer.class)
public abstract class OptifineFogRendererMixin
{
    @Inject(method = "setupFog", at = @At(value = "RETURN"))
    private static void NT$onSetupFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean nearFog, float partialTick, CallbackInfo callback)
    {
        if (NostalgicTweaks.isOptifineInstalled)
        {
            ModClientUtil.Fog.setupFog(camera, fogMode);
            ModClientUtil.Fog.setupNetherFog(camera, fogMode);
        }
    }
}