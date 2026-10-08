package ui.components;

import javax.swing.*;

import ui.MainFrame;
import ui.UiUtil;

import java.awt.*;
import java.awt.event.*;

/**
 * เลโก้ชิ้นส่วนหัว (Header Bar) ของโปรแกรม
 * ทำหน้าที่เป็น Navigation bar ด้านบนสุดของหน้าจอ
 * ประกอบไปด้วย โลโก้ (คลิกเพื่อกลับหน้าโฮม), ปุ่ม Leaderboard (รูปรถเข็น/มงกุฎ), ปุ่ม Login (หน้าผู้ใช้)
 * หน้าไหนอยากมีเมนูด้านบน ก็แค่ add(new HeaderBar()) ได้เลย!
 */
public class HeaderBar extends JPanel {

    // --- การประกาศองค์ประกอบ UI พื้นฐาน (Components) ---
    // สร้าง JLabel สำหรับแสดงโลโก้ของโปรแกรม ใช้โค้ด HTML เพื่อแยกสีของข้อความ ">_word" เป็นสีดำ และ "type" เป็นสีเขียว (#00675B)
    private final JLabel lblLogo = new JLabel("<html><font color='black'>>_word</font><font color='#00675B'>type</font></html>");
    
    // สร้าง JButton สำหรับปุ่ม Leaderboard (แสดงผลจัดอันดับคะแนน)
    private final JButton btnLeader = new JButton();
    
    // สร้าง JButton สำหรับปุ่มบัญชีผู้ใช้ (ใช้แสดงปุ่ม Login หรือชื่อผู้ใช้)
    private final JButton btnUser = new JButton("Login");

    /**
     * คอนสตรักเตอร์สำหรับสร้างและกำหนดค่าการแสดงผลของแถบ Header 
     * รวมไปถึงการจัดวางพิกัด การจัดรูปแบบ (Styling) และการเพิ่ม Event Listeners สำหรับการโต้ตอบ
     */
    public HeaderBar() {
        // --- 1. การตั้งค่าโครงสร้างหลักของ HeaderBar ---
        // ปิดการใช้ Layout Manager อัตโนมัติ (เช่น BorderLayout, FlowLayout) 
        // เพื่อใช้การวางตำแหน่งแบบระบุพิกัด X, Y อิสระ (Absolute Layout)
        setLayout(null);
        
        // ทำให้พื้นหลังของ HeaderBar โปร่งใส (Transparent) เพื่อให้กลืนไปกับสีพื้นหลังของหน้าจอหลัก
        setOpaque(false); 
        
        // กำหนดขนาดและตำแหน่งเริ่มต้นของ HeaderBar
        // กว้าง 1200 พิกเซล (เต็มความกว้างจอ) และสูง 100 พิกเซล วางอยู่ที่ตำแหน่ง (0, 0)
        setBounds(0, 0, 1200, 100); 

        // --- 2. การตั้งค่าและการจัดวาง: โลโก้ (Logo) ---
        // ตั้งค่าฟอนต์ของโลโก้เป็น "Space Grotesk Medium" ขนาด 32
        lblLogo.setFont(new Font("Space Grotesk Medium", Font.PLAIN, 32));
        
        // กำหนดพิกัดและขนาดของปุ่มโลโก้: แกน X=150, Y=36, กว้าง=191, สูง=41
        // ตั้งความกว้างเผื่อไว้เยอะๆ เพื่อป้องกันปัญหาข้อความถูกตัดเมื่อเปลี่ยนฟอนต์
        lblLogo.setBounds(150, 36, 191, 41);
        
        // เปลี่ยนเคอร์เซอร์ของเมาส์ให้เป็นรูปมือ (Hand cursor) เพื่อสื่อให้ผู้ใช้รู้ว่า "คลิกได้"
        lblLogo.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        
        // เพิ่มโลโก้เข้าสู่ HeaderBar Panel
        add(lblLogo);

        // --- 3. การตั้งค่าและการจัดวาง: ปุ่ม Leaderboard ---
        // โหลดรูปภาพไอคอนจากคลาส UiUtil มาใส่เป็นไอคอนหลัก (สถานะปกติ)
        btnLeader.setIcon(UiUtil.loadIcon("LeaderIcon_OFF.png"));
        
        // ปิดการวาดเส้นขอบของปุ่ม
        btnLeader.setBorderPainted(false);
        
        // ปิดการวาดพื้นหลังของปุ่ม (ปุ่มจะกลายเป็นแบบโปร่งใส เห็นแต่ไอคอน)
        btnLeader.setContentAreaFilled(false);
        
        // ปิดกรอบเส้นประเวลาปุ่มได้รับ Focus
        btnLeader.setFocusPainted(false);
        
        // เปลี่ยนเคอร์เซอร์เมื่อชี้เป็นรูปมือ
        btnLeader.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // ตั้งค่าไอคอนสำหรับสถานะ Rollover (เมื่อนำเมาส์ไปชี้) จะเปลี่ยนเป็นสีเขียว เพื่อให้เกิด Effect โต้ตอบ
        btnLeader.setRolloverIcon(UiUtil.loadIcon("LeaderIcon_ON.png")); 
        
        // กำหนดตำแหน่งและขนาดของปุ่ม Leaderboard ทางด้านขวา: X=923, Y=52, กว้าง=18, สูง=19
        btnLeader.setBounds(923, 52, 18, 19);
        
        // เพิ่มปุ่ม Leaderboard เข้าสู่ HeaderBar
        add(btnLeader);

        // --- 4. การตั้งค่าและการจัดวาง: ปุ่ม User (เข้าสู่ระบบ / ข้อมูลส่วนตัว) ---
        // ตั้งค่าฟอนต์ของปุ่ม User
        btnUser.setFont(new Font("Space Grotesk Medium", Font.PLAIN, 16));
        
        // ตั้งค่าสีตัวอักษรเริ่มต้นเป็นสีเทา (#8E9094)
        btnUser.setForeground(Color.decode("#8E9094"));

        // โหลดรูปภาพไอคอนคน (User) แบบปกติ และแบบที่ถูกชี้ (Hover)
        btnUser.setIcon(UiUtil.loadIcon("userIcon.png"));
        btnUser.setRolloverIcon(UiUtil.loadIcon("userIcon_hover.png"));

        // กำหนดระยะห่างระหว่างรูปภาพไอคอนกับข้อความ ("Login") ให้อยู่ห่างกัน 8 พิกเซล
        btnUser.setIconTextGap(8); 
        
        // ปิดส่วนที่ไม่ต้องการ (ขอบ, พื้นหลัง, โฟกัส) เพื่อให้ดูเป็นเพียงข้อความ+ไอคอนคลิกได้
        btnUser.setBorderPainted(false);
        btnUser.setContentAreaFilled(false);
        btnUser.setFocusPainted(false);

        // เปลี่ยนเคอร์เซอร์เมาส์
        btnUser.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // กำหนดตำแหน่งของปุ่ม User ให้อยู่ริมขวาสุด ถัดจากปุ่ม Leaderboard: X=965, Y=48
        btnUser.setBounds(965, 48, 120, 28);
        
        // เพิ่มปุ่ม User เข้าสู่ HeaderBar
        add(btnUser);

        // --- 5. การเพิ่ม Event Listeners (การจัดการเหตุการณ์ต่างๆ) ---

        // (5.1) เอฟเฟกต์เปลี่ยนสีข้อความของปุ่ม User เมื่อนำเมาส์เข้า/ออก
        btnUser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                // เมื่อเมาส์เข้ามาในเขตพื้นที่ของปุ่ม (Hover) ให้เปลี่ยนสีข้อความเป็นสีดำ
                btnUser.setForeground(Color.black);
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                // เมื่อเมาส์ออกจากพื้นที่ปุ่ม ให้กลับไปเป็นสีเทาตามเดิม
                btnUser.setForeground(Color.decode("#8E9094"));
            }
        });

        // (5.2) การทำงานเมื่อคลิกปุ่ม Leaderboard
        btnLeader.addActionListener(e -> {
            // ค้นหา MainFrame ซึ่งเป็นหน้าต่างหลัก (Window Ancestor) ของคอมโพเนนต์นี้
            MainFrame main = (MainFrame) SwingUtilities.getWindowAncestor(this);
            // ถ้าเจอหน้าต่างหลัก ให้เรียกใช้คำสั่งเพื่อเปลี่ยนหน้าไปยังหน้า Leaderboard
            if (main != null) main.showLeaderboard();
        });

        // (5.3) การทำงานเมื่อคลิกปุ่ม User
        btnUser.addActionListener(e -> {
            // ค้นหาหน้าต่างหลักเช่นเดิม
            MainFrame main = (MainFrame) SwingUtilities.getWindowAncestor(this);
            // สั่งเปลี่ยนหน้าจอไปยังหน้า Login
            if (main != null) main.showLogin();
        });

        // (5.4) การทำงานเมื่อคลิกที่โลโก้
        lblLogo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { 
                // ค้นหา MainFrame
                MainFrame main = (MainFrame) SwingUtilities.getWindowAncestor(HeaderBar.this);
                // เมื่อคลิกที่โลโก้ จะทำหน้าที่พาผู้เล่นกลับไปยังหน้าหลัก (Home) เสมอ
                if (main != null ) main.showHome(); 
            }
        });
    }
}
