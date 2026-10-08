package ui;

import java.awt.*;
import javax.swing.*;

import ui.pages.Home;
import ui.pages.Leaderboard;
import ui.pages.Login;
import ui.pages.Register;

/**
 * คลาส MainFrame ทำหน้าที่เป็น "หน้าต่างหลัก" (Window) ของโปรแกรม 
 * <p>
 * หลักการทำงาน: เปรียบเสมือนกรอบรูป (JFrame) หนึ่งกรอบที่เปิดขึ้นมาตอนรันแอปพลิเคชัน 
 * จากนั้นใช้เทคนิค CardLayout ในการสลับเปลี่ยน "หน้าจอ (Panel)" ต่างๆ ภายในกรอบเดิม
 * ทำให้ดูเหมือนเราเปลี่ยนหน้าไปมาได้โดยที่หน้าต่างโปรแกรมไม่กะพริบหรือเปิดหน้าต่างใหม่
 */
public class MainFrame extends JFrame {

    // ตัวจัดการเลย์เอาต์แบบ "สำรับไพ่" (CardLayout) ที่ยอมให้แสดงผลทีละหนึ่งหน้าเท่านั้น
    private CardLayout cardLayout;
    
    // "กล่องเก็บไพ่" (JPanel) ที่จะเป็นคอนเทนเนอร์หลักคอยบรรจุหน้าจอทั้งหมดเอาไว้
    private JPanel cards;
    
    /**
     * คอนสตรักเตอร์ (Constructor) จะถูกเรียกใช้เมื่อสร้างอ็อบเจ็กต์ MainFrame
     * ทำหน้าที่ตั้งค่าหน้าต่าง และโหลดหน้าจอทั้งหมดมารอไว้ในหน่วยความจำ
     */
    public MainFrame() {
        // 1. กำหนดชื่อบนหัวหน้าต่าง (Title Bar) ของโปรแกรม
        setTitle("wordtype");
        
        // 2. ตั้งค่าให้โปรแกรมปิดการทำงานสมบูรณ์ (หยุดรัน) เมื่อผู้ใช้กดปุ่มกากบาท (X) มุมขวาบน
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 3. ล็อกขนาดหน้าต่าง ไม่ให้ผู้ใช้ย่อ/ขยายได้ เพื่อป้องกันไม่ให้เลย์เอาต์ที่เราจัดพิกัดไว้พัง
        setResizable(false);
        
        // 4. กำหนดขนาดของหน้าต่างกว้าง 1200 พิกเซล สูง 700 พิกเซล
        setSize(1200, 700);
        
        // 5. สั่งให้หน้าต่างนี้ไปแสดงอยู่ "กึ่งกลางหน้าจอ" เสมอ โดยไม่สนว่าจอภาพจะใหญ่แค่ไหน
        setLocationRelativeTo(null); 
        
        // -- เริ่มต้นระบบ CardLayout --
        
        // สร้างตัวจัดการเลย์เอาต์ (CardLayout) 
        cardLayout = new CardLayout();
        
        // สร้างพาเนลหลัก (cards) แล้วกำหนดให้ใช้เลย์เอาต์ที่เพิ่งสร้าง
        cards = new JPanel(cardLayout);
        
        // -- สร้างแต่ละหน้าจอ (Instantiating Panels) --
        // สร้างหน้าต่างๆ เตรียมไว้เป็นอ็อบเจ็กต์ (แต่จะยังไม่เห็นบนจอ จนกว่าจะสั่งโชว์)
        Home homePanel = new Home();
        Login loginPanel = new Login();
        Register registerPanel = new Register();
        Leaderboard leaderboardPanel = new Leaderboard(); // แก้ชื่อตัวแปรให้เป็นตัวพิมพ์เล็กนำหน้าตามหลัก Java Naming Convention

        // -- ยัดหน้าจอต่างๆ ลงในกล่อง (cards) --
        // ตอนใส่หน้าจอลงไป ต้องแปะ "ป้ายชื่อ" (String Identifier) เอาไว้ด้วย
        // เพื่อที่ตอนสั่งเปลี่ยนหน้า เราจะได้เรียกชื่อป้ายนั้นถูก
        cards.add(homePanel, "HOME");                                                                                   
        cards.add(loginPanel, "LOGIN");                                                                                 
        cards.add(registerPanel, "REGISTER");
        cards.add(leaderboardPanel, "LEADERBOARD"); 

        // -- นำกล่องที่บรรจุทุกหน้าจอ ไปแปะลงบนหน้าต่างหลัก --
        setContentPane(cards);                                                                                          
                                                                                                                            
        // -- สั่งให้หน้าจอแรกที่แสดงขึ้นมาตอนเปิดโปรแกรมคือหน้าใด --
        // ถึงแม้ว่าระบบจริงควรเริ่มที่หน้า LOGIN แต่สำหรับการทดสอบและพัฒนาระบบ (Development Phase)
        // เราตั้งให้แสดงหน้า HOME ขึ้นมาก่อน เพื่อให้ทดสอบพิมพ์ได้เลยโดยไม่ต้องมานั่งล็อกอินทุกรอบ
        showHome();
    }

    // ==============================================
    // เมธอดสำหรับเปลี่ยนหน้าจอ (Navigation Methods)
    // การเรียกใช้: cardLayout.show(ตัวกล่องหลัก, "ป้ายชื่อหน้าที่ต้องการโชว์");
    // ==============================================

    /**
     * สลับหน้าจอไปยังหน้าเข้าสู่ระบบ (Login)
     */
    public void showLogin() {
        cardLayout.show(cards, "LOGIN");
    }

    /**
     * สลับหน้าจอไปยังหน้าสร้างบัญชีใหม่ (Register)
     */
    public void showRegister() {
        cardLayout.show(cards, "REGISTER");
    }

    /**
     * สลับหน้าจอไปยังหน้าหลักของแอปพลิเคชัน (Home - สำหรับพิมพ์ดีด)
     */
    public void showHome() {
        cardLayout.show(cards, "HOME");
    }

    /**
     * สลับหน้าจอไปยังหน้าตารางสรุปคะแนนสูงสุด (Leaderboard)
     */
    public void showLeaderboard() {
        cardLayout.show(cards, "LEADERBOARD");
    }
}
