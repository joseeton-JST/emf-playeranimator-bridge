package com.joseetoon.emfplayeranimatorbridge.compat;

import com.joseetoon.emfplayeranimatorbridge.anim.BridgeBodyPart;
import net.minecraft.client.model.geom.ModelPart;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;

final class EmfHumanoidBlendTargets {
    private final Map<ModelPart, BridgeBodyPart> partTargets;

    EmfHumanoidBlendTargets(Map<ModelPart, BridgeBodyPart> partTargets) {
        this.partTargets = Map.copyOf(partTargets);
    }

    static EmfHumanoidBlendTargets createIdentityBacked(Map<ModelPart, BridgeBodyPart> partTargets) {
        IdentityHashMap<ModelPart, BridgeBodyPart> copy = new IdentityHashMap<>();
        copy.putAll(partTargets);
        return new EmfHumanoidBlendTargets(copy);
    }

    @Nullable
    BridgeBodyPart logicalPartOf(ModelPart part) {
        return this.partTargets.get(part);
    }
}
