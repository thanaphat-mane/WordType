package ui.pages;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import model.Score;
import ui.UiUtil;
import ui.components.HeaderBar;

/**
 * คลาส Result ทำหน้าที่เป็น "หน้าจอแสดงผลลัพธ์" หลังจากการพิมพ์จบ
 */
public class Result extends JPanel {

    private JLabel lblwpm;
    private JLabel lblAcc;
    private JLabel lblType;
    private JLabel lblChars;
    private JLabel lblChars2;
    private JLabel lblTime;

    private Runnable onNextClick;
    private Runnable onRepeatClick;

    public Result() {
        setLayout(null);
        setBackground(UiUtil.BG_COLOR);
        
        HeaderBar header = new HeaderBar();
        add(header);
        
        // WPM
        JLabel txtwpm = new JLabel("wpm");
        txtwpm.setFont(new Font("Roboto Mono", Font.PLAIN, 20));
        txtwpm.setForeground(Color.decode("#8E9094"));
        txtwpm.setBounds(405, 200, 150, 28);
        add(txtwpm);

        lblwpm = new JLabel("0");
        lblwpm.setFont(new Font("Roboto Mono", Font.BOLD, 64));
        lblwpm.setForeground(UiUtil.TEAL);
        lblwpm.setBounds(400, 225, 250, 100);
        add(lblwpm);

        // Accuracy
        JLabel txtAcc = new JLabel("acc");
        txtAcc.setFont(new Font("Roboto Mono", Font.PLAIN, 20));
        txtAcc.setForeground(Color.decode("#8E9094"));
        txtAcc.setBounds(685, 200, 150, 28);
        add(txtAcc);

        lblAcc = new JLabel("0%");
        lblAcc.setFont(new Font("Roboto Mono", Font.BOLD, 64));
        lblAcc.setForeground(UiUtil.TEAL);
        lblAcc.setBounds(680, 225, 300, 100);
        add(lblAcc);

        // Type
        JLabel txtType = new JLabel("type");
        txtType.setFont(new Font("Roboto Mono", Font.PLAIN, 16));
        txtType.setForeground(Color.decode("#8E9094"));
        txtType.setBounds(311, 360, 200, 28);
        add(txtType);

        lblType = new JLabel("-");
        lblType.setFont(new Font("Roboto Mono", Font.BOLD, 24));
        lblType.setForeground(Color.decode("#000000"));
        lblType.setBounds(311, 385, 220, 40);
        add(lblType);

        // Characters Stats
        JLabel txtChars = new JLabel("characters");
        txtChars.setFont(new Font("Roboto Mono", Font.PLAIN, 16));
        txtChars.setForeground(Color.decode("#8E9094"));
        txtChars.setBounds(551, 360, 200, 28);
        add(txtChars);

        lblChars = new JLabel("0  / ");
        lblChars.setFont(new Font("Roboto Mono", Font.BOLD, 24));
        lblChars.setForeground(Color.decode("#000000"));
        lblChars.setBounds(551, 385, 100, 40); 
        add(lblChars);
        
        lblChars2 = new JLabel("0");
        lblChars2.setFont(new Font("Roboto Mono", Font.BOLD, 24));
        lblChars2.setForeground(Color.decode("#D14B57"));
        lblChars2.setBounds(651, 385, 100, 40); 
        add(lblChars2);

        // Time
        JLabel txtTime = new JLabel("time");
        txtTime.setFont(new Font("Roboto Mono", Font.PLAIN, 16));
        txtTime.setForeground(Color.decode("#8E9094"));
        txtTime.setBounds(813, 360, 150, 28);
        add(txtTime);

        lblTime = new JLabel("0s");
        lblTime.setFont(new Font("Roboto Mono", Font.BOLD, 24));
        lblTime.setForeground(Color.decode("#000000"));
        lblTime.setBounds(813, 385, 150, 40);
        add(lblTime);
        
        // Buttons
        JLabel btnNext = new JLabel();
        btnNext.setIcon(UiUtil.loadIcon("btnNext.png"));
        btnNext.setHorizontalAlignment(SwingConstants.CENTER);
        btnNext.setBounds(562, 480, 24, 24);
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNext.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(onNextClick != null) onNextClick.run();
            }
        });
        add(btnNext);

        JLabel btnRepeat = new JLabel();
        btnRepeat.setIcon(UiUtil.loadIcon("btnrepeat.png"));
        btnRepeat.setBounds(612, 480, 24, 24);
        btnRepeat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRepeat.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(onRepeatClick != null) onRepeatClick.run();
            }
        });
        add(btnRepeat);

        JLabel lblShortcut = new JLabel("<html><font color='#006664'>[ TAB ]</font> <font color='#999999'>- restart</font></html>");
        lblShortcut.setFont(new Font("Menlo", Font.BOLD, 14));
        lblShortcut.setBounds(490, 610, 200, 18);
        lblShortcut.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblShortcut);
        
        // ดักคีย์บอร์ดเฉพาะตอนแสดงหน้านี้ (TAB หรือ ESC เพื่อเริ่มใหม่)
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (!isShowing()) return false;
            if (e.getID() == java.awt.event.KeyEvent.KEY_PRESSED) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE || e.getKeyCode() == java.awt.event.KeyEvent.VK_TAB) {
                    if(onNextClick != null) onNextClick.run();
                    return true;
                }
            }
            return false;
        });
    }     
    
    public void setOnNextClick(Runnable r) {
        this.onNextClick = r;
    }
    
    public void setOnRepeatClick(Runnable r) {
        this.onRepeatClick = r;
    }

    /**
     * นำออบเจกต์ Score มาเซ็ตค่าลงไปใน UI Label
     */
    public void setScore(Score score) {
        lblwpm.setText(String.format("%.0f", score.getWpm()));
        lblAcc.setText(String.format("%.0f%%", score.getAccuracy()));
        
        String modeStr = score.getMode().equals(Score.getModeTime()) ? "time " : "words ";
        lblType.setText(modeStr + score.getAmount());
        
        lblChars.setText(score.getCorrectChars() + " / ");
        lblChars2.setText(String.valueOf(score.getIncorrectChars()));
        
        lblTime.setText(score.getSeconds() + "s");
    }
}
