package ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * คลาสสำหรับปุ่มที่มีลักษณะขอบมนและรองรับการกด (Action)
 * สืบทอดจาก JButton เพื่อให้สามารถใช้งานฟังก์ชันของปุ่มได้ครบถ้วน
 * เหมาะสำหรับปุ่มที่ต้องมีการโต้ตอบ (Interactive) เช่น ปุ่มเริ่มเกม, ปุ่มบันทึก
 */
public class ActionRoundButton extends JButton {
    
    /**
     * คอนสตรักเตอร์สำหรับสร้างปุ่มขอบมนพร้อมข้อความ
     *
     * @param text ข้อความที่จะแสดงบนปุ่ม
     */
    public ActionRoundButton(String text) {
        // เรียกใช้คอนสตรักเตอร์ของคลาสแม่ (JButton) เพื่อกำหนดข้อความบนปุ่ม
        super(text);
        
        // --- การตั้งค่า UI พื้นฐานของปุ่ม ---
        // ปิดการระบายสีพื้นหลังมาตรฐานของปุ่ม เพื่อให้เราสามารถวาดพื้นหลังขอบมนเองได้ใน paintComponent
        setContentAreaFilled(false);
        
        // ปิดการวาดกรอบโฟกัส (เส้นประรอบข้อความเวลาคลิก) เพื่อความสวยงามและมินิมอล
        setFocusPainted(false);
        
        // ปิดการวาดเส้นขอบปุ่มแบบปกติ (ขอบสี่เหลี่ยม) เพราะเราจะวาดขอบมนด้วยกราฟิกเอง
        setBorderPainted(false);
        
        // เปลี่ยนเคอร์เซอร์เมาส์เป็นรูปมือ (Hand Cursor) เมื่อชี้ที่ปุ่ม เพื่อบ่งบอกว่าคลิกได้
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // กำหนดรูปแบบตัวอักษรบนปุ่ม ใช้ฟอนต์ "Roboto Mono" ตัวหนา (BOLD) ขนาด 15
        setFont(new Font("Roboto Mono", Font.BOLD, 15));
    }

    /**
     * เมธอด paintComponent ถูก Override เพื่อจัดการการวาดกราฟิกของปุ่มใหม่ทั้งหมด
     * ทั้งรูปทรงขอบมน และสีพื้นหลังที่เปลี่ยนแปลงตามสถานะการกด (Pressed)
     *
     * @param g กราฟิกคอนเทกซ์ที่ใช้ในการวาด (เหมือนพู่กันสำหรับวาด UI)
     */
    @Override
    protected void paintComponent(Graphics g) {
        // คัดลอกออบเจ็กต์ Graphics มาเป็น Graphics2D ซึ่งมีฟังก์ชันวาดรูปขั้นสูงกว่า
        Graphics2D g2 = (Graphics2D) g.create();
        
        // เปิดใช้งาน Antialiasing เพื่อลดรอยหยักบริเวณขอบโค้งมน ทำให้ขอบปุ่มดูเรียบเนียนขึ้น
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // --- การกำหนดสีพื้นหลังของปุ่มตามสถานะ ---
        // ดึงโมเดลของปุ่มมาตรวจสอบสถานะว่ากำลัง "ถูกกด" (Pressed) อยู่หรือไม่
        if (getModel().isPressed()) {
            // ถ้าเมาส์กำลังกดปุ่มอยู่ (กดค้างไว้) ให้ใช้สีเทาเข้ม (#BFC2C8) เพื่อให้ปุ่มดูยุบลงไป (Visual feedback)
            g2.setColor(Color.decode("#BFC2C8")); 
        } else {
            // ถ้าเป็นสถานะปกติ (ไม่ถูกกด) ให้ใช้สีเทาอ่อน (#D1D3D8)
            g2.setColor(Color.decode("#D1D3D8")); 
        }
        
        // กำหนดสีของข้อความบนปุ่มให้เป็นสีดำเสมอ เพื่อให้ตัดกับสีพื้นหลังปุ่มอย่างชัดเจน
        setForeground(Color.BLACK);
        
        // --- การวาดรูปทรงของปุ่ม ---
        // วาดสี่เหลี่ยมขอบมน (Rounded Rectangle) แบบถมสีเต็ม (Fill) 
        // จุดเริ่มต้น (x=0, y=0) กว้างและสูงเท่ากับขนาดของปุ่ม (getWidth(), getHeight())
        // รัศมีความโค้งของมุมแกน X และ Y คือ 20 พิกเซล
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        
        // คืนทรัพยากรกราฟิกกลับคืนสู่ระบบเพื่อป้องกันปัญหา memory leak
        g2.dispose();
        
        // เรียกใช้ paintComponent ของคลาสแม่ (JButton) เพื่อให้วาดข้อความ (Text) หรือไอคอน (Icon) ทับลงบนพื้นหลังที่เราเพิ่งวาด
        super.paintComponent(g);
    }
}
