package ui.pages;

import java.awt.*;
import javax.swing.*;

import ui.UiUtil;
import ui.components.HeaderBar;
import ui.components.ModeSelector;
import ui.components.TypingView;

public class Home extends JPanel {

    public Home() {
        // 1. ปิด Layout เพื่อให้ใช้แกน X, Y ได้
        setLayout(null);
        setBackground(UiUtil.BG_COLOR);

        // 2. เรียกใช้ "เลโก้ส่วนหัว" (HeaderBar) มาแปะที่หน้าจอนี้!
        HeaderBar header = new HeaderBar();
        add(header);

        ModeSelector modeSelector = new ModeSelector(); 
        modeSelector.setBounds(342,125,486,38); 
        add(modeSelector);

        // --- เรียกใช้ลานพิมพ์ดีดจากคลาส TypingView ---
        TypingView typingView = new TypingView();
        typingView.setBounds(150, 280, 900, 160); 
        add(typingView);


        // -- Restart button --
        JLabel btnRestart = new JLabel();
        btnRestart.setIcon(UiUtil.loadIcon("btnRestart.png"));
        btnRestart.setBounds(588, 465, 24, 24);
        btnRestart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(btnRestart);

        // --- [ ESC ] - restart test  ---
        JLabel lblShortcut = new JLabel("<html><font color='#006664'>[ ESC ]</font> <font color='#000000'>- restart test</font></html>");
        lblShortcut.setFont(new Font("Menlo", Font.BOLD, 14));
            
        // ตั้งพิกัด X ให้อยู่ตรงกลางจอ (ใต้ปุ่ม Restart พอดี)
        lblShortcut.setBounds(508, 610, 184, 18);
        lblShortcut.setHorizontalAlignment(SwingConstants.CENTER); // สั่งจัดข้อความให้อยู่กึ่งกลาง
        add(lblShortcut);

    }
}
