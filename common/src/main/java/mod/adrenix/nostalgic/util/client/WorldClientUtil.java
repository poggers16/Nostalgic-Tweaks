package mod.adrenix.nostalgic.util.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;

/**
 * This utility class uses client only Minecraft code. For safety, the server should not interface with this utility.
 * For a server safe mixin utility use {@link mod.adrenix.nostalgic.util.server.WorldServerUtil}.
 */

public abstract class WorldClientUtil
{
    // Determines where the sun/moon should be rotated when rendering it.
    public static float getSunriseRotation(float vanilla)
    {
        return ModConfig.Candy.oldSunriseAtNorth() ? 0.0F : vanilla;
    }

    // Builds a sky disc for the far plane.
    public static BufferBuilder.RenderedBuffer buildSkyDisc(BufferBuilder builder, float y)
    {
        float x = Math.signum(y) * 512.0F;
        RenderSystem.setShader(GameRenderer::getPositionShader);
        builder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        builder.vertex(0.0, y, 0.0).endVertex();

        for (int i = -180; i <= 180; i += 45)
            builder.vertex(x * Mth.cos((float) i * ((float) Math.PI / 180)), y, 512.0F * Mth.sin((float) i * ((float) Math.PI / 180))).endVertex();
        return builder.end();
    }

    // Caches the model view matrix and the projection matrix so the sky can be overlaid with the blue void correctly.
    public static Matrix4f blueModelView = new Matrix4f();
    public static Matrix4f blueProjection = new Matrix4f();

    // Creates the correct blue color for the void based on the environment.
    public static void setBlueVoidColor()
    {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null)
            return;

        float weatherModifier;
        float partialTick = minecraft.getDeltaFrameTime();
        float timeOfDay = level.getTimeOfDay(partialTick);
        float boundedTime = Mth.cos(timeOfDay * ((float) Math.PI * 2)) * 2.0F + 0.5F;
        boundedTime = Mth.clamp(boundedTime, 0.0F, 1.0F);

        float r = boundedTime;
        float g = boundedTime;
        float b = boundedTime;

        float rainLevel = level.getRainLevel(partialTick);
        float thunderLevel = level.getThunderLevel(partialTick);

        if (rainLevel > 0.0F)
        {
            thunderLevel = (r * 0.3F + g * 0.59F + b * 0.11F) * 0.6F;
            weatherModifier = 1.0F - rainLevel * 0.75F;

            r = r * weatherModifier + thunderLevel * (1.0F - weatherModifier);
            g = g * weatherModifier + thunderLevel * (1.0F - weatherModifier);
            b = b * weatherModifier + thunderLevel * (1.0F - weatherModifier);
        }

        if (thunderLevel > 0.0F)
        {
            float thunderModifier = 1.0F - thunderLevel * 0.75F;
            weatherModifier = (r * 0.3F + g * 0.59F + b * 0.11F) * 0.2F;

            r = r * thunderModifier + weatherModifier * (1.0F - thunderModifier);
            g = g * thunderModifier + weatherModifier * (1.0F - thunderModifier);
            b = b * thunderModifier + weatherModifier * (1.0F - thunderModifier);
        }

        r = Mth.clamp(r, 0.1F, 1.0F);
        g = Mth.clamp(g, 0.1F, 1.0F);
        b = Mth.clamp(b, 0.1F, 1.0F);

        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(0.13F * r, 0.17F * g, 0.7F * b, 1.0F);
    }
}
