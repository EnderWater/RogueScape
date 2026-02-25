package com.example.relics;

import com.example.cards.CardManager;
import com.example.JsonManager;
import com.google.common.reflect.TypeToken;
import net.runelite.client.eventbus.EventBus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
@Singleton
public class RelicManager {

    private final CardManager cardManager;
    private final JsonManager jsonManager;

    @Inject
    private EventBus eventBus;

    private List<Relic> relics;

    @Inject
    public RelicManager(CardManager cardManager, JsonManager jsonManager) {
        this.cardManager = cardManager;
        this.jsonManager = jsonManager;

        loadRelics();
//        activateRelics();
    }

    private void loadRelics() {
        this.relics = jsonManager.load("relics.json", new TypeToken<List<Relic>>(){}.getType());
    }

    private void activateRelics() {
        this.relics.forEach(Relic::activate);
    }

    private void deactivateRelics() {
        this.relics.forEach(Relic::deactivate);
    }

    public void addRelic(Relics relics) {
        Relic relic = this.getRelicById(relics);
        if (relic != null)
            this.relics.add(relic);
    }

    public Relic getRelicById(Relics relics) {
        Relic relic = null;

        switch (relics) {
            case AnthologyOfProficiency:
                relic = new AnthologyOfProficiency(this.eventBus);
                break;
        }
        return relic;
    }
}
