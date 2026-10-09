package ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import service.TypingEngine;
import ui.UiUtil;

/**
 * คอมโพเนนต์สำหรับพื้นที่แสดงและจัดการข้อความที่ใช้ในการฝึกพิมพ์
 * วาดสถานะของ TypingEngine ลงใน JTextPane
 * - ตัวที่ยังไม่พิมพ์ = เทา, ตัวที่ถูก = เข้ม, ตัวที่ผิด/ตัวเกิน = แดง
 * - มีเคอร์เซอร์เส้นบางสีเขียวหน้าตัวถัดไป
 * - เลื่อนข้อความให้บรรทัดที่กำลังพิมพ์อยู่บรรทัดที่ 2 เสมอ
 */
public class TypingView extends JScrollPane {
    private static final int UNTYPED = 0, CORRECT = 1, WRONG = 2;

    private final JTextPane typeArea;
    private final SimpleAttributeSet[] styles = new SimpleAttributeSet[3];
    private final Highlighter.HighlightPainter caretPainter = new CaretPainter();
    
    private boolean caretBlinkVisible = true;
    private final Timer blinkTimer;

    public TypingView() {
        typeArea = new JTextPane();
        typeArea.setOpaque(false);
        typeArea.setEditable(false);
        typeArea.setFocusable(false);
        typeArea.setFont(new Font("Menlo", Font.PLAIN, 36));
        typeArea.setForeground(Color.decode("#8E9094"));
        typeArea.setText("he man own must tell such year keep leave too not " +
                         "when all would get number child that course set many late just");
        typeArea.setCaretPosition(0);

        setViewportView(typeArea);
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        setBorder(null);
        setOpaque(false);
        getViewport().setOpaque(false);

        styles[UNTYPED] = style(new Color(142, 144, 148)); // #8E9094
        styles[CORRECT] = style(new Color(40, 42, 46));
        styles[WRONG] = style(Color.decode("#D14B57")); // สีแดงสำหรับตัวผิด

        blinkTimer = new Timer(500, e -> {
            caretBlinkVisible = !caretBlinkVisible;
            typeArea.repaint();
        });
        blinkTimer.start();
    }

    private static SimpleAttributeSet style(Color c) {
        SimpleAttributeSet a = new SimpleAttributeSet();
        StyleConstants.setForeground(a, c);
        return a;
    }

    /** 
     * วาดเนื้อหาใหม่ทั้งหมดลงบน JTextPane ตามสถานะปัจจุบันของ TypingEngine
     * (เวอร์ชันนี้ใช้วิธี Direct Insertion แปะตัวอักษรจุ่มสีลงกระดานโดยตรง เพื่อให้อ่านโค้ดเข้าใจง่ายที่สุด)
     * 
     * @param engine ออบเจกต์ที่เก็บสถานะการพิมพ์ปัจจุบัน (คำศัพท์, คำที่พิมพ์ไปแล้ว, index ปัจจุบัน)
     * @param showCaret กำหนดว่าจะแสดงเส้นเคอร์เซอร์กะพริบหรือไม่ (false ตอนยังไม่เริ่มพิมพ์)
     */
    public void render(TypingEngine engine, boolean showCaret) {
        if (engine == null) return;
        List<String> words = engine.getWords();
        int cur = engine.getCurrentIndex(); // index ของคำที่กำลังพิมพ์อยู่ ณ ปัจจุบัน

        int caretOffset = 0; // เก็บพิกัดตำแหน่งที่เส้นเคอร์เซอร์กะพริบควรจะอยู่

        try {
            StyledDocument doc = typeArea.getStyledDocument();
            typeArea.getHighlighter().removeAllHighlights();
            doc.remove(0, doc.getLength()); // เคลียร์ข้อความเก่าทั้งหมดเพื่อเตรียมวาดใหม่

            // วนลูปตรวจสอบและแปะข้อความลงหน้าจอทีละคำ
            for (int i = 0; i < words.size(); i++) {
                String word = words.get(i);
                // type = คำที่ผู้ใช้พิมพ์มาแล้ว (ถ้า i > cur แปลว่ายังพิมพ์ไม่ถึง ให้ type เป็นค่าว่าง)
                String type = (i <= cur) ? engine.getTyped(i) : "";
                // passed = เช็คว่าคำนี้ผู้ใช้พิมพ์ผ่าน (กด Space ข้าม) ไปหรือยัง
                boolean passed = i < cur; 
                
                // ถ้าเป็นคำที่กำลังพิมพ์อยู่ ให้จำตำแหน่งปัจจุบัน (ความยาวตัวอักษรบนจอตอนนี้ + จำนวนที่เพิ่งพิมพ์) ไว้วาดเคอร์เซอร์
                if (i == cur) caretOffset = doc.getLength() + type.length();

                // 1. ตรวจสอบตัวอักษรเทียบกับคำศัพท์เป้าหมาย
                for (int k = 0; k < word.length(); k++) {
                    String charStr = String.valueOf(word.charAt(k));
                    if (k < type.length()) {
                        // ผู้ใช้พิมพ์ตัวอักษรนี้มาแล้ว -> เทียบว่าพิมพ์ถูกหรือผิด แล้วเลือกสี
                        SimpleAttributeSet color = type.charAt(k) == word.charAt(k) ? styles[CORRECT] : styles[WRONG];
                        doc.insertString(doc.getLength(), charStr, color); // แปะลงจอพร้อมสีเลย
                    } else if (passed) {
                        // คำที่กด Space ข้ามไปแล้วแต่พิมพ์ไม่ครบ -> บังคับให้ตัวที่เหลือเป็นสีแดง (WRONG)
                        doc.insertString(doc.getLength(), charStr, styles[WRONG]);
                    } else {
                        // คำที่ยังพิมพ์ไม่ถึง -> ให้เป็นสีเทาอ่อน (UNTYPED)
                        doc.insertString(doc.getLength(), charStr, styles[UNTYPED]);
                    }
                }
                
                // 2. ตรวจสอบตัวอักษรที่ผู้ใช้พิมพ์เกินความยาวคำศัพท์
                for (int k = word.length(); k < type.length(); k++) { 
                    String charStr = String.valueOf(type.charAt(k));
                    // ตัวที่พิมพ์เกินแปะลงจอเป็นสีแดงทั้งหมด
                    doc.insertString(doc.getLength(), charStr, styles[WRONG]);
                }
                
                // 3. จัดการช่องว่าง (Space) ท้ายคำ
                SimpleAttributeSet spaceColor = styles[UNTYPED]; // ค่าเริ่มต้นช่องว่างเป็นสีเทาอ่อน
                if (passed) {
                    // ถ้าคำนี้พิมพ์ผ่านมาแล้ว ให้เช็คบิลว่าถูกทั้งคำไหม
                    spaceColor = type.equals(word) ? styles[CORRECT] : styles[WRONG];
                }
                doc.insertString(doc.getLength(), " ", spaceColor); // แปะช่องว่างพร้อมสี
            }
            
            // วาดเส้นเคอร์เซอร์กะพริบตรงพิกัดที่จดไว้ (ถ้าอนุญาต)
            if (showCaret) {
                caretBlinkVisible = true;
                blinkTimer.restart();
                typeArea.getHighlighter().addHighlight(caretOffset, caretOffset + 1, caretPainter);
            }
            
            // เลื่อนหน้าจอให้บรรทัดที่กำลังพิมพ์อยู่บรรทัดที่ 2 เสมอ
            scrollTo(caretOffset);
            final int off = caretOffset;
            SwingUtilities.invokeLater(() -> scrollTo(off)); // เผื่อ layout ยังไม่เสร็จ
            
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private void scrollTo(int offset) {
        try {
            Rectangle r = typeArea.modelToView(offset);
            if (r == null) return;
            JViewport vp = getViewport();
            int maxY = Math.max(0, typeArea.getPreferredSize().height - vp.getHeight());
            int y = Math.min(maxY, Math.max(0, r.y - r.height)); // บรรทัดที่พิมพ์อยู่ = บรรทัดที่ 2
            vp.setViewPosition(new Point(0, y));
        } catch (BadLocationException e) {
            // ไม่ต้องทำอะไร
        }
    }

    /** เคอร์เซอร์เส้นบาง ๆ ทางซ้ายของตัวอักษรถัดไป */
    private class CaretPainter implements Highlighter.HighlightPainter {
        @Override
        @SuppressWarnings("deprecation")
        public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
            if (!caretBlinkVisible) return;
            try {
                Rectangle r = c.modelToView(p0);
                if (r == null) return;
                g.setColor(UiUtil.TEAL);
                g.fillRect(r.x - 1, r.y + 4, 2, r.height - 8);
            } catch (BadLocationException e) {
                // ไม่ต้องทำอะไร
            }
        }
    }
}
