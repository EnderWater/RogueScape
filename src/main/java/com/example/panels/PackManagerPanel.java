package com.example.panels;

import com.example.cards.Card;
import com.example.cards.CardManager;
import com.example.cards.OverlayCard;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PackManagerPanel extends JPanel {
    private final CardManager cardManager;
    private boolean isPackOpen = false;
    private final JPanel selectionSection;
    private final JPanel buttonSection;
    private boolean isHeldCardViewOpen = false;

    @Inject
    PackManagerPanel(CardManager cardManager) {
        this.cardManager = cardManager;

        // Set the view to be a BoxLayout to add things in vertically
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.buttonSection = new JPanel();
        this.buttonSection.setLayout(new BorderLayout());

        // Create the UI to select a card when the pack is opened
        this.selectionSection = new JPanel();
        this.selectionSection.setLayout(new GridLayout(0, 1, 0, 8));

        JButton openPackButton = new JButton("Open pack");
        openPackButton.setPreferredSize(new Dimension(100, 20));
        openPackButton.addActionListener(e -> {
            if (this.cardManager.getAvailablePacks() > 0 && !this.cardManager.isPackOpen()) {
                this.cardManager.openPack();
//                openPackButton.setText("Close pack");
            }
            else if (this.cardManager.isPackOpen()) {
                this.cardManager.closePack();
//                openPackButton.setText("Open pack");
            }
        });

        JButton viewCardsButton = new JButton("Held cards");
        viewCardsButton.setPreferredSize(new Dimension(100, 20));
        viewCardsButton.addActionListener(e -> {
            if (!isHeldCardViewOpen) {
                List<Card> heldCards = this.cardManager.getHeldCards();
                if (!heldCards.isEmpty()) {
                    for (Card card : heldCards) {
                        JPanel cardRow = new JPanel(new BorderLayout(0, 8));

                        JButton openCardButton = new JButton("View card");
                        openCardButton.addActionListener(event -> {
                            this.cardManager.displaySingleCard(card);
                        });
                        JLabel cardLabel = new JLabel(card.getName());

                        cardRow.add(cardLabel, BorderLayout.CENTER);
                        cardRow.add(openCardButton, BorderLayout.EAST);

                        this.selectionSection.add(cardRow);
                    }
                    this.selectionSection.revalidate();
                    this.selectionSection.repaint();
                }
                this.isHeldCardViewOpen = true;
                viewCardsButton.setText("Close cards");
            }
            else {
                this.isHeldCardViewOpen = false;
                viewCardsButton.setText("Held cards");
                this.cardManager.closePack();
                this.selectionSection.removeAll();
                this.selectionSection.revalidate();
                this.selectionSection.repaint();
            }
        });

        this.buttonSection.add(openPackButton, BorderLayout.WEST);
        this.buttonSection.add(viewCardsButton, BorderLayout.EAST);

        add(Box.createVerticalStrut(8));
        add(this.buttonSection);
        add(Box.createVerticalStrut(16));
        add(this.selectionSection);
    }
}
