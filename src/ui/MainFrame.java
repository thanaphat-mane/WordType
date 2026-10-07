package ui;

import java.awt.*;
import javax.swing.*;

import ui.pages.Home;
import ui.pages.Leaderboard;
import ui.pages.Login;
import ui.pages.Register;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel cards;
    
    public MainFrame() {
        setTitle("wordtype");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(1200,700);
        setLocationRelativeTo(null);
        
        // 1. สร้าง CardLayout และกล้องเก็บ cards
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        
        // 2. สร้างหน้าต่างๆ มาไรไว้
        Home homePanel = new Home();
        Login loginPanel = new Login();
        Register registerPanel = new Register();
        Leaderboard LeaderboardPanel = new Leaderboard();

        // 3. ยัดหน้าต่างๆ ลงไปในกล่องเก็บไพ่ พร้อมตั้ง "ป้ายชื่อ"                                                             
        cards.add(homePanel, "HOME");                                                                                   
        cards.add(loginPanel, "LOGIN");                                                                                 
        cards.add(registerPanel, "REGISTER");
        cards.add(LeaderboardPanel, "LEADERBOARD"); 

        // 4. เอากล่องเก็บไพ่ไปแปะในหน้าต่างหลัก                                                                              
        setContentPane(cards);                                                                                          
                                                                                                                            
        // เริ่มต้นโปรแกรมมา ให้เปิดไพ่ใบไหนโชว์ก่อน?                                                                           
        // (ปกติแอปจริงต้องเริ่มด้วย Login แต่ตอนนี้เราทำหน้า Home ค้างอยู่ เลยให้โชว์ Home ไปก่อนครับ)                                   
        showHome();
    }
    // -- เมธอดสลับหน้าจอ --
    public void showLogin() {
        cardLayout.show(cards, "LOGIN");
    }
    public void showRegister() {
        cardLayout.show(cards, "REGISTER");
    }
    public void showHome() {
        cardLayout.show(cards, "HOME");
    }
    public void showLeaderboard() {
        cardLayout.show(cards, "LEADERBOARD");
    }
}
