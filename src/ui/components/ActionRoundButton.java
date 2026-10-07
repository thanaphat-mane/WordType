package ui.components;

import javax.swing.*;
import java.awt.*;

public class ActionRoundButton extends JButton{
    public ActionRoundButton(String text) {                                                                        
        super(text);                                                                                               
        setContentAreaFilled(false);                                                                               
        setFocusPainted(false);                                                                                    
        setBorderPainted(false);                                                                                   
        setCursor(new Cursor(Cursor.HAND_CURSOR));                                                                 
        setFont(new Font("Roboto Mono", Font.BOLD, 15));                                                           
    }
     @Override                                                                                                      
    protected void paintComponent(Graphics g) {                                                                    
        Graphics2D g2 = (Graphics2D) g.create();                                                                   
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);                   
                                                                                                                       
        // เช็คสถานะตอนเมาส์กด (ไม่ค้าง)                                                                               
        if (getModel().isPressed()) {                                                                              
            g2.setColor(Color.decode("#BFC2C8")); // สีตอนกดให้ดูยุบลงไป                                               
        } else {                                                                                                   
            g2.setColor(Color.decode("#D1D3D8")); // สีปกติ                                                          
        }                                                                                                          
                                                                                                                       
        setForeground(Color.BLACK);                                                                                
                                                                                                                       
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);                                                   
        g2.dispose();                                                                                              
                                                                                                                       
        super.paintComponent(g);                                                                                   
    }    
}
