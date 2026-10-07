package ui.components;

import java.awt.*;
import javax.swing.*;

import ui.UiUtil;

public class ScoreRow extends JPanel{
    private boolean isHighlight;
    private boolean isEvenRow; // ตัวแปรเก็บว่าเป็นแถวคู่ไหม 

    public  ScoreRow(String rank, String player, String wpm, String acc, String date, boolean isHighlight) {
        this.isHighlight = isHighlight;

        try {
            int rankNum = Integer.parseInt(rank.replace("#", ""));
            this.isEvenRow = (rankNum % 2 == 0);
        } catch (Exception e) {
            this.isEvenRow = false;
        }
        setLayout(null);
        setOpaque(false);

        Color textColor = isHighlight ? Color.WHITE : Color.decode("#4B4D54");                                                                                   
        Font font = new Font("Roboto Mono", Font.PLAIN, 18);

        if (rank.equals("#1") || rank.equals("1")) {                                                                                                             
            JLabel lblCrown = new JLabel(UiUtil.loadIcon("LeaderIcon_ON.png"));                                                                                  
            lblCrown.setBounds(35, 13, 20, 20);                                                                                                                  
            add(lblCrown);                                                                                                                                       
        } else {                                                                                                                                                 
            JLabel lblRank = new JLabel(rank);                                                                                                                   
            lblRank.setFont(font);                                                                                                                               
            lblRank.setForeground(textColor);                                                                                                                    
            lblRank.setBounds(35, 13, 60, 20);                                                                                                                   
            add(lblRank);                                                                                                                                        
        }
        JLabel lblPlayer = new JLabel(player);                                                                                                                   
        lblPlayer.setFont(font);                                                                                                                                 
        lblPlayer.setForeground(textColor);                                                                                                                      
        lblPlayer.setBounds(140, 13, 250, 20);                                                                                                                   
        add(lblPlayer);

        JLabel lblWpm = new JLabel(wpm);                                                                                                                         
        lblWpm.setFont(new Font("Roboto Mono", Font.BOLD, 18));                                                                                                  
        if (isHighlight) {
            lblWpm.setForeground(Color.WHITE);
        } else if (rank.equals("#1") || rank.equals("1")) {
            lblWpm.setForeground(Color.decode("#006664"));
        } else {
            lblWpm.setForeground(textColor);
        }
        lblWpm.setBounds(470, 13, 60, 20);                                                                                                                       
        add(lblWpm);                                                                                                                                             
                                                                                                                                                                     
        JLabel lblAcc = new JLabel(acc);                                                                                                                         
        lblAcc.setFont(font);                                                                                                                                    
        lblAcc.setForeground(textColor);                                                                                                                         
        lblAcc.setBounds(620, 13, 60, 20);                                                                                                                       
        add(lblAcc);   

                                                                                                                                                                 
        JLabel lblDate = new JLabel(date);                                                                                                                       
        lblDate.setFont(new Font("Roboto Mono", Font.PLAIN, 14));                                                                                                
        lblDate.setForeground(isHighlight ? Color.WHITE : Color.decode("#8E9094"));                                                                              
        lblDate.setBounds(770, 14, 150, 20);                                                                                                                     
        add(lblDate);  
    }
    @Override 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isHighlight) {
            g2.setColor(Color.decode("#006664"));
        } else {
            if (isEvenRow) {
                g2.setColor(Color.decode("#D8D9DD"));
            } else {
                g2.setColor(Color.decode("#C5C7CC"));
            }
        }
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2.dispose();
    }
}
