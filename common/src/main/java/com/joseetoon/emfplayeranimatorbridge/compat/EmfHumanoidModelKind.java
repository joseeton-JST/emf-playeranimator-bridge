package com.joseetoon.emfplayeranimatorbridge.compat;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.PlayerModel;

import javax.annotation.Nullable;

enum EmfHumanoidModelKind {
    PLAYER,
    HUMANOID,
    ILLAGER;

    @Nullable
    static EmfHumanoidModelKind of(EntityModel<?> model) {
        if (model instanceof PlayerModel<?>) {
            return PLAYER;
        }
        if (model instanceof IllagerModel<?>) {
            return ILLAGER;
        }
        if (model instanceof HumanoidModel<?>) {
            return HUMANOID;
        }
        return null;
    }
}
