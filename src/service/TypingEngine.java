package service;

import java.time.LocalDate;
import java.util.List;

import model.Score;


/**
 * คลาสนี้เปรียบเสมือน "สมอง" หรือกลไกหลักสำหรับการเล่นเกมพิมพ์ดีด (Typing Engine)
 * ทำหน้าที่ทุกอย่างเกี่ยวกับระหว่างการพิมพ์ เช่น จดจำคำศัพท์ที่ต้องพิมพ์, รับตัวอักษรที่ผู้ใช้กด, 
 * ตรวจสอบความถูกต้องของการพิมพ์ (ตัวต่อตัว), รวมถึงคำนวณสถิติ WPM (Words Per Minute) และความแม่นยำ
 */
public class TypingEngine {
    // Abstraction Function (AF) - อธิบายว่าข้อมูลในคลาสนี้สะท้อนโลกความเป็นจริงอย่างไร:
    //   เป็นตัวแทนของรอบการพิมพ์หนึ่งครั้งที่ผู้ใช้กำลังทำการทดสอบอยู่
    //   words คือลิสต์ลำดับคำศัพท์ทั้งหมดที่ผู้ใช้ถูกสั่งให้พิมพ์ในด่านนี้
    //   typed คืออาร์เรย์เก็บสายอักขระ (String) ที่ผู้ใช้ได้พิมพ์ไปแล้วในแต่ละลำดับคำ
    //   current คือหมายเลขตำแหน่ง (index) ว่าตอนนี้ผู้ใช้กำลังพิมพ์คำที่เท่าไหร่
    //   finished เป็นสถานะธง (flag) บอกว่าเกมจบรอบแล้วหรือยัง (true = พิมพ์ครบหมดแล้ว)
    //   totalKeystrokes คือจำนวนครั้งที่นิ้วของผู้ใช้กดลงไปบนแป้นพิมพ์ทั้งหมด (รวมกดผิดด้วย)
    //   correctKeystrokes คือจำนวนครั้งที่ผู้ใช้กดแป้นพิมพ์แล้วตัวอักษรนั้นตรงกับคำต้นฉบับพอดี
    //
    // Rep Invariant (RI) - กฎเหล็กของตัวแปรในคลาสนี้ที่จะต้องเป็นจริงเสมอ:
    //   - words ต้องไม่เป็น null และต้องมีคำศัพท์อย่างน้อย 1 คำ (!isEmpty())
    //   - typed ต้องไม่เป็น null และจำนวนช่องของอาร์เรย์ต้องเท่ากับจำนวนคำใน words พอดิบพอดี
    //   - current ต้องมีค่าระหว่าง 0 ถึงจำนวน words (ห้ามติดลบ และห้ามเกินจนเกิด OutOfBounds)
    //   - totalKeystrokes และ correctKeystrokes ต้องห้ามติดลบ (>= 0)
    //   - จำนวนแป้นที่กดถูก (correctKeystrokes) จะต้องไม่มีทางมากกว่าจำนวนแป้นที่กดทั้งหมด (totalKeystrokes)

    // ตัวแปรค่าคงที่ กำหนดให้ผู้ใช้สามารถพิมพ์ตัวอักษรเกินจากคำต้นฉบับได้สูงสุดกี่ตัว (ป้องกันพิมพ์มั่วจนยาวเกินเหตุ)
    private static final int MAX_EXTRA = 8;

    // ลิสต์คำศัพท์ที่ต้องพิมพ์ ใช้ List เพื่อให้ความยาวเปลี่ยนแปลงได้ตอนสร้าง (แต่ถูก fix ไว้ใน Engine แล้ว)
    private final List<String> words;
    // อาร์เรย์ของข้อความที่ผู้ใช้พิมพ์ ใช้ Array เพราะเราทราบจำนวนคำล่วงหน้าและขนาดตายตัวแน่นอน
    private final String[] typed;
    
    // ตำแหน่งปัจจุบันของคำที่กำลังพิมพ์ (เริ่มต้นที่ 0 คือคำแรก)
    private int current = 0;
    // สถานะจบเกม
    private boolean finished = false;

    // ตัวนับสถิติการกดแป้นพิมพ์
    private int totalKeystrokes = 0;
    private int correctKeystrokes = 0;

    /**
     * เมธอดตรวจสอบ Rep Invariant (เพื่อความปลอดภัยในการเขียนโปรแกรมและ Debug)
     * ใช้คำสั่ง assert ซึ่งใน Java จะทำงานต่อเมื่อมีการรันด้วย flag -ea (Enable Assertions)
     */
    private void checkRep() {
        assert words != null && !words.isEmpty() : "words list must not be null or empty";
        assert typed != null && typed.length == words.size() : "typed array must have same length as words";
        assert current >= 0 && current <= words.size() : "current index must be valid";
        assert totalKeystrokes >= 0 : "totalKeystrokes must be >= 0";
        assert correctKeystrokes >= 0 : "correctKeystrokes must be >= 0";
        assert correctKeystrokes <= totalKeystrokes : "correctKeystrokes cannot exceed totalKeystrokes";
    }

    /**
     * คอนสตรักเตอร์สำหรับสร้างระบบกลไกการพิมพ์
     * จะถูกเรียกใช้ทุกครั้งที่เริ่มเล่นเกมด่านใหม่
     *
     * @param words รายการคำศัพท์ทั้งหมดในรอบนี้
     * @throws IllegalArgumentException หากเผลอส่ง list ที่เป็น null หรือว่างเปล่าเข้ามา
     */
    public TypingEngine(List<String> words) {
        // ป้องกันความผิดพลาด หากไม่มีคำศัพท์เลยก็เล่นเกมไม่ได้ ต้องโยน Exception
        if (words == null || words.isEmpty()) {
            throw new IllegalArgumentException("words must not be null");
        }
        
        // กำหนดข้อมูลคำศัพท์ต้นฉบับให้คลาส
        this.words = words;
        
        // สร้างอาร์เรย์เพื่อเก็บคำที่ผู้ใช้พิมพ์ โดยมีความยาวเท่ากับจำนวนคำศัพท์เป๊ะๆ
        this.typed = new String[words.size()];

        // วนลูป (Loop) เพื่อกำหนดให้ทุกช่องในอาร์เรย์เป็น "สตริงว่าง" ("") แทนที่จะเป็น null
        // เพื่อให้เวลาเอาไปต่อ String (Concatenation) หรือวัดความยาว .length() จะได้ไม่เกิด NullPointerException
        for (int i = 0; i < words.size(); i++) {
            typed[i] = "";
        }
        
        // ตรวจสอบความถูกต้องของสถานะเริ่มต้น
        checkRep();
    }

    /**
     * เมธอดหัวใจหลักที่ใช้รับข้อมูลตัวอักษรเมื่อผู้ใช้งานกดปุ่มบนคีย์บอร์ดแต่ละครั้ง
     * และทำการประมวลผลการพิมพ์ในทันที (คำนวณถูก-ผิด, เปลี่ยนคำ ฯลฯ)
     *
     * @param c ตัวอักษรเดี่ยวๆ (char) ที่ผู้ใช้พิมพ์เข้ามา
     */
    public void typeChar(char c){
        // หากเล่นจบเกมไปแล้ว (finished = true) จะตัดการทำงาน (return) ออกทันที ไม่สนว่าผู้ใช้จะกดอะไรต่อ
        if(finished) return;
        
        // ดึงคำต้นฉบับที่กำลังต้องพิมพ์ในตำแหน่งปัจจุบัน
        // List.get(index) ทำงานในเวลา O(1) สำหรับ ArrayList
        String word = words.get(current);
        
        // ดึงข้อความปัจจุบันที่ผู้ใช้สะสมพิมพ์ไว้แล้วสำหรับคำนี้
        String input = typed[current];

        // ----------------------------------------------------
        // กรณีที่ 1: ผู้ใช้กดปุ่ม Spacebar (แปลว่าเขาต้องการจบคำนี้ และไปคำถัดไป)
        // ----------------------------------------------------
        if(c == ' '){
            // ถ้าเขายังไม่ได้พิมพ์อะไรเลยในคำนี้แต่กด Space เราจะเพิกเฉย ไม่ให้ข้ามคำง่ายๆ
            if(input.isEmpty()) return; 
            
            // เช็คว่าคำที่ผู้ใช้พิมพ์มา (input) ตรงกันเป๊ะๆ (equals) กับต้นฉบับ (word) หรือไม่
            // String.equals() ใน Java จะไล่เทียบตัวอักษรทีละตัวว่าเหมือนกันทุกประการหรือเปล่า
            // ถ้าตรงกันเป๊ะ ให้นับการกด Spacebar ครั้งนี้ถือเป็นการกดที่ "ถูกต้อง" เพิ่มเข้าไป
            if(input.equals(word)) correctKeystrokes++;
            
            // ตรวจสอบว่าคำที่เพิ่งกด Space ไปนั้น เป็นคำสุดท้ายของเกมหรือยัง?
            // size() คือจำนวนทั้งหมด เช่น มี 10 คำ (size=10) ตำแหน่ง index จะวิ่งจาก 0-9
            if(current == words.size() - 1){
                finished = true; // ถ้าเป็นคำสุดท้าย ก็จบเกม
            } else {
                current++; // ถ้ายังไม่ใช่คำสุดท้าย ให้ขยับดัชนี (current) ไปยังคำถัดไป (+1)
            }
        }
        // ----------------------------------------------------
        // กรณีที่ 2: ผู้ใช้พิมพ์ตัวอักษรทั่วไป (ไม่ใช่ Spacebar)
        // ----------------------------------------------------
        else {
            // ป้องกันผู้ใช้พิมพ์มั่ว หรือพิมพ์ค้างจนข้อความยาวเกินไป
            // หากตัวอักษรที่พิมพ์มา มีความยาวมากกว่า คำต้นฉบับบวกกับโควต้าการพิมพ์เกิน (MAX_EXTRA) เราจะไม่รับเพิ่ม
            if (input.length() >= word.length() + MAX_EXTRA) return;

            // นับรวมการกดแป้นพิมพ์นี้เข้าไปในยอดการกดทั้งหมดเสมอ (ไม่ว่าจะถูกหรือผิด)
            totalKeystrokes++;

            // ตรวจสอบว่าตัวอักษร (char) ตัวใหม่นี้ 'ถูกต้อง' หรือไม่? โดยมีเงื่อนไข 2 ข้อคือ:
            // 1. ความยาวของสิ่งที่พิมพ์ต้องยังไม่เกินคำต้นฉบับ (ป้องกัน IndexOutOfBoundsException)
            // 2. ตัวอักษรที่ตำแหน่งความยาวปัจจุบันของต้นฉบับ (word.charAt(input.length())) ตรงกับตัวที่รับมา (c)
            // ตัวอย่าง: เป้าหมายคือ "cat" (ยาว 3) ตอนนี้พิมพ์ "ca" (ยาว 2) 
            // ตัวถัดไปที่จะเช็คคือ index 2 ของ "cat" ซึ่งคือ 't', ถ้า c เป็น 't' พอดีก็จะถือว่าถูก
            if (input.length() < word.length() && word.charAt(input.length()) == c) {                                                                                   
                correctKeystrokes++; // เพิ่มยอดพิมพ์ถูก                                                                                                                                   
            }  

            // นำข้อความที่พิมพ์ไว้ก่อนหน้า มาต่อท้าย (Concatenate) ด้วยตัวอักษรใหม่ 
            // แล้วเก็บอัปเดตลงไปในอาร์เรย์ typed
            // หมายเหตุ: เบื้องหลังการบวก String (input + c) Java จะสร้างอ็อบเจกต์ StringBuilder มาทำงานให้
            typed[current] = input + c; 

            // กติกาพิเศษสำหรับการจบเกมแบบไม่ต้องกด Spacebar
            // หากผู้ใช้กำลังพิมพ์อยู่คำสุดท้ายพอดีเป๊ะๆ และข้อความที่พิมพ์ไปแล้วดันเหมือนต้นฉบับเป๊ะๆ
            if (current == words.size() - 1 && typed[current].equals(word)) {                                                                                           
                finished = true; // จบเกมทันทีเพื่อความลื่นไหล                                                                                                                                       
            }  
        }
    }

    /**
     * เมธอดสำหรับลบตัวอักษรล่าสุด (เสมือนผู้ใช้กดปุ่ม Backspace บนคีย์บอร์ด)
     */
    public void backspace(){
        // ถ้าเกมจบแล้ว ก็ไม่ให้แก้ตัวใดๆ ทั้งสิ้น
        if (finished) return;
        
        // ดึงข้อความปัจจุบันของคำนี้ออกมา
        String input = typed[current];
        
        // [กรณี 1] ถ้าคำปัจจุบันผู้ใช้พิมพ์อะไรไปแล้วบ้าง (ความยาว > 0)
        if(!input.isEmpty()){
            // เราจะใช้ substring เพื่อตัดข้อความตั้งแต่ตัวแรก (index 0) ไปจนถึงตัวก่อนสุดท้าย (length - 1)
            // เป็นการลบตัวอักษรขวาสุดออก 1 ตัว
            typed[current] = input.substring(0, input.length() - 1);
        }
        // [กรณี 2] ถ้าคำปัจจุบันยังว่างเปล่า (ลบจนหมดคำแล้ว) แต่มีคำก่อนหน้า (current > 0)
        // และคำก่อนหน้านั้น "ยังพิมพ์ไม่ถูกสมบูรณ์" เราจะอนุญาตให้กระโดดถอยหลังไปแก้คำก่อนหน้าได้
        else if(current > 0 && !typed[current - 1].equals(words.get(current - 1))){
            // ลดตัวเลข current ลง 1 เพื่อย้อนเป้าหมายกลับไปที่คำก่อนหน้า
            current--;
        }
    }

    /**
     * เมธอดสรุปผลรวบยอดการทำงาน คล้ายกับการทำใบเกรดเมื่อจบเกม
     * จะคำนวณจำนวนอักขระที่ถูก/ผิด และนำไปเข้าสูตรคำนวณ WPM และเปอร์เซ็นต์ความแม่นยำ
     *
     * @param mode โหมดเกมที่เล่น เช่น "time" (จับเวลา) หรือ "words" (นับคำ)
     * @param amount ปริมาณของโหมดนั้นๆ เช่น 60 วินาที หรือ 50 คำ
     * @param elapsedSeconds เวลาจริงที่ใช้ไปในการพิมพ์ (หน่วยวินาที)
     * @return อ็อบเจกต์ Score ที่สรุปข้อมูลสถิติทุกอย่างของรอบนี้
     */
    public Score buildScore(String mode, int amount, double elapsedSeconds){
        int correct = 0;
        int incorrect = 0;
        
        // วนลูปเช็คความถูกต้อง "ทีละคำ" ตั้งแต่คำแรกสุด ไปจนถึงคำปัจจุบัน (current)
        for (int i = 0; i <= current; i++) {
            String w = words.get(i); // คำต้นฉบับ
            String t = typed[i];     // คำที่พิมพ์
            
            // หาความยาวสั้นสุด เพื่อไม่ให้วนลูปเช็คตัวอักษรแล้วตกขอบ (OutOfBounds)
            int n = Math.min(w.length(), t.length());
            
            // วนลูปเปรียบเทียบ "ทีละตัวอักษร" ในระยะ n
            for (int k = 0; k < n; k++) {
                // ถ้าตัวอักษรตรงกัน (เช่น 'c' == 'c')
                if (w.charAt(k) == t.charAt(k)) {
                    correct++;
                } else {
                    incorrect++;
                }
            }
            
            // กรณีที่ผู้ใช้พิมพ์ "ยาวเกิน" กว่าต้นฉบับ ส่วนที่เกินมาทั้งหมดถือว่า "ผิด" ทันที
            if (t.length() > w.length()) {
                incorrect += t.length() - w.length();
            }
            
            // เช็คว่าคำนี้เป็นคำที่ผู้ใช้ "ผ่านมาแล้ว" (ข้ามไปแล้ว) ใช่หรือไม่?
            // หรือเป็นคำสุดท้ายในกรณีที่เกมจบลงแล้ว
            boolean passed = i < current || (finished && i == current);
            
            // ถ้าคำนี้ผ่านไปแล้ว แต่ผู้ใช้พิมพ์ "สั้นกว่า" ต้นฉบับ แปลว่าเขาพิมพ์ตกหล่น
            // จำนวนตัวอักษรที่ขาดหายไปทั้งหมดถือเป็นตัวอักษรที่ "ผิด"
            if (passed && t.length() < w.length()) {
                incorrect += w.length() - t.length();
            }
            
            // กฎพิเศษการนับช่องว่าง:
            // ถ้าเป็นคำที่ผ่านมาแล้ว, พิมพ์ถูกเป๊ะทั้งคำ, และยังไม่ใช่คำสุดท้ายของด่าน
            // เราจะถือว่าช่องว่าง (Spacebar) ต่อท้ายที่ผู้ใช้พิมพ์เพื่อเปลี่ยนคำ เป็นการเคาะแป้นที่ถูกต้องอีก 1 ที
            if (passed && t.equals(w) && i < words.size() - 1) {
                correct++; 
            }
        }

        // --- การคำนวณสถิติ ---
        
        // 1. แปลงวินาทีเป็นนาที (Math.max ช่วยป้องกันกรณีเวลาเป็น 0 วินาที ซึ่งจะทำให้การหารพังหรือหารด้วยศูนย์ (Infinity))
        double minutes = Math.max(elapsedSeconds, 0.001) / 60.0;
        
        // 2. คำนวณ WPM (Words Per Minute)
        // กติกาสากลของการพิมพ์ดีดคือ "5 ตัวอักษรที่พิมพ์ถูก (รวม space) นับเป็น 1 คำมาตรฐาน"
        // สูตร: (จำนวนคีย์ที่ถูก / 5) / นาทีที่ใช้
        double wpm = (correct / 5.0) / minutes;
        
        // 3. คำนวณความแม่นยำ (Accuracy)
        // สูตร: (จำนวนคีย์ที่พิมพ์ถูก / จำนวนการกดคีย์ทั้งหมด) * 100
        // ต้องระวังกรณีที่ไม่เคยพิมพ์อะไรเลย (total = 0) จะเกิดการหารด้วยศูนย์ จึงต้องเช็คด้วย Ternary Operator
        double accuracy = totalKeystrokes == 0 ? 0 : correctKeystrokes * 100.0 / totalKeystrokes;

        // สร้างใบเกรด Score (User เป็น null ไปก่อนเพื่อให้ไปเชื่อมโยงในระบบชั้นอื่น)
        // ปัดเศษเวลาให้เป็นจำนวนเต็ม (Math.round) และบันทึกวันที่ปัจจุบัน (LocalDate.now())
        return new Score(null, mode, amount, wpm, accuracy, correct, incorrect,
                (int) Math.round(elapsedSeconds), LocalDate.now());
    }

    /**
     * ดึงสถานะจบเกม
     *
     * @return true ถ้าเกมสิ้นสุดแล้ว (ผู้ใช้พิมพ์ครบทุกคำหรือจบด่าน)
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * ดึงลิสต์ของคำศัพท์ทั้งหมดที่ใช้ในการทดสอบ
     *
     * @return รายการคำศัพท์ (List<String>)
     */
    public List<String> getWords() {
        return words;
    }

    /**
     * ดึงข้อความที่ผู้ใช้พิมพ์ไว้สำหรับคำๆ หนึ่ง โดยระบุผ่านเลขตำแหน่ง
     *
     * @param index ลำดับของคำที่ต้องการดู
     * @return สายอักขระ String ที่ผู้ใช้พิมพ์
     */
    public String getTyped(int index) {
        return typed[index];
    }

    /**
     * ดึงตำแหน่งของคำที่ผู้ใช้กำลังพยายามพิมพ์อยู่ในขณะนี้
     *
     * @return หมายเลขลำดับตั้งแต่ 0 เป็นต้นไป
     */
    public int getCurrentIndex() {
        return current;
    }

    /**
     * นับว่าในรอบนี้มีคำศัพท์ที่ต้องพิมพ์ทั้งหมดกี่คำ
     *
     * @return จำนวนคำที่เป็นเป้าหมาย
     */
    public int getWordCount() {
        return words.size();
    }
}
