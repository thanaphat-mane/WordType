package util;

import java.io.*;
import java.util.*;

import repository.UserRepository;

/**
 * คลาสเครื่องมือ (Utility) สำหรับการจัดการไฟล์ CSV (อ่าน/เขียนไฟล์)
 * <p>
 * เหตุผลที่ต้องมีคลาสนี้: เพื่อแยกตรรกะการจัดการไฟล์ (File I/O) ออกจากการจัดการข้อมูล (Business Logic)
 * ทำให้คลาสอื่นๆ เช่น Repository ไม่ต้องเขียนโค้ดเปิดปิดไฟล์เองซ้ำๆ ลดความซ้ำซ้อนของโค้ด (DRY - Don't Repeat Yourself)
 * และจัดการข้อมูลเป็น String Array ที่แยกตามคอลัมน์แล้วให้พร้อมใช้งานได้ทันที
 */
public class CSVUtil {
    
    /**
     * เมธอดสำหรับอ่านข้อมูลจากไฟล์ CSV ทั้งหมด
     * การทำงาน: เปิดไฟล์ -> อ่านทีละบรรทัด -> แยกคำด้วยเครื่องหมายลูกน้ำ (,) -> เก็บเป็น List ของ String[]
     *
     * @param filePath เส้นทางของไฟล์ CSV (Path) ที่ต้องการจะอ่าน (เช่น "data/users.csv")
     * @return List ของ String[] โดย 1 String[] คือ 1 แถว และแต่ละช่องใน Array คือ 1 คอลัมน์ 
     *         ถ้าไฟล์ยังไม่มี หรืออ่านไม่ได้ จะคืนค่ากลับเป็น List เปล่าๆ ไม่ใช่ null เพื่อป้องกัน NullPointerException
     */
    public static List<String[]> readAll(String filePath) {
        // เตรียม List สำหรับเก็บข้อมูลทุกแถว
        List<String[]> rows = new ArrayList<>();
        
        // สร้างอ็อบเจ็กต์ File เพื่อใช้อ้างอิงถึงไฟล์ตามเส้นทางที่กำหนด
        File file = new File(filePath);

        // ตรวจสอบความปลอดภัย: ถ้าไม่มีไฟล์นี้อยู่ในเครื่อง (อาจจะเพิ่งรันโปรแกรมครั้งแรก)
        // ให้คืนค่า rows เปล่าๆ กลับไปเลย ไม่ต้องไปพยายามเปิดไฟล์ให้เกิด Error
        if (!file.exists())
            return rows;

        // ใช้ Try-with-resources (อยู่ในวงเล็บหลัง try) เพื่อให้ Java ปิดไฟล์ (close) ให้อัตโนมัติหลังใช้งานเสร็จ
        // BufferedReader และ FileReader ช่วยให้อ่านไฟล์ข้อความทีละบรรทัดได้อย่างมีประสิทธิภาพ
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            
            // วนลูปอ่านข้อความทีละบรรทัด เก็บไว้ในตัวแปร line
            // วนไปเรื่อยๆ จนกว่า readLine() จะคืนค่า null (แปลว่าหมดไฟล์แล้ว)
            while ((line = br.readLine()) != null) {
                
                // ตรวจสอบบรรทัดว่าง: เอาช่องว่างซ้ายขวาออก (trim) แล้วดูว่าว่างเปล่า (empty) หรือไม่
                // ถ้าเป็นบรรทัดว่างให้ข้ามไป (continue) ไม่ต้องเอามาเก็บในข้อมูล
                if (line.trim().isEmpty())
                    continue;
                
                // แยกข้อความด้วยเครื่องหมายลูกน้ำ (,) ซึ่งเป็นมาตรฐานของไฟล์ CSV
                // ค่า -1 ใส่เพื่อบอกว่า "ถึงแม้คอลัมน์ท้ายๆ จะว่างเปล่า ก็ให้แยกเก็บไว้ด้วย" 
                // นำ String[] ที่ได้จากการ split เพิ่มเข้าไปใน List ของเรา
                rows.add(line.split(",", -1));
            }
        } catch (IOException e) {
            // หากเกิดข้อผิดพลาดระหว่างอ่านไฟล์ เช่น สิทธิ์ไม่พอ หรือไฟล์ถูกล็อก
            // ให้พิมพ์รายละเอียดข้อผิดพลาดออกทาง Console เพื่อให้โปรแกรมเมอร์รู้สาเหตุ
            e.printStackTrace();
        }

        // คืนค่าชุดข้อมูลทั้งหมดที่อ่านและแยกคอลัมน์แล้วกลับไป
        return rows;
    }

    /**
     * เมธอดสำหรับเพิ่มข้อมูลแถวใหม่ต่อท้ายไฟล์ CSV เดิม (Append)
     * เหมาะสำหรับการบันทึกข้อมูลใหม่ (เช่น สมัครสมาชิกใหม่) โดยไม่ต้องเขียนข้อมูลเก่าทับลงไปใหม่หมด
     *
     * @param filePath เส้นทางของไฟล์ CSV ที่ต้องการเขียนต่อท้าย
     * @param values   อาร์เรย์ของ String ที่เก็บค่าของแต่ละคอลัมน์ใน 1 แถว (เช่น {"user1", "hash_pass"})
     */
    public static void appendRow(String filePath, String[] values) {
        try {
            File file = new File(filePath);
            // หาโฟลเดอร์แม่ที่เก็บไฟล์นี้ เพื่อเช็คว่าโฟลเดอร์มีหรือยัง
            File parent = file.getParentFile();
            
            // ถ้าไฟล์นี้ต้องอยู่ในโฟลเดอร์ย่อย แล้วโฟลเดอร์นั้นยังไม่เคยถูกสร้าง
            if (parent != null && !parent.exists()) {
                // ให้สร้างโฟลเดอร์ทั้งหมดตาม Path นั้น (mkdirs = make directories)
                parent.mkdirs();
            }
            
            // เปิดไฟล์เพื่อเขียน (FileWriter) 
            // ค่า true ในพารามิเตอร์ที่สอง หมายถึง เปิดในโหมด Append (เขียนต่อท้ายของเดิม ไม่ใช่เขียนทับ)
            // ห่อด้วย BufferedWriter เพื่อให้เขียนลงดิสก์ได้เร็วขึ้น
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                // String.join จะเอาอาร์เรย์ values มาต่อกันเป็นข้อความเดียว คั่นด้วยลูกน้ำ (,)
                // เช่น ["A", "B", "C"] จะกลายเป็น "A,B,C"
                // จากนั้นเขียนข้อความนี้ลงไปในไฟล์
                bw.write(String.join(",", values));
                
                // สั่งขึ้นบรรทัดใหม่ เพื่อให้การเขียนครั้งต่อไปอยู่บรรทัดถัดไป
                bw.newLine();
            }
        } catch (Exception e) {
            // จับ Exception ทุกประเภทที่อาจเกิดจากการเขียนไฟล์ แล้วพิมพ์แจ้งทาง Console
            e.printStackTrace();
        }
    }


    /**
     * เมธอดสำหรับเขียนทับไฟล์ CSV ทั้งหมดด้วยข้อมูลชุดใหม่ (Overwrite)
     * ใช้ในกรณีที่มีการแก้ไข (Update) หรือลบ (Delete) ข้อมูลบางส่วน
     * เพราะระบบไฟล์ไม่চ্ছুไฟล์ไม่สามารถแทรกหรือลบบรรทัดตรงกลางไฟล์ได้ง่ายๆ 
     * เราจึงต้องโหลดมาแก้ใน Memory แล้วเขียนทับใหม่ทั้งไฟล์
     *
     * @param filePath เส้นทางของไฟล์ CSV ที่ต้องการเขียนทับ
     * @param rows     ข้อมูลทั้งหมดที่อัปเดตแล้ว (เป็น List ของแต่ละบรรทัด) เพื่อนำไปเขียนแทนที่ข้อมูลเก่าทั้งหมด
     */
    public static void writeAll(String filePath, List<String[]> rows) {
        // ค่า false ใน FileWriter หมายถึงการเปิดในโหมด Overwrite (ลบข้อมูลเก่าทิ้งทั้งหมด แล้วเริ่มเขียนใหม่ตั้งแต่ต้นไฟล์)
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            
            // วนลูปหยิบข้อมูลทีละแถว (String[]) จาก List
            for (String[] row : rows) {
                // นำ String[] แต่ละช่องมาต่อกัน คั่นด้วยเครื่องหมายลูกน้ำ (,) แล้วเขียนลงไฟล์
                bw.write(String.join(",", row));
                // สั่งขึ้นบรรทัดใหม่ เตรียมสำหรับเขียนแถวถัดไป
                bw.newLine();
            }
        } catch (Exception e) {
            // จับข้อผิดพลาด (เช่น ไฟล์กำลังถูกเปิดใช้งานโดยโปรแกรมอื่น ทำให้เขียนทับไม่ได้)
            e.printStackTrace();
        }
    }
}
