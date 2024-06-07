/**
 * @author ArcAnc
 * Created at: 25.05.2024
 * Copyright (c) 2023
 * <p>
 * This code is licensed under "Ancient's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.bettercooldown;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class Database
{
    public static final String MOD_ID = "bettercooldown";

    public static class TimersInfo
    {
        public static final String ITEM = "item";
        public static final String MODEL_DATA = "model_data";
        public static final String NAME = "name";
        public static final String FULL_TIME = "full_time";
        public static final String TIME_LEFT = "time_left";
        public static final String POSITION = "position";
        public static final String PAUSE = "paused";
        public static final String ID = "uuid";
        public static final String SERVER_INFO = "server_info";

        public static final class TexturesInfo
        {
            private static final String TEXTURES = "textures/";
            public static final class GUIInfo
            {
                private static final String GUI = TEXTURES + "gui/";
                public static final class HUDInfo
                {
                    private static final String HUD = GUI + "hud/";

                    public static final class BackgroundInfo
                    {
                        private static final String BACKGROUND = HUD + "background/";
                        public static final Identifier BACKGROUND_BOTTOM_CENTER = rs(BACKGROUND + "bottom_center.png");
                        public static final Identifier BACKGROUND_BOTTOM_LEFT = rs(BACKGROUND + "bottom_left.png");
                        public static final Identifier BACKGROUND_BOTTOM_RIGHT = rs(BACKGROUND + "bottom_right.png");
                        public static final Identifier BACKGROUND_MIDDLE_CENTER = rs(BACKGROUND + "middle_center.png");
                        public static final Identifier BACKGROUND_MIDDLE_LEFT = rs(BACKGROUND + "middle_left.png");
                        public static final Identifier BACKGROUND_MIDDLE_RIGHT = rs(BACKGROUND + "middle_right.png");
                        public static final Identifier BACKGROUND_TOP_CENTER = rs(BACKGROUND + "top_center.png");
                        public static final Identifier BACKGROUND_TOP_LEFT = rs(BACKGROUND + "top_left.png");
                        public static final Identifier BACKGROUND_TOP_RIGHT = rs(BACKGROUND + "top_right.png");
                    }
                }

                public static class ButtonsInfo
                {
                   private static final String BUTTONS = GUI + "buttons/";

                   public static final Identifier EDIT = rs(BUTTONS + "edit.png");
                   public static final Identifier DELETE = rs(BUTTONS + "delete.png");
                }
            }
        }

        public static final class Descriptions
        {
            private static final Function<String, String> DESCRIPTION_ADDER = s -> "gui." + MOD_ID + ".description." + s;

            public static final class Fields
            {
                public static final String FIELD_TIMER_NAME = DESCRIPTION_ADDER.apply("timer_name");
                public static final String FIELD_TIMER_ITEM_NAME = DESCRIPTION_ADDER.apply("item_name");
                public static final String FIELD_TIMER_MODEL_DATA = DESCRIPTION_ADDER.apply("model_data");
                public static final String FIELD_TIMER_TIME = DESCRIPTION_ADDER.apply("time");

                public static final String FIELD_TIMER_NAME_TOOLTIP = DESCRIPTION_ADDER.apply("timer_name.tooltip");
                public static final String FIELD_TIMER_ITEM_NAME_TOOLTIP = DESCRIPTION_ADDER.apply("item_name.tooltip");
                public static final String FIELD_TIMER_MODEL_DATA_TOOLTIP = DESCRIPTION_ADDER.apply("model_data.tooltip");
                public static final String FIELD_TIMER_TIME_TOOLTIP = DESCRIPTION_ADDER.apply("time.tooltip");

                public static final String FIELD_TIMER_NAME_EDIT_TOOLTIP = DESCRIPTION_ADDER.apply("timer_name.edit.tooltip");
                public static final String FIELD_TIMER_ITEM_NAME_EDIT_TOOLTIP = DESCRIPTION_ADDER.apply("item_name.edit.tooltip");
                public static final String FIELD_TIMER_MODEL_DATA_EDIT_TOOLTIP = DESCRIPTION_ADDER.apply("model_data.edit.tooltip");
                public static final String FIELD_TIMER_TIME_EDIT_TOOLTIP = DESCRIPTION_ADDER.apply("time.edit.tooltip");
            }

            public static final class TimeFormat {
                public static final String TIME_FORMAT = DESCRIPTION_ADDER.apply("timer_time_format");
            }


            public static final class CycleButtonInfo
            {
                public static final String POSITION = DESCRIPTION_ADDER.apply("position");
            }

            public static final class ButtonInfo
            {
                public static final String BUTTON_ADD_TIMER = DESCRIPTION_ADDER.apply("add_timer");

                public static final String BUTTON_EDIT_TIMER = DESCRIPTION_ADDER.apply("edit_timer");
            }

            public static final class ListInfo
            {
                public static final String LIST_TIMER = DESCRIPTION_ADDER.apply("list");

                public static final String BUTTON_EDIT_TOOLTIP = DESCRIPTION_ADDER.apply("button.edit.tooltip");
                public static final String BUTTON_DELETE_TOOLTIP = DESCRIPTION_ADDER.apply("button.delete.tooltip");
            }

            public static final class Tabs
            {
                public static final String TAB_NEW_TIMER = DESCRIPTION_ADDER.apply("new_timer");
                public static final String ALL_TIMERS = DESCRIPTION_ADDER.apply("all_timers");
            }
        }
    }

    public static class KeyBindingsInfo
    {
        public static final String CATEGORY = "key.categories." + MOD_ID;
        public static final String OPEN_TIMERS_SCREEN = "key." + MOD_ID + ".open_timers_screen";
        public static final String HUD_SWITCH = "key." + MOD_ID + ".hud_switch";
    }

    public static @NotNull Identifier rs(@NotNull String str)
    {
        return new Identifier(MOD_ID, str);
    }
}
