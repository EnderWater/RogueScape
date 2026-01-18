package com.example.panels;

import com.example.cards.CardManager;

import javax.swing.*;
import java.awt.*;

public class PackManager extends JPanel {
    private final CardManager cardManager;

    PackManager(CardManager cardManager) {
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
