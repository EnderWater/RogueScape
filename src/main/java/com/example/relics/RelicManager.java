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

    @Inject
    private CardManager cardManager;
    @Inject
    private EventBus eventBus;
    private List<Relic> relics;

    public RelicManager() {
        loadRelics();
//        activateRelics();
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
