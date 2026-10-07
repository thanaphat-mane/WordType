package ui.pages;

import java.awt.*;
import javax.swing.*;

import ui.UiUtil;
import ui.components.ActionRoundButton;
import ui.components.HeaderBar;
import ui.components.ModeSelector;
import ui.components.ScoreRow;

public class Leaderboard  extends JPanel {
    public Leaderboard() {
        setLayout(null);
        setBackground(UiUtil.BG_COLOR);

        // 1. HeaderBar                                                                                     
        HeaderBar header = new HeaderBar();                                                                             
        add(header); 

        ModeSelector modeSelector = new ModeSelector(); 
        modeSelector.setBounds(342,125,486,38); 
        add(modeSelector);

        // หัวตางราง table Header
        int headerY = 180;
        Color headeColor = Color.decode("#8E9094");
        Font headerFont = new Font("Roboto Mono", Font.PLAIN, 13);

        JLabel lblRank = new JLabel("rank");
        lblRank.setFont(headerFont);
        lblRank.setForeground(headeColor);
        lblRank.setBounds(185, headerY, 32, 17);
        add(lblRank);

        JLabel lblPlayer = new JLabel("player");
        lblPlayer.setFont(headerFont);
        lblPlayer.setForeground(headeColor);
        lblPlayer.setBounds(290, headerY, 50, 17);
        add(lblPlayer);

        JLabel lblWpm = new JLabel("wpm");
        lblWpm.setFont(headerFont);
        lblWpm.setForeground(headeColor);
        lblWpm.setBounds(620, headerY, 24, 17);
        add(lblWpm);

        JLabel lblAcc = new JLabel("acc");
        lblAcc.setFont(headerFont);
        lblAcc.setForeground(headeColor);
        lblAcc.setBounds(770, headerY, 24, 17);
        add(lblAcc);

        JLabel lblDate = new JLabel("date");
        lblDate.setFont(headerFont);
        lblDate.setForeground(headeColor);
        lblDate.setBounds(920, headerY, 32, 17);
        add(lblDate);

        // Score Rows โซนอันดับ
        int startY = 200;
        int rowHeight = 46;
        int gap = 8;
        int rowWidth = 900;
        int startX = 150;

        ScoreRow row1 = new ScoreRow("#1", "kukps_dev", "98", "99%", "27 Sep 26", false);  
        row1.setBounds(startX, startY, rowWidth, rowHeight);
        add(row1);

        ScoreRow row2 = new ScoreRow("#2", "javamaster", "91", "98%", "27 Sep 26", false);                                                                       
        row2.setBounds(startX, startY + (rowHeight + gap) * 1, rowWidth, rowHeight);                                                                             
        add(row2);                                                                                                                                               
                                                                                                                                                                     
        ScoreRow row3 = new ScoreRow("#3", "typing_pro", "84", "97%", "26 Sep 26", false);                                                                       
        row3.setBounds(startX, startY + (rowHeight + gap) * 2, rowWidth, rowHeight);                                                                             
        add(row3);                                                                                                                                               
                                                                                                                                                                     
        ScoreRow row4 = new ScoreRow("#4", "user01 (you)", "72", "96%", "26 Sep 26", false);                                                                     
        row4.setBounds(startX, startY + (rowHeight + gap) * 3, rowWidth, rowHeight);                                                                             
        add(row4);                                                                                                                                               
                                                                                                                                                                     
        ScoreRow row5 = new ScoreRow("#5", "rookie_09", "68", "94%", "25 Sep 26", false);                                                                        
        row5.setBounds(startX, startY + (rowHeight + gap) * 4, rowWidth, rowHeight);                                                                             
        add(row5);                                                                                                                                               
                                                                                                                                                                     
        ScoreRow bestRow = new ScoreRow("#4", "user01 (your best)", "72", "96%", "26 Sep 26", true);                                                             
        bestRow.setBounds(startX, startY + (rowHeight + gap) * 5 + 15, rowWidth, rowHeight);                                                                     
        add(bestRow); 
        
        // เปลี่ยนหน้า
        ActionRoundButton btnPrev = new ActionRoundButton("<");
        btnPrev.setBounds(518, 550, 32, 32);
        add(btnPrev);

        JLabel lblPageNum = new JLabel("1 / 200");
        lblPageNum.setFont(new Font("Roboto Mono", Font.BOLD, 16));
        lblPageNum.setForeground(Color.BLACK);
        lblPageNum.setBounds(565,556,70,21);
        add(lblPageNum);

        ActionRoundButton btnNext = new ActionRoundButton(">");
        btnNext.setBounds(650, 550, 32, 32);
        add(btnNext);

        // --- [ ESC ] - back to home  ---
        JLabel lblShortcut = new JLabel("<html><font color='#006664'>[ ESC ]</font> <font color='#000000'>- back to home</font></html>");
        lblShortcut.setFont(new Font("Menlo", Font.PLAIN, 14));
            
        // ตั้งพิกัด X ให้อยู่ตรงกลางจอ (ใต้ปุ่ม Restart พอดี)
        lblShortcut.setBounds(508, 610, 184, 18);
        lblShortcut.setHorizontalAlignment(SwingConstants.CENTER); // สั่งจัดข้อความให้อยู่กึ่งกลาง
        add(lblShortcut);
    }

}
