package ui.pages;

import java.awt.*;
import javax.swing.*;

import ui.UiUtil;
import ui.components.ActionRoundButton;
import ui.components.HeaderBar;
import ui.components.ModeSelector;
import ui.components.ScoreRow;

/**
 * คลาส Leaderboard ทำหน้าที่เป็น "หน้าจอแสดงตารางผู้นำ (กระดานคะแนน)"
 * <p>
 * แสดงสถิติการพิมพ์ของผู้เล่นทั้งหมด รวมถึงอันดับ (Rank), ชื่อ, ความเร็ว (WPM),
 * ความแม่นยำ (ACC), และวันที่ทดสอบ เพื่อให้ผู้เล่นสามารถเปรียบเทียบฝีมือกับผู้อื่นได้
 */
public class Leaderboard extends JPanel {
    
    /**
     * คอนสตรักเตอร์สำหรับสร้างหน้าจอ Leaderboard
     * ทำหน้าที่จัดวางเลย์เอาต์ (หัวตาราง, รายชื่อผู้เล่น, ปุ่มเปลี่ยนหน้า)
     */
    public Leaderboard() {
        // 1. ปิดเลย์เอาต์อัตโนมัติ เพื่อจัดวางพิกัด X, Y (Absolute Positioning) ได้อย่างแม่นยำตามดีไซน์
        setLayout(null);
        
        // 2. กำหนดสีพื้นหลังให้เป็นสีมาตรฐาน (เทาอ่อน)
        setBackground(UiUtil.BG_COLOR);

        // ----------------------------------------------------
        // ส่วนประกอบส่วนหัวของหน้าจอ
        // ----------------------------------------------------
        // 3. นำ HeaderBar (แถบเมนูข้างบน) มาติดตั้ง
        HeaderBar header = new HeaderBar();                                                                             
        add(header); 

        // 4. นำ ModeSelector (แถบเลือกโหมด) มาติดตั้ง 
        // เผื่อว่าในอนาคตผู้ใช้อยากดูตารางคะแนนแยกตามโหมด (เช่น โหมด 15 วินาที, โหมด 30 คำ)
        ModeSelector modeSelector = new ModeSelector(); 
        modeSelector.setBounds(342, 125, 486, 38); 
        add(modeSelector);

        // ----------------------------------------------------
        // ส่วนหัวตาราง (Table Header)
        // ----------------------------------------------------
        // ตั้งค่าระยะห่างจากด้านบน (Y) และสไตล์ตัวอักษรของหัวตารางให้เป็นมาตรฐานเดียวกัน
        int headerY = 180;
        Color headerColor = Color.decode("#8E9094"); // สีเทาเข้ม
        Font headerFont = new Font("Roboto Mono", Font.PLAIN, 13);

        // หัวข้อ: อันดับ (rank)
        JLabel lblRank = new JLabel("rank");
        lblRank.setFont(headerFont);
        lblRank.setForeground(headerColor);
        lblRank.setBounds(185, headerY, 32, 17);
        add(lblRank);

        // หัวข้อ: ชื่อผู้เล่น (player)
        JLabel lblPlayer = new JLabel("player");
        lblPlayer.setFont(headerFont);
        lblPlayer.setForeground(headerColor);
        lblPlayer.setBounds(290, headerY, 50, 17);
        add(lblPlayer);

        // หัวข้อ: ความเร็ว (wpm - words per minute)
        JLabel lblWpm = new JLabel("wpm");
        lblWpm.setFont(headerFont);
        lblWpm.setForeground(headerColor);
        lblWpm.setBounds(620, headerY, 24, 17);
        add(lblWpm);

        // หัวข้อ: ความแม่นยำ (acc - accuracy)
        JLabel lblAcc = new JLabel("acc");
        lblAcc.setFont(headerFont);
        lblAcc.setForeground(headerColor);
        lblAcc.setBounds(770, headerY, 24, 17);
        add(lblAcc);

        // หัวข้อ: วันที่ที่ทำการทดสอบ (date)
        JLabel lblDate = new JLabel("date");
        lblDate.setFont(headerFont);
        lblDate.setForeground(headerColor);
        lblDate.setBounds(920, headerY, 32, 17);
        add(lblDate);

        // ----------------------------------------------------
        // ส่วนแถวข้อมูลคะแนน (Score Rows)
        // ----------------------------------------------------
        // การกำหนดค่าตัวแปรตำแหน่งเริ่มต้น ช่วยให้คำนวณระยะห่างระหว่างแถวได้ง่ายขึ้น
        // ไม่ต้องมานั่งพิมพ์เลขพิกัดใหม่ทุกๆ บรรทัด
        int startY = 200;       // จุดเริ่มต้นแกน Y ของแถวแรก
        int rowHeight = 46;     // ความสูงของแต่ละกล่องแถว
        int gap = 8;            // ช่องว่างระหว่างแถว
        int rowWidth = 900;     // ความกว้างของกล่องแถว
        int startX = 150;       // จุดเริ่มต้นแกน X

        // แถวที่ 1 (อันดับ 1)
        // สร้างอ็อบเจ็กต์ ScoreRow โดยส่งข้อมูล (อันดับ, ชื่อ, wpm, acc, date, สถานะว่าใช่ผู้ใช้คนปัจจุบันหรือไม่)
        // boolean สุดท้ายเป็น false หมายถึงนี่ไม่ใช่ตัวเรา (เพื่อใช้แสดงสีไฮไลท์)
        ScoreRow row1 = new ScoreRow("#1", "kukps_dev", "98", "99%", "27 Sep 26", false);  
        row1.setBounds(startX, startY, rowWidth, rowHeight);
        add(row1);

        // แถวที่ 2
        // คำนวณพิกัด Y โดยเอา (ความสูง + ช่องว่าง) * ลำดับบรรทัด เพื่อเลื่อนลงมาด้านล่าง
        ScoreRow row2 = new ScoreRow("#2", "javamaster", "91", "98%", "27 Sep 26", false);                                                                       
        row2.setBounds(startX, startY + (rowHeight + gap) * 1, rowWidth, rowHeight);                                                                             
        add(row2);                                                                                                                                               
                                                                                                                                                                     
        // แถวที่ 3
        ScoreRow row3 = new ScoreRow("#3", "typing_pro", "84", "97%", "26 Sep 26", false);                                                                       
        row3.setBounds(startX, startY + (rowHeight + gap) * 2, rowWidth, rowHeight);                                                                             
        add(row3);                                                                                                                                               
                                                                                                                                                                     
        // แถวที่ 4 (สมมติว่าเป็นคะแนนของเรา แต่ไม่ได้อยู่ในโหมดไฮไลท์)
        ScoreRow row4 = new ScoreRow("#4", "user01 (you)", "72", "96%", "26 Sep 26", false);                                                                     
        row4.setBounds(startX, startY + (rowHeight + gap) * 3, rowWidth, rowHeight);                                                                             
        add(row4);                                                                                                                                               
                                                                                                                                                                     
        // แถวที่ 5
        ScoreRow row5 = new ScoreRow("#5", "rookie_09", "68", "94%", "25 Sep 26", false);                                                                        
        row5.setBounds(startX, startY + (rowHeight + gap) * 4, rowWidth, rowHeight);                                                                             
        add(row5);                                                                                                                                               
                                                                                                                                                                     
        // ----------------------------------------------------
        // แถบแสดงคะแนนสูงสุดของตัวเอง (Personal Best)
        // ----------------------------------------------------
        // เป็นกล่องแยกที่อยู่ล่างสุด เพื่อให้ผู้เล่นจำได้ว่าสถิติที่ดีที่สุดของตัวเองคือเท่าไหร่
        // สังเกตว่าพารามิเตอร์สุดท้ายเป็น true ซึ่งในคลาส ScoreRow จะจัดการไฮไลท์สีให้แตกต่างจากแถวอื่น
        ScoreRow bestRow = new ScoreRow("#4", "user01 (your best)", "72", "96%", "26 Sep 26", true);
        // เลื่อนระยะ Y ลงมาอีก 15 พิกเซล เพื่อแยกจากกลุ่มกระดานผู้นำให้ชัดเจน
        bestRow.setBounds(startX, startY + (rowHeight + gap) * 5 + 15, rowWidth, rowHeight);                                                                     
        add(bestRow); 
        
        // ----------------------------------------------------
        // ส่วนควบคุมการเปลี่ยนหน้า (Pagination)
        // ----------------------------------------------------
        
        // ปุ่มย้อนกลับ (Previous Page)
        ActionRoundButton btnPrev = new ActionRoundButton("<");
        btnPrev.setBounds(518, 550, 32, 32);
        add(btnPrev);

        // ข้อความบอกเลขหน้า (เช่น 1 จาก 200 หน้า)
        JLabel lblPageNum = new JLabel("1 / 200");
        lblPageNum.setFont(new Font("Roboto Mono", Font.BOLD, 16));
        lblPageNum.setForeground(Color.BLACK);
        lblPageNum.setBounds(565, 556, 70, 21);
        add(lblPageNum);

        // ปุ่มไปหน้าถัดไป (Next Page)
        ActionRoundButton btnNext = new ActionRoundButton(">");
        btnNext.setBounds(650, 550, 32, 32);
        add(btnNext);

        // ----------------------------------------------------
        // คำอธิบายคีย์ลัดสำหรับกลับหน้าหลัก
        // ----------------------------------------------------
        // ใช้แท็ก HTML ให้คำว่า [ESC] เป็นสี Teal เพื่อความโดดเด่น
        JLabel lblShortcut = new JLabel("<html><font color='#006664'>[ ESC ]</font> <font color='#000000'>- back to home</font></html>");
        lblShortcut.setFont(new Font("Menlo", Font.PLAIN, 14));
            
        // วางกึ่งกลางจอด้านล่าง
        lblShortcut.setBounds(508, 610, 184, 18);
        lblShortcut.setHorizontalAlignment(SwingConstants.CENTER); 
        add(lblShortcut);
    }

}
