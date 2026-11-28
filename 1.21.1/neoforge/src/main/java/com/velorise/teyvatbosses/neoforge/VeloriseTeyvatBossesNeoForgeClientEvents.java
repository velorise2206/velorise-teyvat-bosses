package com.velorise.teyvatbosses.neoforge;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.AnemoBootsClientController;
import com.velorise.teyvatbosses.client.RuinMechCameraController;
import com.velorise.teyvatbosses.client.RuinMechInputController;
import com.velorise.teyvatbosses.client.BossCameraShake;
import com.velorise.teyvatbosses.client.GeoDrillerClientController;
import com.velorise.teyvatbosses.client.GoldflameQucusaurClientController;
import com.velorise.teyvatbosses.client.RuinSubClientController;
import com.velorise.teyvatbosses.client.TeyvatBossBarRenderer;
import net.minecraft.client.Minecraft;
import com.velorise.teyvatbosses.network.RuinMechSkillPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = TeyvatBossesConstants.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class VeloriseTeyvatBossesNeoForgeClientEvents {

    private VeloriseTeyvatBossesNeoForgeClientEvents() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        RuinMechCameraController.clientTick();
        RuinMechInputController.tickAndDispatch(skillId -> PacketDistributor.sendToServer(new RuinMechSkillPayload(skillId)));
        RuinSubClientController.setPacketSender(PacketDistributor::sendToServer);
        RuinSubClientController.tick(Minecraft.getInstance());
        GeoDrillerClientController.setPacketSender(PacketDistributor::sendToServer);
        GeoDrillerClientController.tick(Minecraft.getInstance());
        GoldflameQucusaurClientController.setPacketSender(PacketDistributor::sendToServer);
        GoldflameQucusaurClientController.tick(Minecraft.getInstance());
        AnemoBootsClientController.setPacketSender(PacketDistributor::sendToServer);
        AnemoBootsClientController.tick(Minecraft.getInstance());
        BossCameraShake.clientTick();
    }

    @SubscribeEvent
    public static void onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
        // Prevent vanilla block placement / punching / hand swing while firing submarine torpedoes (LMB) or Sonar (RMB)
        if ((RuinSubClientController.isPilotingSubmersible() || GeoDrillerClientController.isPilotingDriller()) && (event.isAttack() || event.isUseItem())) {
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (RuinMechCameraController.getPilotedMech() != null) {
            float[] off = RuinMechCameraController.computeOffsets((float) event.getPartialTick());
            event.setYaw(event.getYaw() + off[0]);
            event.setPitch(event.getPitch() + off[1]);
            event.setRoll(event.getRoll() + off[2]);
        } else if (RuinSubClientController.isPilotingSubmersible()) {
            float[] off = RuinSubClientController.computeCameraOffsets((float) event.getPartialTick());
            event.setYaw(event.getYaw() + off[0]);
            event.setPitch(event.getPitch() + off[1]);
            event.setRoll(event.getRoll() + off[2]);
        } else if (GeoDrillerClientController.isPilotingDriller()) {
            float[] off = GeoDrillerClientController.computeCameraOffsets((float) event.getPartialTick());
            event.setYaw(event.getYaw() + off[0]);
            event.setPitch(event.getPitch() + off[1]);
            event.setRoll(event.getRoll() + off[2]);
        }
        if (BossCameraShake.isActive()) {
            float[] off = BossCameraShake.computeOffsets((float) event.getPartialTick());
            event.setYaw(event.getYaw() + off[0]);
            event.setPitch(event.getPitch() + off[1]);
            event.setRoll(event.getRoll() + off[2]);
        }
    }

    @SubscribeEvent
    public static void onCameraDistance(CalculateDetachedCameraDistanceEvent event) {
        if (RuinMechCameraController.getPilotedMech() != null) {
            event.setDistance(RuinMechCameraController.THIRD_PERSON_ZOOM);
        } else if (RuinSubClientController.isPilotingSubmersible()) {
            event.setDistance(RuinSubClientController.THIRD_PERSON_ZOOM);
        } else if (GeoDrillerClientController.isPilotingDriller()) {
            event.setDistance(GeoDrillerClientController.THIRD_PERSON_ZOOM);
        }
    }

    @SubscribeEvent
    public static void onBossBarRender(CustomizeGuiOverlayEvent.BossEventProgress event) {
        int customInc = TeyvatBossBarRenderer.tryRender(
                event.getGuiGraphics(),
                event.getBossEvent(),
                event.getX(),
                event.getY()
        );
        if (customInc > 0) {
            event.setCanceled(true);
            event.setIncrement(customInc);
        }
    }
}
