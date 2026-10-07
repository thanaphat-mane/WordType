package ui.pages;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import ui.UiUtil;
import ui.components.HeaderBar;

public class Login extends JPanel {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnSignIn;
    private JButton btnCreateAccount;

    public Login() {
        setLayout(null);
        setBackground(UiUtil.BG_COLOR);

        // 1. HeaderBar
        HeaderBar headerBar = new HeaderBar();
        add(headerBar);

        // 2. Title "Sign in"
        JLabel lblTitle = new JLabel("Sign in");
        lblTitle.setFont(new Font("Roboto Mono", Font.BOLD, 22));
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setIcon(UiUtil.loadIcon("singInIcon.png"));
        lblTitle.setIconTextGap(13);
        lblTitle.setBounds(538, 198, 126, 31);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle);

        Font inputFont = new Font("Roboto Mono", Font.PLAIN, 15);
        Color fieldBgColor = new Color(217, 217, 217); // สีเทาตาม Figma (#D9D9D9)

        // 3. ช่อง Username
        txtUsername = new JTextField();
        txtUsername.setFont(inputFont);
        txtUsername.setBackground(fieldBgColor);
        txtUsername.setBounds(451, 250, 300, 40);
        txtUsername.putClientProperty("FlatLaf.style", "arc: 12; focusWidth: 0; borderColor: #D9D9D9");
        txtUsername.putClientProperty("JTextField.placeholderText", "username");
        add(txtUsername);

        // 4. ช่อง Password
        txtPassword = new JPasswordField();
        txtPassword.setFont(inputFont);
        txtPassword.setBackground(fieldBgColor);
        txtPassword.setBounds(451, 305, 300, 40);
        txtPassword.setEchoChar('\u2022');
        txtPassword.putClientProperty("FlatLaf.style", "arc: 12; focusWidth: 0; borderColor: #D9D9D9");
        txtPassword.putClientProperty("JTextField.placeholderText", "password");
        add(txtPassword);

        // 5. ปุ่ม Sign in
        btnSignIn = new JButton("Sign in");
        btnSignIn.setBounds(451, 365, 300, 40);
        btnSignIn.setFont(new Font("Roboto Mono", Font.BOLD, 15));
        btnSignIn.setBackground(new Color(0, 102, 102));
        btnSignIn.setForeground(Color.WHITE);
        btnSignIn.setFocusPainted(false);
        btnSignIn.putClientProperty("FlatLaf.style", "arc: 12");
        add(btnSignIn);

        // 6. ปุ่ม "create an account"
        btnCreateAccount = new JButton("create an account");
        btnCreateAccount.setBounds(451, 415, 300, 25);
        btnCreateAccount.setFont(new Font("Roboto Mono", Font.PLAIN, 15));
        btnCreateAccount.setForeground(new Color(128, 128, 128));
        btnCreateAccount.setContentAreaFilled(false);
        btnCreateAccount.setBorderPainted(false);
        btnCreateAccount.setFocusPainted(false);
        btnCreateAccount.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color originalColor = btnCreateAccount.getForeground();
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
        add(btnCreateAccount);
    }
}
