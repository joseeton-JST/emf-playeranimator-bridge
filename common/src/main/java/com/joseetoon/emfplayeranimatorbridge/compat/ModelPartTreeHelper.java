package com.joseetoon.emfplayeranimatorbridge.compat;

import net.minecraft.client.model.geom.ModelPart;

import javax.annotation.Nullable;
import java.util.Set;

final class ModelPartTreeHelper {
    private ModelPartTreeHelper() {
    }

    static void addPartTree(Set<ModelPart> partsToPause, @Nullable ModelPart rootPart) {
        if (rootPart == null) {
            return;
        }

        rootPart.getAllParts().forEach(partsToPause::add);
    }
}
