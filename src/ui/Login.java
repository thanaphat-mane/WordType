package ui;
import java.awt.*;
import javax.swing.*;

public class Login extends JPanel{
    public Login() {
        setLayout(null);
        setBackground(UiUtil.BG_COLOR);
        
        // 1. HeaderBar
        HeaderBar headerBar = new HeaderBar();
        add(headerBar);

        JLabel lblTitle = new JLabel("Sing in");
        lblTitle.setFont(new Font("Roboto Mono", Font.BOLD, 22));
        lblTitle.setForeground(Color.BLACK);

        lblTitle.setIcon(UiUtil.loadIcon("singInIcon.png"));
        lblTitle.setIconTextGap(13);

        lblTitle.setBounds(538, 198, 126, 31);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle);

        //TODO: ตัน

    }
}
