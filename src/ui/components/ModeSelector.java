package ui.components;

import javax.swing.*;

import ui.UiUtil;

public class ModeSelector extends JPanel{
    public  ModeSelector() {
        setLayout(null);
        setOpaque(false);

        // --- โซนปุ่ม 2 mode ButtonGroup คุมว่าให้เลือกได้เเค่ mode เดียว ---
        ButtonGroup modeGroup = new ButtonGroup();

        // สร้างปุ่ม time สีเขียว! 
        RoundButton btnTime = new RoundButton(" time");
        btnTime.setIcon(UiUtil.loadIcon("TImeModeIcon_OFF.png")); // รูปตอนยังไม่โดนเลือก (icon สีดำ)
        btnTime.setSelectedIcon(UiUtil.loadIcon("TImeModeIcon_ON.png")); // รูปตอนถูกเลือก (icon สีขาว)
        btnTime.setBounds(0, 0, 100, 38);
        modeGroup.add(btnTime); // นำเข้า Group (modeGroup)
        add(btnTime); 

        // สร้างปุ่ม words
        RoundButton btnWords = new RoundButton("A words");
        btnWords.setBounds(108,0,100,38);
        modeGroup.add(btnWords); // นำเข้า Group (modeGroup)
        add(btnWords);

        // ทำให้ time โดนเลืิอก ตั้งแต่เปิดโปรแกรม
        btnTime.setSelected(true);
     
        // --- โซนปุ่มตัวเลข 4 ปุ่ม (จัดเข้า ButtonGroup อีกกลุ่ม) --- 
        ButtonGroup amountGroup = new ButtonGroup(); // สำหรับตัวเลข

        RoundButton btnOpt1 = new RoundButton("15");
        btnOpt1.setBounds(240, 0,54,38);
        amountGroup.add(btnOpt1);
        add(btnOpt1);

        RoundButton btnOpt2 = new RoundButton("30");
        btnOpt2.setBounds(302, 0,54,38);
        amountGroup.add(btnOpt2);
        add(btnOpt2);

        // สั่งให้ปุ่มที่ 2 (เลข 30) โดนเลือกเป็นตัวตั้งต้น
        btnOpt2.setSelected(true);

        RoundButton btnOpt3 = new RoundButton("60");
        btnOpt3.setBounds(364, 0,54,38);
        amountGroup.add(btnOpt3);
        add(btnOpt3);

        RoundButton btnOpt4 = new RoundButton("120");
        btnOpt4.setBounds(426, 0,60,38);
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
    }
}
