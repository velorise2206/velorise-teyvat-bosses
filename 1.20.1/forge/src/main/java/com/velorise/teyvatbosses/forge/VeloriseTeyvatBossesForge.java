package com.velorise.teyvatbosses.forge;

import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(VeloriseTeyvatBossesForge.MOD_ID)
public class VeloriseTeyvatBossesForge {
    public static final String MOD_ID = "velorise_teyvat_bosses";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public VeloriseTeyvatBossesForge() {
        LOGGER.info("Initializing Velorise: Teyvat Bosses (Forge 1.20.1)");
    }
}
