package com.example.panels;

import com.example.cards.CardManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import java.awt.*;

public class PackManagerPanel extends JPanel {
    private final CardManager cardManager;
    private boolean isPackOpen = false;

    @Inject
    PackManagerPanel(CardManager cardManager) {
        this.cardManager = cardManager;

        // Set the view to be a BorderLayout
        setLayout(new BorderLayout());

        JButton openPackButton = new JButton("Open pack");
        openPackButton.setPreferredSize(new Dimension(100, 20));
        openPackButton.addActionListener(e -> {
            if (!isPackOpen) {
                this.cardManager.openPack();
                this.isPackOpen = true;
                openPackButton.setText("Close Pack");
            }
            else {
                this.cardManager.closePack();
                this.isPackOpen = false;
                openPackButton.setText("Open Pack");
            }
        });

        JButton viewCardsButton = new JButton("View current cards");
        viewCardsButton.setPreferredSize(new Dimension(100, 20));
        viewCardsButton.addActionListener(e -> {

        });

        add(openPackButton, BorderLayout.WEST);
        add(viewCardsButton, BorderLayout.EAST);
    }
}
