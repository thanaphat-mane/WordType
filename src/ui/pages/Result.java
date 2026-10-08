package ui.pages;

import java.awt.*;
import javax.swing.*;
import ui.UiUtil;
import ui.components.HeaderBar;

/**
 * คลาส Result ทำหน้าที่เป็น "หน้าจอแสดงผลลัพธ์" หลังจากการพิมพ์จบ
 * <p>
 * เมื่อผู้ใช้ทำการทดสอบพิมพ์ดีดเสร็จ ระบบจะนำข้อมูลดิบมาคำนวณ
 * และแสดงผลออกมาในรูปแบบกราฟิกสวยงามในหน้านี้ (เช่น โชว์ตัวเลข WPM และ ACC ขนาดใหญ่)
 */
public class Result extends JPanel {

    /**
     * คอนสตรักเตอร์สำหรับสร้างหน้าจอ Result
     * ประกอบด้วยข้อความ หัวข้อ และตัวเลขผลลัพธ์ต่างๆ
     */
    public Result() {
        // 1. ปิดตัวจัดการเลย์เอาต์อัตโนมัติ เพื่อให้สามารถกำหนดพิกัด X, Y แบบเจาะจงได้
        setLayout(null);
        
        // 2. กำหนดสีพื้นหลังหลักของโปรแกรม
        setBackground(UiUtil.BG_COLOR);
        
        // ----------------------------------------------------
        // 3. ส่วนหัว (Header)
        // ----------------------------------------------------
        // นำ HeaderBar มาวางด้านบนสุดของจอ
        HeaderBar header = new HeaderBar();
        add(header);
        
        // ----------------------------------------------------
        // 4. ส่วนแสดงผลคำต่อนาที (WPM - Words Per Minute)
        // ----------------------------------------------------
        // สร้างป้ายข้อความเล็กบอกชื่อหัวข้อ "wpm"
        JLabel txtwpm = new JLabel("wpm");
        txtwpm.setFont(new Font("Roboto Mono", Font.PLAIN, 20)); // ใช้ Roboto Mono (ต้องแก้ชื่อฟอนต์จาก Robotic Mono ให้ถูก)
        txtwpm.setForeground(Color.decode("#8E9094")); // สีเทา
        txtwpm.setBounds(405, 200, 150, 28);
        add(txtwpm);

        // สร้างป้ายข้อความใหญ่เพื่อแสดงตัวเลข WPM ที่แท้จริง (สมมติว่าเป็น 72)
        JLabel lblwpm = new JLabel("72");
        lblwpm.setFont(new Font("Roboto Mono", Font.BOLD, 64)); // ตัวใหญ่พิเศษ (64px)
        lblwpm.setForeground(UiUtil.TEAL); // ใช้สีเขียวแบรนด์เน้นตัวเลขให้เด่นชัด
        lblwpm.setBounds(400, 225, 250, 100);
        add(lblwpm);

        // ----------------------------------------------------
        // 5. ส่วนแสดงผลความแม่นยำ (ACC - Accuracy)
        // ----------------------------------------------------
        // สร้างป้ายบอกชื่อหัวข้อ "acc"
        JLabel txtAcc = new JLabel("acc");
        txtAcc.setFont(new Font("Roboto Mono", Font.PLAIN, 20));
        txtAcc.setForeground(Color.decode("#8E9094"));
        txtAcc.setBounds(685, 200, 150, 28);
        add(txtAcc);

        // สร้างป้ายแสดงตัวเลขเปอร์เซ็นต์ความแม่นยำ (สมมติว่าเป็น 96%)
        JLabel lblAcc = new JLabel("96%");
        lblAcc.setFont(new Font("Roboto Mono", Font.BOLD, 64));
        lblAcc.setForeground(UiUtil.TEAL);
        lblAcc.setBounds(680, 225, 300, 100);
        add(lblAcc);

        // ----------------------------------------------------
        // 6. ส่วนรายละเอียดโหมดที่ใช้ทดสอบ (Test Type)
        // ----------------------------------------------------
        JLabel txtType = new JLabel("test type");
        txtType.setFont(new Font("Roboto Mono", Font.PLAIN, 16));
        txtType.setForeground(Color.decode("#8E9094"));
        txtType.setBounds(311, 360, 200, 28);
        add(txtType);

        // ระบุว่าพิมพ์ในโหมดอะไร (เช่น โหมดนับคำ "words 25")
        JLabel lblType = new JLabel("words 25");
        lblType.setFont(new Font("Roboto Mono", Font.BOLD, 24));
        lblType.setForeground(Color.decode("#000000")); // สีดำ
        lblType.setBounds(311, 385, 220, 40);
        add(lblType);

        // ----------------------------------------------------
        // 7. ส่วนรายละเอียดจำนวนตัวอักษร (Characters Stats)
        // ----------------------------------------------------
        // สถิติจะแยกย่อยเป็น (ตัวอักษรที่พิมพ์ถูก / ตัวอักษรที่พิมพ์ผิด)
        JLabel txtChars = new JLabel("characters");
        txtChars.setFont(new Font("Roboto Mono", Font.PLAIN, 16));
        txtChars.setForeground(Color.decode("#8E9094"));
        txtChars.setBounds(551, 360, 200, 28);
        add(txtChars);

        // ตัวเลขของตัวอักษรที่ "พิมพ์ถูก" 
        JLabel lblChars = new JLabel("145  / ");
        lblChars.setFont(new Font("Roboto Mono", Font.BOLD, 24));
        lblChars.setForeground(Color.decode("#000000"));
        lblChars.setBounds(551, 385, 220, 40);
        add(lblChars);
        
        // ตัวเลขของตัวอักษรที่ "พิมพ์ผิด" (ใช้สีแดงเตือนสายตาผู้ใช้)
        JLabel lblChars2 = new JLabel("2");
        lblChars2.setFont(new Font("Roboto Mono", Font.BOLD, 24));
        lblChars2.setForeground(Color.decode("#D14B57")); // สีแดง (#D14B57)
        lblChars2.setBounds(625, 385, 220, 40);
        add(lblChars2);

        // ----------------------------------------------------
        // 8. ส่วนระยะเวลาที่ใช้ (Time)
        // ----------------------------------------------------
        JLabel txtTime = new JLabel("time");
        txtTime.setFont(new Font("Roboto Mono", Font.PLAIN, 16));
        txtTime.setForeground(Color.decode("#8E9094"));
        txtTime.setBounds(813, 360, 150, 28);
        add(txtTime);

        // เวลาทั้งหมดที่ใช้ในการทดสอบจนจบ (เช่น 24 วินาที)
        JLabel lblTime = new JLabel("24s");
        lblTime.setFont(new Font("Roboto Mono", Font.BOLD, 24));
        lblTime.setForeground(Color.decode("#000000"));
        lblTime.setBounds(813, 385, 150, 40);
        add(lblTime);

        // ----------------------------------------------------
        // 9. ปุ่มดำเนินการต่อ (Action Buttons)
        // ----------------------------------------------------
        
        // ปุ่ม Next: ไปหน้าถัดไป หรือเริ่มรอบใหม่ในโหมดเดิมแบบสุ่มคำใหม่
        JLabel btnNext = new JLabel();
        btnNext.setIcon(UiUtil.loadIcon("btnNext.png"));
        btnNext.setHorizontalAlignment(SwingConstants.CENTER);
        btnNext.setForeground(Color.decode("#000000"));
        btnNext.setBounds(562, 480, 24, 24);
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR)); // เมาส์เปลี่ยนเป็นรูปมือ
        add(btnNext);

        // ปุ่ม Repeat: ทำซ้ำรอบเดิม (พิมพ์ชุดคำศัพท์เดิมเป๊ะๆ เพื่อแก้ตัว)
        JLabel btnRepeat = new JLabel();
        btnRepeat.setIcon(UiUtil.loadIcon("btnrepeat.png"));
        btnRepeat.setBounds(612, 480, 24, 24);
        btnRepeat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(btnRepeat);

        // ----------------------------------------------------
        // 10. คำอธิบายคีย์ลัด (Shortcut Label)
        // ----------------------------------------------------
        // แนะนำผู้ใช้ว่าสามารถกดปุ่ม [ESC] เพื่อข้ามหน้านี้แล้วเริ่มทดสอบใหม่ได้ทันที
        JLabel lblShortcut = new JLabel("<html><font color='#006664'>[ ESC ]</font> <font color='#000000'>- restart test</font></html>");
        lblShortcut.setFont(new Font("Menlo", Font.BOLD, 14));
        lblShortcut.setBounds(490, 610, 200, 18);
        lblShortcut.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblShortcut);
    }     
}
