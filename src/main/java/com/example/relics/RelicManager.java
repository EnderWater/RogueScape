package com.example.relics;

import com.example.cards.CardManager;
import com.example.cards.JsonManager;
import com.google.common.reflect.TypeToken;
import net.runelite.client.eventbus.EventBus;

import javax.inject.Singleton;
import java.io.Console;
import java.util.List;
@Singleton
public class RelicManager {

    private final CardManager cardManager;
    private final EventBus eventBus;
    private List<Relic> relics;

    public RelicManager(CardManager cardManager, EventBus eventBus) {
        this.cardManager = cardManager;
        this.eventBus = eventBus;
        loadRelics();
        activateRelics();
        //
    }

    private void loadRelics() {
        this.relics = JsonManager.load("relics.json", new TypeToken<List<Relic>>(){}.getType());
    }

    private void activateRelics() {
        this.relics.forEach(Relic::activate);
    }

    private void deactivateRelics() {
        this.relics.forEach(Relic::deactivate);
    }

    public void addRelic(RelicsEnum relicsEnum) {
        Relic relic = this.getRelicById(relicsEnum);
        if (relic != null)
            this.relics.add(relic);
    }

    public Relic getRelicById(RelicsEnum relicsEnum) {
        Relic relic = null;

        switch (relicsEnum) {
            case AnthologyOfProficiency:
                relic = new AnthologyOfProficiency(this.eventBus);
                break;
        }
        return relic;
    }
}
