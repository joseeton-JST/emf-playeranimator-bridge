package com.joseetoon.emfplayeranimatorbridge.neoforge;

import com.joseetoon.emfplayeranimatorbridge.BridgeConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

final class NeoForgeClientConfig {
    static final ModConfigSpec SPEC;

    private static final ModConfigSpec.BooleanValue ENABLE_BRIDGE;
    private static final ModConfigSpec.BooleanValue DEBUG_LOGGING;
    private static final ModConfigSpec.IntValue PAUSE_COOLDOWN_TICKS;
    private static final ModConfigSpec.IntValue RELEASE_BLEND_START_TICKS;
    private static final ModConfigSpec.BooleanValue REAL_CAMERA_FIRST_PERSON_FIX;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("bridge");
        ENABLE_BRIDGE = builder
                .comment("Enable the EMF PlayerAnimator Bridge compatibility hook.")
                .translation("emfplayeranimatorbridge.config.enable_bridge")
                .define("enableBridge", true);
        DEBUG_LOGGING = builder
                .comment("Enable targeted debug logging for compatibility troubleshooting.")
                .translation("emfplayeranimatorbridge.config.debug_logging")
                .define("debugLogging", false);
        REAL_CAMERA_FIRST_PERSON_FIX = builder
                .comment("Real Camera compat fix. Local-player only, first-person only, intended to stop Real Camera body flicker during Player Animator emotes. Has no effect without Real Camera installed and intentionally leaves third person untouched.")
                .translation("emfplayeranimatorbridge.config.real_camera_first_person_fix")
                .define("realCameraFirstPersonFix", true);
        PAUSE_COOLDOWN_TICKS = builder
                .comment("Number of ticks the EMF lock/pause stays in effect after the last frame with a real (non-blank) Player Animator emote. Smooths over brief drops to blank_loop between emotes. Set to 0 to disable cooldown.")
                .translation("emfplayeranimatorbridge.config.emf_pause_cooldown_ticks")
                .defineInRange("emfPauseCooldownTicks", 5, 0, 200);
        RELEASE_BLEND_START_TICKS = builder
                .comment("How many ticks before a finite Player Animator emote ends the release blend should begin. 0 disables early start and waits until the emote fully ends.")
                .translation("emfplayeranimatorbridge.config.release_blend_start_ticks")
                .defineInRange("releaseBlendStartTicks", 4, 0, 20);
        builder.pop();
        SPEC = builder.build();
    }

    private NeoForgeClientConfig() {
    }

    static void apply() {
        BridgeConfig.update(
                ENABLE_BRIDGE.get(),
                DEBUG_LOGGING.get(),
                false,
                PAUSE_COOLDOWN_TICKS.get(),
                RELEASE_BLEND_START_TICKS.get(),
                REAL_CAMERA_FIRST_PERSON_FIX.get()
        );
    }
}
