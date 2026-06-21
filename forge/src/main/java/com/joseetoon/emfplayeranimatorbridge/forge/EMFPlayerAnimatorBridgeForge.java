package com.joseetoon.emfplayeranimatorbridge.forge;

import com.joseetoon.emfplayeranimatorbridge.Constants;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public final class EMFPlayerAnimatorBridgeForge {
    public EMFPlayerAnimatorBridgeForge() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ForgeClientConfig.SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigLoading);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigReloading);
        ForgeClientConfig.apply();
    }

    private void onConfigLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getModId().equals(Constants.MOD_ID)) {
            ForgeClientConfig.apply();
        }
    }

    private void onConfigReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getModId().equals(Constants.MOD_ID)) {
            ForgeClientConfig.apply();
        }
    }
}
