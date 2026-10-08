package service;

import repository.ScoreRepositorty;
import model.*;

import java.util.*;
import java.util.stream.*;

/**
 * คลาสให้บริการ (Service) สำหรับจัดการระบบคะแนน (Score)
 * ทำหน้าที่ประมวลผลข้อมูลคะแนนก่อนส่งไปจัดเก็บที่ Repository หรือดึงข้อมูลมาแสดงผลใน Leaderboard
 */
public class ScoreService {
    
    // ตัวแทนในการติดต่อกับไฟล์เก็บข้อมูลคะแนน
    private final ScoreRepositorty repository = new ScoreRepositorty();

    /**
     * กฎการเรียงลำดับคะแนน (Comparator) สำหรับ Leaderboard
     * กฎของ Comparator คือ:
     * - คืนค่าติดลบ (-1): a ควรอยู่ก่อน b
     * - คืนค่าบวก (1): a ควรอยู่หลัง b
     * - คืนค่าศูนย์ (0): a และ b มีค่าเท่ากัน (ให้พิจารณาเงื่อนไขถัดไป)
     */
    private static final Comparator<Score> RANKING = (a , b) -> {
        // ด่านที่ 1: เทียบความเร็ว (WPM)
        // สังเกตว่าเราเอา b ขึ้นก่อน a (b.getWpm() , a.getWpm()) เพื่อให้เป็นการเรียงจากมากไปน้อย (Descending)
        // เช่น ถ้ายอด WPM ของ a คือ 100 และ b คือ 80 -> Double.compare(80, 100) จะคืนค่าติดลบ (-1)
        // ซึ่งแปลว่า a จะต้องอยู่ก่อน b (100 มาก่อน 80)
        int c = Double.compare(b.getWpm() , a.getWpm());
        
        // ถ้า c ไม่เท่ากับ 0 แปลว่าความเร็วไม่เท่ากัน ตัดสินได้เลยให้ return กลับไป
        if(c != 0) return c;

        // ด่านที่ 2: ถ้า WPM เท่ากัน (c == 0) ให้เทียบความแม่นยำ (Accuracy) ต่อ
        // ใช้หลักการเดิมคือ b ขึ้นก่อน a เพื่อเรียงจากมากไปน้อย
        c = Double.compare(b.getAccuracy(), a.getAccuracy());
        if(c != 0) return c;

        // ด่านที่ 3: ถ้า WPM และ Accuracy เท่ากันเป๊ะ ให้เทียบวันที่ (Date)
        // คราวนี้เอา a ขึ้นก่อน b (a.compareTo(b)) เพื่อเรียงจากน้อยไปมาก (Ascending)
        // ใครเล่นก่อน (วันที่น้อยกว่า/เก่ากว่า) จะได้คืนค่าติดลบ และได้ขึ้นก่อน
        return a.getDate().compareTo(b.getDate());  
    };

    /**
     * บันทึกคะแนนสถิติของผู้เล่น
     * 
     * @param score อ็อบเจกต์คะแนนที่ต้องการบันทึก
     * @throws IllegalArgumentException หากผู้เล่นไม่ได้เข้าสู่ระบบ (ไม่มี username)
     */
    public void save(Score score){
        // เช็คว่า username เป็น null (ไม่ได้กำหนดค่ามา) หรือเป็นสตริงว่างๆ ("") หรือไม่
        // ถ้าเงื่อนไขเป็นจริง ให้โยน Error ทิ้งไปเลย เพื่อป้องกันข้อมูลขยะถูกเซฟลงไฟล์
        if (score.getUsername() == null || score.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty. User must be logged in."); 
        }
        
        // ถ้าข้อมูลถูกต้อง ก็ส่งให้ Repository จัดการเขียนลงไฟล์ CSV
        repository.save(score);
    }

    /**
     * ดึงข้อมูลจัดอันดับคะแนน (Leaderboard) ตามโหมดและเป้าหมายที่ระบุ
     */
    public List<Score> leaderboard(String mode, int amount){
        // 1. ดึงคะแนนทั้งหมดในโหมดนี้ แล้ว .stream() เพื่อเปลี่ยนเป็นสายพานข้อมูลให้จัดการง่ายๆ
        return repository.findbyModeAndAmount(mode, amount).stream()
                
                // 2. จัดกลุ่มข้อมูลลงใน Map เพื่อหา "คะแนนที่ดีที่สุดของแต่ละคน"
                .collect(java.util.stream.Collectors.toMap(
                        score -> score.getUsername(),                    // กำหนดให้ชื่อผู้เล่นเป็น Key
                        score -> score,                                  // กำหนดให้ตัวคะแนน (score) เป็น Value
                        
                        // ถ้าเจอคะแนนที่มีชื่อผู้เล่น (Key) ซ้ำกัน ให้ตัดสินด้วยกฎ RANKING
                        // minBy หมายความว่าให้เลือกเก็บตัวที่ผลลัพธ์ของ RANKING ออกมา "น้อยกว่า (ติดลบ)"
                        // ซึ่งตามกฎ RANKING ของเรา ค่าที่ติดลบคือคนที่มี WPM เยอะกว่านั่นเอง
                        java.util.function.BinaryOperator.minBy(RANKING) 
                ))
                
                // 3. ตอนนี้ใน Map จะมีแต่คะแนนที่เป็น "สถิติที่ดีที่สุด" ของผู้เล่นแต่ละคนเท่านั้น 
                // ใช้ .values() เพื่อดึงเฉพาะคะแนนออกมา แล้ว .stream() เพื่อนำกลับขึ้นสายพานอีกครั้ง
                .values().stream()                      
                
                // 4. สั่ง .sorted(RANKING) ระบบจะสุ่มหยิบคะแนนทีละ 2 ใบมาเปรียบเทียบกันตามกฎ RANKING
                // หากได้ -1 ให้สลับขึ้นหน้า หากได้ 1 ให้ถอยหลัง สลับไปมาจนกว่าทุกคนจะเรียงจากเก่งสุดไปน้อยสุด
                .sorted(RANKING)                        
                
                // 5. เมื่อคะแนนเรียงลำดับเสร็จสมบูรณ์ ก็แพ็คทุกอย่างกลับเข้าใส่กล่อง List และคืนค่า
                .collect(Collectors.toList());          
    }
}
