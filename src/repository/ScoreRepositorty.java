package repository;

import java.time.LocalDate; // ใช้คลาส LocalDate สำหรับแปลงตัวอักษรวันที่ ให้กลายเป็นออบเจกต์เวลาใน Java
import java.util.ArrayList; // ใช้ ArrayList ซึ่งเป็นโครงสร้างข้อมูลที่มีขนาดปรับเปลี่ยนได้อัตโนมัติ
import java.util.List; // ใช้ List ซึ่งเป็น Interface หลักสำหรับกำหนดชนิดของโครงสร้างแบบรายการ
import java.util.Locale; // ใช้ Locale เพื่อช่วยจัดรูปแบบการแสดงผลภาษา, ตัวเลข เช่น การกำหนดไม่ให้ใช้จุลภาคแทนจุดทศนิยม

import util.CSVUtil; // นำเข้า utility คลาสที่เราสร้างขึ้นมาเพื่อช่วยอ่านและเขียนไฟล์ CSV
import model.*; // นำเข้าคลาสทั้งหมดใน package model (เช่น Score) เพื่อนำมาใช้งาน

/**
 * คลาสสำหรับจัดการข้อมูลคะแนน (Score) ในฐานข้อมูล (ในที่นี้คือไฟล์ CSV)
 * ทำหน้าที่บันทึกคะแนนใหม่และดึงข้อมูลคะแนนตามเงื่อนไขที่กำหนด
 * เป็นตัวอย่างของการใช้ Repository Pattern
 * 
 * โครงสร้าง (Schema) ของข้อมูลในไฟล์ CSV 1 บรรทัดคือ: 
 * [0]username, [1]mode, [2]amount, [3]wpm, [4]accuracy, [5]correctChars, [6]incorrectChars, [7]seconds, [8]date
 */
public class ScoreRepositorty { // สังเกตชื่อคลาสมีการสะกดผิด (Repositorty) แต่เราจะคงไว้เพื่อให้โค้ดเดิมทำงานต่อไปได้

    /** ตำแหน่งไฟล์ CSV สัมพัทธ์ที่ใช้เป็นที่เก็บข้อมูลคะแนนทั้งหมดของเกม */
    private static final String FILE_PATH = "./src/data/scores.csv";

    /**
     * บันทึกข้อมูลคะแนนที่ผู้เล่นเพิ่งเล่นเสร็จลงในไฟล์ CSV
     * โดยจะนำข้อมูลที่อยู่ภายในออบเจกต์ Score มาแปลงเป็น String ชิ้นๆ ยัดใส่อาเรย์ แล้วส่งไปต่อท้ายไฟล์
     *
     * @param scores ข้อมูลคะแนนแบบ Object ที่ต้องการบันทึก
     */
    public void save(Score scores){
        // เรียกใช้ฟังก์ชันของ CSVUtil ในการเติมแถวใหม่ต่อท้ายไฟล์ (Append)
        // โดยการสร้าง String Array (new String[]{...}) เพื่อจัดระเบียบข้อมูลตามลำดับคอลัมน์ของ CSV
        CSVUtil.appendRow(FILE_PATH, new String[]{
            scores.getUsername(), // คอลัมน์ [0]: ดึงชื่อผู้ใช้ด้วย Getter
            scores.getMode(),     // คอลัมน์ [1]: ดึงโหมดเวลาหรือจำนวนคำ
            
            // คอลัมน์ [2]: amount เป็นชนิดข้อมูล int (ตัวเลข) เราต้องแปลงเป็น String (ตัวอักษร) ก่อน
            // String.valueOf() เป็นเมธอดของคลาส String ใช้แปลงประเภทข้อมูลต่างๆ (int, double, boolean) ให้กลายเป็นอักษร
            String.valueOf(scores.getAmount()), 
            
            // คอลัมน์ [3]: wpm เป็นค่าทศนิยม (double) 
            // String.format() จะทำหน้าที่เหมือน printf ใน C เพื่อจัดรูปแบบข้อความ
            // เราใช้ Locale.ROOT บังคับให้เป็นรูปแบบสากล เพื่อป้องกันบั๊กเวลาเอาโปรแกรมไปรันในเครื่องที่ตั้งค่าภูมิภาค (เช่น ยุโรป) ที่ใช้ลูกน้ำ (,) แทนจุด (.) ในเลขทศนิยม
            // "%.2f" หมายถึง แปลงตัวเลขทศนิยมให้แสดงผลแบบมีทศนิยมแค่ 2 ตำแหน่ง
            String.format(Locale.ROOT, "%.2f", scores.getWpm()), 
            
            // คอลัมน์ [4]: accuracy แปลงเป็นทศนิยม 2 ตำแหน่งเช่นเดียวกัน
            String.format(Locale.ROOT, "%.2f", scores.getAccuracy()),
            
            // คอลัมน์ [5] ถึง [7]: แปลงค่าตัวเลขจำนวนเต็ม (int) ทั้งหมดให้กลายเป็น String ข้อความเพื่อเขียนลงไฟล์
            String.valueOf(scores.getCorrectChars()),
            String.valueOf(scores.getIncorrectChars()),
            String.valueOf(scores.getSeconds()),
            
            // คอลัมน์ [8]: แปลงออบเจกต์วันที่ (LocalDate) กลับเป็น String ข้อความ (จะได้ format มาตรฐานเป็น YYYY-MM-DD ทันที)
            scores.getDate().toString()
        });
    }

    /**
     * ดึงรายการคะแนนทั้งหมด (ประวัติสถิติ) ที่ตรงกับโหมด (mode) และจำนวนเป้าหมาย (amount) ที่ค้นหา
     * เมธอดนี้จะทำการกวาดดูทุกบรรทัดในฐานข้อมูลเพื่อรวบรวมแชมป์ (หรือสถิติต่างๆ) เฉพาะหมวดนั้น
     *
     * @param mode รูปแบบการเล่น (เช่น "time" หรือ "words")
     * @param amount ปริมาณที่เป็นเป้า (เช่น 15, 30 หรือ จำนวนคำ 10, 25)
     * @return รายการ (List) เป็นกล่องรวมออบเจกต์ Score ที่ผ่านการคัดกรองตามเงื่อนไข
     */
    public List<Score> findbyModeAndAmount(String mode, int amount){
        // สร้าง List แบบว่างๆ ขึ้นมาก่อน โดยใช้คลาสลูกคือ ArrayList (เป็นอาเรย์ที่เพิ่มข้อมูลเรื่อยๆ ได้โดยไม่ต้องกำหนดขนาดตายตัว)
        // รายการนี้จะใช้เพื่อสะสมออบเจกต์ Score ที่ตรงเงื่อนไขแล้วในระหว่างที่วนลูป
        List<Score> result = new ArrayList<>();
        
        // วนลูปอ่านข้อมูลทุกแถวที่ถูกอ่านและแยกคอลัมน์แล้วจากคลาส CSVUtil
        // (CSVUtil.readAll() จะอ่านทุกบรรทัดในไฟล์ แล้วแยกด้วยจุลภาคออกมาเป็น String[] ทีละแถว)
        for (String[] row : CSVUtil.readAll(FILE_PATH)){
            // ใส่ try-catch บล็อกเอาไว้เผื่อว่าบรรทัดไหนในไฟล์ CSV ถูกผู้ใช้มือดีเข้าไปเปิดแก้ไขเองแล้วพิมพ์มั่วๆ 
            // ซึ่งอาจจะทำให้กระบวนการแปลงชนิดข้อมูลอย่าง Integer.parseInt ล้มเหลว (เกิด Exception) และทำให้โปรแกรมล่ม (Crash)
            try{
                // ตรวจสอบความสมบูรณ์ของบรรทัด (Data validation): 
                // หากบรรทัดนี้มีคอลัมน์รวมกันแล้วน้อยกว่า 9 ช่อง แปลว่ามีข้อมูลบางส่วนหายไป 
                // ก็จะสั่ง continue เพื่อกระโดดข้ามบรรทัดที่พังนี้ ไปทำงานลูปรอบถัดไปทันที
                if(row.length < 9) continue;
                
                // ตรวจสอบเงื่อนไขตัวกรอง (Filtering):
                // 1. row[1].equals(mode) โดยมี ! (NOT) อยู่ด้านหน้า แปลว่า ถ้า 'โหมดเกมในคอลัมน์ที่ 1 ไม่ตรงกับโหมดที่ต้องการค้นหา'
                // 2. Integer.parseInt(row[2]) คือการแปลงข้อความในคอลัมน์ที่ 2 (amount) ให้กลับเป็นตัวเลขก่อน แล้วนำไปเปรียบเทียบ (!=) กับค่าที่ถูกส่งเข้ามา
                // หากเงื่อนไขใดเงื่อนไขหนึ่งเป็นจริง (|| หมายถึง หรือ) จะถือว่าบรรทัดนี้เราไม่ต้องการ ให้สั่ง continue ข้ามไปเลย
                if(!row[1].equals(mode) || Integer.parseInt(row[2]) != amount) continue;
                
                // หากรอดเงื่อนไขการตรวจสอบด้านบนมาได้ แปลว่าข้อมูลนี้สมบูรณ์และตรงกับการค้นหา
                // เราจะเอาข้อมูลแต่ละคอลัมน์ที่เป็นแค่ข้อความ (String) มาแกะและแปลงกลับสู่ประเภทดั้งเดิม 
                // จากนั้นจับยัดเข้ากล่อง new Score(...) ตามพารามิเตอร์ของ Constructor เรียงตามลำดับ
                result.add(new Score(
                        row[0], // username (เป็น String อยู่แล้ว)
                        row[1], // mode (เป็น String อยู่แล้ว)
                        Integer.parseInt(row[2]), // amount (ใช้ parseInt แปลงข้อความเป็นเลขจำนวนเต็ม int)
                        Double.parseDouble(row[3]), // wpm (ใช้ parseDouble แปลงข้อความเป็นทศนิยม double)
                        Double.parseDouble(row[4]), // accuracy (แปลงเป็นทศนิยม double)
                        Integer.parseInt(row[5]), // correctChars (แปลงเป็น int)
                        Integer.parseInt(row[6]), // incorrectChars (แปลงเป็น int)
                        Integer.parseInt(row[7]), // seconds (แปลงเป็น int)
                        
                        // สร้าง LocalDate กลับคืนมาจากข้อความ 
                        // ใช้ row[8].trim() ในการตัดพื้นที่ช่องว่างหัวท้ายข้อความออกก่อน เพื่อป้องกัน Error ในการแปลงเวลา 
                        // และใช้ LocalDate.parse() ในการตีความอักษร (YYYY-MM-DD) คืนมาเป็นออบเจกต์
                        LocalDate.parse(row[8].trim()) 
                ));
            }catch(RuntimeException e){
                // catch block นี้จะทำงานก็ต่อเมื่อโค้ดที่อยู่ในบล็อก try เกิดความผิดพลาดรุนแรง (Runtime Exception)
                // เช่น ข้อมูลในไฟล์ไม่ใช่ตัวเลขแต่พยายามจะ parseInt เป็นต้น
                // ระบบจะสั่งพิมพ์แจ้งเตือนออกทางหน้าจอเพื่อให้นักพัฒนาทราบ
                System.err.println("Skip the problematic data rows");
                
                // คำสั่ง e.printStackTrace() จะปริ้นท์เส้นทางการเกิดบั๊กว่าเกิดบรรทัดไหน โค้ดไหนเรียกใช้บ้าง เพื่อช่วยในการตามแก้ปัญหา
                e.printStackTrace();
                
                // หมายเหตุ: สังเกตว่าเราไม่ได้ทำอะไรมากกว่าการปริ้นท์ และไม่ได้หยุดลูป 
                // โปรแกรมจะยังคงวนกลับไปอ่านไฟล์บรรทัดต่อไปตามปกติ (Robustness)
            }
        }

        // เมื่อลูปทำการอ่านทุกบรรทัดในฐานข้อมูลจนจบแล้ว
        // เราก็ส่งตะกร้า (List<Score>) ที่อาจจะบรรจุข้อมูลหลายสิบอัน หรืออาจจะว่างเปล่า (ถ้าหาไม่เจอเลย) ออกไปให้คนเรียกใช้งาน
        return result;
    }
}
