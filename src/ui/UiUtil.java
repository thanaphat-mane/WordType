package ui;

import java.awt.*;
import javax.swing.*;

/**
 * กล่องเครื่องมือช่าง (Utility Class)
 * เก็บฟังก์ชันช่วยเหลือที่ทุกหน้าต้องใช้ เพื่อไม่ให้ต้องเขียนโค้ดซ้ำๆ
 */
public class UiUtil {
    
    // ที่อยู่ของโฟลเดอร์รูปภาพ (อ้างอิงจากโฟลเดอร์ resources)
    public static final String IMG_PATH = "/images/";

    // กำหนดสีประจำโปรเจกต์ (เอามาเรียกใช้ได้เลย เช่น UiUtil.TEAL)
    public static final Color BG_COLOR = new Color(234, 234, 234); // สีพื้นหลังเทาอ่อน
    public static final Color TEAL = new Color(0, 103, 91);       // สีเขียวโลโก้

    /**
     * ฟังก์ชันโหลดรูปภาพแบบปลอดภัย!
     * ถ้าหาไฟล์ไม่เจอ จะไม่เกิดเออเรอร์แดง (NullPointerException) 
     * แต่จะเตือนใน Console แทน ทำให้โปรแกรมไม่ค้าง
     */
    public static ImageIcon loadIcon(String fileName) {
        java.net.URL url = UiUtil.class.getResource(IMG_PATH + fileName);
        if (url == null) {
            System.err.println(IMG_PATH + fileName);
            return null; // ถ้าไม่เจอ ให้คืนค่าว่างไป (โปรแกรมจะไม่แครช)
        }
        return new ImageIcon(url);
    }
}
