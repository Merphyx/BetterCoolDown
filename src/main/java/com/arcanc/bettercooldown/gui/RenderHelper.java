/**
 * @author ArcAnc
 * Created at: 26.05.2024
 * Copyright (c) 2023
 * <p>
 * This code is licensed under "Ancient's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.bettercooldown.gui;

import com.arcanc.bettercooldown.Database;
import com.arcanc.bettercooldown.BetterCoolDown;
import com.arcanc.bettercooldown.timer.Timer;
import com.arcanc.bettercooldown.timer.TimerPosition;
import com.arcanc.bettercooldown.timer.TimersHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class RenderHelper
{

    private static final Map<TimerPosition, Vec2f> ANCHORS = new EnumMap<>(TimerPosition.class);
    private static final Map<TimerPosition, List<Timer>> TIMERS = new EnumMap<>(TimerPosition.class);

    private static final float SOLO_PANEL_HEIGHT = 18f;

    private static final Function<Integer, Float> PANEL_WIDTH = width -> width * 0.2f;
    private static final Function<Integer, Float> PANEL_HEIGHT = listSize -> listSize * SOLO_PANEL_HEIGHT + 8;
    private static boolean RENDER_HUD = true;

    public static void renderHUD(@NotNull DrawContext drawContext, float tickDelta)
    {
        if (!RENDER_HUD)
            return;
        MinecraftClient mc = MinecraftClient.getInstance();
        TextRenderer text = mc.textRenderer;
        Window window = mc.getWindow();
        int width = window.getScaledWidth();
        int height = window.getScaledHeight();

        MatrixStack stack = drawContext.getMatrices();

        stack.push();
        {

            TIMERS.forEach((position, list) ->
            {
                if (!list.isEmpty())
                {
                    Vec2f size = new Vec2f(PANEL_WIDTH.apply(width), PANEL_HEIGHT.apply(list.size()));
                    //BACKGROUND
                    Vec2f pos = ANCHORS.get(position);
                    RenderSystem.enableBlend();
                    drawContext.drawTexture(Database.TimersInfo.TexturesInfo.GUIInfo.HUDInfo.BackgroundInfo.BACKGROUND_TOP_LEFT,
                            (int)pos.x, (int)pos.y, 0, 0, 8, 8, 8, 8);
                    drawContext.drawTexture(Database.TimersInfo.TexturesInfo.GUIInfo.HUDInfo.BackgroundInfo.BACKGROUND_TOP_CENTER,
                            (int)pos.x + 8, (int)pos.y, 0, 0, (int)size.x - 8, 8, 8, 8);
                    drawContext.drawTexture(Database.TimersInfo.TexturesInfo.GUIInfo.HUDInfo.BackgroundInfo.BACKGROUND_TOP_RIGHT,
                            (int)pos.x + 8 + (int)size.x - 8, (int)pos.y, 0, 0, 8, 8, 8, 8);

                    drawContext.drawTexture(Database.TimersInfo.TexturesInfo.GUIInfo.HUDInfo.BackgroundInfo.BACKGROUND_MIDDLE_LEFT,
                            (int)pos.x, (int)pos.y + 8, 0, 0, 8, (int)size.y - 16, 8, 8);
                    drawContext.drawTexture(Database.TimersInfo.TexturesInfo.GUIInfo.HUDInfo.BackgroundInfo.BACKGROUND_MIDDLE_CENTER,
                            (int)pos.x + 8, (int)pos.y + 8, 0, 0, (int)size.x - 8, (int) size.y - 16, 8, 8);
                    drawContext.drawTexture(Database.TimersInfo.TexturesInfo.GUIInfo.HUDInfo.BackgroundInfo.BACKGROUND_MIDDLE_RIGHT,
                            (int)pos.x + 8 + (int)size.x - 8, (int)pos.y + 8, 0, 0, 8, (int)size.y - 16, 8, 8);

                    drawContext.drawTexture(Database.TimersInfo.TexturesInfo.GUIInfo.HUDInfo.BackgroundInfo.BACKGROUND_BOTTOM_LEFT,
                            (int)pos.x, (int)pos.y + (int)size.y - 8, 0, 0, 8, 8, 8, 8);
                    drawContext.drawTexture(Database.TimersInfo.TexturesInfo.GUIInfo.HUDInfo.BackgroundInfo.BACKGROUND_BOTTOM_CENTER,
                            (int)pos.x + 8, (int)pos.y + (int)size.y - 8, 0, 0, (int)size.x - 8, 8, 8, 8);
                    drawContext.drawTexture(Database.TimersInfo.TexturesInfo.GUIInfo.HUDInfo.BackgroundInfo.BACKGROUND_BOTTOM_RIGHT,
                            (int)pos.x + 8 + (int)size.x - 8, (int)pos.y + (int)size.y - 8, 0, 0, 8, 8, 8, 8);
                    RenderSystem.disableBlend();

                    for (int q = 0; q < list.size(); q++)
                    {
                        Timer timer = list.get(q);

                        //ITEM
                        ItemStack itemStack = Registries.ITEM.get(timer.getItem()).getDefaultStack();
                        NbtCompound compound = itemStack.getOrCreateNbt();
                        compound.putInt("CustomModelData", timer.getModelData());
                        drawContext.drawItem(itemStack, (int)pos.x + 4, (int)pos.y + 4 + (int)(q * SOLO_PANEL_HEIGHT) );

                        int nameWidth = text.getWidth(timer.getName());
                        //NAME
                        drawContext.drawText(text, timer.getName(), (int)pos.x + 2 + 2 + 16, (int)pos.y + 8 + (int)(q * SOLO_PANEL_HEIGHT), -1, false);

                        MutableText mutableText = timerTimeToText(timer.getTimeLeft());
                        //TIME
                        drawContext.drawText(text, mutableText, (int)pos.x + 2 + 2 + 16 + 2 + nameWidth, (int)pos.y + 8 + (int)(q * SOLO_PANEL_HEIGHT), -1 ,false);

                    }
                }
            });
        }
        stack.pop();
    }

    public static boolean removeTimer(Timer timer)
    {
        List<Timer> list = TIMERS.get(timer.getPosition());
        return list.remove(timer);
    }

    public static boolean addTimer(Timer timer)
    {
        List<Timer> list = TIMERS.get(timer.getPosition());
        return list.add(timer);
    }

    public static @NotNull MutableText timerTimeToText(long timerTime) {
        long timeLeft = timerTime / 1000;

        int[] time = new int[4];
        time[0] = (int) (timeLeft / 86400); //Days
        timeLeft %= 86400;
        time[1] = (int) (timeLeft / 3600); //Hours
        timeLeft %= 3600;
        time[2] = (int) (timeLeft / 60); //Minutes
        timeLeft %= 60;
        time[3] = (int) timeLeft; //Seconds

        if (time[0] == 0 && time[1] == 0 && time[2] == 0 && time[3] == 0) {
            return Text.translatable(Database.TimersInfo.Descriptions.TimeFormat.TIME_FORMAT);
        }

        StringBuilder formattedTime = new StringBuilder();
        if (time[0] > 0) {
            formattedTime.append(time[0]).append(":");
        }
        if (time[1] > 0 || time[0] > 0) {
            formattedTime.append(String.format("%02d:", time[1]));
        }
        if (time[2] > 0 || time[1] > 0 || time[0] > 0) {
            formattedTime.append(String.format("%02d:", time[2]));
        }


        if (time[0] > 0 || time[1] > 0 || time[2] > 0) {
            formattedTime.append(String.format("%02d", time[3]));
        } else {
            formattedTime.append(time[3]);
        }

        return Text.literal(formattedTime.toString());
    }

    public static void init(@NotNull MinecraftClient client)
    {
        /*Timer testTimer = Timer.newBuilder(UUID.randomUUID()).
                setTimeLeft(5000).
                setPaused(false).
                setItem(new Identifier("apple")).
                setModelData(0).
                setName("Apple").
                setPosition(TimerPosition.TOP_LEFT).
                build();

        TimersHandler.timers.put(testTimer.getId(), testTimer);

        testTimer = Timer.newBuilder(UUID.randomUUID()).
                setTimeLeft(500).
                setPaused(false).
                setItem(new Identifier("iron_sword")).
                setModelData(0).
                setName("sword").
                setPosition(TimerPosition.TOP_LEFT).
                build();

        TimersHandler.timers.put(testTimer.getId(), testTimer);

        testTimer = Timer.newBuilder(UUID.randomUUID()).
                setTimeLeft(1600).
                setPaused(false).
                setItem(new Identifier("iron_sword")).
                setModelData(0).
                setName("sword").
                setPosition(TimerPosition.TOP_LEFT).
                build();

        TimersHandler.timers.put(testTimer.getId(), testTimer);
        */

        BetterCoolDown.LOGGER.info("Start registering anchors");
        int height = client.getWindow().getScaledHeight();
        int width = client.getWindow().getScaledWidth();

        ANCHORS.put(TimerPosition.TOP_LEFT, new Vec2f(width * 0.005f, height * 0.005f));
        ANCHORS.put(TimerPosition.TOP_CENTER, new Vec2f(width /2f - PANEL_WIDTH.apply(width) / 2f, height * 0.005f));
        ANCHORS.put(TimerPosition.TOP_RIGHT, new Vec2f(width - 8 - PANEL_WIDTH.apply(width) - width * 0.005f, height * 0.005f));
        ANCHORS.put(TimerPosition.CENTER_LEFT, new Vec2f(width * 0.005f, height / 2f));
        ANCHORS.put(TimerPosition.CENTER_RIGHT, new Vec2f(width - 8 - PANEL_WIDTH.apply(width) - width * 0.005f, height /2f));

        TIMERS.put(TimerPosition.TOP_LEFT, new ArrayList<>());
        TIMERS.put(TimerPosition.TOP_CENTER, new ArrayList<>());
        TIMERS.put(TimerPosition.TOP_RIGHT, new ArrayList<>());
        TIMERS.put(TimerPosition.CENTER_LEFT, new ArrayList<>());
        TIMERS.put(TimerPosition.CENTER_RIGHT, new ArrayList<>());

        TimersHandler.timers.forEach((uuid, timer) ->
        {
            List<Timer> list = TIMERS.get(timer.getPosition());
            list.add(timer);
        });

        BetterCoolDown.LOGGER.info("Finished registering anchors");
    }

    public static void resize(@NotNull MinecraftClient client)
    {
        int height = client.getWindow().getScaledHeight();
        int width = client.getWindow().getScaledWidth();

        ANCHORS.put(TimerPosition.TOP_LEFT, new Vec2f(width * 0.005f, height * 0.005f));
        ANCHORS.put(TimerPosition.TOP_CENTER, new Vec2f(width / 2f - PANEL_WIDTH.apply(width) / 2f, height * 0.005f));
        ANCHORS.put(TimerPosition.TOP_RIGHT, new Vec2f(width - 8 - PANEL_WIDTH.apply(width) - width * 0.005f, height * 0.005f));
        ANCHORS.put(TimerPosition.CENTER_LEFT, new Vec2f(width * 0.005f, height / 2f));
        ANCHORS.put(TimerPosition.CENTER_RIGHT, new Vec2f(width - 8 - PANEL_WIDTH.apply(width) - width * 0.005f, height /2f));
    }

    public static MinecraftClient mc ()
    {
        return MinecraftClient.getInstance();
    }

    public static TextRenderer mcTextRenderer()
    {
        MinecraftClient minecraftClient = mc();
        return minecraftClient.textRenderer;
    }

    public static void hudSwitch()
    {
        RENDER_HUD = !RENDER_HUD;
    }
}
