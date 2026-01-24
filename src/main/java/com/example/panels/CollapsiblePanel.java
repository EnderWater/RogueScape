package com.example.panels;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;

public class CollapsiblePanel extends JPanel
{
    private final JPanel contentPanel;
    private final JLabel arrowPanel;
    private boolean isExpanded = true;

    public CollapsiblePanel(@Nonnull JPanel child, String title)
    {
        // Give this panel a Box layout so all Panels are stacked vertically
//        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setLayout(new BorderLayout());

        // Create the section title panel
        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Create the section title
        arrowPanel = new JLabel(title);

        // Create the button
        JButton collapseButton = new JButton(title);
        collapseButton.addActionListener((e) -> {
            this.setExpanded(!this.isExpanded);

            setArrowText();

            revalidate();
            repaint();
        });

        child.setAlignmentX(Component.LEFT_ALIGNMENT);
        child.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        setArrowText();

        header.add(arrowPanel, BorderLayout.EAST);
        header.add(collapseButton, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
        add(child);

        this.contentPanel = child;
    }

    public JPanel getContent()
    {
        return contentPanel;
    }

    public void setExpanded(boolean expanded)
    {
        this.isExpanded = expanded;
        contentPanel.setVisible(this.isExpanded);
    }

    private void setArrowText() {
        if (this.isExpanded) {
            arrowPanel.setText("▼");
        } else {
            arrowPanel.setText("▶");
        }
    }
}
