/**
 * @author ArcAnc
 * Created at: 25.05.2024
 * Copyright (c) 2023
 * <p>
 * This code is licensed under "Ancient's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.bettercooldown.timer;

import com.arcanc.bettercooldown.BetterCoolDown;
import com.arcanc.bettercooldown.config.ConfigHandler;
import com.google.gson.JsonArray;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimersHandler
{
    public static final Map<UUID, Timer> timers = new HashMap<>();

    public static void clientTick(MinecraftClient minecraftClient)
    {
        timers.forEach((uuid, timer) -> timer.tick());
    }

    public static void startRequiredTimers(ClientPlayNetworkHandler clientPlayNetworkHandler, PacketSender packetSender, MinecraftClient client)
    {
        BetterCoolDown.LOGGER.info("Starting required timers");
        timers.forEach((uuid, timer) ->
        {
            ServerInfo server = MinecraftClient.getInstance().getCurrentServerEntry();
            String address = "";
            if (server != null)
                address = server.address;
            if (timer.getServerInfo().equals(address))
                timer.setPaused(true);
        });
        BetterCoolDown.LOGGER.info("All required timers has been started");
    }

    public static void stopTimers(ClientPlayNetworkHandler clientPlayNetworkHandler, MinecraftClient client)
    {
        JsonArray obj = new JsonArray();
        BetterCoolDown.LOGGER.info("Stopping all timers");
        timers.forEach((uuid, timer) -> {
            timer.stop();
            obj.add(timer.write());
        });
        BetterCoolDown.LOGGER.info("All timers stopped");
        BetterCoolDown.LOGGER.info("Starting saving timers");
        ConfigHandler.writeTimers(obj);
        BetterCoolDown.LOGGER.info("All timers saved");
    }

    public static void itemUse(PlayerEntity playerEntity, Hand hand)
    {
        World world = playerEntity.getWorld();
        if (world.isClient() && !playerEntity.isSpectator())
        {
            ItemStack stack = playerEntity.getStackInHand(hand);

            timers.forEach((uuid, timer) ->
            {
                Item timerItem = Registries.ITEM.get(timer.getItem());

                NbtCompound compound = stack.getOrCreateNbt();
                int modelData = 0;
                if (compound.contains("CustomModelData"))
                    modelData = compound.getInt("CustomModelData");
                if (stack.getItem() == timerItem && modelData == timer.getModelData())
                {
                    timer.start();
                }
            });
        }
    }
}
