/**
 * @author ArcAnc
 * Created at: 06.06.2024
 * Copyright (c) 2023
 * <p>
 * This code is licensed under "Ancient's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.bettercooldown.mixin;

import com.arcanc.bettercooldown.timer.TimersHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ItemUseHook
{

    @Inject(method = "interactItem", at = @At(value = "RETURN", ordinal = 1))
    public void itemUseHook(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir)
    {
        TimersHandler.itemUse(player, hand);
    }

    @Inject(method = "interactBlock", at = @At(value = "RETURN", ordinal = 1))
    public void interactBlockHook(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir)
    {
        TimersHandler.itemUse(player, hand);
    }
}
