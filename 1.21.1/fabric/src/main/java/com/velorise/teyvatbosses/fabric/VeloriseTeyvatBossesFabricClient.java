package com.velorise.teyvatbosses.fabric;

import com.velorise.teyvatbosses.client.AnemoHypostasisRenderer;
import com.velorise.teyvatbosses.client.AstralionRenderer;
import com.velorise.teyvatbosses.client.AnemoMissileRenderer;
import com.velorise.teyvatbosses.client.AnemoTornadoRenderer;
import com.velorise.teyvatbosses.client.AnemoWindOrbRenderer;
import com.velorise.teyvatbosses.client.BossRespawnAnchorRenderer;
import com.velorise.teyvatbosses.client.CryoIceSpikeRenderer;
import com.velorise.teyvatbosses.client.CryoIcicleRenderer;
import com.velorise.teyvatbosses.client.CryoRegisvineRenderer;
import com.velorise.teyvatbosses.client.ElementalStarRenderer;
import com.velorise.teyvatbosses.client.RuinGuardRenderer;
import com.velorise.teyvatbosses.client.RuinMissileRenderer;
import com.velorise.teyvatbosses.client.SkySpearRenderer;
import com.velorise.teyvatbosses.client.TheOpenEyedRenderer;
import com.velorise.teyvatbosses.client.TrounceBlossomRenderer;
import com.velorise.teyvatbosses.client.WindCurrentRenderer;
import com.velorise.teyvatbosses.client.model.AnemoHypostasisModel;
import com.velorise.teyvatbosses.client.model.AstralionModel;
import com.velorise.teyvatbosses.client.model.CryoRegisvineModel;
import com.velorise.teyvatbosses.client.model.RuinGuardModel;
import com.velorise.teyvatbosses.client.model.TheOpenEyedModel;
import com.velorise.teyvatbosses.client.model.TrounceBlossomModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class VeloriseTeyvatBossesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(CryoRegisvineModel.LAYER_LOCATION, CryoRegisvineModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(TheOpenEyedModel.LAYER_LOCATION, TheOpenEyedModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(AnemoHypostasisModel.LAYER_LOCATION, AnemoHypostasisModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(TrounceBlossomModel.LAYER_LOCATION, TrounceBlossomModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(RuinGuardModel.LAYER_LOCATION, RuinGuardModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(AstralionModel.LAYER_LOCATION, AstralionModel::createBodyLayer);

        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.CRYO_REGISVINE, CryoRegisvineRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.CRYO_ICICLE, CryoIcicleRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.CRYO_ICE_SPIKE, CryoIceSpikeRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.THE_OPEN_EYED, TheOpenEyedRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.ELEMENTAL_STAR, ElementalStarRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.SKY_SPEAR, SkySpearRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.BOSS_RESPAWN_ANCHOR, BossRespawnAnchorRenderer::new);

        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.ANEMO_HYPOSTASIS, AnemoHypostasisRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.ANEMO_WIND_ORB, AnemoWindOrbRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.ANEMO_TORNADO, AnemoTornadoRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.ANEMO_MISSILE, AnemoMissileRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.WIND_CURRENT, WindCurrentRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.TROUNCE_BLOSSOM, TrounceBlossomRenderer::new);

        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.RUIN_GUARD, RuinGuardRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.RUIN_MISSILE, RuinMissileRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.ASTRALION, AstralionRenderer::new);
    }
}
