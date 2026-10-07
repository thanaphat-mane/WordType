package ui.pages;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import ui.UiUtil;
import ui.components.HeaderBar;

public class Register extends JPanel {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnRegister;
    private JButton btnAlreadyHaveAccount;

    public Register() {
        setLayout(null);
        setBackground(UiUtil.BG_COLOR);

        // 1. HeaderBar
        HeaderBar headerBar = new HeaderBar();
        add(headerBar);

        // 2. Title "Register"
        JLabel lblTitle = new JLabel("Sign Up");
        lblTitle.setFont(new Font("Roboto Mono", Font.BOLD, 22));
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setBounds(536, 178, 124, 31);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        lblTitle.setIcon(UiUtil.loadIcon("Registericon.png"));
        lblTitle.setIconTextGap(13);
        add(lblTitle);

        Font inputFont = new Font("Roboto Mono", Font.PLAIN, 15);
        Color fieldBgColor = new Color(217, 217, 217); // สีเทาตาม Figma (#D9D9D9)

        // 3. ช่อง Username
        txtUsername = new JTextField();
        txtUsername.setFont(inputFont);
        txtUsername.setBackground(fieldBgColor);
        txtUsername.setBounds(440, 235, 320, 44);
        txtUsername.putClientProperty("FlatLaf.style", "arc: 12; focusWidth: 0; borderColor: #D9D9D9");
        txtUsername.putClientProperty("JTextField.placeholderText", "username");
        add(txtUsername);

        // 4. ช่อง Password
        txtPassword = new JPasswordField();
        txtPassword.setFont(inputFont);
        txtPassword.setBackground(fieldBgColor);
        txtPassword.setBounds(440, 295, 320, 44);
        txtPassword.setEchoChar('\u2022');
        txtPassword.putClientProperty("FlatLaf.style", "arc: 12; focusWidth: 0; borderColor: #D9D9D9");
        txtPassword.putClientProperty("JTextField.placeholderText", "password");
        add(txtPassword);

        // 5. ช่อง Confirm Password
        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setFont(inputFont);
        txtConfirmPassword.setBackground(fieldBgColor);
        txtConfirmPassword.setBounds(440, 355, 320, 44);
        txtConfirmPassword.setEchoChar('\u2022');
        txtConfirmPassword.putClientProperty("FlatLaf.style", "arc: 12; focusWidth: 0; borderColor: #D9D9D9");
        txtConfirmPassword.putClientProperty("JTextField.placeholderText", "confirm password");
        add(txtConfirmPassword);

        // 6. ปุ่ม Register
        btnRegister = new JButton("Register");
        btnRegister.setBounds(440, 425, 320, 44);
        btnRegister.setFont(new Font("Roboto Mono", Font.BOLD, 15));
        btnRegister.setBackground(new Color(0, 102, 102));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.putClientProperty("FlatLaf.style", "arc: 12");
        add(btnRegister);

        // 7. ปุ่ม "back to sign in"
        btnAlreadyHaveAccount = new JButton("back to sign in");
        btnAlreadyHaveAccount.setBounds(500, 495, 190, 20);
        btnAlreadyHaveAccount.setFont(new Font("Roboto Mono", Font.PLAIN, 15));
        btnAlreadyHaveAccount.setForeground(new Color(128, 128, 128));
        btnAlreadyHaveAccount.setContentAreaFilled(false);
        btnAlreadyHaveAccount.setBorderPainted(false);
        btnAlreadyHaveAccount.setFocusPainted(false);
        btnAlreadyHaveAccount.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color originalColor = btnAlreadyHaveAccount.getForeground();
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
        add(btnAlreadyHaveAccount);
    }
}
