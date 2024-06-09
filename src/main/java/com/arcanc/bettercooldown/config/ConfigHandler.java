/**
 * @author ArcAnc
 * Created at: 01.06.2024
 * Copyright (c) 2023
 * <p>
 * This code is licensed under "Ancient's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.bettercooldown.config;

import com.arcanc.bettercooldown.Database;
import com.arcanc.bettercooldown.BetterCoolDown;
import com.arcanc.bettercooldown.timer.Timer;
import com.arcanc.bettercooldown.timer.TimersHandler;
import com.arcanc.bettercooldown.gui.RenderHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.JsonHelper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigHandler {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static File timerconfigPath;
    public static File configPath;

    public static void init() {
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve(Database.MOD_ID);

        if (!configDir.toFile().exists()) {
            BetterCoolDown.LOGGER.info("The config directory is missing, creating a new one");
            configDir.toFile().mkdirs();
        }

        timerconfigPath = configDir.resolve("timer.json").toFile();
        configPath = configDir.resolve("config.json").toFile();

        if (!timerconfigPath.exists()) {
            initTimerConfig(timerconfigPath);
            BetterCoolDown.LOGGER.info("Timer config successfully created");
        }

        if (!configPath.exists()) {
            initGeneralConfig(configPath);
            BetterCoolDown.LOGGER.info("General config successfully created");
        }
    }

    private static void initTimerConfig(File file) {
        try {
            file.createNewFile();
            file.setWritable(true);
            file.setReadable(true);
        } catch (IOException e) {
            throw new RuntimeException("Invalid Config Path: " + e.getMessage());
        }
        try (FileWriter writer = new FileWriter(file)) {
            JsonArray obj = new JsonArray();
            writer.write(GSON.toJson(obj));
        } catch (IOException e) {
            throw new RuntimeException("Wrong File info");
        }
    }

    private static void initGeneralConfig(File file) {
        try {
            file.createNewFile();
            file.setWritable(true);
            file.setReadable(true);
        } catch (IOException e) {
            throw new RuntimeException("Invalid Config Path: " + e.getMessage());
        }
        try (FileWriter writer = new FileWriter(file)) {
            JsonObject config = new JsonObject();
            config.addProperty("render_hud", RenderHelper.RENDER_HUD);
            config.addProperty("render_background_texture", RenderHelper.RENDER_BACKGROUND_TEXTURE);
            writer.write(GSON.toJson(config));
        } catch (IOException e) {
            throw new RuntimeException("Wrong File info");
        }
    }

    public static void read() {
        BetterCoolDown.LOGGER.info("The config found, starting config reading");
        try {

            FileReader timerReader = new FileReader(timerconfigPath);
            JsonArray timerArray = JsonHelper.deserialize(GSON, timerReader, JsonArray.class);
            timerReader.close();
            if (!timerArray.isEmpty())
                readTimers(timerArray);

            FileReader configReader = new FileReader(configPath);
            JsonObject configObject = JsonHelper.deserialize(GSON, configReader, JsonObject.class);
            configReader.close();
            if (configObject != null) {
                RenderHelper.RENDER_HUD = JsonHelper.getBoolean(configObject, "render_hud", true);
                RenderHelper.RENDER_BACKGROUND_TEXTURE = JsonHelper.getBoolean(configObject, "render_background_texture", true);
            }

            BetterCoolDown.LOGGER.info("Config has been read, data loaded, awaiting game entry");
        } catch (IOException e) {
            throw new RuntimeException("Can't find config file");
        }
    }

    private static void readTimers(JsonArray mainArray) {
        for (int q = 0; q < mainArray.size(); q++) {
            JsonObject obj = mainArray.get(q).getAsJsonObject();
            Timer timer = Timer.read(obj);
            timer.setPaused(true);
            TimersHandler.timers.putIfAbsent(timer.getId(), timer);
        }
    }

    public static void writeTimers(JsonArray array) {
        try (FileWriter writer = new FileWriter(timerconfigPath)) {
            writer.write(GSON.toJson(array));
        } catch (IOException e) {
            throw new RuntimeException("Can't write timer to file");
        }
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(configPath)) {
            JsonObject config = new JsonObject();
            config.addProperty("render_hud", RenderHelper.RENDER_HUD);
            config.addProperty("render_background_texture", RenderHelper.RENDER_BACKGROUND_TEXTURE);
            writer.write(GSON.toJson(config));
        } catch (IOException e) {
            throw new RuntimeException("Can't write config to file");
        }
    }
}
