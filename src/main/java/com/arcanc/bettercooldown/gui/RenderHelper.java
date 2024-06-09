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

import java.util.*;
import java.util.function.Function;

public class RenderHelper {

    private static final Map<TimerPosition, Vec2f> ANCHORS = new EnumMap<>(TimerPosition.class);
    private static final Map<TimerPosition, List<Timer>> TIMERS = new EnumMap<>(TimerPosition.class);

    private static final float SOLO_PANEL_HEIGHT = 18f;

    private static final Function<Integer, Float> PANEL_WIDTH = width -> width * 0.25f;
    private static final Function<Integer, Float> PANEL_HEIGHT = listSize -> listSize * SOLO_PANEL_HEIGHT + 8;
    private static boolean RENDER_HUD = true;
    private static boolean RENDER_BACKGROUND_TEXTURE = true;

    public static void renderHUD(@NotNull DrawContext drawContext, float tickDelta) {
        if (!RENDER_HUD) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        TextRenderer textRenderer = mc.textRenderer;
        Window window = mc.getWindow();
        int screenWidth = window.getScaledWidth();
        int screenHeight = window.getScaledHeight();

        MatrixStack matrixStack = drawContext.getMatrices();
        matrixStack.push();
        {
            TIMERS.forEach((position, timerList) -> {
                if (!timerList.isEmpty()) {
                    Vec2f panelSize = new Vec2f(PANEL_WIDTH.apply(screenWidth), PANEL_HEIGHT.apply(timerList.size()));
                    Vec2f anchorPos = adjustAnchorPosition(ANCHORS.get(position), panelSize, screenWidth, screenHeight);

                    renderPanelBackground(drawContext, anchorPos, panelSize);

                    for (int index = 0; index < timerList.size(); index++) {
                        Timer timer = timerList.get(index);
                        renderTimer(drawContext, textRenderer, timer, anchorPos, index);
                    }
                }
            });
        }
        matrixStack.pop();
    }

    private static Vec2f adjustAnchorPosition(Vec2f anchor, Vec2f size, int screenWidth, int screenHeight) {
        float edgeOffset = 0.005f;

        float x = Math.max(edgeOffset * screenWidth, Math.min(anchor.x, screenWidth - size.x - edgeOffset * screenWidth));
        float y = Math.max(edgeOffset * screenHeight, Math.min(anchor.y, screenHeight - size.y - edgeOffset * screenHeight));

        return new Vec2f(x, y);
    }

    private static void renderPanelBackground(@NotNull DrawContext drawContext, Vec2f pos, Vec2f size) {
        if (!RENDER_BACKGROUND_TEXTURE) return;
        RenderSystem.enableBlend();
        drawContext.drawTexture(Database.TimersInfo.TexturesInfo.GUIInfo.HUDInfo.BackgroundInfo.BACKGROUND_TOP_LEFT,
                (int) pos.x, (int) pos.y, 0, 0, 8, 8, 8, 8);
        drawContext.drawTexture(Database.TimersInfo.TexturesInfo.GUIInfo.HUDInfo.BackgroundInfo.BACKGROUND_TOP_CENTER,
                (int) pos.x + 8, (int) pos.y, 0, 0, (int) size.x - 16, 8, 8, 8);
        drawContext.drawTexture(Database.TimersInfo.TexturesInfo.GUIInfo.HUDInfo.BackgroundInfo.BACKGROUND_TOP_RIGHT,
                (int) pos.x + (int) size.x - 8, (int) pos.y, 0, 0, 8, 8, 8, 8);

        drawContext.drawTexture(Database.TimersInfo.TexturesInfo.GUIInfo.HUDInfo.BackgroundInfo.BACKGROUND_MIDDLE_LEFT,
                (int) pos.x, (int) pos.y + 8, 0, 0, 8, (int) size.y - 16, 8, 8);
        drawContext.drawTexture(Database.TimersInfo.TexturesInfo.GUIInfo.HUDInfo.BackgroundInfo.BACKGROUND_MIDDLE_CENTER,
                (int) pos.x + 8, (int) pos.y + 8, 0, 0, (int) size.x - 16, (int) size.y - 16, 8, 8);
        drawContext.drawTexture(Database.TimersInfo.TexturesInfo.GUIInfo.HUDInfo.BackgroundInfo.BACKGROUND_MIDDLE_RIGHT,
                (int) pos.x + (int) size.x - 8, (int) pos.y + 8, 0, 0, 8, (int) size.y - 16, 8, 8);

        drawContext.drawTexture(Database.TimersInfo.TexturesInfo.GUIInfo.HUDInfo.BackgroundInfo.BACKGROUND_BOTTOM_LEFT,
                (int) pos.x, (int) pos.y + (int) size.y - 8, 0, 0, 8, 8, 8, 8);
        drawContext.drawTexture(Database.TimersInfo.TexturesInfo.GUIInfo.HUDInfo.BackgroundInfo.BACKGROUND_BOTTOM_CENTER,
                (int) pos.x + 8, (int) pos.y + (int) size.y - 8, 0, 0, (int) size.x - 16, 8, 8, 8);
        drawContext.drawTexture(Database.TimersInfo.TexturesInfo.GUIInfo.HUDInfo.BackgroundInfo.BACKGROUND_BOTTOM_RIGHT,
                (int) pos.x + (int) size.x - 8, (int) pos.y + (int) size.y - 8, 0, 0, 8, 8, 8, 8);
        RenderSystem.disableBlend();
    }

    private static void renderTimer(@NotNull DrawContext drawContext, TextRenderer textRenderer, Timer timer, Vec2f pos, int index) {
        ItemStack itemStack = Registries.ITEM.get(timer.getItem()).getDefaultStack();
        NbtCompound compound = itemStack.getOrCreateNbt();
        compound.putInt("CustomModelData", timer.getModelData());
        drawContext.drawItem(itemStack, (int) pos.x + 4, (int) pos.y + 4 + (int) (index * SOLO_PANEL_HEIGHT));

        String parsedName = TextColorParser.parse(timer.getName());
        int nameWidth = textRenderer.getWidth(parsedName);
        drawContext.drawText(textRenderer, Text.literal(parsedName), (int) pos.x + 24, (int) pos.y + 8 + (int) (index * SOLO_PANEL_HEIGHT), -1, false);

        MutableText timeText = convertTimeToFormattedString(timer.getTimeLeft());
        drawContext.drawText(textRenderer, timeText, (int) pos.x + 24 + nameWidth + 4, (int) pos.y + 8 + (int) (index * SOLO_PANEL_HEIGHT), -1, false);
    }

    public static void init(@NotNull MinecraftClient client) {
        BetterCoolDown.LOGGER.info("Start registering anchors");
        resize(client);
        TIMERS.clear();
        Arrays.stream(TimerPosition.values()).forEach(position -> TIMERS.put(position, new ArrayList<>()));
        TimersHandler.timers.values().forEach(timer -> TIMERS.get(timer.getPosition()).add(timer));
        BetterCoolDown.LOGGER.info("Finished registering anchors");
    }

    public static void resize(@NotNull MinecraftClient client) {
        int height = client.getWindow().getScaledHeight();
        int width = client.getWindow().getScaledWidth();

        ANCHORS.put(TimerPosition.TOP_LEFT, new Vec2f(width * 0.005f, height * 0.010f));
        ANCHORS.put(TimerPosition.TOP_CENTER, new Vec2f(width / 2f - PANEL_WIDTH.apply(width) / 2f, height * 0.010f));
        ANCHORS.put(TimerPosition.TOP_RIGHT, new Vec2f(width - PANEL_WIDTH.apply(width) - width * 0.005f, height * 0.010f));
        ANCHORS.put(TimerPosition.CENTER_LEFT, new Vec2f(width * 0.005f, height / 2f));
        ANCHORS.put(TimerPosition.CENTER_RIGHT, new Vec2f(width - PANEL_WIDTH.apply(width) - width * 0.005f, height / 2f));
        ANCHORS.put(TimerPosition.BOTTOM_LEFT, new Vec2f(width * 0.005f, height - PANEL_HEIGHT.apply(1) - height * 0.005f));
        ANCHORS.put(TimerPosition.BOTTOM_RIGHT, new Vec2f(width - PANEL_WIDTH.apply(width) - width * 0.005f, height - PANEL_HEIGHT.apply(1) - height * 0.005f));
    }


    public static boolean removeTimer(Timer timer) {
        List<Timer> list = TIMERS.get(timer.getPosition());
        return list.remove(timer);
    }

    public static boolean addTimer(Timer timer) {
        List<Timer> list = TIMERS.get(timer.getPosition());
        return list.add(timer);
    }

    public static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    public static TextRenderer mcTextRenderer() {
        MinecraftClient minecraftClient = mc();
        return minecraftClient.textRenderer;
    }

    public static void hudSwitch() {
        RENDER_HUD = !RENDER_HUD;
    }

    public static void backgroundSwitch() {
        RENDER_BACKGROUND_TEXTURE = !RENDER_BACKGROUND_TEXTURE;
    }

    public static class TextColorParser {

        public static String parse(String input) {
            return input.replace('&', '§');
        }
    }

    public static long convertStringToMilliseconds(@NotNull String str) {
        if (str.contains(":")) {
            String[] parts = str.split(":");
            int partsLength = parts.length;

            int days = 0, hours = 0, minutes = 0, seconds = 0, milliseconds = 0;

            switch (partsLength) {
                case 5 -> {
                    days = Integer.parseInt(parts[0]);
                    hours = Integer.parseInt(parts[1]);
                    minutes = Integer.parseInt(parts[2]);
                    seconds = Integer.parseInt(parts[3]);
                    milliseconds = Integer.parseInt(parts[4]);
                }
                case 4 -> {
                    hours = Integer.parseInt(parts[0]);
                    minutes = Integer.parseInt(parts[1]);
                    seconds = Integer.parseInt(parts[2]);
                    milliseconds = Integer.parseInt(parts[3]);
                }
                case 3 -> {
                    minutes = Integer.parseInt(parts[0]);
                    seconds = Integer.parseInt(parts[1]);
                    milliseconds = Integer.parseInt(parts[2]);
                }
                case 2 -> {
                    seconds = Integer.parseInt(parts[0]);
                    milliseconds = Integer.parseInt(parts[1]);
                }
                case 1 -> milliseconds = Integer.parseInt(parts[0]);
                default -> throw new IllegalArgumentException("Invalid format: expected up to DD:HH:MM:SS:MS");
            }

            long totalMilliseconds = 0;
            totalMilliseconds += days * 24L * 60L * 60L * 1000L;
            totalMilliseconds += hours * 60L * 60L * 1000L;
            totalMilliseconds += minutes * 60L * 1000L;
            totalMilliseconds += seconds * 1000L;
            totalMilliseconds += milliseconds;

            return totalMilliseconds;
        } else {
            return Long.parseLong(str);
        }
    }

    public static @NotNull MutableText convertTimeToFormattedString(long milliseconds) {

        if (milliseconds == 0) {
            return Text.translatable(Database.TimersInfo.Descriptions.TimeFormat.TIME_FORMAT);
        }

        long hours = milliseconds / (60 * 60 * 1000);
        milliseconds %= (60 * 60 * 1000);

        long minutes = milliseconds / (60 * 1000);
        milliseconds %= (60 * 1000);

        long seconds = milliseconds / 1000;
        milliseconds %= 1000;

        StringBuilder result = new StringBuilder();

        if (hours > 0) {
            result.append(hours).append(":");
        }

        if (minutes > 0 || result.length() > 0) {
            result.append(String.format("%02d:", minutes));
        }

        if (seconds > 0 || result.length() > 0) {
            result.append(String.format("%02d:", seconds));
        }

        result.append(String.format("%03d", milliseconds));

        return Text.literal(result.toString());
    }
}

