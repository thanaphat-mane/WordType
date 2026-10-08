package ui.components;

import javax.swing.*;

import ui.UiUtil;

/**
 * คอมโพเนนต์สำหรับแผงควบคุมการเลือกโหมด (Mode) การพิมพ์ 
 * รองรับการเลือกโหมดหลัก 2 แบบคือ: 
 * 1. แบบจับเวลา (Time) - กำหนดเวลาถอยหลัง (15, 30, 60, 120 วินาที)
 * 2. แบบกำหนดจำนวนคำ (Words) - กำหนดโควต้าคำที่ต้องพิมพ์ (10, 25, 50, 100 คำ)
 * โครงสร้างจะใช้ JToggleButton (RoundButton) จัดกลุ่มด้วย ButtonGroup
 */
public class ModeSelector extends JPanel {
    
    /**
     * คอนสตรักเตอร์สำหรับสร้างและจัดเรียงปุ่มเลือกโหมดและปุ่มตัวเลือกปริมาณ
     * ภายในจะจัดการ Layout, กลุ่มของปุ่ม (ButtonGroup) และ Event Listeners
     */
    public ModeSelector() {
        // ปิด Layout อัตโนมัติ เพื่อจัดวางปุ่มแบบกำหนดพิกัดเอง (Absolute Layout)
        setLayout(null);
        
        // ตั้งค่าให้แผงควบคุมนี้โปร่งใส กลืนไปกับพื้นหลังเดิม
        setOpaque(false);

        // =========================================================
        // --- ส่วนที่ 1: โซนเลือกโหมดหลัก (Time หรือ Words) ---
        // =========================================================
        
        // สร้าง ButtonGroup เพื่อให้ปุ่มในกลุ่มนี้สามารถเลือก (Select) ได้เพียงปุ่มเดียวเท่านั้นในเวลาเดียวกัน (เหมือนวิทยุ)
        ButtonGroup modeGroup = new ButtonGroup();

        // (1.1) สร้างปุ่มโหมด "เวลา" (Time Mode)
        // ใช้คลาส RoundButton ที่สร้างขึ้นเองเพื่อให้ได้ปุ่มขอบมน
        RoundButton btnTime = new RoundButton(" time");
        
        // โหลดไอคอนรูปนาฬิกาสำหรับสถานะปกติ (สีดำ)
        btnTime.setIcon(UiUtil.loadIcon("TImeModeIcon_OFF.png")); 
        
        // โหลดไอคอนรูปนาฬิกาสำหรับสถานะเมื่อถูกเลือก (สีขาว) เพื่อให้เข้ากับพื้นหลังสีเขียวตอน Active
        btnTime.setSelectedIcon(UiUtil.loadIcon("TImeModeIcon_ON.png")); 
        
        // กำหนดตำแหน่งและขนาดของปุ่ม Time (อยู่ที่มุมซ้ายสุด X=0)
        btnTime.setBounds(0, 0, 100, 38);
        
        // นำปุ่มเพิ่มเข้าสู่ Group เพื่อบังคับการเลือกแบบทีละปุ่ม
        modeGroup.add(btnTime); 
        
        // นำปุ่มแปะลงบน Panel นี้
        add(btnTime); 

        // (1.2) สร้างปุ่มโหมด "จำนวนคำ" (Words Mode)
        RoundButton btnWords = new RoundButton("A words");
        // จัดตำแหน่งให้อยู่ถัดจากปุ่ม Time ไปทางขวา
        btnWords.setBounds(108, 0, 100, 38);
        // เพิ่มเข้ากลุ่มเดียวกันกับปุ่ม Time
        modeGroup.add(btnWords); 
        // นำปุ่มแปะลงบน Panel
        add(btnWords);

        // กำหนดค่าเริ่มต้นของระบบ: เมื่อเปิดแผงนี้ขึ้นมา ให้ปุ่ม Time ถูกเลือกไว้ล่วงหน้า
        btnTime.setSelected(true);
     
        // =========================================================
        // --- ส่วนที่ 2: โซนเลือกปริมาณเป้าหมาย (Options) ---
        // =========================================================
        
        // สร้าง ButtonGroup อีกกลุ่มแยกต่างหาก สำหรับจัดการตัวเลือกปริมาณ 4 ตัวเลือก
        ButtonGroup amountGroup = new ButtonGroup(); 

        // สร้างปุ่มตัวเลือกที่ 1
        RoundButton btnOpt1 = new RoundButton("15");
        // จัดให้ปุ่มอยู่ถัดจากโซนเลือกโหมด โดยมีระยะห่างให้สวยงาม (X=240)
        btnOpt1.setBounds(240, 0, 54, 38);
        amountGroup.add(btnOpt1);
        add(btnOpt1);

        // สร้างปุ่มตัวเลือกที่ 2
        RoundButton btnOpt2 = new RoundButton("30");
        btnOpt2.setBounds(302, 0, 54, 38);
        amountGroup.add(btnOpt2);
        add(btnOpt2);

        // กำหนดค่าเริ่มต้น: สั่งให้ปุ่มที่ 2 (ตัวเลข 30) โดนเลือกเป็นตัวตั้งต้นตอนเปิดโปรแกรม
        btnOpt2.setSelected(true);

        // สร้างปุ่มตัวเลือกที่ 3
        RoundButton btnOpt3 = new RoundButton("60");
        btnOpt3.setBounds(364, 0, 54, 38);
        amountGroup.add(btnOpt3);
        add(btnOpt3);

        // สร้างปุ่มตัวเลือกที่ 4 (ต้องใช้ความกว้างเยอะหน่อย เพราะเลขมีถึง 3 หลักในบางกรณี)
        RoundButton btnOpt4 = new RoundButton("120");
        btnOpt4.setBounds(426, 0, 60, 38);
        amountGroup.add(btnOpt4);
        add(btnOpt4);
                                                                                                        
        // =========================================================
        // --- ส่วนที่ 3: Event Listeners (การทำงานเชื่อมโยงกัน) ---
        // =========================================================
        
        // ตั้งค่าเหตุการณ์: เมื่อผู้เล่นคลิกปุ่มโหมด "เวลา" (Time)
        // ข้อความในปุ่มตัวเลือกทั้ง 4 จะถูกเปลี่ยนเพื่อแสดงหน่วยเป็น 'วินาที'
        btnTime.addActionListener(e -> {                                                                                
            btnOpt1.setText("15");                                                                                      
            btnOpt2.setText("30");                                                                                      
            btnOpt3.setText("60");                                                                                      
            btnOpt4.setText("120");                                                                                     
        });                                                                                                             
                                                                                                                            
        // ตั้งค่าเหตุการณ์: เมื่อผู้เล่นคลิกปุ่มโหมด "จำนวนคำ" (Words)
        // ข้อความในปุ่มตัวเลือกทั้ง 4 จะถูกเปลี่ยนเป็นชุดตัวเลขเป้าหมายแบบ 'จำนวนคำ'
        btnWords.addActionListener(e -> {                                                                               
            btnOpt1.setText("10");                                                                                      
            btnOpt2.setText("25");                                                                                      
            btnOpt3.setText("50");                                                                                      
            btnOpt4.setText("100");                                                                                     
        });
    }
}
