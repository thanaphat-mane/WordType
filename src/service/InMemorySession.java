package service;

import model.User;

/**
 * คลาสสำหรับจัดการเซสชัน (Session) ในหน่วยความจำของโปรแกรม (In-Memory)
 * ทำหน้าที่เสมือนป้ายห้อยคอหรือที่นั่งชั่วคราว เพื่อบอกว่าใครกำลังใช้งานโปรแกรมอยู่ ณ ขณะนี้
 * โดยข้อมูลจะถูกเก็บไว้ในตัวแปรธรรมดา ซึ่งหมายความว่าหากปิดโปรแกรม ข้อมูลการล็อกอินนี้ก็จะหายไปทันที
 * คลาสนี้ Implement มาจาก SessionManager เพื่อให้โครงสร้างของระบบมีความยืดหยุ่น (ใช้หลัก Dependency Inversion)
 */
public class InMemorySession implements SessionManager{

    /**
     * ตัวแปร currentuUser ใช้เก็บอ็อบเจกต์ User ของผู้ใช้งานที่กำลังล็อกอินอยู่
     * ถ้าไม่มีใครล็อกอิน ตัวแปรนี้จะมีค่าเป็น null
     */
    private User currentuUser;

    /**
     * เมธอดสำหรับทำหน้าที่ "ล็อกอิน" โดยจดจำผู้ใช้เข้าสู่ระบบ
     *
     * @param user อ็อบเจกต์ของผู้ใช้งานที่ล็อกอินสำเร็จและต้องการบันทึกสถานะ
     * @throws IllegalArgumentException จะมีการโยน Exception นี้หากส่งค่า null เข้ามาป้องกันไม่ให้สถานะผิดเพี้ยน
     */
    @Override
    public void login(User user) {
        // ตรวจสอบว่า object ที่ส่งเข้ามาต้องไม่เป็น null (Defensive Programming)
        // เพราะเราไม่ควรให้มีการล็อกอินด้วยความว่างเปล่า ถ้าเกิดส่ง null เข้ามาเราจะหยุดการทำงานด้วยการโยน Exception
        if(user == null) {
            throw new IllegalArgumentException("User cannot be null when logging in");
        }
        
        // บันทึกอ็อบเจกต์ผู้ใช้ไว้ในตัวแปร currentuUser ซึ่งอยู่ในหน่วยความจำ (RAM) ของคลาสนี้
        this.currentuUser = user;
    }

    /**
     * เมธอดสำหรับทำหน้าที่ "ออกจากระบบ" (Logout)
     * การทำงานคือเพียงแค่ล้างค่าข้อมูลผู้ใช้ปัจจุบันทิ้งไป
     */
    @Override
    public void logout() {
        // เซ็ตตัวแปร currentuUser ให้กลับไปเป็น null
        // ในภาษา Java เมื่อไม่มีตัวแปรใดอ้างอิงถึงอ็อบเจกต์แล้ว Garbage Collector (GC) 
        // จะคอยมาเก็บกวาดอ็อบเจกต์ออกจากหน่วยความจำ (RAM) ให้เองอัตโนมัติ
        this.currentuUser = null;
    }

    /**
     * เมธอดสำหรับสอบถามว่าใครคือคนที่กำลังล็อกอินอยู่ในปัจจุบัน
     *
     * @return คืนค่าอ็อบเจกต์ User ที่กำลังใช้งานอยู่ หรือคืนค่า null หากปัจจุบันไม่มีคนล็อกอิน
     */
    @Override
    public User getCurrentUser() {
        // คืนค่าตัวแปรเก็บสถานะปัจจุบันกลับไปตรงๆ
        return currentuUser;
    }

    /**
     * เมธอดสำหรับตรวจสอบอย่างรวดเร็วว่าตอนนี้ "มีคนล็อกอินอยู่หรือไม่"
     *
     * @return คืนค่า true ถ้ามีคนล็อกอินอยู่ (ตัวแปรไม่เป็น null), คืนค่า false ถ้ายังไม่มีใครล็อกอิน
     */
    @Override
    public boolean isLoggedIn() {
        // เรียกใช้เมธอด getCurrentUser() แล้วเปรียบเทียบกับ null
        // หากไม่เท่ากับ (!=) null แสดงว่าประโยคนี้จะเป็น true คือมีคนอยู่ในระบบ
        return getCurrentUser() != null;
    }
    
}
