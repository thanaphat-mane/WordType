package util;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * คลาสเครื่องมือ (Utility) สำหรับการจัดการเรื่องรหัสผ่าน (Password)
 * ทำหน้าที่เข้ารหัสผ่าน (Hashing) และตรวจสอบรหัสผ่าน (Verification)
 * <p>
 * เหตุผลที่ต้องมีคลาสนี้: เพื่อความปลอดภัย เราจะไม่เก็บรหัสผ่านเป็นข้อความธรรมดา (Plain text) 
 * ลงในฐานข้อมูลหรือไฟล์ CSV เด็ดขาด เพราะถ้าข้อมูลหลุดไป แฮกเกอร์จะรู้รหัสผ่านทันที
 * เราจึงใช้อัลกอริทึม SHA-256 เพื่อแปลงรหัสผ่านเป็นค่า Hash ซึ่งเป็นการเข้ารหัสทางเดียว (One-way hash)
 * ไม่สามารถถอดรหัสกลับมาเป็นข้อความเดิมได้โดยตรง
 */
public class PasswordUtil {

    /**
     * เมธอดสำหรับเข้ารหัสผ่านด้วยอัลกอริทึม SHA-256
     * การทำงาน: รับข้อความรหัสผ่านปกติ -> แปลงเป็นไบต์ -> นำเข้าฟังก์ชัน Hash -> แปลงกลับเป็นสตริงฐาน 16 (Hex)
     *
     * @param plainPassword รหัสผ่านต้นฉบับที่ผู้ใช้กรอกเข้ามา (Plain text)
     * @return ค่า Hash ของรหัสผ่านในรูปแบบสตริงฐาน 16 (Hexadecimal string) ซึ่งมีความยาวคงที่ 64 ตัวอักษร
     * @throws RuntimeException หากเกิดข้อผิดพลาดจากระบบที่ไม่รองรับอัลกอริทึมหรือการเข้ารหัส
     */
    public static String hash(String plainPassword) {
        try {
            // 1. ดึงตัวจัดการอัลกอริทึม SHA-256 จาก Java Security API
            // SHA-256 จะให้ผลลัพธ์เป็นค่าแฮชขนาด 256 บิตเสมอ ไม่ว่าข้อความต้นฉบับจะยาวแค่ไหน
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            // 2. แปลงข้อความรหัสผ่านให้เป็นกลุ่มของไบต์ (byte array) ด้วยมาตรฐาน UTF-8
            // จากนั้นสั่งให้ MessageDigest ทำการแฮชข้อมูล (digest)
            byte[] hashBytes = digest.digest(plainPassword.getBytes("UTF-8"));
            
            // 3. เตรียมตัวแปร StringBuilder สำหรับต่อข้อความ (เร็วกว่าการใช้ + ต่อ String ธรรมดา)
            // เพื่อใช้แปลงข้อมูลไบต์แต่ละตัวให้กลายเป็นตัวอักษรฐานสิบหก (Hexadecimal)
            StringBuilder sb = new StringBuilder();
            
            // 4. วนลูปอ่านค่าทีละไบต์จากอาร์เรย์ hashBytes
            for (byte b : hashBytes) {
                // 5. แปลงไบต์เป็นตัวเลขฐานสิบหก 2 หลัก (เช่น 0a, 1f, b4)
                // %02x หมายถึง: x = ฐานสิบหกตัวพิมพ์เล็ก, 2 = ความยาว 2 ตัวอักษร, 0 = ถ้าไม่ครบให้เติม 0 ข้างหน้า
                sb.append(String.format("%02x", b));
            }
            
            // 6. แปลง StringBuilder คืนเป็น String แล้วส่งค่ากลับไปใช้งาน
            return sb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            // หากเครื่องที่รัน Java ไม่มีอัลกอริทึม SHA-256 หรือไม่รู้จัก UTF-8 (ซึ่งเป็นไปได้ยากมาก)
            // จะจับ Exception แล้วโยนเป็น RuntimeException เพื่อให้โปรแกรมหยุดการทำงานหรือจัดการต่อในระดับบน
            throw new RuntimeException(e);
        }
    }

    /**
     * เมธอดสำหรับตรวจสอบว่ารหัสผ่านที่กรอกมา ถูกต้องหรือไม่
     * <p>
     * วิธีตรวจสอบ: เนื่องจากเราไม่สามารถถอดรหัส Hash กลับเป็นรหัสเดิมได้
     * เราจึงต้องนำ "รหัสผ่านใหม่ที่เพิ่งกรอก" มาผ่านการ Hash ด้วยวิธีเดียวกัน
     * แล้วนำผลลัพธ์ Hash ใหม่ ไปเทียบกับ "Hash เดิมที่เคยบันทึกไว้ในระบบ"
     * ถ้าตรงกันเป๊ะ แสดงว่ารหัสผ่านต้นฉบับคือตัวเดียวกัน
     *
     * @param plainPassword  รหัสผ่านที่ผู้ใช้กรอกเข้ามาตอนพยายามเข้าสู่ระบบ (Login)
     * @param hashedPassword ค่า Hash ของรหัสผ่านที่เคยเก็บไว้ในระบบ (เช่น ดึงมาจากไฟล์ users.csv)
     * @return true ถ้ารหัสผ่านตรงกัน, false ถ้าไม่ตรงกัน
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        // นำรหัสผ่านดิบ (plainPassword) ไปเข้าเมธอด hash() ที่เราเขียนไว้ด้านบน
        // จากนั้นใช้เมธอด .equals() เทียบกับ hashedPassword
        // .equals() สำคัญมากสำหรับการเทียบเนื้อหาของ String ห้ามใช้เครื่องหมาย == เด็ดขาด
        return hash(plainPassword).equals(hashedPassword);
    }
}
