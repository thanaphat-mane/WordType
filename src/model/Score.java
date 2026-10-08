package model;

import java.time.*; // นำเข้าไลบรารีที่เกี่ยวกับวันที่และเวลา เช่น LocalDate ซึ่งจะถูกใช้เก็บวันที่เล่น

/**
 * คลาสสำหรับเก็บข้อมูลคะแนนและสถิติการพิมพ์ของผู้เล่นแต่ละรอบ
 * คลาสนี้ทำตัวเหมือนตารางสถิติที่บันทึกข้อมูลทุกอย่างที่เกิดขึ้นในการเล่นหนึ่งตา
 */
public class Score {
    // -------------------------------------------------------------------------
    // Abstraction Function (AF):
    //   ออบเจกต์นี้เป็นตัวแทนของบันทึกการพิมพ์ 1 ครั้งของผู้เล่น
    //   - username คือชื่อของผู้เล่นที่ทำสถิตินี้ขึ้นมา
    //   - mode คือโหมดของเกม มีแค่ "time" (จับเวลา) หรือ "words" (นับคำ)
    //   - amount คือตัวเลขที่เป็นเป้าหมาย (เช่น ตั้งเวลา 15, 30 วินาที หรือ 10, 25 คำ)
    //   - wpm คือความเร็วที่วัดได้เป็น "คำต่อนาที" (Words Per Minute)
    //   - accuracy คือเปอร์เซ็นต์ของจำนวนแป้นที่กดถูกเมื่อเทียบกับการกดทั้งหมด
    //   - correctChars / incorrectChars คือสถิติการกดแป้นพิมพ์
    //   - seconds คือระยะเวลาที่ใช้เล่นไปทั้งหมด (ถ้าโหมดเวลา ก็เท่ากับจำนวน amount, ถ้าโหมดคำ จะเป็นเวลาที่ใช้จริง)
    //   - date คือวันที่ทำสถิตินี้
    //
    // Rep Invariant (RI):
    //   เป็นกฎความถูกต้องของข้อมูล (Data Integrity) ในคลาสนี้
    //   - username ห้าม null/ว่าง
    //   - mode ต้องเป็น "time" หรือ "words" เป๊ะๆ
    //   - amount ต้องมากกว่า 0 (เล่น 0 วินาทีเป็นไปไม่ได้)
    //   - wpm, accuracy, characters, seconds ต้องห้ามติดลบ
    //   - accuracy อยู่ในช่วง 0-100 เท่านั้น
    //   - date ห้าม null
    // -------------------------------------------------------------------------

    /** ค่าคงที่ (Constant) สำหรับบอกว่าโหมดคือเวลา ใช้คำสั่ง static final ทำให้ตัวแปรนี้แชร์กันทุกออบเจกต์และไม่สามารถแก้ไขได้ */
    private static final String MODE_TIME = "time";
    
    /** ค่าคงที่ (Constant) สำหรับบอกว่าโหมดคือพิมพ์ตามจำนวนคำ */
    private static final String MODE_WORDS = "words";

    // ประกาศฟิลด์เป็น private final หมายความว่าเมื่อกำหนดค่าตอนสร้างออบเจกต์ (ใน Constructor) แล้ว จะไม่สามารถแก้ไขได้อีกเลย (Immutable Design)
    private final String mode;
    private final String username;
    private final int amount;
    private final double wpm;
    private final double accuracy;
    private final int correctChars;
    private final int incorrectChars;
    private final int seconds;
    private final LocalDate date;

    /**
     * เมธอดส่วนตัว (private) เพื่อตรวจสอบความถูกต้องของข้อมูล (Rep Invariant) 
     * ป้องกันบั๊กจากการส่งค่าพารามิเตอร์ผิดพลาดตอนสร้างออบเจกต์
     */
    private void checkRep() {
        // ใช้ assert ตรวจสอบว่า username ต้องมีอยู่จริง (!= null) และถ้าใช้ .trim() ตัดช่องว่างแล้ว ความยาวต้องมากกว่า 0 (!isEmpty())
        assert username != null && !username.trim().isEmpty() : "username must not be null or empty";
        // .equals() เป็นเมธอดของคลาส String ใช้เปรียบเทียบเนื้อหาของสตริง (ถ้าใช้ == จะเป็นการเปรียบเทียบที่อยู่บนหน่วยความจำ)
        assert mode != null && (mode.equals(MODE_TIME) || mode.equals(MODE_WORDS)) : "mode must be 'time' or 'words'";
        // ค่าปริมาณ (amount) ของโหมดเกมต้องมากกว่าศูนย์เท่านั้น (ไม่มีการเล่นติดลบ)
        assert amount > 0 : "amount must be > 0";
        // WPM (Words Per Minute) ติดลบไม่ได้ ต่ำสุดคือ 0
        assert wpm >= 0.0 : "wpm must be >= 0";
        // เปอร์เซ็นต์ความแม่นยำต้องจำกัดอยู่ในช่วง 0.0 ถึง 100.0 เปอร์เซ็นต์เท่านั้น
        assert accuracy >= 0.0 && accuracy <= 100.0 : "accuracy must be between 0 and 100";
        // ไม่สามารถพิมพ์ถูกหรือผิดในจำนวนที่ติดลบได้
        assert correctChars >= 0 : "correctChars must be >= 0";
        assert incorrectChars >= 0 : "incorrectChars must be >= 0";
        // เวลาในการเล่นวินาที ต้องไม่ติดลบ
        assert seconds >= 0 : "seconds must be >= 0";
        // object ของวันที่ (LocalDate) ต้องถูกสร้างขึ้นมาจริงๆ (ไม่เป็น null)
        assert date != null : "date must not be null";
    }

    /**
     * Constructor สร้างออบเจกต์ Score ใหม่
     *
     * @param username ชื่อผู้เล่น
     * @param mode โหมดการเล่น (time หรือ words)
     * @param amount ปริมาณที่ตั้งไว้ในโหมด (เช่น เวลา 15 หรือ 30 วินาที, จำนวนคำ 10 หรือ 25 คำ)
     * @param wpm คำต่อนาที (Words Per Minute)
     * @param accuracy ความแม่นยำ (เปอร์เซ็นต์)
     * @param correctChars จำนวนตัวอักษรที่พิมพ์ถูก
     * @param incorrectChars จำนวนตัวอักษรที่พิมพ์ผิด
     * @param seconds เวลาที่ใช้ในการพิมพ์ (วินาที)
     * @param date วันที่ที่เล่น
     */
    public Score(String username, String mode, int amount, double wpm, double accuracy,
            int correctChars, int incorrectChars, int seconds, LocalDate date) {
        // ทำการคัดลอกค่าจากอาร์กิวเมนต์ที่ถูกส่งเข้ามากำหนดให้กับตัวแปรภายในออบเจกต์นี้
        this.username = username;
        this.mode = mode;
        this.amount = amount;
        this.wpm = wpm;
        this.accuracy = accuracy;
        this.correctChars = correctChars;
        this.incorrectChars = incorrectChars;
        this.seconds = seconds;
        this.date = date;
        // บังคับเช็คกฎเหล็กทันทีหลังจากกำหนดค่า เพื่อไม่ให้มี Score พังๆ หลุดเข้าไปในระบบ
        checkRep();
    }

    /**
     * คัดลอกข้อมูลคะแนนปัจจุบันแต่เปลี่ยนเฉพาะชื่อผู้ใช้ 
     * เป็นแพทเทิร์นทั่วไปใน Immutable Object (ออบเจกต์ที่ห้ามแก้ค่าภายใน) หากต้องการเปลี่ยนค่าอะไร ต้องสร้างออบเจกต์ใหม่เท่านั้น!
     *
     * @param newUsername ชื่อผู้ใช้ใหม่ที่จะนำมาแทนที่
     * @return ออบเจกต์ Score ตัวใหม่ที่ลอกเลียนค่าเดิมมาทั้งหมด ยกเว้นฟิลด์ชื่อผู้ใช้
     */
    public Score withUsername(String newUsername) {
        // ใช้คำสั่ง 'new' เพื่อจองพื้นที่ใน Memory ใหม่ และส่งข้อมูลชุดเดิมต่อให้ Constructor รวมถึงส่งชื่อที่อัปเดตใหม่เข้าไป
        return new Score(newUsername, mode, amount, wpm, accuracy, correctChars, incorrectChars, seconds, date);
    }

    // -------------------------------------------------------------------------
    // โซน Getter Method
    // ด้านล่างเป็นฟังก์ชันสำหรับคืนค่า (return) ค่าจากฟิลด์ต่างๆ 
    // โดยคลาสภายนอกจะต้องใช้ฟังก์ชันเหล่านี้เพราะตัวแปรถูกซ่อนเป็น private
    // -------------------------------------------------------------------------

    public String getMode() {
        return mode; // คืนค่าโหมดที่เล่น
    }

    public String getUsername() {
        return username; // คืนค่าชื่อผู้เล่น
    }

    public int getAmount() {
        return amount; // คืนค่าเป้าหมาย (amount) ของเกม
    }

    public double getAccuracy() {
        return accuracy; // คืนค่าความแม่นยำเป็นเปอร์เซ็นต์
    }

    public int getCorrectChars() {
        return correctChars; // คืนค่าจำนวนตัวอักษรที่พิมพ์ถูก
    }

    public LocalDate getDate() {
        return date; // คืนค่าวันที่เล่นที่เป็น LocalDate
    }

    public int getIncorrectChars() {
        return incorrectChars; // คืนค่าจำนวนตัวอักษรที่กดพลาด
    }

    // ฟังก์ชันนี้เป็น static คืนค่า MODE_TIME จากคลาสโดยตรง (เรียกด้วย Score.getModeTime() ได้เลยโดยไม่ต้องใช้ new Score(...))
    public static String getModeTime() {
        return MODE_TIME;
    }

    // ฟังก์ชันนี้เป็น static คืนค่า MODE_WORDS
    public static String getModeWords() {
        return MODE_WORDS;
    }

    public int getSeconds() {
        return seconds; // คืนค่าจำนวนวินาทีที่ใช้ไป
    }

    public double getWpm() {
        return wpm; // คืนค่า WPM
    }
}
