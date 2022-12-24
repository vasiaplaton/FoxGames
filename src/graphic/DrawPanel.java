package graphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import geometry.Vec2;

public class DrawPanel extends JPanel {
    private final BoardGraphics boardGraphics;

    public DrawPanel() {
        boardGraphics = new BoardGraphics();
        setFocusable(true);
        requestFocus();
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                boardGraphics.click(new Vec2(e.getX(), e.getY()));
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D canvas = (Graphics2D) g;
        boardGraphics.draw(canvas, getWidth(), getHeight());
    }
}
