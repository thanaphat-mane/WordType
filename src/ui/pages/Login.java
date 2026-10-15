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
 * คลาส Login คือ "หน้าต่างสำหรับเข้าสู่ระบบ"
 * <p>
 * มีช่องให้กรอกชื่อผู้ใช้ (Username) และรหัสผ่าน (Password)
 * รวมไปถึงปุ่มลิงก์เพื่อให้กดไปยังหน้าสมัครสมาชิก (Register) ได้หากยังไม่มีบัญชี
 */
public class Login extends JPanel {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnSignIn;
    private JButton btnCreateAccount;
    private JLabel lblError;
    private final AuthService authService = new AuthService();

    /**
     * คอนสตรักเตอร์ สร้างหน้าจอ Login
     */
    public Login() {
        initComponents();
        setupListeners();
    }

    /**
     * เมธอดสำหรับสร้างและจัดวางองค์ประกอบต่างๆ บนหน้าจอ (UI Components)
     * เช่น การตั้งค่าพื้นหลัง, การสร้างกล่องข้อความ, ปุ่มกด และการจัด Layout
     */
    private void initComponents() {
        setLayout(null);
        setBackground(UiUtil.BG_COLOR);

        HeaderBar headerBar = new HeaderBar();
        add(headerBar);

        JLabel lblTitle = new JLabel("Sign in");
        lblTitle.setFont(new Font("Roboto Mono", Font.BOLD, 22));
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setIcon(UiUtil.loadIcon("singInIcon.png"));
        lblTitle.setIconTextGap(13);
        lblTitle.setBounds(538, 198, 126, 31);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle);

        Font inputFont = new Font("Roboto Mono", Font.PLAIN, 15);
        Color fieldBgColor = new Color(217, 217, 217);

        txtUsername = new JTextField();
        txtUsername.setFont(inputFont);
        txtUsername.setBackground(fieldBgColor);
        txtUsername.setBounds(451, 250, 300, 40);
        txtUsername.putClientProperty("FlatLaf.style", "arc: 12; focusWidth: 0; borderColor: #D9D9D9");
        txtUsername.putClientProperty("JTextField.placeholderText", "username");
        add(txtUsername);

        txtPassword = new JPasswordField();
        txtPassword.setFont(inputFont);
        txtPassword.setBackground(fieldBgColor);
        txtPassword.setBounds(451, 305, 300, 40);
        txtPassword.setEchoChar('\u2022');
        txtPassword.putClientProperty("FlatLaf.style", "arc: 12; focusWidth: 0; borderColor: #D9D9D9");
        txtPassword.putClientProperty("JTextField.placeholderText", "password");
        add(txtPassword);

        lblError = new JLabel("");
        lblError.setFont(new Font("Roboto Mono", Font.PLAIN, 12));
        lblError.setForeground(new Color(220,53,69));
        lblError.setBounds(451,347,300,16);
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblError);

        btnSignIn = new JButton("Sign in");
        btnSignIn.setBounds(451, 365, 300, 40);
        btnSignIn.setFont(new Font("Roboto Mono", Font.BOLD, 15));
        btnSignIn.setBackground(new Color(0, 102, 102));
        btnSignIn.setForeground(Color.WHITE);
        btnSignIn.setFocusPainted(false);
        btnSignIn.putClientProperty("FlatLaf.style", "arc: 12");
        add(btnSignIn);

        btnCreateAccount = new JButton("create an account");
        btnCreateAccount.setBounds(451, 415, 300, 25);
        btnCreateAccount.setFont(new Font("Roboto Mono", Font.PLAIN, 15));
        btnCreateAccount.setForeground(new Color(128, 128, 128));
        btnCreateAccount.setContentAreaFilled(false);
        btnCreateAccount.setBorderPainted(false);
        btnCreateAccount.setFocusPainted(false);
        btnCreateAccount.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(btnCreateAccount);
    }

    /**
     * เมธอดสำหรับผูกเหตุการณ์ต่างๆ (Events) เข้ากับองค์ประกอบ UI
     * เช่น การคลิกเมาส์ (Mouse Listener), การกดปุ่ม (Action Listener), และเหตุการณ์ของหน้าต่าง (Hierarchy Listener)
     */
    private void setupListeners() {
        Color originalColor = btnCreateAccount.getForeground();
        
        // จัดการเอฟเฟกต์ Hover เมื่อเอาเมาส์ไปชี้ที่ปุ่มสร้างบัญชี
        btnCreateAccount.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                btnCreateAccount.setForeground(Color.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                btnCreateAccount.setForeground(originalColor);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                btnCreateAccount.setForeground(Color.WHITE);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                if (btnCreateAccount.getBounds().contains(evt.getPoint())) {
                    btnCreateAccount.setForeground(Color.BLACK);
                } else {
                    btnCreateAccount.setForeground(originalColor);
                }
            }
        });

        // เมื่อกดปุ่ม 'create an account' ให้สลับหน้าไปที่ Register
        btnCreateAccount.addActionListener(e -> {
            MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
            if (mainFrame != null) {
                mainFrame.showRegister();
            }
        });

        // เมื่อกด Enter ที่ช่อง Username ให้โฟกัสข้ามไปช่อง Password
        txtUsername.addActionListener(e -> txtPassword.requestFocusInWindow());
        // เมื่อกด Enter ที่ช่อง Password ให้ทำการล็อกอินทันที
        txtPassword.addActionListener(e -> btnSignIn.doClick());

        // เมื่อกดปุ่ม Sign in ให้เรียกใช้ฟังก์ชัน performLogin()
        btnSignIn.addActionListener(e -> performLogin());

        // ตรวจจับเมื่อหน้านี้ถูกแสดงขึ้นมา ให้ล้างข้อมูลเก่าออกให้หมด
        this.addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                    txtUsername.setText("");
                    txtPassword.setText("");
                    lblError.setText("");
                    txtUsername.requestFocusInWindow();
                }
            }
        });
    }

    /**
     * เมธอดสำหรับจัดการกระบวนการล็อกอิน
     * ดึงข้อมูลจากช่องกรอกข้อความ แล้วส่งไปตรวจสอบกับ AuthService
     * หากสำเร็จจะบันทึก Session และพาไปหน้า Home หากล้มเหลวจะแสดงข้อความแจ้งเตือน
     */
    private void performLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        AuthResult result = authService.login(username, password);
        
        if(result.isSuccess()) {
            lblError.setText("");
            service.InMemorySession.getInstance().login(result.getUser());
            MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
            if (mainFrame != null) {
                mainFrame.showHome();
            }
        } else {
            lblError.setText(result.getMessage());
        }
    }
}
