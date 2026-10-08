package ui.components;

import java.awt.*;
import javax.swing.*;

import ui.UiUtil;

/**
 * คอมโพเนนต์สำหรับแสดงข้อมูลคะแนน (Score) หนึ่งรายการในรูปแบบของ "แถว" (Row)
 * ใช้สำหรับแสดงผลในหน้ารายการจัดอันดับ (Leaderboard) หรือตารางประวัติคะแนน
 * รองรับการแสดงผลแถวสลับสี (Zebra striping) และการเน้นสีแถวพิเศษ (Highlight) 
 * เช่น เพื่อแสดงแถวคะแนนของผู้ใช้งานปัจจุบันให้โดดเด่น
 */
public class ScoreRow extends JPanel {
    // ตัวแปรเก็บสถานะการเน้นสีแถว (true = เน้นสีเขียวเพื่อบอกว่าเป็นข้อมูลของเรา, false = แถวปกติ)
    private boolean isHighlight; 
    
    // ตัวแปรเก็บสถานะว่าแถวนี้เป็นแถวลำดับเลขคู่หรือไม่ เพื่อใช้เทคนิคสลับสีพื้นหลัง (Zebra Striping) ให้อ่านง่าย
    private boolean isEvenRow; 

    /**
     * คอนสตรักเตอร์สร้างข้อมูลแถวของตารางคะแนน
     *
     * @param rank        อันดับ หรือ หมายเลขของคะแนน (เช่น "1", "#2")
     * @param player      ชื่อผู้เล่น
     * @param wpm         ความเร็วในการพิมพ์ คำต่อนาที (Words Per Minute)
     * @param acc         ความแม่นยำในการพิมพ์ (Accuracy) เป็นเปอร์เซ็นต์
     * @param date        วันที่และเวลาที่ทำการพิมพ์
     * @param isHighlight กำหนดให้แถวนี้ถูกเน้น (Highlight) เป็นพิเศษหรือไม่
     */
    public ScoreRow(String rank, String player, String wpm, String acc, String date, boolean isHighlight) {
        // บันทึกสถานะการเน้นสี
        this.isHighlight = isHighlight;

        // --- การวิเคราะห์ลำดับ (Rank) เพื่อคำนวณการสลับสีแถว (Zebra striping) ---
        try {
            // ลบเครื่องหมาย "#" ออก (ถ้ามี) แล้วแปลงสตริงตัวเลขให้กลายเป็นชนิดข้อมูล int
            int rankNum = Integer.parseInt(rank.replace("#", ""));
            // ตรวจสอบว่าเป็นแถวคู่หรือไม่ โดยใช้ Modulo (%) หารด้วย 2 ถ้าเหลือเศษ 0 แปลว่าเป็นแถวคู่
            this.isEvenRow = (rankNum % 2 == 0);
        } catch (Exception e) {
            // หากเกิดข้อผิดพลาดในการแปลงค่า (เช่น rank ไม่ใช่ตัวเลข) ให้ถือว่าไม่ใช่แถวคู่ไว้ก่อน
            this.isEvenRow = false; 
        }
        
        // --- การตั้งค่า Panel เบื้องต้น ---
        // ยกเลิก Layout อัตโนมัติ เพื่อจัดวาง Component ภายในแบบ Manual (ใช้ระบุพิกัด X,Y เอาเอง)
        setLayout(null);
        
        // ตั้งค่าให้พื้นหลังโปร่งใส เพราะเราจะทำหน้าที่ระบายสีพื้นหลังขอบมนเองใน paintComponent
        setOpaque(false);

        // --- การเตรียมทรัพยากรกราฟิก (สีและฟอนต์) ---
        // ถ้าเป็นแถว Highlight ข้อความทั้งหมดในแถวจะเป็นสีขาว ถ้าไม่ใช่ข้อความจะเป็นสีเทาเข้ม
        Color textColor = isHighlight ? Color.WHITE : Color.decode("#4B4D54");                                                                                   
        // ฟอนต์พื้นฐานสำหรับข้อความส่วนใหญ่ในแถว
        Font font = new Font("Roboto Mono", Font.PLAIN, 18);

        // --- 1. การแสดงผลอันดับ (Rank) ---
        // หากอันดับเป็นที่ 1 จะมีสิทธิพิเศษในการแสดงไอคอนมงกุฎแทนตัวเลข!
        if (rank.equals("#1") || rank.equals("1")) {                                                                                                             
            // โหลดไอคอนมงกุฎจาก UiUtil 
            JLabel lblCrown = new JLabel(UiUtil.loadIcon("LeaderIcon_ON.png"));                                                                                  
            // วางตำแหน่งไอคอนไว้ด้านซ้ายสุดของแถว
            lblCrown.setBounds(35, 13, 20, 20);                                                                                                                  
            add(lblCrown);                                                                                                                                       
        } else {                                                                                                                                                 
            // หากไม่ใช่อันดับ 1 ให้แสดงข้อความอันดับปกติ
            JLabel lblRank = new JLabel(rank);                                                                                                                   
            lblRank.setFont(font);                                                                                                                               
            lblRank.setForeground(textColor);                                                                                                                    
            lblRank.setBounds(35, 13, 60, 20);                                                                                                                   
            add(lblRank);                                                                                                                                        
        }
        
        // --- 2. การแสดงผลชื่อผู้เล่น (Player Name) ---
        JLabel lblPlayer = new JLabel(player);                                                                                                                   
        lblPlayer.setFont(font);                                                                                                                                 
        lblPlayer.setForeground(textColor);                                                                                                                      
        // กำหนดพิกัดเว้นระยะจากเลข Rank เข้ามาพอสมควร โดยมีความกว้าง 250 รองรับชื่อยาวๆ
        lblPlayer.setBounds(140, 13, 250, 20);                                                                                                                   
        add(lblPlayer);

        // --- 3. การแสดงผลความเร็ว WPM (Words Per Minute) ---
        JLabel lblWpm = new JLabel(wpm);                                                                                                                         
        // ใช้ตัวหนา (BOLD) เพื่อให้คะแนนความเร็วโดดเด่นสะดุดตา
        lblWpm.setFont(new Font("Roboto Mono", Font.BOLD, 18));                                                                                                  
        
        // ลอจิกพิเศษสำหรับการเปลี่ยนสีคะแนน WPM
        if (isHighlight) {
            lblWpm.setForeground(Color.WHITE); // ถ้าเป็นแถวของเรา สีขาวตัดพื้นเขียว
        } else if (rank.equals("#1") || rank.equals("1")) {
            lblWpm.setForeground(Color.decode("#006664")); // ถ้าเป็นที่ 1 แม้ไม่ใช่แถวเรา ให้คะแนนเป็นสีเขียวเข้มเพื่อความโดดเด่น
        } else {
            lblWpm.setForeground(textColor); // แถวทั่วไปก็ใช้สีข้อความปกติ
        }
        lblWpm.setBounds(470, 13, 60, 20);                                                                                                                       
        add(lblWpm);                                                                                                                                             
                                                                                                                                                                     
        // --- 4. การแสดงผลเปอร์เซ็นต์ความแม่นยำ (Accuracy) ---
        JLabel lblAcc = new JLabel(acc);                                                                                                                         
        lblAcc.setFont(font);                                                                                                                                    
        lblAcc.setForeground(textColor);                                                                                                                         
        lblAcc.setBounds(620, 13, 60, 20);                                                                                                                       
        add(lblAcc);   

        // --- 5. การแสดงผลวันที่และเวลา (Date) ---
        JLabel lblDate = new JLabel(date);                                                                                                                       
        // วันที่ใช้ฟอนต์ขนาดเล็กลง (14)
        lblDate.setFont(new Font("Roboto Mono", Font.PLAIN, 14));                                                                                                
        // ถ้าไม่ใช่แถว Highlight ให้สีวันที่จางลงอีกหน่อย เป็นสีเทาอ่อน (#8E9094) 
        lblDate.setForeground(isHighlight ? Color.WHITE : Color.decode("#8E9094"));                                                                              
        // จัดชิดขวาของแถว
        lblDate.setBounds(770, 14, 150, 20);                                                                                                                     
        add(lblDate);  
    }
    
    /**
     * วาดกราฟิกของแถวคะแนนแบบกำหนดเอง (Custom Painting)
     * โดยจะเปลี่ยนสีพื้นหลังขึ้นอยู่กับว่าเป็นแถวเน้น, แถวคู่, หรือแถวคี่ และทำขอบมนให้กับแถว
     *
     * @param g กราฟิกคอนเทกซ์ที่ใช้ในการวาด
     */
    @Override 
    protected void paintComponent(Graphics g) {
        // วาดส่วนประกอบพื้นฐานของ JPanel
        super.paintComponent(g);
        
        // ใช้ Graphics2D เพื่อให้วาดรูปขั้นสูงได้
        Graphics2D g2 = (Graphics2D) g.create();
        // ลบรอยหยักที่ขอบเขตของรูปทรง
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- การกำหนดสีพื้นหลังของแถว ---
        if (isHighlight) {
            // ถ้าเป็นแถวของตัวเอง (Highlight) ให้พื้นหลังเป็นสีเขียวประจำแอป (#006664)
            g2.setColor(Color.decode("#006664"));
        } else {
            // ถ้าไม่ใช่แถว Highlight จะสลับสีพื้นหลังเพื่อให้อ่านง่าย
            if (isEvenRow) {
                // แถวคู่เป็นสีเทาสว่าง (#D8D9DD)
                g2.setColor(Color.decode("#D8D9DD"));
            } else {
                // แถวคี่เป็นสีเทาเข้มกว่านิดหน่อย (#C5C7CC)
                g2.setColor(Color.decode("#C5C7CC"));
            }
        }
        
        // วาดพื้นหลังรูปสี่เหลี่ยมผืนผ้าแบบกำหนดขอบมน โดยให้มุมมีความโค้งรัศมี 15 พิกเซล
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        
        // ทำลายออบเจ็กต์ Graphics ย่อยเมื่อวาดเสร็จ ป้องกันหน่วยความจำรั่วไหล
        g2.dispose();
    }
}
