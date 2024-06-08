/**
 * @author ArcAnc
 * Created at: 31.05.2024
 * Copyright (c) 2023
 * <p>
 * This code is licensed under "Ancient's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.bettercooldown.keys;

import com.arcanc.bettercooldown.Database;
import com.arcanc.bettercooldown.BetterCoolDown;
import com.arcanc.bettercooldown.gui.RenderHelper;
import com.arcanc.bettercooldown.gui.TimersScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyHandler
{
    public static KeyBinding openScreen;
    public static KeyBinding hudSwitch;

    public static void registerKeyInputs()
    {
        ClientTickEvents.END_CLIENT_TICK.register(client ->
        {
            if (openScreen.wasPressed())
            {
                if (MinecraftClient.getInstance().currentScreen == null)
                    MinecraftClient.getInstance().setScreen(new TimersScreen());
            }
            if (hudSwitch.wasPressed())
            {
                RenderHelper.hudSwitch();
            }
        });
    }

    public static void init()
    {
        BetterCoolDown.LOGGER.info("Start {} keybinding initialization", Database.MOD_ID);
        openScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                Database.KeyBindingsInfo.OPEN_TIMERS_SCREEN,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                Database.KeyBindingsInfo.CATEGORY
        ));

        hudSwitch = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                Database.KeyBindingsInfo.HUD_SWITCH,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                 Database.KeyBindingsInfo.CATEGORY
        ));

        registerKeyInputs();

        BetterCoolDown.LOGGER.info("Finished {} keybinding initialization", Database.MOD_ID);
    }
}
