package ui.pages;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import ui.UiUtil;
import ui.components.HeaderBar;

/**
 * คลาส Register ทำหน้าที่เป็น "หน้าจอสำหรับสมัครบัญชีใหม่"
 * <p>
 * มีช่องให้กรอก ชื่อผู้ใช้ (Username), รหัสผ่าน (Password), 
 * และช่องยืนยันรหัสผ่าน (Confirm Password) เพื่อป้องกันการพิมพ์รหัสผิดพลาดตอนสมัคร
 */
public class Register extends JPanel {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnRegister;
    private JButton btnAlreadyHaveAccount;

    /**
     * คอนสตรักเตอร์ สร้างหน้าจอ Register
     */
    public Register() {
        // 1. ปิดเลย์เอาต์อัตโนมัติ เพื่อจัดเรียงแบบ Absolute Layout (กำหนด X, Y เอง)
        setLayout(null);
        setBackground(UiUtil.BG_COLOR); // สีพื้นหลังหลัก (เทาอ่อน)

        // 2. วางแถบเมนู (HeaderBar) ไว้บนสุดของหน้า
        HeaderBar headerBar = new HeaderBar();
        add(headerBar);

        // ----------------------------------------------------
        // 3. ป้ายหัวข้อหน้า "Sign Up"
        // ----------------------------------------------------
        JLabel lblTitle = new JLabel("Sign Up");
        lblTitle.setFont(new Font("Roboto Mono", Font.BOLD, 22)); // ตัวหนา ขนาด 22
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setBounds(536, 178, 124, 31);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER); // จัดข้อความไว้ตรงกลาง

        // ใส่รูปไอคอนของหน้าลงทะเบียน
        lblTitle.setIcon(UiUtil.loadIcon("Registericon.png"));
        // ขยับข้อความออกจากไอคอน 13px
        lblTitle.setIconTextGap(13);
        add(lblTitle);

        // -- ตัวแปรสไตล์กลาง (Shared Styles) สำหรับทุกช่องกรอกข้อมูล --
        Font inputFont = new Font("Roboto Mono", Font.PLAIN, 15);
        Color fieldBgColor = new Color(217, 217, 217); // สีเทาแบบช่อง Input (จาก Figma)

        // ----------------------------------------------------
        // 4. ช่องกรอกชื่อผู้ใช้ (Username)
        // ----------------------------------------------------
        txtUsername = new JTextField();
        txtUsername.setFont(inputFont);
        txtUsername.setBackground(fieldBgColor);
        txtUsername.setBounds(440, 235, 320, 44);
        
        // ตกแต่งให้ขอบมนและซ่อนเส้นขอบสีฟ้า (Focus Ring) โดยใช้ FlatLaf API
        txtUsername.putClientProperty("FlatLaf.style", "arc: 12; focusWidth: 0; borderColor: #D9D9D9");
        txtUsername.putClientProperty("JTextField.placeholderText", "username");
        add(txtUsername);

        // ----------------------------------------------------
        // 5. ช่องกรอกรหัสผ่าน (Password)
        // ----------------------------------------------------
        txtPassword = new JPasswordField();
        txtPassword.setFont(inputFont);
        txtPassword.setBackground(fieldBgColor);
        txtPassword.setBounds(440, 295, 320, 44);
        
        // สั่งให้ซ่อนข้อความที่ผู้ใช้พิมพ์เป็นสัญลักษณ์จุด (Bullet) ป้องกันคนรอบข้างเห็นรหัสผ่าน
        txtPassword.setEchoChar('\u2022');
        
        txtPassword.putClientProperty("FlatLaf.style", "arc: 12; focusWidth: 0; borderColor: #D9D9D9");
        txtPassword.putClientProperty("JTextField.placeholderText", "password");
        add(txtPassword);

        // ----------------------------------------------------
        // 6. ช่องยืนยันรหัสผ่าน (Confirm Password)
        // ----------------------------------------------------
        // ช่องนี้เพิ่มขึ้นมาเพื่อให้ผู้ใช้ยืนยันว่ารหัสที่พิมพ์ด้านบนนั้นถูกต้อง ไม่ได้พิมพ์ตกหล่น
        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setFont(inputFont);
        txtConfirmPassword.setBackground(fieldBgColor);
        txtConfirmPassword.setBounds(440, 355, 320, 44);
        txtConfirmPassword.setEchoChar('\u2022');
        txtConfirmPassword.putClientProperty("FlatLaf.style", "arc: 12; focusWidth: 0; borderColor: #D9D9D9");
        txtConfirmPassword.putClientProperty("JTextField.placeholderText", "confirm password");
        add(txtConfirmPassword);

        // ----------------------------------------------------
        // 7. ปุ่มกดยืนยันการสมัคร (Register Button)
        // ----------------------------------------------------
        btnRegister = new JButton("Register");
        btnRegister.setBounds(440, 425, 320, 44); // ความสูง 44 เพื่อให้สมดุลกับช่องกรอก
        btnRegister.setFont(new Font("Roboto Mono", Font.BOLD, 15));
        
        // ใช้สี Teal เหมือนหน้า Login สร้างความเป็นอันหนึ่งอันเดียวกัน (Brand Identity)
        btnRegister.setBackground(new Color(0, 102, 102));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false); // เอาเส้นประออกจากปุ่ม
        btnRegister.putClientProperty("FlatLaf.style", "arc: 12");
        add(btnRegister);

        // ----------------------------------------------------
        // 8. ปุ่มลิงก์กลับไปหน้า Login (กรณีมีบัญชีอยู่แล้ว)
        // ----------------------------------------------------
        // ทำให้ปุ่มนี้ดูเหมือนปุ่มลิงก์ปกติ โดยการปิดลูกเล่นพื้นหลังและกรอบของ JButton
        btnAlreadyHaveAccount = new JButton("back to sign in");
        btnAlreadyHaveAccount.setBounds(500, 495, 190, 20);
        btnAlreadyHaveAccount.setFont(new Font("Roboto Mono", Font.PLAIN, 15));
        btnAlreadyHaveAccount.setForeground(new Color(128, 128, 128));
        btnAlreadyHaveAccount.setContentAreaFilled(false);
        btnAlreadyHaveAccount.setBorderPainted(false);
        btnAlreadyHaveAccount.setFocusPainted(false);
        btnAlreadyHaveAccount.setCursor(new Cursor(Cursor.HAND_CURSOR)); // เมาส์รูปนิ้ว

        // จัดการเหตุการณ์เมาส์ (Mouse Events) เพื่อสร้าง Hover Effect (เอาเมาส์ชี้แล้วเปลี่ยนสี)
        Color originalColor = btnAlreadyHaveAccount.getForeground();
        
        btnAlreadyHaveAccount.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                // ลากเมาส์เข้าปุ่ม เปลี่ยนเป็นสีดำ
                btnAlreadyHaveAccount.setForeground(Color.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                // ลากเมาส์ออก คืนสีเทาเดิม
                btnAlreadyHaveAccount.setForeground(originalColor);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                // จังหวะกดคลิกลงไป เปลี่ยนเป็นสีขาว
                btnAlreadyHaveAccount.setForeground(Color.WHITE);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                // จังหวะปล่อยเมาส์ (ถ้าปล่อยในเขตปุ่มให้กลับไปสีตอนชี้ (Hover))
                if (btnAlreadyHaveAccount.getBounds().contains(evt.getPoint())) {
                    btnAlreadyHaveAccount.setForeground(Color.BLACK);
                } else {
                    btnAlreadyHaveAccount.setForeground(originalColor);
                }
            }
        });
        add(btnAlreadyHaveAccount);
    }
}
