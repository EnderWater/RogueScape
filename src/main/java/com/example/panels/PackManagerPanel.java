package com.example.panels;

import com.example.cards.CardManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import java.awt.*;

@Singleton
public class PackManagerPanel extends JPanel {
    private CardManager cardManager;

    @Inject
    PackManagerPanel(CardManager cardManager) {
        this.cardManager = cardManager;

        // Set the view to be a BorderLayout
        setLayout(new BorderLayout());

        JButton openPackButton = new JButton("Open pack");
        openPackButton.setPreferredSize(new Dimension(100, 20));
        openPackButton.addActionListener(e -> {
            this.cardManager.openPack();
        });

        JButton viewCardsButton = new JButton("View current cards");
        viewCardsButton.setPreferredSize(new Dimension(100, 20));
        viewCardsButton.addActionListener(e -> {

        });

        add(openPackButton, BorderLayout.WEST);
        add(viewCardsButton, BorderLayout.EAST);
    }
}
