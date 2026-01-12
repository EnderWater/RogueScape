package com.example.panels;

import javax.swing.*;
import java.awt.*;

public class CollapsiblePanel extends JPanel
{
    private final JPanel contentPanel;
    private boolean isExpanded = true;

    public CollapsiblePanel(JPanel child, String title)
    {
        // Give this panel a Box layout so all Panels are stacked vertically
//        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setLayout(new BorderLayout());

        // Create the section title panel
        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Create the section title
        JLabel sectionTitle = new JLabel(title);

        // Create the button
        JButton collapseButton = new JButton("▼");
        collapseButton.addActionListener((e) -> {
            this.setExpanded(!this.isExpanded);

            if (this.isExpanded) {
                collapseButton.setText("▼");
            } else {
                collapseButton.setText("▶");
            }

            revalidate();
            repaint();
        });

        child.setAlignmentX(Component.LEFT_ALIGNMENT);
        child.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        header.add(sectionTitle, BorderLayout.WEST);
        header.add(collapseButton, BorderLayout.EAST);
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
}
