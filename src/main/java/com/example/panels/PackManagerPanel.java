package com.example.panels;

import com.example.cards.Card;
import com.example.cards.CardManager;
import com.example.cards.OverlayCard;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.IconTextField;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

public class PackManagerPanel extends JPanel {
    private final CardManager cardManager;
    private boolean isPackOpen = false;
    private final JPanel selectionSection;
    private final JPanel heldCardsSection;
    private final JPanel buttonSection;
    private final JButton viewCardsButton;
    private final Timer filterTimer;
    private final IconTextField searchBar;
    private static final int FILTER_DELAY = 250; // in milliseconds
    private boolean isHeldCardViewOpen = false;

    @Inject
    PackManagerPanel(CardManager cardManager) {
        this.cardManager = cardManager;

        // Set the view to be a BoxLayout to add things in vertically
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.buttonSection = new JPanel();
        this.buttonSection.setLayout(new BorderLayout());

        this.heldCardsSection = new JPanel();
        this.heldCardsSection.setLayout(new GridLayout(0, 1, 0, 8));

        // Create the UI to select a card when the pack is opened
        this.selectionSection = new JPanel();
        this.selectionSection.setLayout(new BoxLayout(this.selectionSection, BoxLayout.Y_AXIS));

        this.selectionSection.add(heldCardsSection);

        JButton openPackButton = new JButton("Open pack");
        openPackButton.setPreferredSize(new Dimension(100, 20));
        openPackButton.addActionListener(e -> {
            if (this.cardManager.getAvailablePacks() > 0 && !this.cardManager.isPackOpen()) {
                this.cardManager.openPack();
            }
            else if (this.cardManager.isPackOpen()) {
                this.cardManager.closePack();
            }
        });

        searchBar = new IconTextField();
        searchBar.setIcon(IconTextField.Icon.SEARCH);
        searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        searchBar.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                filterTimer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                filterTimer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                filterTimer.restart();
            }
        });
        filterTimer = new Timer(FILTER_DELAY, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                filter();
                filterTimer.stop();
            }
        });

        this.viewCardsButton = new JButton("Held cards");
        viewCardsButton.setPreferredSize(new Dimension(100, 20));
        viewCardsButton.addActionListener(e -> {
            this.buildHeldCardsSection();
        });

        this.buttonSection.add(openPackButton, BorderLayout.WEST);
        this.buttonSection.add(viewCardsButton, BorderLayout.EAST);

        add(Box.createVerticalStrut(8));
        add(this.buttonSection);
        add(Box.createVerticalStrut(16));
        add(this.selectionSection);
    }

    private void buildHeldCardsSection() {
        this.selectionSection.removeAll();

        if (!isHeldCardViewOpen) {
            List<Card> heldCards = this.cardManager.getHeldCards();

            this.selectionSection.add(this.searchBar);
            this.selectionSection.add(Box.createVerticalStrut(8));

            this.renderCardList(heldCards);
        }
        else {
            this.isHeldCardViewOpen = false;
            viewCardsButton.setText("Held cards");
            this.cardManager.closePack();
        }

        this.selectionSection.revalidate();
        this.selectionSection.repaint();
    }

    private void renderCardList(List<Card> heldCards) {
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

                this.heldCardsSection.add(cardRow);
            }
            this.selectionSection.add(this.heldCardsSection);
        }
        this.isHeldCardViewOpen = true;
        viewCardsButton.setText("Close cards");
    }

    void filter()
    {
        this.heldCardsSection.removeAll();
        List<Card> heldCards = this.cardManager.getHeldCards();

        String query = searchBar.getText().toLowerCase();
        List<Card> searchedCards = heldCards.stream().filter(card -> card.getName().toLowerCase().contains(query)).collect(Collectors.toList());

        this.renderCardList(searchedCards);

        this.heldCardsSection.revalidate();
        this.heldCardsSection.repaint();
    }
}
