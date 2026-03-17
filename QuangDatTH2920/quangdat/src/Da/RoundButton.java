package da;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundButton extends JButton {
    private Color backgroundColor;

    public RoundButton(String label, Color color) {
        super(label);
        this.backgroundColor = color;
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (getModel().isPressed()) g2.setColor(backgroundColor.darker());
        else if (getModel().isRollover()) g2.setColor(backgroundColor.brighter());
        else g2.setColor(backgroundColor);

        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
        
        FontMetrics fm = g2.getFontMetrics();
        Rectangle r = fm.getStringBounds(getText(), g2).getBounds();
        int x = (getWidth() - r.width) / 2;
        int y = (getHeight() - r.height) / 2 + fm.getAscent();
        
        g2.setColor(getForeground());
        g2.drawString(getText(), x, y);
        g2.dispose();
    }
}