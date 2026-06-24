package com.joseetoon.emfplayeranimatorbridge.compat;

import com.joseetoon.emfplayeranimatorbridge.BridgeConfig;
import com.joseetoon.emfplayeranimatorbridge.Constants;
import com.joseetoon.emfplayeranimatorbridge.anim.BridgeBodyPart;
import com.joseetoon.emfplayeranimatorbridge.anim.PlayerAnimatorStateHelper;
import com.joseetoon.emfplayeranimatorbridge.anim.PlayerAnimatorStateHelper.AnimationState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import traben.entity_model_features.EMFAnimationApi;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.parts.EMFModelPartRoot;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class EmfAnimationBridge {

    private static final Set<UUID> LOCKED_VANILLA = new HashSet<>();
    private static final Set<UUID> PART_PAUSED = new HashSet<>();
    private static final Map<UUID, Long> LAST_REAL_EMOTE_TICK = new HashMap<>();
    private static final Map<UUID, String> LAST_ACTIVE_SIGNATURES = new HashMap<>();
    private static final Map<UUID, String> DIAGNOSTIC_STATES = new HashMap<>();
    private static final Map<UUID, String> COMPAT_DECISIONS = new HashMap<>();
    private static final Set<String> UNSUPPORTED_MODELS_LOGGED = new HashSet<>();
    private static final RealCameraCompatState REAL_CAMERA_COMPAT_STATE = new RealCameraCompatState();

    private EmfAnimationBridge() {
    }

    public static boolean consumeRenderSkipFlag() {
        return RenderSkipState.consume();
    }

    public static void preRenderToBuffer(LivingEntity entity, EntityModel<?> model, float tickDelta) {
        RenderSkipState.clear();

        if (!BridgeConfig.isBridgeEnabled()) {
            clearAllState(entity);
            return;
        }

        long currentTick = entity.level().getGameTime();
        UUID entityId = entity.getUUID();
        boolean realCameraCompatEligible = isRealCameraCompatEligible(entity);
        RealCameraRenderPhase realCameraPhase = realCameraCompatEligible
                ? RealCameraRenderPhaseTracker.currentPhase()
                : RealCameraRenderPhase.NONE;
        boolean realCameraCompatRender = realCameraCompatEligible && realCameraPhase != RealCameraRenderPhase.NONE;

        if (realCameraCompatRender) {
            logCompatDecision(entity, "compat_phase:" + realCameraPhase.debugName());
            if (REAL_CAMERA_COMPAT_STATE.isRepeatedCompatRender(entityId, currentTick, realCameraPhase)) {
                if (REAL_CAMERA_COMPAT_STATE.wasSkipEligible(entityId)) {
                    RenderSkipState.arm();
                    logCompatDecision(entity, "skip_main_render:armed_repeated:" + realCameraPhase.debugName());
                } else if (realCameraPhase == RealCameraRenderPhase.REAL_CAMERA_BIND_CAPTURE) {
                    logCompatDecision(entity, "skip_main_render:suppressed_bind_capture");
                }
                return;
            }
            REAL_CAMERA_COMPAT_STATE.recordCompatRender(entityId, currentTick, realCameraPhase);
        } else {
            REAL_CAMERA_COMPAT_STATE.clearTracking(entityId);
        }

        AnimationState state = PlayerAnimatorStateHelper.inspect(entity);
        boolean emfAnimatedModel = EMFAnimationApi.isModelAnimatedByEMF(model);

        if (state.hasRelevantAnimation()) {
            LAST_REAL_EMOTE_TICK.put(entityId, currentTick);
        }

        boolean withinCooldown = isWithinCooldown(entityId, currentTick);
        boolean shouldHold = state.hasRelevantAnimation() || withinCooldown;

        if (!emfAnimatedModel) {
            if (RealCameraCompatState.shouldPreserveBridgeStateOnNonEmfRender(shouldHold)) {
                REAL_CAMERA_COMPAT_STATE.setEmoteActive(entityId, state.hasRelevantAnimation());
                REAL_CAMERA_COMPAT_STATE.setSkipEligible(entityId, false);
                if (realCameraCompatRender && realCameraPhase == RealCameraRenderPhase.REAL_CAMERA_BIND_CAPTURE) {
                    logCompatDecision(entity, "skip_main_render:suppressed_non_emf_bind_capture");
                }
                logDiagnosticState(entity, "model_not_emf_hold:" + model.getClass().getName());
                return;
            }
            clearAllState(entity);
            logDiagnosticState(entity, "model_not_emf:" + model.getClass().getName());
            return;
        }

        if (!shouldHold) {
            REAL_CAMERA_COMPAT_STATE.setEmoteActive(entityId, false);
            REAL_CAMERA_COMPAT_STATE.setSkipEligible(entityId, false);
            if (PART_PAUSED.remove(entityId)) {
                EMFAnimationApi.resumeAllCustomAnimationsForEntity(EMFAnimationApi.emfEntityOf(entity));
                clearActiveSignature(entity);
            }
            if (LOCKED_VANILLA.remove(entityId)) {
                EMFAnimationApi.unlockEntityToVanillaModel(EMFAnimationApi.emfEntityOf(entity));
                clearActiveSignature(entity);
            }
            logDiagnosticState(entity, "ignored:" + state.reason().name().toLowerCase() + ":" + model.getClass().getName());
            return;
        }

        if (BridgeConfig.isEmfPerPartPauseEnabled()) {
            applyPerPartPause(entity, model, state);
        } else {
            applyVanillaLock(entity, model, state);
        }

        boolean shouldSkipMainRender = realCameraCompatRender
                && realCameraPhase == RealCameraRenderPhase.REAL_CAMERA_BODY_RENDER
                && state.hasRelevantAnimation()
                && willEmfRenderVanillaRoot(entity);
        REAL_CAMERA_COMPAT_STATE.setEmoteActive(entityId, state.hasRelevantAnimation());
        REAL_CAMERA_COMPAT_STATE.setSkipEligible(entityId, shouldSkipMainRender);
        if (shouldSkipMainRender) {
            RenderSkipState.arm();
            logCompatDecision(entity, "skip_main_render:armed:" + realCameraPhase.debugName());
        } else if (realCameraCompatRender && realCameraPhase == RealCameraRenderPhase.REAL_CAMERA_BIND_CAPTURE) {
            logCompatDecision(entity, "skip_main_render:suppressed_bind_capture");
        }
    }

    public static void postRenderToBuffer(LivingEntity entity, EntityModel<?> model) {
        RenderSkipState.clear();

        long currentTick = entity.level().getGameTime();
        UUID entityId = entity.getUUID();
        int cooldown = BridgeConfig.getEmfPauseCooldownTicks();

        if (LOCKED_VANILLA.contains(entityId)) {
            long lastReal = LAST_REAL_EMOTE_TICK.getOrDefault(entityId, Long.MIN_VALUE);
            if (cooldown == 0 || currentTick - lastReal > cooldown) {
                EMFAnimationApi.unlockEntityToVanillaModel(EMFAnimationApi.emfEntityOf(entity));
                LOCKED_VANILLA.remove(entityId);
                clearActiveSignature(entity);
            }
        }

        if (PART_PAUSED.contains(entityId)) {
            long lastReal = LAST_REAL_EMOTE_TICK.getOrDefault(entityId, Long.MIN_VALUE);
            if (cooldown == 0 || currentTick - lastReal > cooldown) {
                EMFAnimationApi.resumeAllCustomAnimationsForEntity(EMFAnimationApi.emfEntityOf(entity));
                PART_PAUSED.remove(entityId);
                clearActiveSignature(entity);
            }
        }
    }

    private static void applyVanillaLock(LivingEntity entity, EntityModel<?> model, AnimationState state) {
        UUID entityId = entity.getUUID();
        if (PART_PAUSED.remove(entityId)) {
            EMFAnimationApi.resumeAllCustomAnimationsForEntity(EMFAnimationApi.emfEntityOf(entity));
        }
        if (LOCKED_VANILLA.add(entityId)) {
            EMFAnimationApi.lockEntityToVanillaModel(EMFAnimationApi.emfEntityOf(entity));
        }
        clearDiagnosticState(entity);
        logActiveSummary(entity, state, "locked_to_vanilla", model);
    }

    private static void applyPerPartPause(LivingEntity entity, EntityModel<?> model, AnimationState state) {
        if (LOCKED_VANILLA.remove(entity.getUUID())) {
            EMFAnimationApi.unlockEntityToVanillaModel(EMFAnimationApi.emfEntityOf(entity));
        }

        Set<BridgeBodyPart> activeParts = state.activeParts();
        Collection<ModelPart> modelParts = getModelPartsToPause(model, activeParts);
        if (modelParts.isEmpty()) {
            clearActiveSignature(entity);
            PART_PAUSED.remove(entity.getUUID());
            logDiagnosticState(entity, "unsupported_model:" + model.getClass().getName() + ":" + activeParts);
            logUnsupportedModelOnce(model);
            return;
        }

        boolean paused = EMFAnimationApi.pauseCustomAnimationsForThesePartsOfEntity(
                EMFAnimationApi.emfEntityOf(entity),
                modelParts.toArray(ModelPart[]::new));
        if (paused) {
            PART_PAUSED.add(entity.getUUID());
            clearDiagnosticState(entity);
            logActiveSummary(entity, state, "paused", model);
        } else {
            PART_PAUSED.remove(entity.getUUID());
            clearActiveSignature(entity);
            logDiagnosticState(entity, "pause_failed:" + model.getClass().getName() + ":" + activeParts + ":" + modelParts.size());
        }
    }

    private static boolean isWithinCooldown(UUID entityId, long currentTick) {
        int cooldown = BridgeConfig.getEmfPauseCooldownTicks();
        if (cooldown == 0) {
            return false;
        }
        Long last = LAST_REAL_EMOTE_TICK.get(entityId);
        return last != null && currentTick - last <= cooldown;
    }

    private static boolean isRealCameraCompatEligible(LivingEntity entity) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.options == null || minecraft.player == null) {
            return false;
        }

        return RealCameraCompatPolicy.shouldUseLocalFirstPersonFix(
                Compatibility.isRealCameraAvailable(),
                BridgeConfig.isRealCameraFirstPersonFixEnabled(),
                minecraft.player == entity,
                minecraft.options.getCameraType().isFirstPerson()
        );
    }

    private static boolean willEmfRenderVanillaRoot(LivingEntity entity) {
        try {
            Class<?> emfClass = Class.forName("traben.entity_model_features.EMF");
            Method configMethod = emfClass.getMethod("config");
            Object handler = configMethod.invoke(null);
            if (handler == null) {
                return true;
            }

            Method getConfigMethod = handler.getClass().getMethod("getConfig");
            Object config = getConfigMethod.invoke(handler);
            if (config == null) {
                return true;
            }

            Object emfEntity = EMFAnimationApi.emfEntityOf(entity);
            if (emfEntity == null) {
                return true;
            }

            Method getModeMethod = config.getClass().getMethod("getVanillaHologramModeFor", emfEntity.getClass());
            Object mode = getModeMethod.invoke(config, emfEntity);
            if (mode == null) {
                return true;
            }

            Field offField = mode.getClass().getField("OFF");
            Object off = offField.get(null);
            return mode != off;
        } catch (Throwable ignored) {
            return true;
        }
    }

    private static void clearAllState(LivingEntity entity) {
        UUID entityId = entity.getUUID();
        clearEmfCompatState(entity);
        LAST_REAL_EMOTE_TICK.remove(entityId);
        REAL_CAMERA_COMPAT_STATE.clearTracking(entityId);
        RenderSkipState.clear();
        clearActiveSignature(entity);
        clearDiagnosticState(entity);
        clearCompatDecision(entity);
    }

    private static void clearEmfCompatState(LivingEntity entity) {
        UUID entityId = entity.getUUID();
        if (PART_PAUSED.remove(entityId)) {
            EMFAnimationApi.resumeAllCustomAnimationsForEntity(EMFAnimationApi.emfEntityOf(entity));
        }
        if (LOCKED_VANILLA.remove(entityId)) {
            EMFAnimationApi.unlockEntityToVanillaModel(EMFAnimationApi.emfEntityOf(entity));
        }
        clearActiveSignature(entity);
    }

    private static Collection<ModelPart> getModelPartsToPause(EntityModel<?> model, Set<BridgeBodyPart> activeParts) {
        if (!(model instanceof IEMFModel emfModel) || !emfModel.emf$isEMFModel()) {
            return Set.of();
        }

        EMFModelPartRoot emfRoot = emfModel.emf$getEMFRootModel();
        if (emfRoot == null) {
            return Set.of();
        }

        Set<ModelPart> partsToPause = new HashSet<>();
        if (model instanceof PlayerModel<?>) {
            addHeadParts(partsToPause, activeParts, childOrNull(emfRoot, "head"), childOrNull(emfRoot, "hat"));
            addTorsoPart(partsToPause, activeParts, childOrNull(emfRoot, "body"), childOrNull(emfRoot, "jacket"));
            addArmParts(partsToPause, activeParts, childOrNull(emfRoot, "left_arm"), childOrNull(emfRoot, "left_sleeve"), childOrNull(emfRoot, "right_arm"), childOrNull(emfRoot, "right_sleeve"));
            addLegParts(partsToPause, activeParts, childOrNull(emfRoot, "left_leg"), childOrNull(emfRoot, "left_pants"), childOrNull(emfRoot, "right_leg"), childOrNull(emfRoot, "right_pants"));
            return partsToPause;
        }
        if (model instanceof HumanoidModel<?>) {
            addHeadParts(partsToPause, activeParts, childOrNull(emfRoot, "head"), childOrNull(emfRoot, "hat"));
            addTorsoPart(partsToPause, activeParts, childOrNull(emfRoot, "body"), null);
            addArmParts(partsToPause, activeParts, childOrNull(emfRoot, "left_arm"), null, childOrNull(emfRoot, "right_arm"), null);
            addLegParts(partsToPause, activeParts, childOrNull(emfRoot, "left_leg"), null, childOrNull(emfRoot, "right_leg"), null);
            return partsToPause;
        }
        if (model instanceof IllagerModel<?>) {
            addIllagerParts(partsToPause, activeParts, emfRoot);
        }
        return partsToPause;
    }

    private static void addIllagerParts(Set<ModelPart> partsToPause, Set<BridgeBodyPart> activeParts, ModelPart root) {
        addHeadParts(partsToPause, activeParts, childOrNull(root, "head"), childOrNull(root, "hat"));
        addTorsoPart(partsToPause, activeParts, childOrNull(root, "body"), null);

        if (activeParts.contains(BridgeBodyPart.LEFT_ARM) || activeParts.contains(BridgeBodyPart.RIGHT_ARM)) {
            addIfPresent(partsToPause, childOrNull(root, "arms"));
            if (activeParts.contains(BridgeBodyPart.LEFT_ARM)) {
                addIfPresent(partsToPause, childOrNull(root, "left_arm"));
            }
            if (activeParts.contains(BridgeBodyPart.RIGHT_ARM)) {
                addIfPresent(partsToPause, childOrNull(root, "right_arm"));
            }
        }

        if (activeParts.contains(BridgeBodyPart.LEFT_LEG)) {
            addIfPresent(partsToPause, childOrNull(root, "left_leg"));
        }
        if (activeParts.contains(BridgeBodyPart.RIGHT_LEG)) {
            addIfPresent(partsToPause, childOrNull(root, "right_leg"));
        }
    }

    private static void addHeadParts(Set<ModelPart> partsToPause, Set<BridgeBodyPart> activeParts, @Nullable ModelPart head, @Nullable ModelPart hat) {
        if (!activeParts.contains(BridgeBodyPart.HEAD)) {
            return;
        }
        addIfPresent(partsToPause, head);
        addIfPresent(partsToPause, hat);
    }

    private static void addTorsoPart(Set<ModelPart> partsToPause, Set<BridgeBodyPart> activeParts, @Nullable ModelPart torso, @Nullable ModelPart overlay) {
        if (!activeParts.contains(BridgeBodyPart.TORSO)) {
            return;
        }
        addIfPresent(partsToPause, torso);
        addIfPresent(partsToPause, overlay);
    }

    private static void addArmParts(Set<ModelPart> partsToPause, Set<BridgeBodyPart> activeParts,
                                    @Nullable ModelPart leftArm, @Nullable ModelPart leftOverlay,
                                    @Nullable ModelPart rightArm, @Nullable ModelPart rightOverlay) {
        if (activeParts.contains(BridgeBodyPart.LEFT_ARM)) {
            addIfPresent(partsToPause, leftArm);
            addIfPresent(partsToPause, leftOverlay);
        }
        if (activeParts.contains(BridgeBodyPart.RIGHT_ARM)) {
            addIfPresent(partsToPause, rightArm);
            addIfPresent(partsToPause, rightOverlay);
        }
    }

    private static void addLegParts(Set<ModelPart> partsToPause, Set<BridgeBodyPart> activeParts,
                                    @Nullable ModelPart leftLeg, @Nullable ModelPart leftOverlay,
                                    @Nullable ModelPart rightLeg, @Nullable ModelPart rightOverlay) {
        if (activeParts.contains(BridgeBodyPart.LEFT_LEG)) {
            addIfPresent(partsToPause, leftLeg);
            addIfPresent(partsToPause, leftOverlay);
        }
        if (activeParts.contains(BridgeBodyPart.RIGHT_LEG)) {
            addIfPresent(partsToPause, rightLeg);
            addIfPresent(partsToPause, rightOverlay);
        }
    }

    private static void addIfPresent(Set<ModelPart> partsToPause, @Nullable ModelPart part) {
        ModelPartTreeHelper.addPartTree(partsToPause, part);
    }

    @Nullable
    private static ModelPart childOrNull(ModelPart root, String childName) {
        try {
            return root.getChild(childName);
        } catch (NoSuchElementException ignored) {
            return null;
        }
    }

    private static void logUnsupportedModelOnce(EntityModel<?> model) {
        if (!BridgeConfig.isDebugLoggingEnabled()) {
            return;
        }

        String modelName = model.getClass().getName();
        if (UNSUPPORTED_MODELS_LOGGED.add(modelName)) {
            Constants.LOG.info("{}: no compatible model-part mapping exists for {}", Constants.MOD_NAME, modelName);
        }
    }

    private static void logDiagnosticState(LivingEntity entity, String state) {
        if (!BridgeConfig.isDebugLoggingEnabled()) {
            return;
        }

        UUID entityId = entity.getUUID();
        String previous = DIAGNOSTIC_STATES.put(entityId, state);
        if (state.equals(previous)) {
            return;
        }

        Constants.LOG.info("{} [{}] -> {}", Constants.MOD_NAME, entity.getName().getString(), state);
    }

    private static void logActiveSummary(LivingEntity entity, AnimationState state, String mode, EntityModel<?> model) {
        if (!BridgeConfig.isDebugLoggingEnabled()) {
            return;
        }
        String signature = mode + ":" + model.getClass().getName() + ":" + state.activeParts();
        UUID entityId = entity.getUUID();
        String previous = LAST_ACTIVE_SIGNATURES.put(entityId, signature);
        if (signature.equals(previous)) {
            return;
        }
        Constants.LOG.info("{} [{}] -> {}", Constants.MOD_NAME, entity.getName().getString(), signature);
    }

    private static void clearActiveSignature(LivingEntity entity) {
        LAST_ACTIVE_SIGNATURES.remove(entity.getUUID());
    }

    private static void clearDiagnosticState(LivingEntity entity) {
        if (!BridgeConfig.isDebugLoggingEnabled()) {
            return;
        }

        DIAGNOSTIC_STATES.remove(entity.getUUID());
    }

    private static void logCompatDecision(LivingEntity entity, String decision) {
        if (!BridgeConfig.isDebugLoggingEnabled()) {
            return;
        }

        UUID entityId = entity.getUUID();
        String previous = COMPAT_DECISIONS.put(entityId, decision);
        if (decision.equals(previous)) {
            return;
        }

        Constants.LOG.info("{} [{}] -> {}", Constants.MOD_NAME, entity.getName().getString(), decision);
    }

    private static void clearCompatDecision(LivingEntity entity) {
        if (!BridgeConfig.isDebugLoggingEnabled()) {
            return;
        }

        COMPAT_DECISIONS.remove(entity.getUUID());
    }
}
