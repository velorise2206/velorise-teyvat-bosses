package com.velorise.teyvatbosses.neoforge;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.CryoIcicleRenderer;
import com.velorise.teyvatbosses.client.CryoRegisvineRenderer;
import com.velorise.teyvatbosses.client.ElementalStarRenderer;
import com.velorise.teyvatbosses.client.TheOpenEyedRenderer;
import com.velorise.teyvatbosses.client.model.CryoRegisvineModel;
import com.velorise.teyvatbosses.client.model.TheOpenEyedModel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = TeyvatBossesConstants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class VeloriseTeyvatBossesNeoForgeClient {

    private VeloriseTeyvatBossesNeoForgeClient() {}

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CryoRegisvineModel.LAYER_LOCATION, CryoRegisvineModel::createBodyLayer);
        event.registerLayerDefinition(TheOpenEyedModel.LAYER_LOCATION, TheOpenEyedModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.CRYO_REGISVINE.get(), CryoRegisvineRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.CRYO_ICICLE.get(), CryoIcicleRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.THE_OPEN_EYED.get(), TheOpenEyedRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.ELEMENTAL_STAR.get(), ElementalStarRenderer::new);
    }
}
