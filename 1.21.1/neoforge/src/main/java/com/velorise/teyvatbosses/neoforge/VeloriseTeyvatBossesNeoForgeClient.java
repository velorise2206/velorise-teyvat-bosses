package com.velorise.teyvatbosses.neoforge;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.AnemoCompanionRenderer;
import com.velorise.teyvatbosses.client.AnemoHypostasisRenderer;
import com.velorise.teyvatbosses.client.AnemoMissileRenderer;
import com.velorise.teyvatbosses.client.AnemoTornadoRenderer;
import com.velorise.teyvatbosses.client.AnemoWindOrbRenderer;
import com.velorise.teyvatbosses.client.BossCameraShake;
import com.velorise.teyvatbosses.client.BossRespawnAnchorRenderer;
import com.velorise.teyvatbosses.entity.BossClientBridge;
import com.velorise.teyvatbosses.client.BossTrophyRenderer;
import com.velorise.teyvatbosses.client.CryoIceSpikeRenderer;
import com.velorise.teyvatbosses.client.CryoIcicleRenderer;
import com.velorise.teyvatbosses.client.CryoRegisvineRenderer;
import com.velorise.teyvatbosses.client.CryoTurretRenderer;
import com.velorise.teyvatbosses.client.GeoDrillerClientController;
import com.velorise.teyvatbosses.client.FlamewindFeatherRenderer;
import com.velorise.teyvatbosses.client.GeoDrillerRenderer;
import com.velorise.teyvatbosses.client.GoldflameFireballRenderer;
import com.velorise.teyvatbosses.client.GoldflameQucusaurClientController;
import com.velorise.teyvatbosses.client.GoldflameQucusaurEggRenderer;
import com.velorise.teyvatbosses.client.GoldflameQucusaurMountRenderer;
import com.velorise.teyvatbosses.client.GoldflameQucusaurRenderer;
import com.velorise.teyvatbosses.client.model.GoldflameQucusaurModel;
import com.velorise.teyvatbosses.client.PrimoGeovishapPartRenderer;
import com.velorise.teyvatbosses.client.PrimoGeovishapRenderer;
import com.velorise.teyvatbosses.client.PrimoInfusedStoneRenderer;
import com.velorise.teyvatbosses.client.RuinGuardRenderer;
import com.velorise.teyvatbosses.client.RuinMechCockpitHud;
import com.velorise.teyvatbosses.client.RuinMechRenderer;
import com.velorise.teyvatbosses.client.RuinMissileRenderer;
import com.velorise.teyvatbosses.client.RuinNautilusRenderer;
import com.velorise.teyvatbosses.client.RuinSubClientController;
import com.velorise.teyvatbosses.client.RuinSubmersibleRenderer;
import com.velorise.teyvatbosses.client.RuinTorpedoRenderer;
import com.velorise.teyvatbosses.client.TrounceBlossomRenderer;
import com.velorise.teyvatbosses.client.WindCurrentRenderer;
import com.velorise.teyvatbosses.client.model.AnemoHypostasisModel;
import com.velorise.teyvatbosses.client.model.BedrockAnimationLoader;
import com.velorise.teyvatbosses.client.model.CryoRegisvineModel;
import com.velorise.teyvatbosses.client.model.DepthMineModel;
import com.velorise.teyvatbosses.client.model.GeoDrillerModel;
import com.velorise.teyvatbosses.client.model.PrimoGeovishapModel;
import com.velorise.teyvatbosses.client.model.PrimoInfusedStoneModel;
import com.velorise.teyvatbosses.client.model.RuinGuardModel;
import com.velorise.teyvatbosses.client.model.RuinNautilusModel;
import com.velorise.teyvatbosses.client.model.RuinTorpedoModel;
import com.velorise.teyvatbosses.client.model.TrounceBlossomModel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import com.velorise.teyvatbosses.item.TeyvatBossLocatorItem;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = TeyvatBossesConstants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class VeloriseTeyvatBossesNeoForgeClient {

    private VeloriseTeyvatBossesNeoForgeClient() {}

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CryoRegisvineModel.LAYER_LOCATION, CryoRegisvineModel::createBodyLayer);
        event.registerLayerDefinition(AnemoHypostasisModel.LAYER_LOCATION, AnemoHypostasisModel::createBodyLayer);
        event.registerLayerDefinition(TrounceBlossomModel.LAYER_LOCATION, TrounceBlossomModel::createBodyLayer);
        event.registerLayerDefinition(RuinGuardModel.LAYER_LOCATION, RuinGuardModel::createBodyLayer);
        event.registerLayerDefinition(RuinNautilusModel.LAYER_LOCATION, RuinNautilusModel::createBodyLayer);
        event.registerLayerDefinition(RuinTorpedoModel.LAYER_LOCATION, RuinTorpedoModel::createBodyLayer);
        event.registerLayerDefinition(DepthMineModel.LAYER_LOCATION, DepthMineModel::createBodyLayer);
        event.registerLayerDefinition(PrimoGeovishapModel.LAYER_LOCATION, PrimoGeovishapModel::createBodyLayer);
        event.registerLayerDefinition(PrimoGeovishapModel.LAYER_LOCATION_EMISSIVE, PrimoGeovishapModel::createEmissiveLayer);
        event.registerLayerDefinition(GeoDrillerModel.LAYER_LOCATION, GeoDrillerModel::createBodyLayer);
        event.registerLayerDefinition(PrimoInfusedStoneModel.LAYER_LOCATION, PrimoInfusedStoneModel::createBodyLayer);
        event.registerLayerDefinition(GoldflameQucusaurModel.LAYER_LOCATION, GoldflameQucusaurModel::createBodyLayer);
        event.registerLayerDefinition(GoldflameQucusaurModel.LAYER_LOCATION_EMISSIVE, GoldflameQucusaurModel::createEmissiveLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.CRYO_REGISVINE.get(), CryoRegisvineRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.CRYO_ICICLE.get(), CryoIcicleRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.CRYO_ICE_SPIKE.get(), CryoIceSpikeRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.BOSS_RESPAWN_ANCHOR.get(), BossRespawnAnchorRenderer::new);

        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.ANEMO_HYPOSTASIS.get(), AnemoHypostasisRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.ANEMO_WIND_ORB.get(), AnemoWindOrbRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.ANEMO_TORNADO.get(), AnemoTornadoRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.ANEMO_MISSILE.get(), AnemoMissileRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.WIND_CURRENT.get(), WindCurrentRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.TROUNCE_BLOSSOM.get(), TrounceBlossomRenderer::new);

        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.RUIN_GUARD.get(), RuinGuardRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.RUIN_MISSILE.get(), RuinMissileRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.RUIN_MECH.get(), RuinMechRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.CRYO_TURRET.get(), CryoTurretRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.RUIN_NAUTILUS.get(), RuinNautilusRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.RUIN_TORPEDO.get(), RuinTorpedoRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.RUIN_SUBMERSIBLE.get(), RuinSubmersibleRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.ANEMO_COMPANION.get(), AnemoCompanionRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.PRIMO_GEOVISHAP.get(), PrimoGeovishapRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.GEO_DRILLER.get(), GeoDrillerRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.PRIMO_INFUSED_STONE.get(), PrimoInfusedStoneRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.PRIMO_GEOVISHAP_PART.get(), PrimoGeovishapPartRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.GOLDFLAME_QUCUSAUR.get(), GoldflameQucusaurRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.GOLDFLAME_QUCUSAUR_MOUNT.get(), GoldflameQucusaurMountRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.GOLDFLAME_QUCUSAUR_EGG.get(), GoldflameQucusaurEggRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.FLAMEWIND_FEATHER.get(), FlamewindFeatherRenderer::new);
        event.registerEntityRenderer(VeloriseTeyvatBossesNeoForge.GOLDFLAME_FIREBALL.get(), GoldflameFireballRenderer::new);

        event.registerBlockEntityRenderer(VeloriseTeyvatBossesNeoForge.BOSS_TROPHY_BE.get(), BossTrophyRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        BossClientBridge.setShakeHandler(BossCameraShake::triggerAt);
        event.enqueueWork(() -> {
            CompassItemPropertyFunction bossCompassProp = new CompassItemPropertyFunction(
                    (level, stack, entity) -> TeyvatBossLocatorItem.getLockedTarget(stack));
            for (Item compass : new Item[]{
                    VeloriseTeyvatBossesNeoForge.CRYO_REGISVINE_COMPASS.get(),
                    VeloriseTeyvatBossesNeoForge.ANEMO_HYPOSTASIS_COMPASS.get(),
                    VeloriseTeyvatBossesNeoForge.RUIN_GUARD_COMPASS.get(),
                    VeloriseTeyvatBossesNeoForge.RUIN_NAUTILUS_COMPASS.get(),
                    VeloriseTeyvatBossesNeoForge.PRIMO_GEOVISHAP_COMPASS.get(),
                    VeloriseTeyvatBossesNeoForge.GOLDFLAME_QUCUSAUR_COMPASS.get()
            }) {
                ItemProperties.register(compass, ResourceLocation.withDefaultNamespace("angle"), bossCompassProp);
            }
        });
    }

    @SubscribeEvent
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(BedrockAnimationLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(TeyvatBossesConstants.id("ruin_mech_hud"), (gui, delta) -> {
            RuinMechCockpitHud.render(gui, delta.getGameTimeDeltaPartialTick(false));
            RuinSubClientController.renderHud(gui);
            GeoDrillerClientController.renderHud(gui);
            GoldflameQucusaurClientController.renderHud(gui);
        });
    }
}
