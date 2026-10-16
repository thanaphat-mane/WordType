package service;

import model.User;
import repository.UserRepository;
import util.PasswordUtil;

/**
 * คลาส AuthService ทำหน้าที่เป็น Service Layer ให้บริการเกี่ยวกับ 
 * การตรวจสอบตัวตน (Authentication) และการจัดการบัญชีผู้ใช้งาน
 * เช่น การสมัครสมาชิกใหม่ (Register) และการเข้าสู่ระบบ (Login)
 * คลาสนี้จะเชื่อมต่อกับ UserRepository เพื่อเข้าถึงข้อมูลในฐานข้อมูล และใช้ PasswordUtil สำหรับเข้ารหัสรหัสผ่าน
 */
public class AuthService {
    // สร้างอ็อบเจกต์ UserRepository สำหรับจัดการบันทึกและค้นหาข้อมูลผู้ใช้ในฐานข้อมูล
    // ประกาศเป็น final เพราะจะไม่ถูกเปลี่ยนแปลงไปอ้างอิงอ็อบเจกต์อื่น
    private final UserRepository userRepository = new UserRepository();

    /**
     * สมัครสมาชิกใหม่ โดยจะมีการตรวจสอบความถูกต้องของข้อมูล (Validation) ก่อนทำการบันทึก
     *
     * @param username ชื่อผู้ใช้ที่ต้องการสมัคร
     * @param password รหัสผ่านแบบข้อความธรรมดา (plain text) ที่ผู้ใช้กรอก
     * @return คืนค่า AuthResult.success(user) ถ้าสำเร็จ หรือ AuthResult.fail(message) พร้อมข้อความอธิบายถ้าล้มเหลว
     */
    public AuthResult register(String username, String password) {
        // [1] ตรวจสอบว่าผู้ใช้ไม่ได้กรอก username หรือกรอกมาแค่ช่องว่าง
        // string.trim() จะทำการตัดช่องว่าง (space) ที่อยู่หน้าและหลังข้อความทิ้งไป
        // string.isEmpty() จะเช็คว่าข้อความมีความยาวเป็น 0 หรือไม่ (ใต้เบื้องหลังคือเช็คว่า length == 0)
        if (username == null || username.trim().isEmpty()) {
            return AuthResult.fail("Please enter a username");
        }
        
        // [2] ตรวจสอบว่าผู้ใช้ไม่ได้กรอก password หรือกรอกมาแค่ช่องว่าง
        if (password == null || password.trim().isEmpty()) {
            return AuthResult.fail("Please enter a password");
        }

        // [3] ตรวจสอบความปลอดภัยของรหัสผ่าน (Password Policy)
        // - ความยาวอย่างน้อย 8 ตัวอักษร
        // - มีตัวพิมพ์ใหญ่อย่างน้อย 1 ตัว (A-Z)
        // - มีตัวพิมพ์เล็กอย่างน้อย 1 ตัว (a-z)
        // - มีตัวอักษรพิเศษอย่างน้อย 1 ตัว (เช่น !@#$%^&*...)
        String passwordError = validatePassword(password);
        if (passwordError != null) {
            return AuthResult.fail(passwordError);
        }

        // ตัดช่องว่างหน้าและหลังของ username เพื่อให้ข้อมูลสะอาด และป้องกันการสมัครซ้ำโดยใช้ space บัง
        username = username.trim();
        
        // [4] ป้องกันไม่ให้มีอักขระ ',' (จุลภาค) ในชื่อผู้ใช้
        // string.contains(",") ใน Java ภายใต้จะทำการหาอักขระภายใน Array ของตัวอักษร 
        // สาเหตุที่ต้องห้ามเพราะเราบันทึกข้อมูลแบบ CSV (Comma Separated Values) ซึ่งใช้ ',' เป็นตัวแบ่งคอลัมน์
        // หากปล่อยให้ใช้ ',' ได้ ไฟล์ CSV จะเพี้ยนและอ่านข้อมูลผิดช่อง
        if (username.contains(",")) {
            return AuthResult.fail("Username cannot contain ','");
        }
        
        // [5] ตรวจสอบว่า username นี้ถูกใช้งานไปแล้วหรือยัง
        // userRepository.existsByUsername() จะไปค้นหาในฐานข้อมูลว่ามีชื่อนี้หรือไม่
        if (userRepository.existsByUsername(username)) {
            return AuthResult.fail("Username already exists");
        }

        // [6] เมื่อข้อมูลทุกอย่างถูกต้อง สร้างอ็อบเจกต์ User ใหม่
        // เราจะไม่เก็บรหัสผ่านเป็นข้อความธรรมดาเด็ดขาด แต่จะนำไปแฮช (Hash) ผ่าน PasswordUtil.hash()
        // การ Hash คือการแปลงข้อความไปเป็นชุดตัวอักษรแบบสุ่มที่ไม่สามารถแปลงกลับได้ เพื่อความปลอดภัย
        User user = new User(username, PasswordUtil.hash(password));
        
        // [7] สั่งให้ UserRepository ทำการบันทึกผู้ใช้ลงในฐานข้อมูล
        userRepository.save(user);
        
        // [8] คืนค่าผลลัพธ์ความสำเร็จพร้อมแนบข้อมูลผู้ใช้ที่สร้างใหม่กลับไป
        return AuthResult.success(user);
    }

    /**
     * ตรวจสอบการเข้าสู่ระบบ (Login/Authentication)
     * ค้นหาผู้ใช้จาก username ในฐานข้อมูล แล้วนำรหัสผ่านที่ผู้ใช้กรอกมาเปรียบเทียบกับ Hash ที่เก็บไว้
     * 
     * @param username ชื่อผู้ใช้ที่กรอกตอน login
     * @param password รหัสผ่าน (plain text) ที่กรอกตอน login
     * @return AuthResult.success(user) ถ้าข้อมูลถูกต้อง หรือ AuthResult.fail(message) ถ้าข้อมูลผิด
     */
    public AuthResult login(String username, String password) {
        // [1] ตรวจสอบเบื้องต้นว่ากรอก username มาหรือไม่ (เหมือนตอนสมัคร)
        if (username == null || username.trim().isEmpty()) {
            return AuthResult.fail("Please enter a username");
        }
        
        // [2] ตรวจสอบว่ากรอก password มาหรือไม่
        if (password == null || password.isEmpty()) {
            return AuthResult.fail("Please enter a password");
        }

        // [3] ค้นหาข้อมูลผู้ใช้จากฐานข้อมูล โดยใช้ username (ตัดช่องว่างทิ้งด้วย trim() เผื่อเผลอพิมพ์เกิน)
        User user = userRepository.FindByUsername(username.trim());
        
        // [4] ตรวจสอบความถูกต้อง (Authentication)
        // เงื่อนไขแรก (user == null): แปลว่าหา username นี้ไม่เจอในฐานข้อมูล
        // เงื่อนไขสอง (!PasswordUtil.verify(...)): แปลว่าหาผู้ใช้เจอ แต่นำรหัสผ่านปัจจุบันไปเข้าสูตรคำนวณแล้วค่า Hash ไม่ตรงกัน
        // 
        // ข้อสังเกตเรื่องความปลอดภัย: เราจะแสดงข้อความ "Invalid username or password" แบบคลุมเครือ 
        // ไม่บอกเจาะจงว่า username ผิด หรือ password ผิด เพื่อป้องกันแฮกเกอร์สุ่มหา username ที่มีอยู่ในระบบ (Username Enumeration)
        if (user == null || !PasswordUtil.verify(password, user.getHashedPassword())) {
            return AuthResult.fail("Invalid username or password");
        }
        
        // [5] ถ้าผ่านทุกด่าน แสดงว่าล็อกอินถูกต้อง คืนค่าผู้ใช้นั้นกลับไปในสถานะสำเร็จ
        return AuthResult.success(user);
    }

    /**
     * ตรวจสอบว่ารหัสผ่านผ่านเกณฑ์ความปลอดภัยทั้งหมดหรือไม่:
     * 1. มีความยาวอย่างน้อย 8 ตัวอักษร
     * 2. มีตัวอักษรพิมพ์ใหญ่อย่างน้อย 1 ตัว (A-Z)
     * 3. มีตัวอักษรพิมพ์เล็กอย่างน้อย 1 ตัว (a-z)
     * 4. มีตัวอักษรพิเศษอย่างน้อย 1 ตัว (เช่น !@#$%^&*...)
     * 
     * เขียนด้วย loop พื้นฐาน ไม่ใช้ Library และไม่ใช้ matches
     *
     * @param password รหัสผ่านที่ต้องการตรวจสอบ
     * @return true ถ้าผ่านเกณฑ์ความปลอดภัยทั้งหมด, false ถ้าไม่ผ่าน
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasSpecial = false;

        // วนลูปตรวจสอบตัวอักษรทีละตัวแบบพื้นฐาน
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (c >= 'A' && c <= 'Z') {
                hasUpper = true;
            } else if (c >= 'a' && c <= 'z') {
                hasLower = true;
            } else if (c >= '0' && c <= '9') {
                // ตัวเลขทั่วไป
            } else if (c != ' ') {
                // อักขระพิเศษ (ไม่ใช่ตัวอักษร ไม่ใช่ตัวเลข และไม่ใช่ช่องว่าง)
                hasSpecial = true;
            }
        }

        return hasUpper && hasLower && hasSpecial;
    }

    /**
     * ตรวจสอบความถูกต้องของรหัสผ่านและคืนข้อความเตือน
     *
     * @param password รหัสผ่านที่ต้องการตรวจสอบ
     * @return ข้อความแจ้งเตือนความต้องการของรหัสผ่าน หรือ null ถ้ารหัสผ่านถูกต้อง
     */
    public static String validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "Please enter a password";
        }
        if (!isValidPassword(password)) {
            return "Password must be at least 8 characters (1 uppercase, 1 lowercase, 1 special character)";
        }
        return null;
    }

    /**
     * เมธอดสำหรับตรวจสอบรหัสผ่าน (Alias ของ isValidPassword)
     *
     * @param password รหัสผ่านที่ต้องการตรวจสอบ
     * @return true ถ้าผ่านเงื่อนไข, false ถ้าไม่ผ่าน
     */
    public static boolean checkPassword(String password) {
        return isValidPassword(password);
    }
}
