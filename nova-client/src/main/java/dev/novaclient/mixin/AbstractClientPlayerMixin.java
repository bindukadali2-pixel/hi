package dev.novaclient.mixin;

import dev.novaclient.config.NovaConfig;
import dev.novaclient.cosmetics.Capes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {
    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true, require = 0)
    private void novaclient$applyCape(CallbackInfoReturnable<PlayerSkin> cir) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || (Object) this != mc.player) return;
        ClientAsset.Texture cape = Capes.assetOf(NovaConfig.INSTANCE.cape);
        if (cape == null) return;
        PlayerSkin s = cir.getReturnValue();
        cir.setReturnValue(new PlayerSkin(s.body(), cape, cape, s.model(), s.secure()));
    }
}
