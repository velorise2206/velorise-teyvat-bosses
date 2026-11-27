package com.velorise.teyvatbosses.fabric.mixin;

import com.velorise.teyvatbosses.client.TeyvatBossBarRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

/**
 * Replaces vanilla boss bar rendering for the 5 Teyvat Bosses on Fabric
 * while preserving vanilla rendering for all other boss bars.
 */
@Mixin(BossHealthOverlay.class)
public abstract class BossHealthOverlayMixin {

    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final Map<UUID, LerpingBossEvent> events;

    @Shadow protected abstract void drawBar(GuiGraphics guiGraphics, int x, int y, BossEvent bossEvent);

    @Inject(require = 0, method = "render(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At("HEAD"), cancellable = true)
    private void velorise$renderTeyvatBossBars(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (this.events.isEmpty()) return;

        boolean hasTeyvatBoss = false;
        for (LerpingBossEvent ev : this.events.values()) {
            if (TeyvatBossBarRenderer.isTeyvatBoss(ev)) {
                hasTeyvatBoss = true;
                break;
            }
        }
        if (!hasTeyvatBoss) return;

        this.minecraft.getProfiler().push("bossHealth");
        int width = guiGraphics.guiWidth();
        int y = 12;
        int maxY = guiGraphics.guiHeight() / 3;

        for (LerpingBossEvent event : this.events.values()) {
            int x = width / 2 - 91;
            int customInc = TeyvatBossBarRenderer.tryRender(guiGraphics, event, x, y);
            if (customInc > 0) {
                y += customInc;
            } else {
                this.drawBar(guiGraphics, x, y, event);
                Component name = event.getName();
                int textW = this.minecraft.font.width(name);
                guiGraphics.drawString(this.minecraft.font, name, width / 2 - textW / 2, y - 9, 16777215);
                y += 10 + this.minecraft.font.lineHeight;
            }
            if (y >= maxY) {
                break;
            }
        }

        this.minecraft.getProfiler().pop();
        ci.cancel();
    }
}
