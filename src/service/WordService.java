package service;

import java.util.*;
import util.CSVUtil;

/**
 * คลาส WordService ทำหน้าที่เป็น Service Layer สำหรับจัดการและจัดหาข้อมูล "คำศัพท์" (Words) 
 * เพื่อนำไปใช้เป็นโจทย์ให้ผู้ใช้พิมพ์ในเกม โดยมีหน้าที่หลักคือการดึงข้อมูลตั้งต้นมาจากไฟล์ CSV
 * และมีความสามารถในการ "สุ่ม" คำศัพท์จำนวนตามที่ต้องการ
 */
public class WordService {
    // เส้นทางอ้างอิงไฟล์ (File Path) ที่ระบุตำแหน่งของไฟล์ CSV 
    // ประกาศเป็น static final แปลว่าเป็นค่าคงที่ระดับคลาสที่ไม่สามารถเปลี่ยนแปลงได้
    private static final String FILE_PATH = "./src/data/words.csv";

    // คลังเก็บคำศัพท์ระดับคลาส (Word Pool)
    // เก็บคำศัพท์ทุกคำที่อ่านมาจากไฟล์ โดยใช้ ArrayList ซึ่งให้ประสิทธิภาพดีเวลาอ่านข้อมูล
    // สังเกตว่าใช้ List<String> เป็น Interface เพื่อความยืดหยุ่นในการเขียน
    private final List<String> WORD_POOL = new ArrayList<>();

    // ชุดคำศัพท์ฉุกเฉิน (Fallback List) สำหรับกรณีร้ายแรง
    // เช่น ไฟล์ CSV หาย, เปิดไม่ได้, หรืออ่านมาแล้วไฟล์ว่างเปล่า
    // ใช้ Arrays.asList() เพื่อสร้างลิสต์แบบตายตัว (Fixed-size) ขึ้นมาอย่างรวดเร็ว
    private static final List<String> FALLBACK = Arrays.asList(
            "system", "class", "object", "method", "public", "static", "void", "string", "return",
            "frame", "panel", "button", "layout", "event", "thread", "interface", "extends",
            "import", "package", "final");

    /**
     * คอนสตรักเตอร์ (Constructor) จะถูกรันอัตโนมัติ 1 ครั้งเมื่อมีคนเรียก `new WordService()`
     * หน้าที่ของมันในที่นี้คือการอ่านและเตรียมข้อมูล (Load Data) ล่วงหน้า เพื่อให้พร้อมใช้งานทันที
     */
    public WordService(){
        // เรียกใช้ CSVUtil.readAll() เพื่ออ่านไฟล์ทั้งหมด 
        // CSVUtil ปกติจะอ่านไฟล์และคืนค่าเป็น List ของ Array ข้อความ (List<String[]>)
        // ลูปชั้นนอก: วนลูปอ่านข้อมูล "ทีละบรรทัด" (Row)
        for (String[] row : CSVUtil.readAll(FILE_PATH)) {
            
            // ลูปชั้นใน: บรรทัดหนึ่งๆ อาจจะมีคำหลายคำถูกแบ่งด้วยเครื่องหมายจุลภาค (Comma)
            // จึงต้องวนลูปอ่าน "ทีละเซลล์" (Cell) ในบรรทัดนั้น
            for (String cell : row) {
                // String.trim() ช่วยตัดช่องว่าง (Space) หลงเหลืออยู่ที่ต้นและท้ายข้อความ
                // เช่น "  apple " กลายเป็น "apple" เพื่อความสะอาดของข้อมูล
                String word = cell.trim();
                
                // ถ้ายาวกว่า 0 (ไม่ใช่ช่องว่างเปล่าๆ) จึงนำคำนั้นเพิ่ม (add) เข้าไปที่คลังคำศัพท์ (WORD_POOL)
                if(!word.isEmpty()) {
                    WORD_POOL.add(word);
                }
            }
        }
        
        // ตรวจสอบความปลอดภัย (Fallback Mechanism)
        // หากวนลูปด้านบนเสร็จแล้วคลังคำศัพท์ยังว่างเปล่า (isEmpty()) 
        // แปลว่าไฟล์ไม่มีข้อมูล หรือโหลดล้มเหลว
        // จะนำคำทั้งหมดจากชุด FALLBACK เทลงไปใน WORD_POOL ด้วยคำสั่ง addAll() เพื่อให้เกมยังไปต่อได้
        if(WORD_POOL.isEmpty()) {
            WORD_POOL.addAll(FALLBACK);
        }
    }

    /**
     * เมธอดสำหรับ "สุ่ม" ดึงคำศัพท์จากคลังมาใช้งานตามจำนวนที่ระบุ
     * เมธอดนี้มีความสำคัญมากเพราะเป็นตัวจ่ายโจทย์เข้าไปใน TypingEngine
     *
     * @param count จำนวนคำที่ต้องการสุ่ม เช่น ต้องการ 50 คำ ก็ส่ง 50
     * @return ลิสต์ (List) ของคำศัพท์ที่ถูกสุ่มขึ้นมาตามจำนวนที่สั่ง
     */
    public List<String> randomWords(int count){
        // สร้างลิสต์ใหม่ (result) เพื่อเก็บคำศัพท์เฉพาะชุดที่จะส่งคืนให้ผู้เรียก
        List<String> result = new ArrayList<>();
        
        // อ็อบเจกต์ Random ของ Java 
        // ใต้เบื้องหลังจะใช้หลักการคณิตศาสตร์ (Linear Congruential Generator - LCG) เพื่อสร้างตัวเลขสุ่มแบบเทียม
        Random random = new Random();
        
        // วนลูปตามจำนวนรอบ (count) ที่ถูกเรียกมา
        for (int i = 0; i < count; i++) {
            // random.nextInt(bound) จะสุ่มตัวเลขจำนวนเต็มตั้งแต่ 0 จนถึง bound - 1
            // เช่น ถ้าคลังมี 100 คำ (size = 100) มันจะสุ่มเลขตั้งแต่ 0 ถึง 99 
            // ซึ่งตรงกับเลข Index พอดีเด๊ะๆ
            int randomIndex = random.nextInt(WORD_POOL.size());

            // นำเลขสุ่มไปใช้เป็น Index ชี้ (get) ดึงคำศัพท์จากคลัง แล้วเพิ่มใส่ลงใน result
            // ข้อสังเกต: การใช้วิธีนี้มีโอกาสที่คำเดิมจะถูกสุ่มซ้ำ (Sampling with replacement) 
            // ซึ่งสำหรับการพิมพ์ดีดถือว่าเป็นเรื่องยอมรับได้
            result.add(WORD_POOL.get(randomIndex));
        }
        
        // คืนค่าชุดคำศัพท์ที่สุ่มเสร็จสมบูรณ์แล้วกลับไปให้ผู้เรียกใช้งาน
        return result;
    }
}
