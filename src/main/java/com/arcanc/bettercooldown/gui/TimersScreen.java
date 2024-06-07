/**
 * @author ArcAnc
 * Created at: 29.05.2024
 * Copyright (c) 2023
 * <p>
 * This code is licensed under "Ancient's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.bettercooldown.gui;

import com.arcanc.bettercooldown.utils.TextColorParser;
import com.arcanc.bettercooldown.Database;
import com.arcanc.bettercooldown.BetterCoolDown;
import com.arcanc.bettercooldown.gui.widget.TimerList;
import com.arcanc.bettercooldown.timer.Timer;
import com.arcanc.bettercooldown.timer.TimerPosition;
import com.arcanc.bettercooldown.timer.TimersHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class TimersScreen extends Screen
{
    private TabNavigationWidget tabs;
    private final TabManager tabManager = new TabManager(this::addDrawableChild, this::remove);
    private final TimersTab timersTab = new TimersTab();
    private final NewTimerTab newTimerTab = new NewTimerTab();
    private final TextColorParser colorParser = new TextColorParser();
    public TimersScreen()
    {
        super(Text.empty());
    }

    @Override
    protected void init()
    {

        tabs = TabNavigationWidget.builder(this.tabManager, this.width).tabs(timersTab, newTimerTab).build();
        this.addDrawableChild(tabs);
        this.tabs.selectTab(0, false);

        initTabNavigation();
    }

    public void initTabNavigation() {
        if (this.tabs != null) {
            this.tabs.setWidth(this.width);
            this.tabs.init();
            int i = this.tabs.getNavigationFocus().getBottom();
            ScreenRect screenRect = new ScreenRect(0, i, this.width, this.height - i);
            this.tabManager.setTabArea(screenRect);
        }
    }

    private class NewTimerTab extends GridScreenTab
    {
        private final TextFieldWidget timerName;
        private final TextFieldWidget itemName;
        private final TextFieldWidget modelData;
        private final TextFieldWidget time;
        private final CyclingButtonWidget<TimerPosition> position;

        private final ButtonWidget addTimer;
        public NewTimerTab()
        {
            super(Text.translatable(Database.TimersInfo.Descriptions.Tabs.TAB_NEW_TIMER));
            GridWidget.Adder adder = this.grid.setRowSpacing(6).createAdder(1);

            TextRenderer textRenderer = RenderHelper.mcTextRenderer();
            timerName = new TextFieldWidget(textRenderer, 170, 15, Text.empty());
            TimersScreen.this.setInitialFocus(timerName);
            adder.add(LayoutWidgets.createLabeledWidget(textRenderer, timerName, Text.translatable(Database.TimersInfo.Descriptions.Fields.FIELD_TIMER_NAME)),
                    adder.copyPositioner().alignHorizontalCenter());
            timerName.setTooltip(Tooltip.of(Text.translatable(Database.TimersInfo.Descriptions.Fields.FIELD_TIMER_NAME_TOOLTIP)));

            itemName = new TextFieldWidget(textRenderer, 170, 15, Text.empty());
            adder.add(LayoutWidgets.createLabeledWidget(textRenderer, itemName, Text.translatable(Database.TimersInfo.Descriptions.Fields.FIELD_TIMER_ITEM_NAME)),
                    adder.copyPositioner().alignHorizontalCenter());
            itemName.setTooltip(Tooltip.of(Text.translatable(Database.TimersInfo.Descriptions.Fields.FIELD_TIMER_ITEM_NAME_TOOLTIP)));

            modelData = new TextFieldWidget(textRenderer, 170, 15, Text.empty());
            adder.add(LayoutWidgets.createLabeledWidget(textRenderer, modelData, Text.translatable(Database.TimersInfo.Descriptions.Fields.FIELD_TIMER_MODEL_DATA)),
                    adder.copyPositioner().alignHorizontalCenter());
            modelData.setTooltip(Tooltip.of(Text.translatable(Database.TimersInfo.Descriptions.Fields.FIELD_TIMER_MODEL_DATA_TOOLTIP)));

            time = new TextFieldWidget(textRenderer, 170, 15, Text.empty());
            time.setText(String.valueOf(0));
            adder.add(LayoutWidgets.createLabeledWidget(textRenderer, time, Text.translatable(Database.TimersInfo.Descriptions.Fields.FIELD_TIMER_TIME)),
                    adder.copyPositioner().alignHorizontalCenter());
            time.setTooltip(Tooltip.of(Text.translatable(Database.TimersInfo.Descriptions.Fields.FIELD_TIMER_TIME_TOOLTIP)));

            position = CyclingButtonWidget.<TimerPosition>builder(pos ->
                    {
                        String name = pos.name();
                        String[] divided = name.substring(1).split("_");
                        return Text.literal(name.charAt(0) + divided[0].toLowerCase() + " " + divided[1].charAt(0) + divided[1].substring(1).toLowerCase());
                    }).
                    initially(TimerPosition.TOP_LEFT).
                    values(TimerPosition.values()).
                    build(0,0, 170, 15, Text.translatable(Database.TimersInfo.Descriptions.CycleButtonInfo.POSITION));
            adder.add(position, adder.copyPositioner().alignHorizontalCenter());

            addTimer = ButtonWidget.builder(Text.translatable(Database.TimersInfo.Descriptions.ButtonInfo.BUTTON_ADD_TIMER), b ->
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

                long timeLong = 30;

                try
                {
                    timeLong = Long.parseLong(time.getText());
                }
                catch (NumberFormatException exception)
                {
                    BetterCoolDown.LOGGER.warn("You insert wrong data into time field. Timer set to 30 sec");
                }

                if (modelID < 0 || timeLong <= 0)
                    return;

                Timer newTimer = Timer.newBuilder(UUID.randomUUID()).
                        setFullTime(timeLong).
                        setTimeLeft(timeLong).
                        setItem(new Identifier(itemName.getText())).
                        setModelData(modelID).
                        setName(colorParser.parse(timerName.getText())).
                        setPosition(position.getValue()).
                        setPaused(true).
                        setPrevTime(0).
                        build();
                TimersHandler.timers.put(newTimer.getId(), newTimer);
                RenderHelper.addTimer(newTimer);
                timersTab.list.updateEntries();
                tabManager.setCurrentTab(timersTab, false);
            }).
                    size(170, 15).
                    build();
            adder.add(addTimer, adder.copyPositioner().alignHorizontalCenter());
        }
    }

    private class TimersTab extends GridScreenTab
    {
        private final TimerList list;
        public TimersTab()
        {
            super(Text.translatable(Database.TimersInfo.Descriptions.Tabs.ALL_TIMERS));
            GridWidget.Adder adder = this.grid.setRowSpacing(8).createAdder(1);

            TextRenderer textRenderer = RenderHelper.mcTextRenderer();
            list = new TimerList(RenderHelper.mc(), TimersScreen.this, 180, 180, 0, 26);
            list.setTimers(TimersHandler.timers.values().stream().toList());
            adder.add(LayoutWidgets.createLabeledWidget(textRenderer, list, Text.translatable(Database.TimersInfo.Descriptions.ListInfo.LIST_TIMER)),
                    adder.copyPositioner().alignHorizontalCenter());
        }
    }

    public void updateTimersEntries()
    {
        this.timersTab.list.updateEntries();
    }
}
