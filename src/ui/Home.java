package ui;

import java.awt.*;
import javax.swing.*;

public class Home extends JPanel {

    public Home() {
        // 1. ปิด Layout เพื่อให้ใช้แกน X, Y ได้
        setLayout(null);
        setBackground(UiUtil.BG_COLOR);

        // 2. เรียกใช้ "เลโก้ส่วนหัว" (HeaderBar) มาแปะที่หน้าจอนี้!
        HeaderBar header = new HeaderBar();
        add(header);

        // ---  โซนปุ่ม 2 mode ButtonGroup คุมว่าให้เลือกได้เเค่ mode เดียว ----
        ButtonGroup modeGroup = new ButtonGroup();

        // สร้างปุ่ม time สีเขียว! 
        RoundButton btnTime = new RoundButton(" time");
        btnTime.setIcon(UiUtil.loadIcon("TImeModeIcon_OFF.png")); // รูปตอนยังไม่โดนเลือก (icon สีดำ)
        btnTime.setSelectedIcon(UiUtil.loadIcon("TImeModeIcon_ON.png")); // รูปตอนถูกเลือก (icon สีขาว)
        btnTime.setBounds(342, 125, 100, 38);
        modeGroup.add(btnTime); // นำเข้า Group (modeGroup)
        add(btnTime); 

        // สร้างปุ่ม words
        RoundButton btnWords = new RoundButton("A words");
        btnWords.setBounds(450,125,100,38);
        modeGroup.add(btnWords); // นำเข้า Group (modeGroup)
        add(btnWords);

        // ทำให้ time โดนเลืิอก ตั้งแต่เปิดโปรแกรม
        btnTime.setSelected(true);
     
        // --- โซนปุ่มตัวเลข 4 ปุ่ม (จัดเข้า ButtonGroup อีกกลุ่ม) --- 
        ButtonGroup amountGroup = new ButtonGroup(); // สำหรับตัวเลข

        RoundButton btnOpt1 = new RoundButton("15");
        btnOpt1.setBounds(582, 125,54,38);
        amountGroup.add(btnOpt1);
        add(btnOpt1);

        RoundButton btnOpt2 = new RoundButton("30");
        btnOpt2.setBounds(644, 125,54,38);
        amountGroup.add(btnOpt2);
        add(btnOpt2);

        // สั่งให้ปุ่มที่ 2 (เลข 30) โดนเลือกเป็นตัวตั้งต้น
        btnOpt2.setSelected(true);

        RoundButton btnOpt3 = new RoundButton("60");
        btnOpt3.setBounds(706, 125,54,38);
        amountGroup.add(btnOpt3);
        add(btnOpt3);

        RoundButton btnOpt4 = new RoundButton("120");
        btnOpt4.setBounds(768, 125,60,38);
        amountGroup.add(btnOpt4);
        add(btnOpt4);
                                                                                                        
        btnTime.addActionListener(e -> {                                                                                
            btnOpt1.setText("15");                                                                                      
            btnOpt2.setText("30");                                                                                      
            btnOpt3.setText("60");                                                                                      
            btnOpt4.setText("120");                                                                                     
        });                                                                                                             
                                                                                                                            
        btnWords.addActionListener(e -> {                                                                               
            btnOpt1.setText("10");                                                                                      
            btnOpt2.setText("25");                                                                                      
            btnOpt3.setText("50");                                                                                      
            btnOpt4.setText("100");                                                                                     
        });   

        // --- เรียกใช้ลานพิมพ์ดีดจากคลาส TypingView ---
        TypingView typingView = new TypingView();
        typingView.setBounds(150, 280, 900, 160); 
        add(typingView);


        // -- Restart button --
        JLabel btnRestart = new JLabel();
        btnRestart.setIcon(UiUtil.loadIcon("btnRestart.png"));
        btnRestart.setBounds(588, 465, 24, 24);
        btnRestart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(btnRestart);

        // --- ข้อความ [ ESC ] - restart test ใต้ปุ่มหมุน ---
        JLabel lblShortcut = new JLabel("<html><font color='#006664'>[ ESC ]</font> <font color='#000000'>- restart test</font></html>");
        lblShortcut.setFont(new Font("Menlo", Font.BOLD, 14));
            
        // ตั้งพิกัด X ให้อยู่ตรงกลางจอ (ใต้ปุ่ม Restart พอดีเป๊ะ)
        lblShortcut.setBounds(508, 610, 184, 18);
        lblShortcut.setHorizontalAlignment(SwingConstants.CENTER); // สั่งจัดข้อความให้อยู่กึ่งกลาง
        add(lblShortcut);

    }
}
