package com.arcanc.bettercooldown;

import com.arcanc.bettercooldown.config.ConfigHandler;
import com.arcanc.bettercooldown.gui.RenderHelper;
import com.arcanc.bettercooldown.keys.KeyHandler;
import com.arcanc.bettercooldown.timer.TimersHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterCoolDown implements ClientModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(Database.MOD_ID);



	@Override
	public void onInitializeClient()
	{
		LOGGER.info("Start {} initialization", Database.MOD_ID);

		KeyHandler.init();

		ConfigHandler.init();
		ConfigHandler.read();


		ClientLifecycleEvents.CLIENT_STARTED.register(RenderHelper :: init);
		ClientPlayConnectionEvents.JOIN.register(TimersHandler :: startRequiredTimers);
		ClientTickEvents.START_CLIENT_TICK.register(TimersHandler :: clientTick);
		ClientPlayConnectionEvents.DISCONNECT.register(TimersHandler :: stopTimers);

		HudRenderCallback.EVENT.register(RenderHelper :: renderHUD);

		LOGGER.info("Finished {} initialization", Database.MOD_ID);
	}
}