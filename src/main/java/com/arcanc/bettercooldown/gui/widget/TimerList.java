/**
 * @author ArcAnc
 * Created at: 01.06.2024
 * Copyright (c) 2023
 * <p>
 * This code is licensed under "Ancient's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.bettercooldown.gui.widget;

import com.arcanc.bettercooldown.Database;
import com.arcanc.bettercooldown.gui.RenderHelper;
import com.arcanc.bettercooldown.gui.TimersEditScreen;
import com.arcanc.bettercooldown.gui.TimersScreen;
import com.arcanc.bettercooldown.timer.Timer;
import com.arcanc.bettercooldown.timer.TimersHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class TimerList extends AlwaysSelectedEntryListWidget<TimerList.TimerEntry> {

    private final TimersScreen screen;
    public TimerList(MinecraftClient minecraftClient, TimersScreen screen, int width, int height, int y, int itemHeight) {
        super(minecraftClient, width, height, y, itemHeight);
        this.screen = screen;
        this.setRenderBackground(false);
        this.setRenderHeader(false, 0);
    }

    public void updateEntries()
    {
        this.clearEntries();
        TimersHandler.timers.forEach((uuid, timer) ->
                addEntry(TimerList.this.new TimerEntry(timer, RenderHelper.mc())));
    }

    public void setTimers(@NotNull List<Timer> timers)
    {
        this.clearEntries();
        timers.forEach(timer -> addEntry(TimerList.this.new TimerEntry(timer, RenderHelper.mc())));
    }

    @Override
    public int getRowWidth() {
        return 180;
    }

    @Override
    protected int getScrollbarPositionX()
    {
        return this.getX() + this.getWidth() + 5;
    }

    public class TimerEntry extends AlwaysSelectedEntryListWidget.Entry<TimerEntry>
    {
        private final EntryButton buttonEdit;
        private final EntryButton buttonDelete;

        private final Timer timer;
        private final MinecraftClient client;

        public TimerEntry(Timer timer, MinecraftClient client)
        {
            this.timer = timer;
            this.client = client;
            buttonEdit = new EntryButton(Database.TimersInfo.TexturesInfo.GUIInfo.ButtonsInfo.EDIT,
                    b ->
                            this.client.setScreen(new TimersEditScreen(screen, timer)),
                    Supplier::get);
            buttonEdit.setTooltip(Tooltip.of(Text.translatable(Database.TimersInfo.Descriptions.ListInfo.BUTTON_EDIT_TOOLTIP)));

            buttonDelete = new EntryButton(Database.TimersInfo.TexturesInfo.GUIInfo.ButtonsInfo.DELETE,
                    b ->
                    {
                        RenderHelper.removeTimer(this.timer);
                        TimersHandler.timers.remove(this.timer.getId());
                        TimerList.this.removeEntry(this);
                        TimerList.this.updateEntries();
                    },
                    Supplier :: get);
            buttonDelete.setTooltip(Tooltip.of(Text.translatable(Database.TimersInfo.Descriptions.ListInfo.BUTTON_DELETE_TOOLTIP)));
        }

        @Override
        public Text getNarration() {
            return Text.empty();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta)
        {
            TextRenderer textRenderer = this.client.textRenderer;
            if (this.client.options.getTouchscreen().getValue() || hovered)
            {
                context.fill(x, y, x + entryWidth, y + entryHeight, -1601138544);
            }

            ItemStack stack = Registries.ITEM.get(timer.getItem()).getDefaultStack();
            NbtCompound nbt = stack.getOrCreateNbt();
            nbt.putInt("CustomModelData", timer.getModelData());

            context.drawItem(stack, x + 5, y + entryHeight / 2 - 8);

            MutableText timerName = Text.literal(timer.getName());

            context.drawText(textRenderer, timerName , x + 5 + 16 + 2, y + entryHeight / 2 - textRenderer.fontHeight / 2, -1, false);

            context.drawText(textRenderer, RenderHelper.timerTimeToText(timer.getTimeLeft()), x + 5 + 16 + 2  + textRenderer.getWidth(timerName) + 2, y + entryHeight / 2 - textRenderer.fontHeight / 2, -1, false);

            buttonEdit.setX(x + entryWidth - 10 - 8 - 2 - 8);
            buttonEdit.setY(y + entryHeight / 2 - 4);
            buttonDelete.setX(x + entryWidth - 10 - 8);
            buttonDelete.setY(y + entryHeight / 2 - 4);

            buttonEdit.render(context, mouseX, mouseY, tickDelta);
            buttonDelete.render(context, mouseX, mouseY, tickDelta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            if (isMouseOver(mouseX, mouseY))
            {
                boolean buttonClicked = buttonEdit.mouseClicked(mouseX, mouseY, button) || buttonDelete.mouseClicked(mouseX, mouseY, button);
                return !buttonClicked;
            }

            return false;
        }
    }

    public static class EntryButton extends ButtonWidget
    {
        private final Identifier texture;
        public EntryButton(Identifier texture, PressAction onPress, NarrationSupplier narrationSupplier)
        {
            super(0,0, 8, 8, Text.empty(), onPress, narrationSupplier);
            this.texture = texture;
        }

        @Override
        protected void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float delta)
        {
            context.getMatrices().push();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            context.drawTexture(texture, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0, isSelected() ? 32 : 0, 32, 32, 32, 64);
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            context.getMatrices().pop();
        }
    }
}
