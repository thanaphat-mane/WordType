package ui.components;

import javax.swing.*;

import ui.MainFrame;
import ui.UiUtil;

import java.awt.*;
import java.awt.event.*;;

/**
 * เลโก้ชิ้นส่วนหัว (Header Bar)
 * ประกอบไปด้วย โลโก้, ปุ่ม Leaderboard, ปุ่ม Login
 * หน้าไหนอยากมีเมนูด้านบน ก็แค่ add(new HeaderBar()) ได้เลย!
 */
public class HeaderBar extends JPanel {

    // สร้างปุ่มและข้อความเตรียมไว้
    private final JLabel lblLogo = new JLabel("<html><font color='black'>>_word</font><font color='#00675B'>type</font></html>");
    private final JButton btnLeader = new JButton();
    private final JButton btnUser = new JButton("Login");

    public HeaderBar() {
        // 1. ปิด Layout อัตโนมัติ เพื่อตั้งพิกัดอิสระ (Absolute Layout)
        setLayout(null);
        setOpaque(false); // ให้พื้นหลังโปร่งใส กลืนไปกับหน้าหลัก
        setBounds(0, 0, 1200, 100); // ขนาดของแถบ Header คือกว้าง 1200 สูง 100

        // 2. ตั้งค่าโลโก้ ">_wordtype" ใช้โค้ด HTML แบ่งสีในประโยค
        lblLogo.setFont(new Font("Space Grotesk Medium", Font.PLAIN, 32));

        // กำหนดพิกัดครั้งเดียว (ตั้งความกว้างเผื่อไว้ 300 เลย จะได้ไม่โดนตัด)
        lblLogo.setBounds(150, 36, 191, 41);
        add(lblLogo);

        // 4. ตั้งค่าปุ่ม Leaderboard (รูปมงกุฎ)
        btnLeader.setIcon(UiUtil.loadIcon("LeaderIcon_OFF.png"));
        // ตั้งค่าให้ปุ่มล่องหน (ไม่มีกรอบ ไม่มีพื้นหลัง) เห็นแค่รูป
        btnLeader.setBorderPainted(false);
        btnLeader.setContentAreaFilled(false);
        btnLeader.setFocusPainted(false);
        btnLeader.setCursor(new Cursor(Cursor.HAND_CURSOR)); // เมาส์ชี้แล้วเป็นรูปมือ
        btnLeader.setRolloverIcon(UiUtil.loadIcon("LeaderIcon_ON.png")); // เมาส์ชี้เป็นสีเขียว
        btnLeader.setBounds(923, 52, 18, 19);
        add(btnLeader);

        // 5. ตั้งค่าปุ่ม User (รููปคน)
        btnUser.setFont(new Font("Space Grotesk Medium", Font.PLAIN, 16));
        btnUser.setForeground(Color.decode("#8E9094"));

        btnUser.setIcon(UiUtil.loadIcon("userIcon.png"));
        btnUser.setRolloverIcon(UiUtil.loadIcon("userIcon_hover.png"));

        btnUser.setIconTextGap(8); // icon ห่างจาก login 8px
        btnUser.setBorderPainted(false);
        btnUser.setContentAreaFilled(false);
        btnUser.setFocusPainted(false);

        btnUser.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUser.setBounds(965, 48, 120, 28);
        add(btnUser);

        btnUser.addMouseListener(new MouseAdapter() {
            public  void mouseEntered(MouseEvent evt) {
                btnUser.setForeground(Color.black);
            }
            public  void mouseExited(MouseEvent evt) {
                btnUser.setForeground(Color.decode("#8E9094"));
            }
        });

        // กดเข้าหน้า Leaderboard
        btnLeader.addActionListener(e -> {
            MainFrame main = (MainFrame) SwingUtilities.getWindowAncestor(this);
            if (main != null) main.showLeaderboard();
        });

        // กดเข้าหน้า Login
        btnUser.addActionListener(e -> {
            MainFrame main = (MainFrame) SwingUtilities.getWindowAncestor(this);
            if (main != null) main.showLogin();
        });

        // กด logo กลับหน้า home
        lblLogo.setCursor(new Cursor(Cursor.HAND_CURSOR)); // ทำให้เมาส์เป็นรูปมือเวลาชี้โลโก้
        lblLogo.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { 
                MainFrame main = (MainFrame) SwingUtilities.getWindowAncestor(HeaderBar.this);
                if (main != null ) main.showHome(); 
            }
        });
    }
}

