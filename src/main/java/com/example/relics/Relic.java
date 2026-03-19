package com.example.relics;


import com.example.overlays.OverlayItem;
import lombok.Getter;
import net.runelite.client.eventbus.EventBus;

public abstract class Relic implements OverlayItem
{
    @Getter
    private final String name;
    protected final Relics relicType;
    protected final EventBus eventBus;

    protected Relic(String name, EventBus eventBus, Relics relics)
    {
        this.name = name;
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

    @Override
    public String getSearchableString() {
        return getName();
    }
}
