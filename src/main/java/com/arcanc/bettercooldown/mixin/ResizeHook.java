package com.arcanc.bettercooldown.mixin;

import com.arcanc.bettercooldown.gui.RenderHelper;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class ResizeHook {
	@Inject(at = @At("TAIL"), method = "onResolutionChanged")
	private void init(CallbackInfo info)
	{
		RenderHelper.resize((MinecraftClient) ((Object)this));
	}
}