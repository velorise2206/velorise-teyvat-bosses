package com.velorise.teyvatbosses;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TeyvatBossesConstants {
    public static final String MOD_ID = "velorise_teyvat_bosses";
    public static final String MOD_NAME = "Velorise: Teyvat Bosses";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private TeyvatBossesConstants() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
