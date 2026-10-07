package ui;
import javax.swing.JToggleButton;

import java.awt.*;

public class RoundButton extends JToggleButton{
    public  RoundButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(new Font("Roboto Mono", Font.BOLD, 15));
    }
    // ฟังชั่นนี้วาด "วาดขอบมน"
    @Override 
    protected void paintComponent(Graphics g) {
        Graphics2D g2 =(Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // ลบรอยหยักให้ภาพเนียน

        if(isSelected()) {
            g2.setColor(UiUtil.TEAL);
            setForeground(Color.WHITE);
        } else {
            g2.setColor(Color.decode("#D1D3D8"));
            setForeground(Color.BLACK);
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2.dispose();
        super.paintComponent(g);

    }
}
