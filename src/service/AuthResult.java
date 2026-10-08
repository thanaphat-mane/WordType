package service;

import model.*;

/**
 * คลาสนี้เปรียบเสมือนกล่องที่ใช้สำหรับเก็บผลลัพธ์ของการตรวจสอบตัวตน (Authentication)
 * เช่น เมื่อผู้ใช้ล็อกอินหรือสมัครสมาชิก คลาสนี้จะบอกว่า "สำเร็จ" หรือ "ล้มเหลว" 
 * - หากสำเร็จ จะเก็บข้อมูลผู้ใช้ (User) เอาไว้ข้างใน
 * - หากล้มเหลว จะเก็บข้อความอธิบายข้อผิดพลาด (Message) เอาไว้แทน
 * รูปแบบการใช้คลาสลักษณะนี้เรียกว่า Result Pattern ซึ่งช่วยให้เราจัดการกับ Error ได้ง่ายขึ้น
 * โดยไม่ต้องใช้ Exception ตลอดเวลา
 */
public class AuthResult {
    // ใช้ตัวแปรแบบ final เพื่อให้แน่ใจว่าหลังจากสร้าง AuthResult แล้ว ค่าเหล่านี้จะไม่ถูกแก้ไขอีก (Immutable)
    // การทำให้เป็น Immutable ช่วยลดบั๊กจากการเผลอเปลี่ยนค่า และปลอดภัยในการทำงานแบบหลาย Thread
    private final User user;
    private final String message;

    /**
     * คอนสตรักเตอร์สำหรับสร้างผลลัพธ์การตรวจสอบตัวตน (Private Constructor)
     * ถูกตั้งให้เป็น private เพื่อบังคับให้ภายนอกต้องเรียกใช้ผ่านเมธอด success() และ fail() เท่านั้น (Factory Method)
     *
     * @param user ผู้ใช้ (User) หรือ null หากผลลัพธ์นั้นคือล้มเหลว
     * @param message ข้อความสาเหตุที่ล้มเหลว หรือ null หากผลลัพธ์นั้นคือสำเร็จ
     */
    private AuthResult(User user, String message) {
        // กำหนดค่า user ที่รับเข้ามาให้กับตัวแปรภายใน
        this.user = user;
        // กำหนดค่า message ที่รับเข้ามาให้กับตัวแปรภายใน
        this.message = message;
    }

    /**
     * Factory Method สร้างผลลัพธ์ในกรณีที่การทำงานสำเร็จ
     * 
     * @param user อ็อบเจกต์ผู้ใช้ที่ล็อกอินหรือสมัครสำเร็จ
     * @return AuthResult ที่เก็บข้อมูล user เอาไว้ และให้ message เป็น null เพราะไม่มีข้อผิดพลาด
     */
    public static AuthResult success(User user) {
        // คืนค่าอ็อบเจกต์ AuthResult ใหม่ โดยส่ง user เข้าไป และให้ message เป็น null
        return new AuthResult(user, null);
    }

    /**
     * Factory Method สร้างผลลัพธ์ในกรณีที่การทำงานล้มเหลว
     *
     * @param message ข้อความอธิบายสาเหตุที่ล้มเหลว
     * @return AuthResult ที่เก็บ message เอาไว้ และให้ user เป็น null เพราะไม่สำเร็จ
     */
    public static AuthResult fail(String message) {
        // คืนค่าอ็อบเจกต์ AuthResult ใหม่ โดยส่ง user เป็น null และส่ง message อธิบายข้อผิดพลาด
        return new AuthResult(null, message);
    }

    /**
     * เมธอดสำหรับตรวจสอบว่าผลลัพธ์คือการทำงานที่สำเร็จหรือไม่
     * เราสามารถใช้เมธอดนี้ในเงื่อนไข if (result.isSuccess()) ได้เลยเพื่อความอ่านง่าย
     *
     * @return จะคืนค่า true ถ้า user ไม่เท่ากับ null (หมายความว่ามีข้อมูลผู้ใช้ = สำเร็จ)
     */
    public boolean isSuccess() {
        // ถ้า user ไม่เป็น null แสดงว่าสำเร็จ คืนค่า true
        return user != null;
    }

    /**
     * เมธอดสำหรับดึงข้อมูลผู้ใช้งานที่ผ่านกระบวนการสำเร็จ
     *
     * @return คืนค่าอ็อบเจกต์ User ที่ถูกเก็บไว้ หรือเป็น null ถ้าเกิดข้อผิดพลาด
     */
    public User getUser() {
        // คืนค่า user กลับไปให้คลาสที่เรียกใช้
        return user;
    }

    /**
     * เมธอดสำหรับดึงข้อความอธิบายสาเหตุเมื่อกระบวนการล้มเหลว
     *
     * @return คืนค่าข้อความสาเหตุที่ล้มเหลว (String) หรือ null ถ้ากระบวนการสำเร็จ
     */
    public String getMessage() {
        // คืนค่าข้อความ error กลับไป
        return message;
    }
}
