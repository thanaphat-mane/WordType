import java.awt.*;
import java.io.*;
import javax.swing.*;
import service.AuthResult;
import service.AuthService;

/**
 * คลาสหลัก (Main) สำหรับจุดเริ่มต้น (Entry Point) ของแอปพลิเคชัน
 * <p>
 * หน้าที่หลัก: โหลดทรัพยากรเริ่มต้น (เช่น ฟอนต์), ตั้งค่าผู้ใช้จำลอง (Seed data), 
 * กำหนดธีมของหน้าต่างโปรแกรม, และสั่งให้หน้าจอหลัก (MainFrame) ปรากฏขึ้นมาทำงาน
 */
public class Main {

    // รายชื่อไฟล์ฟอนต์ทั้งหมดที่เราเตรียมไว้ในโฟลเดอร์ resources/fonts/
    // ที่ต้องโหลดเอง เพราะเราไม่อยากบังคับให้ผู้ใช้ต้องติดตั้งฟอนต์ลงในเครื่องก่อนใช้งาน
    private static final String[] FONT_FILES = {
        "RobotoMono-Regular.ttf",
        "RobotoMono-Medium.ttf",
        "RobotoMono-Bold.ttf",
        "SpaceGrotesk-Medium.ttf"
    };

    /** 
     * เมธอดสำหรับโหลดและลงทะเบียนฟอนต์เข้าสู่ระบบ
     * เพื่อให้สามารถเรียกใช้ new Font("ชื่อฟอนต์", ...) ได้เหมือนกับว่าติดตั้งฟอนต์ในระบบแล้ว
     */
    private static void loadFonts() {
        // 1. ดึง GraphicsEnvironment ซึ่งเป็นตัวจัดการหน้าจอและกราฟิกของระบบปัจจุบัน
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        
        // 2. วนลูปอ่านรายชื่อไฟล์ฟอนต์ที่กำหนดไว้
        for (String file : FONT_FILES) {
            // ใช้ try-with-resources ดึงไฟล์ฟอนต์มาเป็น InputStream 
            // วิธีนี้รองรับการดึงไฟล์จากใน .jar และปิดสตรีมให้อัตโนมัติเมื่อเสร็จสิ้น
            try (InputStream in = Main.class.getResourceAsStream("/fonts/" + file)) {
                
                // ตรวจสอบความปลอดภัย ถ้าหาไฟล์ไม่เจอ
                if (in == null) {
                    System.err.println("ไม่พบไฟล์ฟอนต์: /fonts/" + file);
                    continue; // ข้ามไปโหลดไฟล์ฟอนต์ถัดไป
                }
                
                // 3. สร้างอ็อบเจ็กต์ Font จาก InputStream โดยระบุรูปแบบเป็น TrueType Font
                Font font = Font.createFont(Font.TRUETYPE_FONT, in);
                
                // 4. สั่งให้ระบบ (GraphicsEnvironment) จดจำฟอนต์นี้เอาไว้
                ge.registerFont(font);
                
                // พิมพ์บอกสถานะการโหลดว่าสำเร็จแล้ว (ช่วยในการตรวจสอบบั๊ก)
                System.out.println("โหลดฟอนต์สำเร็จ: " + font.getFontName() + " (ตระกูล: " + font.getFamily() + ")");
            } catch (Exception e) {
                // หากไฟล์ฟอนต์เสียหาย หรืออ่านไม่ได้ จะจับ Exception พิมพ์บอก และไปทำฟอนต์ตัวต่อไปไม่ให้โปรแกรมแครช
                e.printStackTrace();
            }
        }
    }

    /**
     * เมธอดสำหรับสร้างบัญชีผู้ใช้จำลอง (Seed data)
     * <p>
     * เหตุผล: เพื่อความสะดวกในการพัฒนาระบบ ไม่ต้องมานั่งสมัครสมาชิกใหม่ทุกครั้งที่ล้างข้อมูล 
     * มีประโยชน์มากเวลาทดสอบระบบล็อกอิน
     */
    private static void seedTestUsers() {
        // 1. เตรียมข้อมูลผู้ใช้จำลอง ในรูปแบบอาร์เรย์ 2 มิติ {username, password}
        String[][] accounts = {
            {"KuyKranuiTan", "1234"},
            {"user1", "1234"},
            {"user2", "1234"}
        };
        
        // 2. เรียกใช้งานคลาสบริการ AuthService ที่มีหน้าที่จัดการผู้ใช้
        AuthService auth = new AuthService();
        
        // 3. วนลูปส่งข้อมูลไปลงทะเบียน
        for (String[] acc : accounts) {
            // พยายามลงทะเบียน หากชื่อผู้ใช้ซ้ำ (จากรอบที่แล้ว) บริการจะแจ้งกลับมาเอง
            AuthResult r = auth.register(acc[0], acc[1]);
            
            // แสดงสถานะว่าสร้างบัญชีสำเร็จ หรือมีเหตุผลใด (เช่น บัญชีมีอยู่แล้ว)
            System.out.println("[เตรียมข้อมูลจำลอง] " + acc[0] + ": " + (r.isSuccess() ? "สร้างบัญชีสำเร็จ" : r.getMessage()));
        }
    }

    /**
     * เมธอดจุดเริ่มต้นการทำงานของโปรแกรม (Entry point)
     * เมื่อรันโปรแกรม Java จะมองหาเมธอดนี้เป็นที่แรก
     *
     * @param args อาร์กิวเมนต์ที่ส่งมาจาก command line (ปัจจุบันไม่ได้ใช้)
     */
   public static void main(String[] args) {
        // 1. วาง FlatLaf และตั้งค่าสำหรับหน้าจอ Mac ไว้บนสุด! (ชิงตัดหน้าก่อนที่ Java จะโหลด UI ตัวอื่น)
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "WordType");
        System.setProperty("apple.awt.application.appearance", "system");
        com.formdev.flatlaf.FlatLightLaf.setup();

        // 2. โหลดฟอนต์ทั้งหมดให้เรียบร้อย "ก่อน" ที่จะสร้างหน้าต่างใดๆ 
        loadFonts();

        // 3. สร้างผู้ใช้จำลองลงในระบบ
        seedTestUsers();

        // 4. เริ่มต้นสร้างหน้าต่างผู้ใช้ (GUI)
        // ใช้ SwingUtilities.invokeLater เพื่อให้การสร้างหน้าต่างไปทำงานบน "Event Dispatch Thread" (EDT)
        SwingUtilities.invokeLater(() -> {
            // สร้างอินสแตนซ์ของหน้าจอหลัก (MainFrame) และสั่งให้ปรากฏตัว (setVisible(true))
            new ui.MainFrame().setVisible(true);
        });
    }
}
