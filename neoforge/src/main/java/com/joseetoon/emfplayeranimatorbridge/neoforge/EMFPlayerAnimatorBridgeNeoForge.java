package com.joseetoon.emfplayeranimatorbridge.neoforge;

import com.joseetoon.emfplayeranimatorbridge.Constants;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;

@Mod(Constants.MOD_ID)
public final class EMFPlayerAnimatorBridgeNeoForge {
    public EMFPlayerAnimatorBridgeNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, NeoForgeClientConfig.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener(this::onConfigLoading);
        modEventBus.addListener(this::onConfigReloading);
    }

    private void onConfigLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getModId().equals(Constants.MOD_ID)) {
            NeoForgeClientConfig.apply();
        }
    }

    private void onConfigReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getModId().equals(Constants.MOD_ID)) {
            NeoForgeClientConfig.apply();
        }
    }
}
