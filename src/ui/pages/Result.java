package ui.pages;
import java.awt.*;
import javax.swing.*;
import ui.UiUtil;
import ui.components.HeaderBar;

public class Result extends JPanel{

    public Result() {
        setLayout(null);
        setBackground(UiUtil.BG_COLOR);
        

        // 1. HeaderBar
        HeaderBar header = new HeaderBar();
        add(header);
        
        
        // wpm
       JLabel txtwpm = new JLabel("wpm");
       txtwpm.setFont(new Font("Robotic Mono", Font.PLAIN, 20));
       txtwpm.setForeground(Color.decode("#8E9094"));
       txtwpm.setBounds(405, 200, 150, 28);
       add(txtwpm);

       JLabel lblwpm = new JLabel("72");
       lblwpm.setFont(new Font("Robotic Mono", Font.BOLD, 64));
       lblwpm.setForeground(UiUtil.TEAL);
       lblwpm.setBounds(400, 225, 250, 100);
       add(lblwpm);

       // acc 
       JLabel txtAcc = new JLabel("acc");
       txtAcc.setFont(new Font("Robotic Mono", Font.PLAIN, 20));
       txtAcc.setForeground(Color.decode("#8E9094"));
       txtAcc.setBounds(685, 200, 150, 28);
       add(txtAcc);

       JLabel lblAcc = new JLabel("96%");
       lblAcc.setFont(new Font("Robotic Mono", Font.BOLD, 64));
       lblAcc.setForeground(UiUtil.TEAL);
       lblAcc.setBounds(680, 225, 300, 100);
       add(lblAcc);

       // test types
       JLabel txtType = new JLabel("test type");
       txtType.setFont(new Font("Robotic Mono", Font.PLAIN, 16));
       txtType.setForeground(Color.decode("#8E9094"));
       txtType.setBounds(311, 360, 200, 28);
       add(txtType);

       JLabel lblType = new JLabel("words 25");
       lblType.setFont(new Font("Robotic Mono", Font.BOLD, 24));
       lblType.setForeground(Color.decode("#000000"));
       lblType.setBounds(311, 385, 220, 40);
       add(lblType);


       // characters
       JLabel txtChars = new JLabel("characters");
       txtChars.setFont(new Font("Robotic Mono", Font.PLAIN, 16));
       txtChars.setForeground(Color.decode("#8E9094"));
       txtChars.setBounds(551, 360, 200, 28);
       add(txtChars);

       JLabel lblChars = new JLabel("145  / ");
       lblChars.setFont(new Font("Robotic Mono", Font.BOLD, 24));
       lblChars.setForeground(Color.decode("#000000"));
       lblChars.setBounds(551, 385, 220, 40);
       add(lblChars);
       
       JLabel lblChars2 = new JLabel("2");
       lblChars2.setFont(new Font("Robotic Mono", Font.BOLD, 24));
       lblChars2.setForeground(Color.decode("#D14B57"));
       lblChars2.setBounds(625, 385, 220, 40);
       add(lblChars2);

       // time
       JLabel txtTime = new JLabel("time");
       txtTime.setFont(new Font("Robotic Mono", Font.PLAIN, 16));
       txtTime.setForeground(Color.decode("#8E9094"));
       txtTime.setBounds(813, 360, 150, 28);
       add(txtTime);

       JLabel lblTime = new JLabel("24s");
       lblTime.setFont(new Font("Robotic Mono", Font.BOLD, 24));
       lblTime.setForeground(Color.decode("#000000"));
       lblTime.setBounds(813, 385, 150, 40);
       add(lblTime);

       // Next button
       JLabel btnNext = new JLabel();
       btnNext.setIcon(UiUtil.loadIcon("btnNext.png"));
       btnNext.setHorizontalAlignment(SwingConstants.CENTER);
       btnNext.setForeground(Color.decode("#000000"));
       btnNext.setBounds(562, 480, 24, 24);
       btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
       add(btnNext);

       // Restart button
       JLabel btnRepeat = new JLabel();
       btnRepeat.setIcon(UiUtil.loadIcon("btnrepeat.png"));
       btnRepeat.setBounds(612, 480, 24, 24);
       btnRepeat.setCursor(new Cursor(Cursor.HAND_CURSOR));
       add(btnRepeat);

       // Restart test [ESC]
       JLabel lblShortcut = new JLabel("<html><font color='#006664'>[ ESC ]</font> <font color='#000000'>- restart test</font></html>");
       lblShortcut.setFont(new Font("Menlo", Font.BOLD, 14));
       lblShortcut.setBounds(490, 610, 200, 18);
       lblShortcut.setHorizontalAlignment(SwingConstants.CENTER);
       add(lblShortcut);
    }     
       
}


