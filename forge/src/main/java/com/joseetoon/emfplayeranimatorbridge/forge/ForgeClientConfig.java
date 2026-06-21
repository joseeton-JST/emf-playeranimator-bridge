package com.joseetoon.emfplayeranimatorbridge.forge;

import com.joseetoon.emfplayeranimatorbridge.BridgeConfig;
import net.minecraftforge.common.ForgeConfigSpec;

final class ForgeClientConfig {
    static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.BooleanValue ENABLE_BRIDGE;
    private static final ForgeConfigSpec.BooleanValue DEBUG_LOGGING;
    private static final ForgeConfigSpec.BooleanValue EMF_PER_PART_PAUSE;
    private static final ForgeConfigSpec.IntValue PAUSE_COOLDOWN_TICKS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("bridge");
        ENABLE_BRIDGE = builder
                .comment("Enable the EMF PlayerAnimator Bridge compatibility hook.")
                .define("enableBridge", true);
        DEBUG_LOGGING = builder
                .comment("Enable targeted debug logging for compatibility troubleshooting.")
                .define("debugLogging", false);
        EMF_PER_PART_PAUSE = builder
                .comment("Legacy/debug: pause EMF custom animations per-ModelPart instead of locking the entity to the vanilla model. The per-part pause does NOT prevent EMF from rendering its custom model; it only skips applying the EMF pose to those specific parts. For correct visual override of Player Animator emotes over EMF custom models, leave this OFF (the bridge will use lockEntityToVanillaModel instead).")
                .define("emfPerPartPause", false);
        PAUSE_COOLDOWN_TICKS = builder
                .comment("Number of ticks the EMF lock/pause stays in effect after the last frame with a real (non-blank) Player Animator emote. Smooths over brief drops to blank_loop between emotes. Set to 0 to disable cooldown.")
                .defineInRange("emfPauseCooldownTicks", 5, 0, 200);
        builder.pop();
        SPEC = builder.build();
    }

    private ForgeClientConfig() {
    }

    static void apply() {
        BridgeConfig.update(ENABLE_BRIDGE.get(), DEBUG_LOGGING.get(), EMF_PER_PART_PAUSE.get(), PAUSE_COOLDOWN_TICKS.get());
    }
}
