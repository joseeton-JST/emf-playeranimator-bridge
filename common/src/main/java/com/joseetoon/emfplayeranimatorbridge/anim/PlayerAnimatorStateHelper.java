package com.joseetoon.emfplayeranimatorbridge.anim;

import com.joseetoon.emfplayeranimatorbridge.mixin.AnimationStackAccessor;
import dev.kosmx.playerAnim.api.layered.AnimationContainer;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Consumer;

public final class PlayerAnimatorStateHelper {

    public enum IgnoreReason {
        NOT_ANIMATED,
        STACK_INACTIVE,
        ONLY_BLANK_LOOP,
        NO_ENABLED_PARTS,
        HAS_RELEVANT_ANIMATION
    }

    private static final Set<ResourceLocation> IGNORED_ANIMATION_IDS = Set.of(
            ResourceLocation.fromNamespaceAndPath("seriousplayeranimations", "blank_loop")
    );

    private PlayerAnimatorStateHelper() {
    }

    public record AnimationState(boolean hasRelevantAnimation,
                                 Set<BridgeBodyPart> activeParts,
                                 IgnoreReason reason) {
        public static final AnimationState INACTIVE = new AnimationState(false, Set.of(), IgnoreReason.NOT_ANIMATED);
    }

    @Nullable
    public static AnimationApplier getAnimation(LivingEntity entity) {
        if (entity instanceof IAnimatedPlayer animatedPlayer) {
            return animatedPlayer.playerAnimator_getAnimation();
        }
        return null;
    }

    public static boolean isAnimated(LivingEntity entity) {
        return inspect(entity).hasRelevantAnimation();
    }

    public static boolean isAnyAnimationActive(LivingEntity entity) {
        AnimationApplier animation = getAnimation(entity);
        return animation != null && animation.isActive();
    }

    public static Set<BridgeBodyPart> getActiveBodyParts(LivingEntity entity) {
        if (!(entity instanceof IAnimatedPlayer animatedPlayer)) {
            return Set.of();
        }
        return inspect(animatedPlayer.getAnimationStack()).activeParts();
    }

    public static Set<BridgeBodyPart> getActiveBodyParts(@Nullable IAnimation animation) {
        return inspect(animation).activeParts();
    }

    public static AnimationState inspect(LivingEntity entity) {
        if (!(entity instanceof IAnimatedPlayer animatedPlayer)) {
            return AnimationState.INACTIVE;
        }
        return inspect(animatedPlayer.getAnimationStack());
    }

    public static AnimationState inspect(@Nullable IAnimation animation) {
        if (animation == null) {
            return AnimationState.INACTIVE;
        }

        boolean[] hasNonIgnoredKeyframe = new boolean[1];
        boolean[] hasEnabledParts = new boolean[1];
        boolean[] hasRelevantAnimation = new boolean[1];
        EnumSet<BridgeBodyPart> activeParts = EnumSet.noneOf(BridgeBodyPart.class);

        consumeAnimations(animation, keyframePlayer -> {
            if (!keyframePlayer.isActive()) {
                return;
            }
            if (isIgnoredAnimation(keyframePlayer)) {
                return;
            }
            hasNonIgnoredKeyframe[0] = true;
            addActiveParts(keyframePlayer, activeParts, hasEnabledParts);
        });

        if (hasNonIgnoredKeyframe[0] && hasEnabledParts[0]) {
            hasRelevantAnimation[0] = true;
        }

        IgnoreReason reason = resolveReason(animation, hasNonIgnoredKeyframe[0], hasEnabledParts[0]);
        Set<BridgeBodyPart> immutableParts = activeParts.isEmpty() ? Set.of() : Collections.unmodifiableSet(activeParts);
        return new AnimationState(hasRelevantAnimation[0], immutableParts, reason);
    }

    public static boolean isWithinFinalTicksWindow(LivingEntity entity, int remainingTicksThreshold) {
        if (remainingTicksThreshold <= 0) {
            return false;
        }
        if (!(entity instanceof IAnimatedPlayer animatedPlayer)) {
            return false;
        }
        return isWithinFinalTicksWindow(animatedPlayer.getAnimationStack(), remainingTicksThreshold);
    }

    public static boolean isWithinFinalTicksWindow(@Nullable IAnimation animation, int remainingTicksThreshold) {
        if (animation == null || remainingTicksThreshold <= 0) {
            return false;
        }

        return getFiniteRelevantRemainingTicks(animation)
                .stream()
                .anyMatch(remainingTicks -> remainingTicks <= remainingTicksThreshold);
    }

    public static OptionalInt getFiniteRelevantRemainingTicks(LivingEntity entity) {
        if (!(entity instanceof IAnimatedPlayer animatedPlayer)) {
            return OptionalInt.empty();
        }
        return getFiniteRelevantRemainingTicks(animatedPlayer.getAnimationStack());
    }

    public static OptionalInt getFiniteRelevantRemainingTicks(@Nullable IAnimation animation) {
        if (animation == null) {
            return OptionalInt.empty();
        }

        int[] bestRemainingTicks = {Integer.MAX_VALUE};
        consumeAnimations(animation, keyframePlayer -> {
            if (!keyframePlayer.isActive() || keyframePlayer.isInfinite() || isIgnoredAnimation(keyframePlayer)) {
                return;
            }

            int remainingTicks = keyframePlayer.getStopTick() - keyframePlayer.getCurrentTick();
            if (remainingTicks < 0) {
                return;
            }

            boolean[] hasEnabledParts = new boolean[1];
            addActiveParts(keyframePlayer, EnumSet.noneOf(BridgeBodyPart.class), hasEnabledParts);
            if (hasEnabledParts[0]) {
                bestRemainingTicks[0] = Math.min(bestRemainingTicks[0], remainingTicks);
            }
        });
        return bestRemainingTicks[0] == Integer.MAX_VALUE
                ? OptionalInt.empty()
                : OptionalInt.of(bestRemainingTicks[0]);
    }

    private static IgnoreReason resolveReason(@Nullable IAnimation animation,
                                              boolean hasNonIgnoredKeyframe,
                                              boolean hasEnabledParts) {
        if (animation == null) {
            return IgnoreReason.NOT_ANIMATED;
        }
        if (hasNonIgnoredKeyframe && hasEnabledParts) {
            return IgnoreReason.HAS_RELEVANT_ANIMATION;
        }
        if (!hasNonIgnoredKeyframe) {
            AnimationApplier applier = asApplier(animation);
            if (applier != null && !applier.isActive()) {
                return IgnoreReason.STACK_INACTIVE;
            }
            return IgnoreReason.ONLY_BLANK_LOOP;
        }
        return IgnoreReason.NO_ENABLED_PARTS;
    }

    @Nullable
    private static AnimationApplier asApplier(@Nullable IAnimation animation) {
        if (animation instanceof AnimationApplier applier) {
            return applier;
        }
        return null;
    }

    private static void consumeAnimations(@Nullable IAnimation animation, Consumer<KeyframeAnimationPlayer> consumer) {
        if (animation == null) {
            return;
        }
        if (animation instanceof KeyframeAnimationPlayer keyframePlayer) {
            consumer.accept(keyframePlayer);
            return;
        }
        if (animation instanceof ModifierLayer<?> modifierLayer) {
            consumeAnimations(modifierLayer.getAnimation(), consumer);
            return;
        }
        if (animation instanceof AnimationContainer<?> animationContainer) {
            consumeAnimations(animationContainer.getAnim(), consumer);
            return;
        }
        if (animation instanceof AnimationStack animationStack) {
            ArrayList<Pair<Integer, IAnimation>> layers = ((AnimationStackAccessor) animationStack).emfplayeranimatorbridge$getLayers();
            for (Pair<Integer, IAnimation> layer : layers) {
                consumeAnimations(layer.getRight(), consumer);
            }
        }
    }

    private static void addActiveParts(KeyframeAnimationPlayer keyframePlayer,
                                       Set<BridgeBodyPart> activeParts,
                                       boolean[] hasEnabledPartsOut) {
        for (Map.Entry<String, KeyframeAnimationPlayer.BodyPart> entry : keyframePlayer.bodyParts.entrySet()) {
            if (!isBodyPartActive(entry.getValue())) {
                continue;
            }

            hasEnabledPartsOut[0] = true;
            BridgeBodyPart bridgeBodyPart = BridgeBodyPart.fromAnimationName(entry.getKey());
            if (bridgeBodyPart != null) {
                activeParts.add(bridgeBodyPart);
            }
        }
    }

    private static boolean isBodyPartActive(KeyframeAnimationPlayer.BodyPart bodyPart) {
        return bodyPart.part != null && bodyPart.part.isEnabled();
    }

    private static boolean isIgnoredAnimation(KeyframeAnimationPlayer keyframeAnimationPlayer) {
        KeyframeAnimation animationData = keyframeAnimationPlayer.getData();
        for (ResourceLocation ignoredAnimationId : IGNORED_ANIMATION_IDS) {
            if (PlayerAnimationRegistry.getAnimationOptional(ignoredAnimationId)
                    .filter(animationData::equals)
                    .isPresent()) {
                return true;
            }
        }
        return false;
    }
}
