package ui.pages;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import model.Score;
import ui.MainFrame;
import ui.UiUtil;

public class SelectMode extends JPanel {

    private String selectedMode = Score.getModeTime();
    private int selectedAmount = 30;

    private JToggleButton btnTimeMode;
    private JToggleButton btnWordsMode;
    private JToggleButton btn15;
    private JToggleButton btn30;
    private JToggleButton btn60;
    private JToggleButton btn120;
    private JButton btnStart;

    public SelectMode() {
        initComponents();
    }

    private void initComponents() {
        setLayout(null);
        setBackground(UiUtil.BG_COLOR);

        JLabel lblTitle = new JLabel("CHOOSE YOUR MODE");
        lblTitle.setFont(new Font("Roboto Mono", Font.BOLD, 28));
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setBounds(0, 180, 1200, 40);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle);

        // Mode Group
        ButtonGroup modeGroup = new ButtonGroup();
        btnTimeMode = createToggleButton("Time Mode", 400, 250, 150, 40);
        btnWordsMode = createToggleButton("Words Mode", 650, 250, 150, 40);
        modeGroup.add(btnTimeMode);
        modeGroup.add(btnWordsMode);
        btnTimeMode.setSelected(true);
        add(btnTimeMode);
        add(btnWordsMode);

        // Amount Group
        ButtonGroup amountGroup = new ButtonGroup();
        btn15 = createToggleButton("15 / 10", 350, 320, 100, 40);
        btn30 = createToggleButton("30 / 25", 475, 320, 100, 40);
        btn60 = createToggleButton("60 / 50", 600, 320, 100, 40);
        btn120 = createToggleButton("120 / 100", 725, 320, 100, 40);
        amountGroup.add(btn15);
        amountGroup.add(btn30);
        amountGroup.add(btn60);
        amountGroup.add(btn120);
        btn30.setSelected(true);
        add(btn15);
        add(btn30);
        add(btn60);
        add(btn120);

        // Start Button
        btnStart = new JButton("START TYPING");
        btnStart.setBounds(450, 420, 300, 50);
        btnStart.setFont(new Font("Roboto Mono", Font.BOLD, 18));
        btnStart.setBackground(new Color(0, 102, 102));
        btnStart.setForeground(Color.WHITE);
        btnStart.setFocusPainted(false);
        btnStart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnStart.putClientProperty("FlatLaf.style", "arc: 12");
        add(btnStart);

        // Events
        btnTimeMode.addActionListener(e -> selectedMode = Score.getModeTime());
        btnWordsMode.addActionListener(e -> selectedMode = Score.getModeWords());

        btn15.addActionListener(e -> selectedAmount = selectedMode.equals(Score.getModeTime()) ? 15 : 10);
        btn30.addActionListener(e -> selectedAmount = selectedMode.equals(Score.getModeTime()) ? 30 : 25);
        btn60.addActionListener(e -> selectedAmount = selectedMode.equals(Score.getModeTime()) ? 60 : 50);
        btn120.addActionListener(e -> selectedAmount = selectedMode.equals(Score.getModeTime()) ? 120 : 100);

        btnStart.addActionListener(e -> {
            MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
            if (mainFrame != null) {
                // ส่งค่าที่ผู้ใช้เลือกไปให้หน้า Home ตั้งค่าเริ่มต้น
                mainFrame.getHomePanel().setInitialMode(selectedMode, selectedAmount);
                mainFrame.showHome();
            }
        });
    }

    private JToggleButton createToggleButton(String text, int x, int y, int width, int height) {
        JToggleButton btn = new JToggleButton(text);
        btn.setBounds(x, y, width, height);
        btn.setFont(new Font("Roboto Mono", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("FlatLaf.style", "arc: 12");
        return btn;
    }
}
