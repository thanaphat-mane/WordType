package service;

import model.User;
/**
 * คลาสนี้ทำหน้าที่เป็น "โกดังเก็บข้อมูลส่วนกลาง" (Singleton Pattern) 
 * ใช้สำหรับจดจำว่า "ใครกำลังล็อกอินอยู่ในระบบตอนนี้" 
 * เพื่อให้ทุกๆ หน้าต่าง (เช่น Home, Login, HeaderBar) สามารถดึงข้อมูลชุดเดียวกันไปใช้งานได้
 */
public class InMemorySession implements SessionManager{

    // ตัวเเปร instance จพถูกสร้างขึ้นมาเเค่ตัวเดียวในระบบ 
    private static InMemorySession instance;

    // เก็บข้อมูลของผู้ใช้ที่กำลังใช้งานระบบอยู่ ณ ปัจจุบัน
    private User currentuUser;

    /**
     * ช่อง Constructor ให้เป็น private
     * เพื่อนป้องกันไม่ให้มีการใช้คำส่ัง new InMemorySession() จาก class อื่น
     * บังคับให้ทุกคนต้องเรียกข้อมูลผ่าน getInstance() เท่านั้น
     */
    private  InMemorySession() {}

    /**
     * เมธอดสำหรับเรียกใช้งาน Session จากทุกที่ในระบบ
     * หากยังไม่เคยมีการสร้างโกดังเก็บข้อมูล จำทำการสร้างขึ้นมาใหม่เพียง 1 ครั้ง
     * 
     * @return instance ตัวเเปรส่วนกลางของ Session
     */
    public static InMemorySession getInstance() {                                                                                              
        if (instance == null) {                                                                                                                
            instance = new InMemorySession();                                                                                                  
        }                                                                                                                                      
        return instance;                                                                                                                       
    }

    /**    (non-Javadoc)
     * บันทึกข้อมูลผู้ใช้เมื้อ login Success
     * 
     * @param user ข้อมูลผู้ใช้ที่ตรวจสอบรหัสผ่านถูกต่้องเเล้ว
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

    /**    (non-Javadoc)
     * ลบข้อมูลผู้ใช้ปัจจุบันออกเมื่อทำการ logout
     */
    @Override
    public void logout() {
        // เซ็ตตัวแปร currentuUser ให้กลับไปเป็น null
        // ในภาษา Java เมื่อไม่มีตัวแปรใดอ้างอิงถึงอ็อบเจกต์แล้ว Garbage Collector (GC) 
        // จะคอยมาเก็บกวาดอ็อบเจกต์ออกจากหน่วยความจำ (RAM) ให้เองอัตโนมัติ
        this.currentuUser = null;
    }

    /**    (non-Javadoc)
     * ดึงข้อมูลของผู้ใช้ที่ login อยู่ปัจจุบัน
     * 
     * @return  ข้อมูล User หรือ null หากยังไม่มีใคร login
     */
    @Override
    public User getCurrentUser() {
        // คืนค่าตัวแปรเก็บสถานะปัจจุบันกลับไปตรงๆ
        return currentuUser;
    }

    /**    (non-Javadoc)
     * ตรวจสอบสถานะว่าผู้ใช้ login อยู่หรือไม่
     * 
     * @return true ถ้ามีคน login อยู่, false ถ้าไม่มีใคร login
     */
    @Override
    public boolean isLoggedIn() {
        // เรียกใช้เมธอด getCurrentUser() แล้วเปรียบเทียบกับ null
        // หากไม่เท่ากับ (!=) null แสดงว่าประโยคนี้จะเป็น true คือมีคนอยู่ในระบบ
        return getCurrentUser() != null;
    }
    
}
