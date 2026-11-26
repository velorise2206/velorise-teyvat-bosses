package com.velorise.teyvatbosses.neoforge;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.client.RuinMechCameraController;
import com.velorise.teyvatbosses.client.RuinMechInputController;
import com.velorise.teyvatbosses.network.RuinMechSkillPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = TeyvatBossesConstants.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class VeloriseTeyvatBossesNeoForgeClientEvents {

    private VeloriseTeyvatBossesNeoForgeClientEvents() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        RuinMechCameraController.clientTick();
        RuinMechInputController.tickAndDispatch(skillId -> PacketDistributor.sendToServer(new RuinMechSkillPayload(skillId)));
    }

    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (RuinMechCameraController.getPilotedMech() != null) {
            float[] off = RuinMechCameraController.computeOffsets((float) event.getPartialTick());
            event.setYaw(event.getYaw() + off[0]);
            event.setPitch(event.getPitch() + off[1]);
            event.setRoll(event.getRoll() + off[2]);
        }
    }

    @SubscribeEvent
    public static void onCameraDistance(CalculateDetachedCameraDistanceEvent event) {
        if (RuinMechCameraController.getPilotedMech() != null) {
            event.setDistance(RuinMechCameraController.THIRD_PERSON_ZOOM);
        }
    }
}
