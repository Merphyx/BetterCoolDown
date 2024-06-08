/**
 * @author ArcAnc
 * Created at: 06.06.2024
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
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TimersEditScreen extends Screen
{

    private TextFieldWidget timerName;
    private TextFieldWidget itemName;
    private TextFieldWidget modelData;
    private TextFieldWidget time;
    private CyclingButtonWidget<TimerPosition> position;
    private ButtonWidget addTimer;

    private final TimersScreen timersScreen;
    private final Timer timer;
    public TimersEditScreen(TimersScreen screen, Timer timer)
    {
        super(Text.empty());
        this.timersScreen = screen;
        this.timer = timer;
    }

    @Override
    protected void init()
    {
        int centerX = this.width / 2;

        TextRenderer textRenderer = RenderHelper.mcTextRenderer();
        timerName = new TextFieldWidget(textRenderer, centerX - 85, 25, 170, 15, Text.empty());
        this.setFocused(timerName);
        timerName.setText(timer.getName());
        timerName.setTooltip(Tooltip.of(Text.translatable(Database.TimersInfo.Descriptions.Fields.FIELD_TIMER_NAME_EDIT_TOOLTIP)));
        addDrawableChild(timerName);

        itemName = new TextFieldWidget(textRenderer, centerX - 85, 45, 170, 15, Text.empty());
        itemName.setTooltip(Tooltip.of(Text.translatable(Database.TimersInfo.Descriptions.Fields.FIELD_TIMER_ITEM_NAME_EDIT_TOOLTIP)));
        itemName.setText(timer.getItem().toString());
        addDrawableChild(itemName);

        modelData = new TextFieldWidget(textRenderer, centerX - 85, 65, 170, 15, Text.empty());
        modelData.setText(String.valueOf(timer.getModelData()));
        modelData.setTooltip(Tooltip.of(Text.translatable(Database.TimersInfo.Descriptions.Fields.FIELD_TIMER_MODEL_DATA_EDIT_TOOLTIP)));
        addDrawableChild(modelData);

        time = new TextFieldWidget(textRenderer, centerX - 85, 85, 170, 15, Text.empty());
        time.setText(RenderHelper.convertTimeToFormattedString(timer.getFullTime()).getString());
        time.setTooltip(Tooltip.of(Text.translatable(Database.TimersInfo.Descriptions.Fields.FIELD_TIMER_TIME_EDIT_TOOLTIP)));
        addDrawableChild(time);

        position = CyclingButtonWidget.<TimerPosition>builder(pos ->
                {
                    String name = pos.name();
                    String[] divided = name.substring(1).split("_");
                    return Text.literal(name.charAt(0) + divided[0].toLowerCase() + " " + divided[1].charAt(0) + divided[1].substring(1).toLowerCase());
                })

                .initially(timer.getPosition())

                .values(TimerPosition.values())

                .build(centerX - 85, 105, 170, 15, Text.translatable(Database.TimersInfo.Descriptions.CycleButtonInfo.POSITION));
        addDrawableChild(position);

        addTimer = ButtonWidget.builder(Text.translatable(Database.TimersInfo.Descriptions.ButtonInfo.BUTTON_EDIT_TIMER), b ->
                {
                    if (timerName.getText().isEmpty() || timerName.getText().isBlank())
                        return;
                    if (itemName.getText().isEmpty() || itemName.getText().isBlank())
                        return;
                    Item item = Registries.ITEM.get(new Identifier(itemName.getText()));
                    if (item == Items.AIR)
                        return;
                    int modelID = 0;
                    try
                    {
                        modelID = Integer.parseInt(modelData.getText());
                    }
                    catch (NumberFormatException exception)
                    {
                        BetterCoolDown.LOGGER.warn("You insert wrong data into model id field. Used default data");
                    }

                    long timeLong = 30000;

                    try
                    {
                        timeLong = RenderHelper.convertStringToMilliseconds(time.getText());
                    }
                    catch (NumberFormatException exception)
                    {
                        BetterCoolDown.LOGGER.warn("You insert wrong data into time field. Timer set to 30 sec");
                    }
                    if (modelID < 0 || timeLong <= 0)
                        return;

                    TimersHandler.timers.remove(timer.getId());
                    RenderHelper.removeTimer(timer);
                    timersScreen.updateTimersEntries();

                    Timer newTimer = Timer.newBuilder(timer.getId()).
                            setFullTime(timeLong).
                            setTimeLeft(timeLong).
                            setItem(new Identifier(itemName.getText())).
                            setModelData(modelID).
                            setName(timerName.getText()).
                            setPosition(position.getValue()).
                            setPaused(true).
                            setPrevTime(0).
                            build();

                    TimersHandler.timers.put(newTimer.getId(), newTimer);
                    RenderHelper.addTimer(newTimer);
                    timersScreen.updateTimersEntries();
                    this.close();
                }).
                size(170, 15).
                position(centerX - 85, 125).
                build();
        addDrawableChild(addTimer);
    }

    @Override
    public void close()
    {
        this.client.setScreen(timersScreen);
    }
}
