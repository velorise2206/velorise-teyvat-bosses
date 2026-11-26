package com.velorise.teyvatbosses.neoforge;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.AnemoHypostasisRenderer;
import com.velorise.teyvatbosses.client.AnemoMissileRenderer;
import com.velorise.teyvatbosses.client.AnemoTornadoRenderer;
import com.velorise.teyvatbosses.client.AnemoWindOrbRenderer;
import com.velorise.teyvatbosses.client.AstralionRenderer;
import com.velorise.teyvatbosses.client.BossRespawnAnchorRenderer;
import com.velorise.teyvatbosses.client.BossTrophyRenderer;
import com.velorise.teyvatbosses.client.CryoIceSpikeRenderer;
import com.velorise.teyvatbosses.client.CryoIcicleRenderer;
import com.velorise.teyvatbosses.client.CryoRegisvineRenderer;
import com.velorise.teyvatbosses.client.ElementalStarRenderer;
import com.velorise.teyvatbosses.client.RuinGuardRenderer;
import com.velorise.teyvatbosses.client.RuinMechCockpitHud;
import com.velorise.teyvatbosses.client.RuinMechRenderer;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
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
        event.registerLayerDefinition(AnemoHypostasisModel.LAYER_LOCATION, AnemoHypostasisModel::createBodyLayer);
        event.registerLayerDefinition(TrounceBlossomModel.LAYER_LOCATION, TrounceBlossomModel::createBodyLayer);
        event.registerLayerDefinition(RuinGuardModel.LAYER_LOCATION, RuinGuardModel::createBodyLayer);
        event.registerLayerDefinition(AstralionModel.LAYER_LOCATION, AstralionModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.CRYO_REGISVINE.get(), CryoRegisvineRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.CRYO_ICICLE.get(), CryoIcicleRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.CRYO_ICE_SPIKE.get(), CryoIceSpikeRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.THE_OPEN_EYED.get(), TheOpenEyedRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.ELEMENTAL_STAR.get(), ElementalStarRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.SKY_SPEAR.get(), SkySpearRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.BOSS_RESPAWN_ANCHOR.get(), BossRespawnAnchorRenderer::new);

        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.ANEMO_HYPOSTASIS.get(), AnemoHypostasisRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.ANEMO_WIND_ORB.get(), AnemoWindOrbRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.ANEMO_TORNADO.get(), AnemoTornadoRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.ANEMO_MISSILE.get(), AnemoMissileRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.WIND_CURRENT.get(), WindCurrentRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.TROUNCE_BLOSSOM.get(), TrounceBlossomRenderer::new);

        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.RUIN_GUARD.get(), RuinGuardRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.RUIN_MISSILE.get(), RuinMissileRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.ASTRALION.get(), AstralionRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.RUIN_MECH.get(), RuinMechRenderer::new);

        event.registerBlockEntityRenderer(VeloriseTeyvatBossesNeoForge.BOSS_TROPHY_BE.get(), BossTrophyRenderer::new);
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(TeyvatBossesConstants.id("ruin_mech_hud"), (gui, delta) ->
                RuinMechCockpitHud.render(gui, delta.getGameTimeDeltaPartialTick(false)));
    }
}
