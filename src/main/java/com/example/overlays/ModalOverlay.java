package com.example.overlays;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Singleton
public class ModalOverlay extends Overlay {
    @Inject
    private Client client;

    private final Rectangle modalBounds = new Rectangle();
    private final Rectangle inputBounds = new Rectangle();
    private final List<Button> buttons = new ArrayList<>();

    @Getter
    private boolean visible = false;

    private String message = "";

    private Consumer<ModalResult> onResult;
    private Consumer<String> onInputSubmit;

    private String inputText = "";
    private boolean inputFocused = false;

    private ModalMode mode = ModalMode.CONFIRM;

    private static final int WIDTH = 320;
    private static final int HEIGHT = 160;

    private static final int BUTTON_HEIGHT = 26;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_SPACING = 10;

    public enum ModalMode {
        CONFIRM,
        INPUT
    }

    public enum ModalResult {
        NONE,
        CONFIRM,
        CANCEL,
        CLOSE
    }

    @Getter
    private ModalResult result = ModalResult.NONE;

    @Inject
    public ModalOverlay() {
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D g) {
        if (!visible) return null;

        int canvasWidth = client.getCanvasWidth();
        int canvasHeight = client.getCanvasHeight();

        int x = (canvasWidth - WIDTH) / 2;
        int y = (canvasHeight - HEIGHT) / 2;

        modalBounds.setBounds(x, y, WIDTH, HEIGHT);

        drawBackdrop(g);
        drawModal(g, x, y);
        drawMessage(g, x, y);

        if (mode == ModalMode.INPUT) {
            drawInput(g, x, y);
        }

        drawButtons(g, x, y);

        return null;
    }

    private void drawBackdrop(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, client.getCanvasWidth(), client.getCanvasHeight());
    }

    private void drawModal(Graphics2D g, int x, int y) {
        g.setColor(new Color(30, 30, 30, 240));
        g.fillRoundRect(x, y, WIDTH, HEIGHT, 10, 10);

        g.setColor(Color.GRAY);
        g.drawRoundRect(x, y, WIDTH, HEIGHT, 10, 10);
    }

    private void drawMessage(Graphics2D g, int x, int y) {

        g.setColor(Color.WHITE);

        FontMetrics metrics = g.getFontMetrics();

        int maxTextWidth = WIDTH - 20; // padding
        int cursorY = y + 40;

        String[] words = message.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {

            String test = line + word + " ";

            if (metrics.stringWidth(test) > maxTextWidth) {

                String lineStr = line.toString().trim();
                int lineWidth = metrics.stringWidth(lineStr);

                int textX = x + (WIDTH - lineWidth) / 2;

                g.drawString(lineStr, textX, cursorY);

                line = new StringBuilder(word + " ");
                cursorY += metrics.getHeight();

            } else {
                line.append(word).append(" ");
            }
        }

        if (line.length() > 0) {

            String lineStr = line.toString().trim();
            int lineWidth = metrics.stringWidth(lineStr);

            int textX = x + (WIDTH - lineWidth) / 2;

            g.drawString(lineStr, textX, cursorY);
        }
    }

    private void drawInput(Graphics2D g, int x, int y) {
        int width = WIDTH - 40;
        int height = 26;

        int ix = x + 20;
        int iy = y + 60;

        inputBounds.setBounds(ix, iy, width, height);

        g.setColor(new Color(50, 50, 50));
        g.fillRect(ix, iy, width, height);

        g.setColor(inputFocused ? Color.WHITE : Color.GRAY);
        g.drawRect(ix, iy, width, height);

        String display = inputText.isEmpty() && !inputFocused
                ? "Enter text..."
                : inputText;

        g.setColor(Color.WHITE);
        g.drawString(display, ix + 6, iy + 18);
    }

    private void drawButtons(Graphics2D g, int x, int y) {
        buttons.clear();

        int totalWidth = (BUTTON_WIDTH * 2) + BUTTON_SPACING;
        int startX = x + (WIDTH - totalWidth) / 2;
        int buttonY = y + HEIGHT - 40;

        Rectangle confirmRect = new Rectangle(startX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        Rectangle cancelRect = new Rectangle(startX + BUTTON_WIDTH + BUTTON_SPACING, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);

        drawButton(g, confirmRect, "Confirm");
        drawButton(g, cancelRect, "Cancel");

        buttons.add(new Button(confirmRect, ModalResult.CONFIRM));
        buttons.add(new Button(cancelRect, ModalResult.CANCEL));
    }

    private void drawButton(Graphics2D g, Rectangle rect, String text) {
        g.setColor(new Color(60, 60, 60));
        g.fillRect(rect.x, rect.y, rect.width, rect.height);

        g.setColor(Color.GRAY);
        g.drawRect(rect.x, rect.y, rect.width, rect.height);

        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);

        int textX = rect.x + (rect.width - textWidth) / 2;
        int textY = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();

        g.setColor(Color.WHITE);
        g.drawString(text, textX, textY);
    }

    public boolean handleClick(MouseEvent event) {
        if (!visible || event.isConsumed()) return false;

        Point p = event.getPoint();

        if (!modalBounds.contains(p)) {
            close(ModalResult.CLOSE);
            return true;
        }

        if (mode == ModalMode.INPUT) {
            if (inputBounds.contains(p)) {
                inputFocused = true;
                return true;
            } else {
                inputFocused = false;
            }
        }

        for (Button button : buttons) {
            if (button.bounds.contains(p)) {
                if (mode == ModalMode.INPUT && button.result == ModalResult.CONFIRM) {
                    if (onInputSubmit != null) {
                        onInputSubmit.accept(inputText);
                    }
                }

                close(button.result);
                return true;
            }
        }

        return true;
    }

    public void showConfirm(String message, Consumer<ModalResult> onResult) {
        this.mode = ModalMode.CONFIRM;
        this.message = message;
        this.visible = true;
        this.result = ModalResult.NONE;
        this.onResult = onResult;

        this.inputText = "";
        this.inputFocused = false;
    }

    public void showInput(String message, Consumer<String> onSubmit) {
        this.mode = ModalMode.INPUT;
        this.message = message;
        this.visible = true;

        this.inputText = "";
        this.inputFocused = true;

        this.onInputSubmit = onSubmit;
    }

    public void hide() {
        this.visible = false;
    }

    private void close(ModalResult result) {
        this.result = result;
        this.visible = false;

        if (onResult != null) {
            onResult.accept(result);
            onResult = null;
        }

        onInputSubmit = null;
    }

    private static class Button {
        Rectangle bounds;
        ModalResult result;

        Button(Rectangle bounds, ModalResult result) {
            this.bounds = bounds;
            this.result = result;
        }
    }

    public void onKeyTyped(char c) {
        if (!visible || mode != ModalMode.INPUT || !inputFocused) return;

        if (Character.isLetterOrDigit(c) || Character.isSpaceChar(c)) {
            inputText += c;
        }
    }

    public void onKeyPressed(int keyCode) {
        if (!visible || mode != ModalMode.INPUT || !inputFocused) return;

        if (keyCode == java.awt.event.KeyEvent.VK_BACK_SPACE && !inputText.isEmpty()) {
            inputText = inputText.substring(0, inputText.length() - 1);
        }

        if (keyCode == KeyEvent.VK_ENTER) {
            if (onInputSubmit != null) {
                onInputSubmit.accept(inputText);
            }
            close(result);
        }
    }
}