package com.velorise.teyvatbosses.fabric;

import com.velorise.teyvatbosses.client.AnemoCompanionRenderer;
import com.velorise.teyvatbosses.client.AnemoHypostasisRenderer;
import com.velorise.teyvatbosses.client.AnemoMissileRenderer;
import com.velorise.teyvatbosses.client.AnemoTornadoRenderer;
import com.velorise.teyvatbosses.client.AnemoWindOrbRenderer;
import com.velorise.teyvatbosses.client.BossRespawnAnchorRenderer;
import com.velorise.teyvatbosses.client.BossTrophyRenderer;
import com.velorise.teyvatbosses.client.CryoIceSpikeRenderer;
import com.velorise.teyvatbosses.client.CryoIcicleRenderer;
import com.velorise.teyvatbosses.client.CryoRegisvineRenderer;
import com.velorise.teyvatbosses.client.CryoTurretRenderer;
import com.velorise.teyvatbosses.client.GeoDrillerClientController;
import com.velorise.teyvatbosses.client.GeoDrillerRenderer;
import com.velorise.teyvatbosses.client.PrimoGeovishapRenderer;
import com.velorise.teyvatbosses.client.RuinGuardRenderer;
import com.velorise.teyvatbosses.client.RuinMechCameraController;
import com.velorise.teyvatbosses.client.RuinMechCockpitHud;
import com.velorise.teyvatbosses.client.RuinMechInputController;
import com.velorise.teyvatbosses.client.RuinMechRenderer;
import com.velorise.teyvatbosses.client.RuinMissileRenderer;
import com.velorise.teyvatbosses.client.RuinNautilusRenderer;
import com.velorise.teyvatbosses.client.RuinSubClientController;
import com.velorise.teyvatbosses.client.RuinSubmersibleRenderer;
import com.velorise.teyvatbosses.client.RuinTorpedoRenderer;
import com.velorise.teyvatbosses.client.TrounceBlossomRenderer;
import com.velorise.teyvatbosses.client.WindCurrentRenderer;
import com.velorise.teyvatbosses.client.model.AnemoHypostasisModel;
import com.velorise.teyvatbosses.client.model.CryoRegisvineModel;
import com.velorise.teyvatbosses.client.model.DepthMineModel;
import com.velorise.teyvatbosses.client.model.GeoDrillerModel;
import com.velorise.teyvatbosses.client.model.PrimoGeovishapModel;
import com.velorise.teyvatbosses.client.model.RuinGuardModel;
import com.velorise.teyvatbosses.client.model.RuinNautilusModel;
import com.velorise.teyvatbosses.client.model.RuinTorpedoModel;
import com.velorise.teyvatbosses.client.model.TrounceBlossomModel;
import com.velorise.teyvatbosses.network.RuinMechSkillPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class VeloriseTeyvatBossesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(CryoRegisvineModel.LAYER_LOCATION, CryoRegisvineModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(AnemoHypostasisModel.LAYER_LOCATION, AnemoHypostasisModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(TrounceBlossomModel.LAYER_LOCATION, TrounceBlossomModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(RuinGuardModel.LAYER_LOCATION, RuinGuardModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(RuinNautilusModel.LAYER_LOCATION, RuinNautilusModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(RuinTorpedoModel.LAYER_LOCATION, RuinTorpedoModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(DepthMineModel.LAYER_LOCATION, DepthMineModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(PrimoGeovishapModel.LAYER_LOCATION, PrimoGeovishapModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(GeoDrillerModel.LAYER_LOCATION, GeoDrillerModel::createBodyLayer);

        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.CRYO_REGISVINE, CryoRegisvineRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.CRYO_ICICLE, CryoIcicleRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.CRYO_ICE_SPIKE, CryoIceSpikeRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.BOSS_RESPAWN_ANCHOR, BossRespawnAnchorRenderer::new);

        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.ANEMO_HYPOSTASIS, AnemoHypostasisRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.ANEMO_WIND_ORB, AnemoWindOrbRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.ANEMO_TORNADO, AnemoTornadoRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.ANEMO_MISSILE, AnemoMissileRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.WIND_CURRENT, WindCurrentRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.TROUNCE_BLOSSOM, TrounceBlossomRenderer::new);

        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.RUIN_GUARD, RuinGuardRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.RUIN_MISSILE, RuinMissileRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.RUIN_MECH, RuinMechRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.CRYO_TURRET, CryoTurretRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.RUIN_NAUTILUS, RuinNautilusRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.RUIN_TORPEDO, RuinTorpedoRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.RUIN_SUBMERSIBLE, RuinSubmersibleRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.ANEMO_COMPANION, AnemoCompanionRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.PRIMO_GEOVISHAP, PrimoGeovishapRenderer::new);
        EntityRendererRegistry.register(VeloriseTeyvatBossesFabric.GEO_DRILLER, GeoDrillerRenderer::new);

        BlockEntityRenderers.register(VeloriseTeyvatBossesFabric.BOSS_TROPHY_BE, BossTrophyRenderer::new);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            RuinMechCameraController.clientTick();
            RuinMechInputController.tickAndDispatch(skillId -> ClientPlayNetworking.send(new RuinMechSkillPayload(skillId)));
            RuinSubClientController.setPacketSender(ClientPlayNetworking::send);
            RuinSubClientController.tick(client);
            GeoDrillerClientController.setPacketSender(ClientPlayNetworking::send);
            GeoDrillerClientController.tick(client);
        });
        HudRenderCallback.EVENT.register((gui, deltaTracker) -> {
            RuinMechCockpitHud.render(gui, deltaTracker.getGameTimeDeltaPartialTick(false));
            RuinSubClientController.renderHud(gui);
            GeoDrillerClientController.renderHud(gui);
        });
    }
}
