package com.joseetoon.emfplayeranimatorbridge.forge;

import com.joseetoon.emfplayeranimatorbridge.BridgeConfig;
import net.minecraftforge.common.ForgeConfigSpec;

final class ForgeClientConfig {
    static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.BooleanValue ENABLE_BRIDGE;
    private static final ForgeConfigSpec.BooleanValue DEBUG_LOGGING;
    private static final ForgeConfigSpec.IntValue PAUSE_COOLDOWN_TICKS;
    private static final ForgeConfigSpec.BooleanValue REAL_CAMERA_FIRST_PERSON_FIX;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("bridge");
        ENABLE_BRIDGE = builder
                .comment("Enable the EMF PlayerAnimator Bridge compatibility hook.")
                .define("enableBridge", true);
        DEBUG_LOGGING = builder
                .comment("Enable targeted debug logging for compatibility troubleshooting.")
                .define("debugLogging", false);
        REAL_CAMERA_FIRST_PERSON_FIX = builder
                .comment("Real Camera compat fix. Local-player only, first-person only, intended to stop Real Camera body flicker during Player Animator emotes. Has no effect without Real Camera installed and intentionally leaves third person untouched.")
                .define("realCameraFirstPersonFix", true);
        PAUSE_COOLDOWN_TICKS = builder
                .comment("Number of ticks the EMF lock/pause stays in effect after the last frame with a real (non-blank) Player Animator emote. Smooths over brief drops to blank_loop between emotes. Set to 0 to disable cooldown.")
                .defineInRange("emfPauseCooldownTicks", 5, 0, 200);
        builder.pop();
        SPEC = builder.build();
    }

    private ForgeClientConfig() {
    }

    static void apply() {
        BridgeConfig.update(
                ENABLE_BRIDGE.get(),
                DEBUG_LOGGING.get(),
                false,
                PAUSE_COOLDOWN_TICKS.get(),
                REAL_CAMERA_FIRST_PERSON_FIX.get()
        );
    }
}
