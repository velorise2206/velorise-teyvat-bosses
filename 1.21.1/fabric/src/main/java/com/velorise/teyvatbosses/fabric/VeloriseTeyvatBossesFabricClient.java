package com.velorise.teyvatbosses.fabric;

import com.velorise.teyvatbosses.client.BossRespawnAnchorRenderer;
import com.velorise.teyvatbosses.client.CryoIceSpikeRenderer;
import com.velorise.teyvatbosses.client.CryoIcicleRenderer;
import com.velorise.teyvatbosses.client.CryoRegisvineRenderer;
import com.velorise.teyvatbosses.client.ElementalStarRenderer;
import com.velorise.teyvatbosses.client.SkySpearRenderer;
import com.velorise.teyvatbosses.client.TheOpenEyedRenderer;
import com.velorise.teyvatbosses.client.model.CryoRegisvineModel;
import com.velorise.teyvatbosses.client.model.TheOpenEyedModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class VeloriseTeyvatBossesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(CryoRegisvineModel.LAYER_LOCATION, CryoRegisvineModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(TheOpenEyedModel.LAYER_LOCATION, TheOpenEyedModel::createBodyLayer);

        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.CRYO_REGISVINE, CryoRegisvineRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.CRYO_ICICLE, CryoIcicleRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.CRYO_ICE_SPIKE, CryoIceSpikeRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.THE_OPEN_EYED, TheOpenEyedRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.ELEMENTAL_STAR, ElementalStarRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.SKY_SPEAR, SkySpearRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.BOSS_RESPAWN_ANCHOR, BossRespawnAnchorRenderer::new);
    }
}
