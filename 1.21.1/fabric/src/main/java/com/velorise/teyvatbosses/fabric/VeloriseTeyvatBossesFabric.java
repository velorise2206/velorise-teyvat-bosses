package com.velorise.teyvatbosses.fabric;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import net.fabricmc.api.ModInitializer;

public class VeloriseTeyvatBossesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        TeyvatBossesConstants.LOGGER.info("Initializing {} (Fabric 1.21.1)", TeyvatBossesConstants.MOD_NAME);
    }
}
