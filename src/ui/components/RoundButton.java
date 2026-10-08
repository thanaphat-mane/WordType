package ui.components;
import javax.swing.JToggleButton;

import ui.UiUtil;

import java.awt.*;

/**
 * ปุ่มสลับสถานะ (Toggle Button) แบบมีลักษณะขอบมน
 * สืบทอดความสามารถจาก JToggleButton ซึ่งสามารถจดจำสถานะว่า "ถูกเลือก" (Selected) หรือไม่
 * เหมาะสำหรับใช้ทำปุ่มเลือกโหมด ตัวเลือกในเมนูต่างๆ ที่ผู้ใช้สามารถเปิด/ปิดการทำงานได้
 */
public class RoundButton extends JToggleButton {
    
    /**
     * คอนสตรักเตอร์สำหรับสร้างปุ่มขอบมนแบบสลับสถานะได้ พร้อมกำหนดข้อความ
     *
     * @param text ข้อความที่จะแสดงบนปุ่ม
     */
    public RoundButton(String text) {
        // เรียกคอนสตรักเตอร์ของ JToggleButton เพื่อใส่ข้อความ
        super(text);
        
        // --- การปิดระบบกราฟิกมาตรฐานของ Java Swing ---
        // ปิดการระบายสีพื้นหลังสี่เหลี่ยมแบบเดิม เพราะเราจะวาดสี่เหลี่ยมขอบมนด้วยตัวเอง
        setContentAreaFilled(false);
        
        // ปิดการวาดเส้นประแสดงสถานะโฟกัส เพื่อความเรียบง่ายและดูทันสมัย
        setFocusPainted(false);
        
        // ปิดการวาดเส้นขอบ (Border) เดิม
        setBorderPainted(false);
        
        // เปลี่ยนไอคอนลูกศรเมาส์เป็นรูปมือเมื่อชี้ที่ปุ่ม
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // ตั้งค่าฟอนต์หลักของปุ่ม ใช้ฟอนต์ "Roboto Mono" ขนาด 15 ตัวหนา
        setFont(new Font("Roboto Mono", Font.BOLD, 15));
    }
    
    /**
     * เมธอดสำหรับวาดรูปปุ่มใหม่ (Custom Render) 
     * จัดการทั้งความโค้งมนของขอบปุ่ม และการเปลี่ยนสีตามสถานะ Selected (ถูกเลือก) หรือไม่
     *
     * @param g กราฟิกคอนเทกซ์ที่เปรียบเสมือนพู่กันใช้ในการวาดบนคอมโพเนนต์
     */
    @Override 
    protected void paintComponent(Graphics g) {
        // ใช้ Graphics2D เพื่อให้สามารถเข้าถึงความสามารถในการวาดที่ละเอียดขึ้น
        Graphics2D g2 = (Graphics2D) g.create();
        
        // เปิดโหมด Antialiasing ช่วยลบรอยหยัก ทำให้ขอบมนของปุ่มดูเนียนตา ไม่แตกเป็นพิกเซล
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 

        // --- การตรวจสอบสถานะปุ่มเพื่อสลับสี (Toggle logic) ---
        // เมธอด isSelected() มาจาก JToggleButton ถ้าปุ่มถูกคลิกเลือก (Active) จะมีค่าเป็น true
        if (isSelected()) {
            // ถัาปุ่มกำลังถูกเลือกอยู่ ให้กำหนดสีพู่กันเป็นสีเขียว (UiUtil.TEAL) ซึ่งเป็นสีหลักของแอป
            g2.setColor(UiUtil.TEAL);
            // เปลี่ยนสีตัวอักษรให้เป็นสีขาว เพื่อให้ตัดกับพื้นหลังสีเขียว
            setForeground(Color.WHITE);
        } else {
            // ถ้าปุ่มไม่ได้ถูกเลือก (Inactive) ให้กำหนดสีพู่กันเป็นสีเทาอ่อน (#D1D3D8)
            g2.setColor(Color.decode("#D1D3D8"));
            // และเปลี่ยนสีตัวอักษรเป็นสีดำ เพื่อให้ตัดกับสีพื้นเทาอ่อน
            setForeground(Color.BLACK);
        }

        // --- วาดพื้นหลังของปุ่ม ---
        // วาดรูปสี่เหลี่ยมขอบมนแบบทึบ (Fill) 
        // ความกว้าง/ความสูง อ้างอิงจากขนาดคอมโพเนนต์ ส่วนเลข 20, 20 คือความโค้งของมุม (Corner Radius)
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        
        // คืนหน่วยความจำของ Graphics2D เพื่อป้องกัน memory leak 
        g2.dispose();
        
        // เรียกใช้ paintComponent ของคลาสแม่ เพื่อวาดข้อความ(Text) และไอคอน(Icon) ที่ถูกตั้งไว้ทับลงไป
        super.paintComponent(g);
    }
}
