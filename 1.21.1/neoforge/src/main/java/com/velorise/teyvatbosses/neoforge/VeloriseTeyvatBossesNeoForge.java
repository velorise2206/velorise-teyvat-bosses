package com.velorise.teyvatbosses.neoforge;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(TeyvatBossesConstants.MOD_ID)
public class VeloriseTeyvatBossesNeoForge {
    public VeloriseTeyvatBossesNeoForge(IEventBus modEventBus) {
        TeyvatBossesConstants.LOGGER.info("Initializing {} (NeoForge 1.21.1)", TeyvatBossesConstants.MOD_NAME);
    }
}
