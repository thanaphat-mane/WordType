package ui.pages;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import ui.UiUtil;
import ui.components.HeaderBar;

/**
 * คลาส Login คือ "หน้าต่างสำหรับเข้าสู่ระบบ"
 * <p>
 * มีช่องให้กรอกชื่อผู้ใช้ (Username) และรหัสผ่าน (Password)
 * รวมไปถึงปุ่มลิงก์เพื่อให้กดไปยังหน้าสมัครสมาชิก (Register) ได้หากยังไม่มีบัญชี
 */
public class Login extends JPanel {

    // ประกาศตัวแปรอ็อบเจ็กต์ของ UI ที่ต้องมีการดึงค่าทีหลัง (เช่น ตอนกดปุ่ม)
    private JTextField txtUsername;
    private JPasswordField txtPassword; // ใช้ JPasswordField เพื่อซ่อนข้อความที่พิมพ์ไม่ให้คนอื่นเห็น
    private JButton btnSignIn;
    private JButton btnCreateAccount;

    /**
     * คอนสตรักเตอร์ สร้างหน้าจอ Login
     */
    public Login() {
        // 1. ปิดเลย์เอาต์อัตโนมัติ เพื่อจัดวาง X, Y (Absolute Layout) ด้วยตัวเองให้ตรงกับงานออกแบบเป๊ะๆ
        setLayout(null);
        setBackground(UiUtil.BG_COLOR); // สีเทาพื้นหลัง

        // 2. แปะแถบเมนูด้านบน (HeaderBar)
        HeaderBar headerBar = new HeaderBar();
        add(headerBar);

        // ----------------------------------------------------
        // 3. ป้ายหัวข้อหน้า (Title Label)
        // ----------------------------------------------------
        JLabel lblTitle = new JLabel("Sign in");
        lblTitle.setFont(new Font("Roboto Mono", Font.BOLD, 22)); // ฟอนต์หนาขนาด 22
        lblTitle.setForeground(Color.BLACK);
        
        // ติดตั้งไอคอนรูปคนหน้าข้อความ "Sign in"
        lblTitle.setIcon(UiUtil.loadIcon("singInIcon.png"));
        // ตั้งระยะห่างระหว่างรูปไอคอนกับตัวอักษร 13 พิกเซล เพื่อไม่ให้ชิดกันเกินไป
        lblTitle.setIconTextGap(13);
        
        // วางกึ่งกลางจอด้านบน
        lblTitle.setBounds(538, 198, 126, 31);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle);

        // -- ตัวแปรสไตล์ที่ต้องใช้ซ้ำหลายๆ ช่อง --
        Font inputFont = new Font("Roboto Mono", Font.PLAIN, 15);
        Color fieldBgColor = new Color(217, 217, 217); // รหัสสี #D9D9D9 ตามที่นักออกแบบส่งมาให้ (Figma)

        // ----------------------------------------------------
        // 4. ช่องกรอกชื่อผู้ใช้ (Username Field)
        // ----------------------------------------------------
        txtUsername = new JTextField();
        txtUsername.setFont(inputFont);
        txtUsername.setBackground(fieldBgColor);
        txtUsername.setBounds(451, 250, 300, 40);
        
        // *เคล็ดลับพิเศษ*: เราใช้คุณสมบัติของไลบรารี FlatLaf (ที่ติดตั้งไว้ใน Main.java)
        // เพื่อลบเส้นขอบหนาๆ (focusWidth: 0), ปรับขอบให้มน (arc: 12), และตั้งสีเส้นขอบให้เนียนไปกับพื้นหลัง
        txtUsername.putClientProperty("FlatLaf.style", "arc: 12; focusWidth: 0; borderColor: #D9D9D9");
        
        // ใส่ Placeholder เป็นข้อความลางๆ "username" เพื่อใบ้ว่าช่องนี้ต้องกรอกอะไร
        txtUsername.putClientProperty("JTextField.placeholderText", "username");
        add(txtUsername);

        // ----------------------------------------------------
        // 5. ช่องกรอกรหัสผ่าน (Password Field)
        // ----------------------------------------------------
        txtPassword = new JPasswordField();
        txtPassword.setFont(inputFont);
        txtPassword.setBackground(fieldBgColor);
        txtPassword.setBounds(451, 305, 300, 40);
        
        // ตั้งค่าตัวอักษรเวลาพิมพ์ให้กลายเป็น "จุดทึบ" (Bullet) เพื่อความปลอดภัย (กันคนมองเห็นข้างหลัง)
        txtPassword.setEchoChar('\u2022'); 
        
        txtPassword.putClientProperty("FlatLaf.style", "arc: 12; focusWidth: 0; borderColor: #D9D9D9");
        txtPassword.putClientProperty("JTextField.placeholderText", "password");
        add(txtPassword);

        // ----------------------------------------------------
        // 6. ปุ่มกดเข้าสู่ระบบ (Sign In Button)
        // ----------------------------------------------------
        btnSignIn = new JButton("Sign in");
        btnSignIn.setBounds(451, 365, 300, 40);
        btnSignIn.setFont(new Font("Roboto Mono", Font.BOLD, 15));
        
        // ใช้สีแบรนด์ (Teal เข้มๆ) มาเป็นสีพื้นหลังปุ่ม
        btnSignIn.setBackground(new Color(0, 102, 102));
        btnSignIn.setForeground(Color.WHITE); // ตัวหนังสือสีขาวตัดกับพื้นหลังเข้ม
        
        // ปิดกล่องโฟกัส (เส้นประรอบข้อความในปุ่มของ Java ปกติ) ให้ปุ่มดูเนี้ยบขึ้น
        btnSignIn.setFocusPainted(false);
        btnSignIn.putClientProperty("FlatLaf.style", "arc: 12"); // ปรับให้ปุ่มโค้งมนด้วย FlatLaf
        add(btnSignIn);

        // ----------------------------------------------------
        // 7. ปุ่มสร้างบัญชีใหม่ (Create Account Link)
        // ----------------------------------------------------
        // ปุ่มนี้เราจะไม่ทำให้เป็นกรอบสี่เหลี่ยม แต่จะทำให้ "เหมือนเป็นลิงก์ (Link) ธรรมดาบนหน้าเว็บ"
        btnCreateAccount = new JButton("create an account");
        btnCreateAccount.setBounds(451, 415, 300, 25);
        btnCreateAccount.setFont(new Font("Roboto Mono", Font.PLAIN, 15));
        btnCreateAccount.setForeground(new Color(128, 128, 128)); // สีเทากลาง
        
        // ปิดเอฟเฟกต์การระบายสีพื้นหลัง ทำให้ปุ่มโปร่งใส
        btnCreateAccount.setContentAreaFilled(false);
        // ปิดกรอบเส้นขอบปุ่ม
        btnCreateAccount.setBorderPainted(false);
        btnCreateAccount.setFocusPainted(false);
        
        // เมื่อนำเมาส์ไปชี้ ให้เปลี่ยนไอคอนเมาส์เป็นรูปนิ้วคลิก
        btnCreateAccount.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // -- เพิ่มความสามารถ "ปุ่มลิงก์" ให้สมบูรณ์แบบด้วย MouseListener --
        // ต้องคอยดักจับพฤติกรรมของเมาส์ เพื่อให้ข้อความเปลี่ยนสีตอบสนองเหมือนปุ่มบนหน้าเว็บ
        Color originalColor = btnCreateAccount.getForeground(); // จำสีเดิม (เทา) ไว้ก่อน
        
        btnCreateAccount.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                // เมื่อเมาส์ลากเข้ามาในเขตปุ่ม (Hover) -> เปลี่ยนสีข้อความเป็นสีดำให้ดูเด่นขึ้น
                btnCreateAccount.setForeground(Color.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                // เมื่อเมาส์ลากออกจากเขตปุ่ม -> เปลี่ยนกลับเป็นสีเดิม
                btnCreateAccount.setForeground(originalColor);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                // ระหว่างที่กดปุ่มค้างไว้ (Active) -> เปลี่ยนเป็นสีขาว
                btnCreateAccount.setForeground(Color.WHITE);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                // เมื่อปล่อยปุ่ม (คลิกเสร็จ) ต้องเช็คก่อนว่า เมาส์ยังอยู่ในเขตปุ่มหรือไม่
                // ถ้ายังอยู่ ก็กลับไปเป็นสีดำ(สถานะ Hover), ถ้าลากออกไปแล้วปล่อย ก็กลับไปสีเดิม
                if (btnCreateAccount.getBounds().contains(evt.getPoint())) {
                    btnCreateAccount.setForeground(Color.BLACK);
                } else {
                    btnCreateAccount.setForeground(originalColor);
                }
            }
        });
        
        add(btnCreateAccount);
    }
}
