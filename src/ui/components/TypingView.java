package ui.components;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

/**
 * คอมโพเนนต์สำหรับพื้นที่แสดงและจัดการข้อความที่ใช้ในการฝึกพิมพ์
 * โดยโครงสร้างหลักจะเป็น JScrollPane (เพื่อรองรับกรณีข้อความยาวเกินหน้าจอ) 
 * และมี JTextPane อยู่ด้านในเป็นพื้นที่สำหรับเขียน/จัดรูปแบบข้อความพิมพ์
 */
public class TypingView extends JScrollPane {
    
    // คอมโพเนนต์หลักที่ใช้สำหรับแสดงและตกแต่งข้อความในการพิมพ์
    private JTextPane typeArea;

    /**
     * คอนสตรักเตอร์สร้างพื้นที่การพิมพ์ (Typing Area) 
     * พร้อมตั้งค่าการบล็อคการแก้ไขข้อความด้วยตัวผู้ใช้เอง สี ฟอนต์ 
     * และพฤติกรรมของการเลื่อนหน้าจอ
     */
    public TypingView() {
        // ====================================================================
        // --- 1. การตั้งค่า JTextPane (พื้นที่ข้อความด้านใน) ---
        // ====================================================================
        typeArea = new JTextPane();
        
        // ตั้งค่าให้พื้นหลังข้อความโปร่งใส เพื่อเผยให้เห็นพื้นหลังของแอปพลิเคชัน
        typeArea.setOpaque(false); 
        
        // **สำคัญมาก**: ล็อกพื้นที่ข้อความไว้ไม่ให้ผู้ใช้งานสามารถคลิกเอาเคอร์เซอร์ไปวางแล้วพิมพ์มั่วๆ ได้
        // เพราะเราจะควบคุมการพิมพ์ด้วย KeyEvent จากโค้ดหลังบ้าน (Engine) เอง
        typeArea.setEditable(false); 
        
        // ปิดไม่ให้คอมโพเนนต์นี้รับโฟกัสจากการคลิกเมาส์
        typeArea.setFocusable(false); 
        
        // ปิดระบบการไฮไลท์ (เช่น การใช้เมาส์ลากคลุมดำข้อความเพื่อคัดลอก) เพราะเกมพิมพ์ดีดไม่จำเป็นต้องใช้
        typeArea.setHighlighter(null); 
        
        // ตั้งค่าฟอนต์เป็น "Menlo" ซึ่งเป็น Monospace ฟอนต์ ทำให้ความกว้างตัวอักษรเท่ากันหมด
        // ขนาด 36 ซึ่งใหญ่พอให้อ่านและพิมพ์ได้ง่าย
        typeArea.setFont(new Font("Menlo", Font.PLAIN, 36)); 
        
        // สีตัวอักษรตั้งต้น (สำหรับคำที่ยังไม่ได้พิมพ์) เป็นสีเทาอ่อน (#8E9094) 
        typeArea.setForeground(Color.decode("#8E9094")); 

        // กำหนดข้อความทดสอบเริ่มต้นลงใน TextPane (จะถูกเขียนทับด้วยระบบสุ่มคำในอนาคต)
        typeArea.setText("he man own must tell such year keep leave too not " +
                         "when all would get number child that course set many late just");
        
        // นำเคอร์เซอร์จำลองไปไว้ที่ตำแหน่งที่ 0 (ตัวอักษรแรกสุด)
        typeArea.setCaretPosition(0);


        // ====================================================================
        // --- 2. การตั้งค่า JScrollPane (ตัวคลุมภายนอกซึ่งก็คือคลาสนี้เอง) ---
        // ====================================================================
        
        // นำ JTextPane ที่เตรียมไว้ ไปใส่เป็นมุมมอง (Viewport) ของ JScrollPane
        // เพื่อให้ถ้าข้อความยาวเกิน มันจะถูกขังอยู่ในนี้
        setViewportView(typeArea);
        
        // ปิดการแสดงผลแถบเลื่อนแนวตั้ง (Vertical Scrollbar) อย่างถาวร
        // เนื่องจากเราต้องการให้หน้าตาเกมคลีนที่สุด และเราอาจควบคุมการเลื่อนหน้าจออัตโนมัติด้วยโค้ด 
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER); 
        
        // ปิดการแสดงผลแถบเลื่อนแนวนอน (Horizontal Scrollbar) อย่างถาวร
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 
        
        // ลบเส้นขอบของ ScrollPane ออก เพื่อไม่ให้มีกรอบสี่เหลี่ยมขัดตา
        setBorder(null); 
        
        // ทำให้ ScrollPane ทั้งก้อนโปร่งใส
        setOpaque(false);
        
        // และทำให้พื้นที่แสดงผล (Viewport) ที่อยู่ตรงกลางโปร่งใสด้วย (จำเป็นสำหรับ JScrollPane)
        getViewport().setOpaque(false); 
    }
    
    // TODO: สร้าง method render ไว้รอรับข้อมูลจาก TypingEngine ในอนาคต
    // เช่น: public void render(TypingEngine engine, boolean showCaret) { ... }
    // เพื่ออัปเดตสีของตัวอักษรทีละตัวตามสถานะ (พิมพ์ถูก = ขาว, พิมพ์ผิด = แดง, ยังไม่พิมพ์ = เทา)
}
