package com.joseetoon.emfplayeranimatorbridge.mixin.client;

import com.joseetoon.emfplayeranimatorbridge.api.HumanoidModelAccess;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(IllagerModel.class)
public abstract class IllagerModelAccessMixin implements HumanoidModelAccess {
    @Shadow private ModelPart root;
    @Shadow public ModelPart head;
    @Shadow public ModelPart hat;
    @Shadow public ModelPart leftLeg;
    @Shadow public ModelPart rightLeg;
    @Shadow public ModelPart rightArm;
    @Shadow public ModelPart leftArm;

    @Override
    public ModelPart emfbridge$getHead() {
        return this.head;
    }

    @Override
    public ModelPart emfbridge$getHat() {
        return this.hat;
    }

    @Override
    public ModelPart emfbridge$getBody() {
        return this.root.getChild("body");
    }

    @Override
    public ModelPart emfbridge$getLeftArm() {
        return this.leftArm;
    }

    @Override
    public ModelPart emfbridge$getRightArm() {
        return this.rightArm;
    }

    @Override
    public ModelPart emfbridge$getLeftLeg() {
        return this.leftLeg;
    }

    @Override
    public ModelPart emfbridge$getRightLeg() {
        return this.rightLeg;
    }
}
