package com.example.relics;


import net.runelite.client.eventbus.EventBus;

public abstract class Relic
{
    protected final String relicType;
    protected final EventBus eventBus;
//    protected final String name;

    protected Relic(EventBus eventBus, String relicType)
    {
        this.eventBus = eventBus;
        this.relicType = relicType;
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
