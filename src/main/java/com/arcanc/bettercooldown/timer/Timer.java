/**
 * @author ArcAnc
 * Created at: 25.05.2024
 * Copyright (c) 2023
 * <p>
 * This code is licensed under "Ancient's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.bettercooldown.timer;

import com.arcanc.bettercooldown.Database;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Timer
{
    private final Identifier item;
    private final int modelData;
    private final String name;
    private final long fullTime;
    private long timeLeft;

    private final String serverInfo;
    private final TimerPosition position;
    private boolean paused = true;

    private long prevTime;

    private final UUID id;

    private Timer(long fullTime, long timeLeft, String name, Identifier item, int modelData, TimerPosition position, UUID id, @Nullable String serverInfo)
    {
        this.fullTime = fullTime;
        this.timeLeft = timeLeft;
        this.name = name;
        this.item = item;
        this.modelData = modelData;
        this.position = position;
        this.serverInfo = serverInfo != null ? serverInfo : MinecraftClient.getInstance().getCurrentServerEntry() != null ? MinecraftClient.getInstance().getCurrentServerEntry().address : "";
        this.id = id;
    }

    public void tick()
    {
        if (!canTick())
        {
            prevTime = 0;
            return;
        }

        if (prevTime <= 0)
            prevTime = System.currentTimeMillis();

        final long timeDiff = System.currentTimeMillis() - prevTime;

        timeLeft -= timeDiff;
        prevTime = System.currentTimeMillis();

        if(timeLeft <= 0)
        {
            stop();
            timeLeft = 0;
        }
    }

    public void start()
    {

        if (this.timeLeft <= 0)
            this.timeLeft = this.fullTime;
        this.setPaused(false);
    }

    public boolean canTick()
    {
        return isActive() && !isPaused();
    }

    public void stop()
    {
        this.setPaused(true);
    }

    public boolean isActive()
    {
        return this.timeLeft > 0;
    }

    public Identifier getItem() {
        return item;
    }

    public int getModelData() {
        return modelData;
    }

    public String getName() {
        return name;
    }

    public long getFullTime() {
        return fullTime;
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public boolean isPaused() {
        return paused || MinecraftClient.getInstance().isPaused();
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public String getServerInfo() {
        return serverInfo;
    }

    public TimerPosition getPosition() {
        return position;
    }

    public UUID getId() {
        return id;
    }

    public JsonObject write()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty(Database.TimersInfo.ITEM, this.item.toString());
        obj.addProperty(Database.TimersInfo.MODEL_DATA, this.modelData);
        obj.addProperty(Database.TimersInfo.NAME, this.name);
        obj.addProperty(Database.TimersInfo.FULL_TIME, this.fullTime);
        obj.addProperty(Database.TimersInfo.TIME_LEFT, this.timeLeft);
        obj.addProperty(Database.TimersInfo.SERVER_INFO, this.serverInfo);
        obj.addProperty(Database.TimersInfo.POSITION, this.position.ordinal());
        obj.addProperty(Database.TimersInfo.PAUSE, this.paused);
        obj.addProperty(Database.TimersInfo.ID, this.id.toString());

        return obj;
    }

    public static Timer read(JsonObject obj)
    {
        final String item = obj.get(Database.TimersInfo.ITEM).getAsString();
        final int modelData = obj.get(Database.TimersInfo.MODEL_DATA).getAsInt();
        final String name = obj.get(Database.TimersInfo.NAME).getAsString();
        final long fullTime = obj.get(Database.TimersInfo.FULL_TIME).getAsLong();
        final long timeleft = obj.get(Database.TimersInfo.TIME_LEFT).getAsLong();
        final String serverInfo = obj.get(Database.TimersInfo.SERVER_INFO).getAsString();
        final TimerPosition position = TimerPosition.values()[obj.get(Database.TimersInfo.POSITION).getAsInt()];
        final boolean paused = obj.get(Database.TimersInfo.PAUSE).getAsBoolean();
        final UUID id = UUID.fromString(obj.get(Database.TimersInfo.ID).getAsString());
        return new Builder(id).
                setItem(new Identifier(item)).
                setModelData(modelData).
                setName(name).
                setFullTime(fullTime).
                setTimeLeft(timeleft).
                setServerInfo(serverInfo).
                setPosition(position).
                setPaused(paused).
                setPrevTime(System.currentTimeMillis()).
                build();
    }

    public static Builder newBuilder(UUID id)
    {
        return new Builder(id);
    }

    public static class Builder
    {
        private Identifier item;
        private int modelData;
        private String name;
        private long fullTime;
        private long timeLeft;
        private String serverInfo;
        private TimerPosition position;
        private boolean paused = true;
        private long prevTime;
        private final UUID id;

        public Builder(UUID id)
        {
            this.id = id;
        }

        public Builder setTimeLeft(long timeLeft) {
            this.timeLeft = timeLeft;
            return this;
        }

        public Builder setPaused(boolean paused) {
            this.paused = paused;
            return this;
        }

        public Builder setItem(Identifier item) {
            this.item = item;
            return this;
        }

        public Builder setModelData(int modelData) {
            this.modelData = modelData;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPosition(TimerPosition position) {
            this.position = position;
            return this;
        }

        public Builder setPrevTime(long prevTime) {
            this.prevTime = prevTime;
            return this;
        }

        public Builder setServerInfo(String serverInfo) {
            this.serverInfo = serverInfo;
            return this;
        }

        public Builder setFullTime(long fullTime) {
            this.fullTime = fullTime;
            return this;
        }

        public Timer build()
        {
            Timer timer = new Timer(this.fullTime, this.timeLeft, this.name, this.item, this.modelData, this.position, this.id, this.serverInfo);
            timer.setPaused(this.paused);
            timer.prevTime = this.prevTime;
            return timer;
        }
    }
}
