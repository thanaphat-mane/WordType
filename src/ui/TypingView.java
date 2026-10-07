package ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

public class TypingView extends JScrollPane {
    
    private JTextPane typeArea;

    public TypingView() {
        // --- 1. สร้าง JTextPane ไว้ข้างใน ---
        typeArea = new JTextPane();
        typeArea.setOpaque(false); // พื้นหลังโปร่งใส
        typeArea.setEditable(false); // ล็อกไว้ไม่ให้คลิกพิมพ์มั่ว
        typeArea.setFocusable(false); // ปิดรับโฟกัส
        typeArea.setHighlighter(null); // ปิดการลากแถบคลุมดำ
        
        typeArea.setFont(new Font("Menlo", Font.PLAIN, 36)); 
        typeArea.setForeground(Color.decode("#8E9094")); // สีเทาจางๆ

        // ข้อความเริ่มต้น
        typeArea.setText("he man own must tell such year keep leave too not " +
                         "when all would get number child that course set many late just");
        
        typeArea.setCaretPosition(0);
        setViewportView(typeArea);

        // --- 2. ตั้งค่า JScrollPane (ตัวเอง) คลุม JTextPane ---
        setViewportView(typeArea);
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        setBorder(null); 
        setOpaque(false);
        getViewport().setOpaque(false);
    }
    
    // TODO: สร้าง method render ไว้รอรับ TypingEngine ในอนาคต
    // public void render(TypingEngine engine, boolean showCaret) { ... }
}
