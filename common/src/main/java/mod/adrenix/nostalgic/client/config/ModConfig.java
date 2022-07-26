package mod.adrenix.nostalgic.client.config;

import mod.adrenix.nostalgic.client.config.tweak.*;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class ModConfig
{
    /* Configuration Mixin References */

    private static final ClientConfig.Animation ANIMATION = CommonRegistry.getAnimation();
    private static final ClientConfig.EyeCandy CANDY = CommonRegistry.getCandy();
    private static final ClientConfig.Sound SOUND = CommonRegistry.getSound();
    private static final ClientConfig.Swing SWING = CommonRegistry.getSwing();
    private static final ClientConfig CONFIG = CommonRegistry.getRoot();
    private static <E extends Enum<E> & TweakVersion.IDisabled<E>> E getVersion(@Nullable ITweak tweak, E current)
    {
        return !isModEnabled(tweak) ? current.getDisabled() : current;
    }

    public static boolean isModEnabled(@Nullable ITweak tweak)
    {
        if (tweak != null)
            tweak.setEnabled();
        return CONFIG.isModEnabled;
    }

    /* Swing Speed Mixin Options */

    public static class Swing
    {
        public static int getSpeedFromItem(Item item)
        {
            Map.Entry<String, Integer> entry = CustomSwings.getEntryFromItem(item);

            if (isSpeedGlobal())
                return SWING.global;
            else if (entry != null)
                return entry.getValue();
            else if (item instanceof SwordItem)
                return SWING.sword;
            else if (item instanceof BlockItem)
                return SWING.block;
            else if (item instanceof DiggerItem)
                return SWING.tool;
            return SWING.item;
        }

        public static int getSwingSpeed(AbstractClientPlayer player)
        {
            if (isModEnabled(null))
                return getSpeedFromItem(player.getMainHandItem().getItem());
            return DefaultConfig.Swing.NEW_SPEED;
        }

        public static boolean isOverridingFatigue() { return isModEnabled(null) && SWING.fatigue != DefaultConfig.Swing.GLOBAL; }
        public static boolean isOverridingSpeeds() { return !isModEnabled(SwingTweak.OVERRIDE_SPEEDS) || SWING.overrideSpeeds; }
        public static boolean isOverridingHaste() { return isModEnabled(null) && SWING.haste != DefaultConfig.Swing.GLOBAL; }
        public static boolean isSpeedGlobal() { return SWING.global != DefaultConfig.Swing.GLOBAL; }
        public static int getFatigueSpeed() { return isSpeedGlobal() ? SWING.global : SWING.fatigue; }
        public static int getHasteSpeed() { return isSpeedGlobal() ? SWING.global : SWING.haste; }
        public static int getSwingSpeed() { return getSwingSpeed(Minecraft.getInstance().player); }
        public static int getGlobalSpeed() { return SWING.global; }
    }

    /* Sound Mixin Options */

    public static class Sound
    {
        public static boolean oldAttack() { return isModEnabled(SoundTweak.OLD_ATTACK) && SOUND.oldAttack; }
        public static boolean oldDamage() { return isModEnabled(SoundTweak.OLD_HURT) && SOUND.oldHurt; }
        public static boolean oldFall() { return isModEnabled(SoundTweak.OLD_FALL) && SOUND.oldFall; }
        public static boolean oldStep() { return isModEnabled(SoundTweak.OLD_STEP) && SOUND.oldStep; }
        public static boolean oldDoor() { return isModEnabled(SoundTweak.OLD_DOOR) && SOUND.oldDoor; }
        public static boolean oldBed() { return isModEnabled(SoundTweak.OLD_BED) && SOUND.oldBed; }
        public static boolean oldXP() { return isModEnabled(SoundTweak.OLD_XP) && SOUND.oldXP; }
    }

    /* Eye Candy Mixin Options */

    public static class Candy
    {
        /* Boolean Tweaks */

        // Block Candy
        public static boolean fixAmbientOcclusion() { return isModEnabled(CandyTweak.FIX_AO) && CANDY.fixAmbientOcclusion; }
        public static boolean oldTrappedChest() { return isModEnabled(CandyTweak.TRAPPED_CHEST) && CANDY.oldTrappedChest; }
        public static boolean oldEnderChest() { return isModEnabled(CandyTweak.ENDER_CHEST) && CANDY.oldEnderChest; }
        public static boolean oldChestVoxel() { return isModEnabled(CandyTweak.CHEST_VOXEL) && CANDY.oldChestVoxel; }
        public static boolean oldChest() { return isModEnabled(CandyTweak.CHEST) && CANDY.oldChest; }

        // Interface Candy
        public static boolean oldPlainSelectedItemName() { return isModEnabled(CandyTweak.PLAIN_SELECTED_ITEM_NAME) && CANDY.oldPlainSelectedItemName; }
        public static boolean oldNoSelectedItemName() { return isModEnabled(CandyTweak.NO_SELECTED_ITEM_NAME) && CANDY.oldNoSelectedItemName; }
        public static boolean oldDurabilityColors() { return isModEnabled(CandyTweak.DURABILITY_COLORS) && CANDY.oldDurabilityColors; }
        public static boolean oldNoItemTooltips() { return isModEnabled(CandyTweak.NO_ITEM_TOOLTIPS) && CANDY.oldNoItemTooltips; }
        public static boolean oldVersionOverlay() { return isModEnabled(CandyTweak.VERSION_OVERLAY) && CANDY.oldVersionOverlay; }
        public static boolean oldLoadingScreens() { return isModEnabled(CandyTweak.LOADING_SCREENS) && CANDY.oldLoadingScreens; }
        public static boolean removeLoadingBar() { return isModEnabled(CandyTweak.REMOVE_LOADING_BAR) && CANDY.removeLoadingBar; }
        public static boolean oldButtonHover() { return isModEnabled(CandyTweak.BUTTON_HOVER) && CANDY.oldButtonHover; }
        public static boolean oldChatInput() { return isModEnabled(CandyTweak.CHAT_INPUT) && CANDY.oldChatInput; }
        public static boolean oldTooltips() { return isModEnabled(CandyTweak.TOOLTIP_BOXES) && CANDY.oldTooltipBoxes; }
        public static boolean oldChatBox() { return isModEnabled(CandyTweak.CHAT_BOX) && CANDY.oldChatBox; }

        // Item Candy
        public static boolean fixItemModelGaps() { return isModEnabled(CandyTweak.FIX_ITEM_MODEL_GAP) && CANDY.fixItemModelGap; }
        public static boolean oldFloatingItems() { return isModEnabled(CandyTweak.FLAT_ITEMS) && CANDY.old2dItems; }
        public static boolean oldFlatEnchantment() { return isModEnabled(CandyTweak.FLAT_ENCHANTED_ITEMS) && oldFloatingItems() && CANDY.old2dEnchantedItems; }
        public static boolean oldFlatThrowing() { return isModEnabled(CandyTweak.FLAT_THROW_ITEMS) && CANDY.old2dThrownItems; }
        public static boolean oldItemHolding() { return isModEnabled(CandyTweak.ITEM_HOLDING) && CANDY.oldItemHolding; }
        public static boolean oldItemMerging() { return isModEnabled(CandyTweak.ITEM_MERGING) && CANDY.oldItemMerging; }
        public static boolean oldFlatFrames() { return isModEnabled(CandyTweak.FLAT_FRAMES) && CANDY.old2dFrames; }

        // Lighting Candy
        public static boolean oldSmoothLighting() { return isModEnabled(CandyTweak.SMOOTH_LIGHTING) && CANDY.oldSmoothLighting; }
        public static boolean oldNetherLighting() { return isModEnabled(CandyTweak.NETHER_LIGHTING) && CANDY.oldNetherLighting; }
        public static boolean oldLeavesLighting() { return isModEnabled(CandyTweak.LEAVES_LIGHTING) && CANDY.oldLeavesLighting; }
        public static boolean oldWaterLighting() { return isModEnabled(CandyTweak.WATER_LIGHTING) && CANDY.oldWaterLighting; }
        public static boolean oldLightFlicker() { return isModEnabled(CandyTweak.LIGHT_FLICKER) && CANDY.oldLightFlicker; }
        public static boolean oldLighting() { return isModEnabled(CandyTweak.LIGHTING) && CANDY.oldLighting; }

        // Particle Candy
        public static boolean oldNoCriticalHitParticles() { return isModEnabled(CandyTweak.NO_CRIT_PARTICLES) && CANDY.oldNoCritParticles; }
        public static boolean oldMixedExplosionParticles() { return isModEnabled(CandyTweak.MIXED_EXPLOSION_PARTICLES) && CANDY.oldMixedExplosionParticles; }
        public static boolean oldNoEnchantHitParticles() { return isModEnabled(CandyTweak.NO_MAGIC_HIT_PARTICLES) && CANDY.oldNoMagicHitParticles; }
        public static boolean oldExplosionParticles() { return isModEnabled(CandyTweak.EXPLOSION_PARTICLES) && CANDY.oldExplosionParticles; }
        public static boolean oldNoDamageParticles() { return isModEnabled(CandyTweak.NO_DAMAGE_PARTICLES) && CANDY.oldNoDamageParticles; }
        public static boolean oldOpaqueExperience() { return isModEnabled(CandyTweak.OPAQUE_EXPERIENCE) && CANDY.oldOpaqueExperience; }
        public static boolean oldSweepParticles() { return isModEnabled(CandyTweak.SWEEP) && CANDY.oldSweepParticles; }

        // Title Screen Candy
        public static boolean overrideTitleScreen() { return isModEnabled(CandyTweak.OVERRIDE_TITLE_SCREEN) && CANDY.overrideTitleScreen; }
        public static boolean removeAccessibilityButton() { return isModEnabled(CandyTweak.TITLE_ACCESSIBILITY) && CANDY.removeTitleAccessibilityButton; }
        public static boolean removeTitleModLoaderText() { return isModEnabled(CandyTweak.TITLE_MOD_LOADER_TEXT) && CANDY.removeTitleModLoaderText; }
        public static boolean removeLanguageButton() { return isModEnabled(CandyTweak.TITLE_LANGUAGE) && CANDY.removeTitleLanguageButton; }
        public static boolean titleBottomLeftText() { return isModEnabled(CandyTweak.TITLE_BOTTOM_LEFT_TEXT) && CANDY.titleBottomLeftText; }
        public static boolean oldTitleBackground() { return isModEnabled(CandyTweak.TITLE_BACKGROUND) && CANDY.oldTitleBackground; }
        public static boolean oldLogoOutline() { return isModEnabled(CandyTweak.LOGO_OUTLINE) && CANDY.oldLogoOutline; }
        public static boolean oldAlphaLogo() { return isModEnabled(CandyTweak.ALPHA_LOGO) && CANDY.oldAlphaLogo; }
        public static boolean uncapTitleFPS() { return isModEnabled(CandyTweak.UNCAP_TITLE_FPS) && CANDY.uncapTitleFPS; }

        // World Candy
        public static boolean oldSunriseSunsetFog() { return isModEnabled(CandyTweak.SUNRISE_SUNSET_FOG) && CANDY.oldSunriseSunsetFog; }
        public static boolean oldBlueVoidOverride() { return isModEnabled(CandyTweak.BLUE_VOID_OVERRIDE) && CANDY.oldBlueVoidOverride; }
        public static boolean oldDarkVoidHeight() { return isModEnabled(CandyTweak.DARK_VOID_HEIGHT) && CANDY.oldDarkVoidHeight; }
        public static boolean oldSunriseAtNorth() { return isModEnabled(CandyTweak.SUNRISE_AT_NORTH) && CANDY.oldSunriseAtNorth; }
        public static boolean oldSquareBorder() { return isModEnabled(CandyTweak.SQUARE_BORDER) && CANDY.oldSquareBorder; }
        public static boolean oldTerrainFog() { return isModEnabled(CandyTweak.TERRAIN_FOG) && CANDY.oldTerrainFog; }
        public static boolean oldHorizonFog() { return isModEnabled(CandyTweak.HORIZON_FOG) && CANDY.oldHorizonFog; }
        public static boolean oldNetherFog() { return isModEnabled(CandyTweak.NETHER_FOG) && CANDY.oldNetherFog; }
        public static boolean oldStars() { return isModEnabled(CandyTweak.STARS) && CANDY.oldStars; }

        /* Version Tweaks */

        public static TweakVersion.ButtonLayout getButtonLayout() { return getVersion(CandyTweak.TITLE_BUTTON_LAYOUT, CANDY.oldButtonLayout); }
        public static TweakVersion.Overlay getLoadingOverlay() { return getVersion(CandyTweak.LOADING_OVERLAY, CANDY.oldLoadingOverlay); }
        public static TweakVersion.Generic getSkyColor() { return getVersion(CandyTweak.SKY_COLOR, CANDY.oldSkyColor); }
        public static TweakVersion.Generic getFogColor() { return getVersion(CandyTweak.FOG_COLOR, CANDY.oldFogColor); }
        public static TweakVersion.Generic getBlueVoid() { return getVersion(CandyTweak.BLUE_VOID, CANDY.oldBlueVoid); }
        public static TweakVersion.Hotbar getHotbar() { return getVersion(CandyTweak.CREATIVE_HOTBAR, CANDY.oldCreativeHotbar); }

        /* String Tweaks */

        private static String parseColor(String text)
        {
            text = text.replaceAll("%v", SharedConstants.getCurrentVersion().getName());
            text = text.replaceAll("%", "§");
            return text;
        }

        public static String getOverlayText() { return parseColor(CANDY.oldOverlayText); }
        public static String getVersionText() { return parseColor(CANDY.titleVersionText); }

        /* Integer Tweaks */

        public static int getCloudHeight() { return isModEnabled(CandyTweak.CLOUD_HEIGHT) ? CANDY.oldCloudHeight : 192; }
    }

    /* Animation Mixin Options */

    public static class Animation
    {
        public static float getArmSwayIntensity()
        {
            float mirror = shouldMirrorArmSway() ? -1.0F : 1.0F;
            return isModEnabled(null) ? (((float) ANIMATION.armSwayIntensity) * mirror / 100.0F) : 1.0F;
        }

        public static boolean shouldMirrorArmSway() { return isModEnabled(null) && ANIMATION.armSwayMirror; }
        public static boolean oldVerticalBobbing() { return isModEnabled(AnimationTweak.BOB_VERTICAL) && ANIMATION.oldVerticalBobbing; }
        public static boolean oldCollideBobbing() { return isModEnabled(AnimationTweak.COLLIDE_BOB) && ANIMATION.oldCollideBobbing; }
        public static boolean oldGhastCharging() { return isModEnabled(AnimationTweak.GHAST_CHARGING) && ANIMATION.oldGhastCharging; }
        public static boolean oldToolExplosion() { return isModEnabled(AnimationTweak.TOOL_EXPLODE) && ANIMATION.oldToolExplosion; }
        public static boolean oldSwingDropping() { return isModEnabled(AnimationTweak.SWING_DROP) && ANIMATION.oldSwingDropping; }
        public static boolean oldSkeletonArms() { return isModEnabled(AnimationTweak.SKELETON_ARMS) && ANIMATION.oldSkeletonArms; }
        public static boolean oldItemCooldown() { return isModEnabled(AnimationTweak.COOLDOWN) && ANIMATION.oldItemCooldown; }
        public static boolean oldItemReequip() { return isModEnabled(AnimationTweak.REEQUIP) && ANIMATION.oldItemReequip; }
        public static boolean oldZombieArms() { return isModEnabled(AnimationTweak.ZOMBIE_ARMS) && ANIMATION.oldZombieArms; }
        public static boolean oldSneaking() { return isModEnabled(AnimationTweak.SNEAK_SMOOTH) && ANIMATION.oldSneaking; }
        public static boolean oldArmSway() { return isModEnabled(AnimationTweak.ARM_SWAY) && ANIMATION.oldArmSway; }
        public static boolean oldSwing() { return isModEnabled(AnimationTweak.ITEM_SWING) && ANIMATION.oldSwing; }
    }
}