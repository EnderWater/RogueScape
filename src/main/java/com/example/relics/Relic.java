package com.example.relics;


import net.runelite.client.eventbus.EventBus;

public abstract class Relic
{
    protected final Relics relicType;
    protected final EventBus eventBus;

    protected Relic(EventBus eventBus, Relics relics)
    {
        this.eventBus = eventBus;
        this.relicType = relics;
    }

    public void activate()
    {
        eventBus.register(this);
    }

    public void deactivate()
    {
        eventBus.unregister(this);
    }
}
