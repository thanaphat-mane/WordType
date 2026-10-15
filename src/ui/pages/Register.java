package ui.pages;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import service.AuthResult;
import service.AuthService;
import ui.MainFrame;
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
    private JLabel lblError;

    // ระบบจัดการบัญชี (เชื่อมต่อฐานข้อมูล)
    private final AuthService authService = new AuthService();

    /**
     * คอนสตรักเตอร์ สร้างหน้าจอ Register
     */
    public Register() {
        initComponents();
        setupListeners();
    }

    /**
     * เมธอดสำหรับสร้างและจัดวางองค์ประกอบต่างๆ บนหน้าจอ (UI Components)
     */
    private void initComponents() {
        setLayout(null);
        setBackground(UiUtil.BG_COLOR); // สีพื้นหลังหลัก (เทาอ่อน)

        // วางแถบเมนู (HeaderBar) ไว้บนสุดของหน้า
        HeaderBar headerBar = new HeaderBar();
        add(headerBar);

        // ป้ายหัวข้อหน้า "Sign Up"
        JLabel lblTitle = new JLabel("Sign Up");
        lblTitle.setFont(new Font("Roboto Mono", Font.BOLD, 22)); // ตัวหนา ขนาด 22
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setBounds(536, 178, 124, 31);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setIcon(UiUtil.loadIcon("Registericon.png"));
        lblTitle.setIconTextGap(13);
        add(lblTitle);

        Font inputFont = new Font("Roboto Mono", Font.PLAIN, 15);
        Color fieldBgColor = new Color(217, 217, 217);

        // ช่องกรอกชื่อผู้ใช้ (Username)
        txtUsername = new JTextField();
        txtUsername.setFont(inputFont);
        txtUsername.setBackground(fieldBgColor);
        txtUsername.setBounds(440, 235, 320, 44);
        txtUsername.putClientProperty("FlatLaf.style", "arc: 12; focusWidth: 0; borderColor: #D9D9D9");
        txtUsername.putClientProperty("JTextField.placeholderText", "username");
        add(txtUsername);

        // ช่องกรอกรหัสผ่าน (Password)
        txtPassword = new JPasswordField();
        txtPassword.setFont(inputFont);
        txtPassword.setBackground(fieldBgColor);
        txtPassword.setBounds(440, 295, 320, 44);
        txtPassword.setEchoChar('\u2022');
        txtPassword.putClientProperty("FlatLaf.style", "arc: 12; focusWidth: 0; borderColor: #D9D9D9");
        txtPassword.putClientProperty("JTextField.placeholderText", "password");
        add(txtPassword);

        // ช่องยืนยันรหัสผ่าน (Confirm Password)
        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setFont(inputFont);
        txtConfirmPassword.setBackground(fieldBgColor);
        txtConfirmPassword.setBounds(440, 355, 320, 44);
        txtConfirmPassword.setEchoChar('\u2022');
        txtConfirmPassword.putClientProperty("FlatLaf.style", "arc: 12; focusWidth: 0; borderColor: #D9D9D9");
        txtConfirmPassword.putClientProperty("JTextField.placeholderText", "confirm password");
        add(txtConfirmPassword);

        // ป้ายแจ้งเตือน Error
        lblError = new JLabel("");
        lblError.setFont(new Font("Roboto Mono", Font.PLAIN, 12));
        lblError.setForeground(new Color(220,53,69));
        lblError.setBounds(440, 405, 320, 16);
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblError);

        // ปุ่มกดยืนยันการสมัคร (Register Button)
        btnRegister = new JButton("Register");
        btnRegister.setBounds(440, 425, 320, 44);
        btnRegister.setFont(new Font("Roboto Mono", Font.BOLD, 15));
        btnRegister.setBackground(new Color(0, 102, 102));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.putClientProperty("FlatLaf.style", "arc: 12");
        add(btnRegister);

        // ปุ่มลิงก์กลับไปหน้า Login
        btnAlreadyHaveAccount = new JButton("back to sign in");
        btnAlreadyHaveAccount.setBounds(500, 495, 190, 20);
        btnAlreadyHaveAccount.setFont(new Font("Roboto Mono", Font.PLAIN, 15));
        btnAlreadyHaveAccount.setForeground(new Color(128, 128, 128));
        btnAlreadyHaveAccount.setContentAreaFilled(false);
        btnAlreadyHaveAccount.setBorderPainted(false);
        btnAlreadyHaveAccount.setFocusPainted(false);
        btnAlreadyHaveAccount.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(btnAlreadyHaveAccount);
    }

    /**
     * เมธอดสำหรับผูกเหตุการณ์ต่างๆ (Events)
     */
    private void setupListeners() {
        Color originalColor = btnAlreadyHaveAccount.getForeground();
        
        // เอฟเฟกต์ Hover ของปุ่ม back to sign in
        btnAlreadyHaveAccount.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                btnAlreadyHaveAccount.setForeground(Color.BLACK);
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                btnAlreadyHaveAccount.setForeground(originalColor);
            }
            @Override
            public void mousePressed(MouseEvent evt) {
                btnAlreadyHaveAccount.setForeground(Color.WHITE);
            }
            @Override
            public void mouseReleased(MouseEvent evt) {
                if (btnAlreadyHaveAccount.getBounds().contains(evt.getPoint())) {
                    btnAlreadyHaveAccount.setForeground(Color.BLACK);
                } else {
                    btnAlreadyHaveAccount.setForeground(originalColor);
                }
            }
        });

        // กดกลับไปหน้า Login 
        btnAlreadyHaveAccount.addActionListener(e -> {
            MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
            if (mainFrame != null) {
                mainFrame.showLogin();
            }
        });

        // เรียงลำดับการกด Enter ข้ามช่อง
        txtUsername.addActionListener(e -> txtPassword.requestFocusInWindow());
        txtPassword.addActionListener(e -> txtConfirmPassword.requestFocusInWindow());
        txtConfirmPassword.addActionListener(e -> btnRegister.doClick());

        // กดปุ่ม Register
        btnRegister.addActionListener(e -> performRegister());

        // เคลียร์ค่าในช่องอัตโนมัติเมื่อเข้ามาหน้านี้
        this.addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                    txtUsername.setText("");
                    txtPassword.setText("");
                    txtConfirmPassword.setText("");
                    lblError.setText("");
                    txtUsername.requestFocusInWindow();
                }
            }
        });
    }

    /**
     * เมธอดจำลองการตรวจสอบก่อนสมัคร (Validation)
     * (ยังไม่ได้เชื่อมกับระบบฐานข้อมูล)
     */
    private void performRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirmPassword.getPassword());

        // 1. เช็คว่ากรอกครบทุกช่องไหม
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            lblError.setText("Please fill in all fields.");
            return; 
        }

        // 2. เช็คว่ารหัสผ่าน 2 ช่องตรงกันไหม
        if (!password.equals(confirm)) {
            lblError.setText("Passwords do not match!");
            return;
        }

        // 3. ส่งข้อมูลไปบันทึกลงฐานข้อมูลผ่าน AuthService
        AuthResult result = authService.register(username, password);

        // 4. ตรวจสอบผลลัพธ์
        if (result.isSuccess()) {
            lblError.setText("");
            
            // สำเร็จแล้วให้ใช้ MainFrame เด้งกลับหน้า Login ทันที
            MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
            if (mainFrame != null) {
                mainFrame.showLogin();
            }
        } else {
            // หากไม่สำเร็จ (เช่น ชื่อซ้ำ) ให้แสดงข้อความแจ้งเตือนที่ได้มาจากระบบลงใน Label
            lblError.setText(result.getMessage());
        }
    }
}
