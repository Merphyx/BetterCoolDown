/**
 * @author ArcAnc
 * Created at: 25.05.2024
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

public class ConfigHandler
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static File configPath;

    public static void init()
    {
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve(Database.MOD_ID);

        if (!configDir.toFile().exists())
        {
            BetterCoolDown.LOGGER.info("The config is missing, creating new config");
            configDir.toFile().mkdirs();
        }
        configPath = configDir.resolve("client_config.json").toFile();

        if (!configPath.exists())
        {
            initConfig(configPath);
            BetterCoolDown.LOGGER.info("Config successfully created");
        }

    }

    private static void initConfig(File file){
        try
        {
            file.createNewFile();
            file.setWritable(true);
            file.setReadable(true);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Invalid Config Path: " + e.getMessage());
        }
        try
        {
            FileWriter writer = new FileWriter(file);
            JsonArray obj = new JsonArray();
            writer.write(GSON.toJson(obj));
            writer.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Wrong File info");
        }
    }

    public static void read()
    {
        BetterCoolDown.LOGGER.info("The config found, starting config reading");
        try
        {
            FileReader reader = new FileReader(configPath);
            JsonArray mainArray = JsonHelper.deserialize(GSON, reader, JsonArray.class);
            reader.close();
            if (!mainArray.isEmpty())
                readTimers(mainArray);
            BetterCoolDown.LOGGER.info("Config has been read, data loaded, awaiting game entry");
        } catch (IOException e) {
            throw new RuntimeException("Can't find config file");
        }
    }

    private static void readTimers(JsonArray mainArray)
    {
        for (int q = 0; q < mainArray.size(); q++)
        {
            JsonObject obj = mainArray.get(q).getAsJsonObject();
            Timer timer = Timer.read(obj);
            timer.setPaused(true);
            TimersHandler.timers.putIfAbsent(timer.getId(), timer);
        }
    }

    public static void writeTimers(JsonArray array)
    {
        try
        {
            FileWriter writer = new FileWriter(configPath);
            writer.write(GSON.toJson(array));
            writer.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Can't write timer to file");
        }
    }
}
