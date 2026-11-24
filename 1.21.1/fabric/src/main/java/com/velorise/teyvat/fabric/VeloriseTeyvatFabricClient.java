package com.velorise.teyvat.fabric;

import com.velorise.teyvat.fabric.network.FabricCelestiaNetwork;
import net.fabricmc.api.ClientModInitializer;

public final class VeloriseTeyvatFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricCelestiaNetwork.registerClient();
    }
}
