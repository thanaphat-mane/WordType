package ui.pages;

import java.util.function.Consumer;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicToggleButtonUI;

import model.Score;
import service.TypingEngine;
import service.WordService;
import ui.UiUtil;
import ui.components.HeaderBar;
import ui.components.TypingView;

/**
 * คลาส Home เป็นหน้าจอหลักของแอปพลิเคชันพิมพ์ดีด
 * รับผิดชอบการแสดงผลโหมดต่างๆ การจับเวลา และตรวจสอบการพิมพ์แบบ Real-time
 */
public class Home extends JPanel {
    
    // ส่วนประกอบของแถบเมนูด้านบน
    private final HeaderBar header = new HeaderBar();
    private Runnable onUserButtonClick;   // ฟังก์ชันทำงานเมื่อผู้ใช้กดปุ่มโปรไฟล์
    private Runnable onLeaderboardClick;  // ฟังก์ชันทำงานเมื่อกดปุ่มดูตารางคะแนน
    private Consumer<Score> onFinished; // ฟังก์ชันทำงานเมื่อรอบการพิมพ์จบลงเพื่อบันทึกคะแนน

    // ===== ตัวแปรสำหรับการจัดการระบบพิมพ์ดีด (Typing) =====
    private final WordService wordService = new WordService(); // คลาสสำหรับสุ่มและดึงคำศัพท์
    private final JLabel lblCounter = new JLabel(" ");         // ป้ายกำกับสำหรับแสดงเวลานับถอยหลัง หรือจำนวนคำ
    private TypingView typingView;                             // คอมโพเนนต์จัดการวาดข้อความบนหน้าจอ
    private TypingEngine engine;                               // เอนจินตรวจสอบการพิมพ์ (ถูก/ผิด ตำแหน่งปัจจุบัน)
    private java.util.List<String> currentWords;               // ชุดคำศัพท์ที่ใช้ในการพิมพ์รอบปัจจุบัน
    private String mode = Score.getModeTime();                 // โหมดการเล่น (เริ่มต้นเป็นโหมดจับเวลา)
    private int amount = 30;                                   // ระยะเวลา (วินาที) หรือจำนวนคำที่เป็นเป้าหมาย
    private boolean running = false;                           // สถานะบอกว่ากำลังจับเวลา/กำลังพิมพ์อยู่หรือไม่
    private long startNanos;                                   // เก็บเวลาเริ่มต้นแบบนาโนวินาที เพื่อความแม่นยำในการหา WPM
    private int remaining;                                     // เก็บเวลาหรือคำที่เหลือ
    private Timer countdown;                                   // ตัวจับเวลาของ Swing

    // ตัวแปรส่วน UI (ปุ่มกด, แผง)
    private ButtonGroup buttonGroup1; // กลุ่มปุ่มสลับโหมด (Time / Words)
    private ButtonGroup buttonGroup2; // กลุ่มปุ่มเลือกตัวเลขเป้าหมาย (15, 30, 60, 120)
    private JPanel jPanel1;
    private JToggleButton btnTimeMode;
    private JToggleButton btnWordsMode;
    private JToggleButton btnTime120;
    private JToggleButton btnTime15;
    private JToggleButton btnTime30;
    private JToggleButton btnTime60;
    private JScrollPane Typeping; // ตัวเก่า (ไม่ได้ใช้แล้วแต่ยังเก็บไว้ในโครงสร้าง GUI)
    private JTextPane TextTypeing;
    private JButton btnRestart;
    private JLabel jLabel5;

    public Home() {
        initComponents();
        
        jPanel1.add(header); // นำ header มาแปะหน้าจอ

        // เสกปุ่มต่างๆ ให้กลายเป็นรูปร่างแคปซูลโค้งมน
        styleCapsuleButton(btnTimeMode);
        styleCapsuleButton(btnWordsMode);
        styleCapsuleButton(btnTime15);
        styleCapsuleButton(btnTime30);
        styleCapsuleButton(btnTime60);
        styleCapsuleButton(btnTime120);

        // บังคับให้ปุ่ม Time เริ่มต้นมาเป็นสีเขียว (ถูกเลือกเป็นค่าตั้งต้น)
        btnTimeMode.setSelected(true);

        initTyping(); // เรียกเมธอดตั้งค่าเกี่ยวกับการพิมพ์
    }

    public void setOnLeaderboardClick(Runnable r) {
        this.onLeaderboardClick = r;
    }

    public void setOnFinished(Consumer<Score> c) {
        this.onFinished = c;
    }

    public String getMode() {
        return mode;
    }

    public int getAmount() {
        return amount;
    }

    /**
     * ตั้งค่า UI ส่วนของพื้นที่แสดงข้อความพิมพ์และตัวอักษร
     */
    private void initTyping() {
        // ตั้งค่าป้ายแสดงการนับเวลาถอยหลัง (ซ่อนไว้ก่อนจนกว่าจะเริ่มพิมพ์)
        lblCounter.setFont(new Font("Roboto Mono", Font.BOLD, 28));
        lblCounter.setForeground(UiUtil.TEAL);
        lblCounter.setBounds(150, 232, 400, 40);
        lblCounter.setVisible(false);
        jPanel1.add(lblCounter);

        Typeping.setBounds(150, 280, 900, 150);
        Typeping.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        TextTypeing.setFont(new Font("Menlo", Font.PLAIN, 36));
        
        // TypingView ถูกอัปเดตให้สอดคล้องกับ UI ใหม่แล้ว และถูกเตรียมไว้ใน ui.components.TypingView
        typingView = new TypingView(); 
        
        // ถอดคอมโพเนนต์อันเก่าออกและแทนที่ด้วย typingView ใหม่
        jPanel1.remove(Typeping);
        typingView.setBounds(150, 280, 900, 150);
        jPanel1.add(typingView);
        
        btnTime30.setSelected(true); // เลือกค่าเริ่มต้นเป็น 30

        btnRestart.addActionListener(e -> restart()); // ตั้งค่าปุ่มเริ่มใหม่

        // เพิ่ม Keyboard Event ไปยังระดับ Global ให้รับค่าการพิมพ์เมื่อหน้าต่างแอคทีฟอยู่
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(this::handleKey);

        restart(); // รีเซ็ตการพิมพ์รอบใหม่
    }

    /**
     * ฟังก์ชันรับและดักจับการกดปุ่มจากผู้ใช้ (Keyboard)
     */
    private boolean handleKey(java.awt.event.KeyEvent e) {
        if (!isShowing()) return false; // ถ้าหน้าไม่แสดงอยู่ไม่ต้องรับค่า
        if (e.isControlDown() || e.isAltDown() || e.isMetaDown()) return false; // ข้ามปุ่ม Shortcut (Ctrl, Alt)

        switch (e.getID()) {
            case java.awt.event.KeyEvent.KEY_PRESSED:
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    restart(); // กด ESC เพื่อเริ่มใหม่
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    onBackspace(); // จัดการปุ่มลบ (Backspace)
                }
                return true; 
            case java.awt.event.KeyEvent.KEY_TYPED:
                char c = e.getKeyChar();
                if (c >= 32 && c != 127) onChar(c); // รับเฉพาะตัวอักษรที่พิมพ์ได้
                return true;
            case java.awt.event.KeyEvent.KEY_RELEASED:
                return true;
            default:
                return false;
        }
    }

    /**
     * เมื่อผู้ใช้พิมพ์ตัวอักษรเข้ามา
     */
    private void onChar(char c) {
        if (engine == null || engine.isFinished()) return;
        if (!running) startTyping(); // ถ้ายังไม่เริ่มจับเวลา ให้เริ่มทันทีที่พิมพ์ตัวแรก
        engine.typeChar(c);        // ส่งตัวอักษรไปตรวจสอบในเอนจิน
        afterInput();              // อัปเดตหน้าจอหลังพิมพ์เสร็จ
    }

    /**
     * เมื่อผู้ใช้กดปุ่มลบ (Backspace)
     */
    private void onBackspace() {
        if (!running) return;
        engine.backspace();
        afterInput();
    }

    /**
     * ทำงานหลังจากการพิมพ์ทุกตัวอักษร หรือ การลบข้อความ 
     * เพื่ออัปเดตหน้าจอ และเช็คว่าจบการพิมพ์หรือยัง
     */
    private void afterInput() {
        updateCounter();
        typingView.render(engine, true); // สั่งให้คอมโพเนนต์วาดคำศัพท์ใหม่ (แบบมี Cursor)
        if (engine.isFinished()) finishTyping(); // ถ้าคำหมดแล้ว ให้จบทันที
    }

    /**
     * เมื่อมีการเปลี่ยนการตั้งค่าโหมด (Time/Words) หรือจำนวนตัวเลข
     */
    private void applySettings(String newMode) {
        mode = newMode;
        amount = selectedAmount();
        restart();
    }

    /**
     * ดึงค่าตัวเลขจากปุ่มที่ผู้ใช้กำลังเลือกอยู่ (15, 30, 60, 120)
     */
    private int selectedAmount() {
        JToggleButton[] all = { btnTime15, btnTime30, btnTime60, btnTime120 };
        for (JToggleButton b : all) {
            if (b.isSelected()) return Integer.parseInt(b.getText().trim());
        }
        return Score.getModeTime().equals(mode) ? 30 : 25; // ค่าเริ่มต้นเผื่อเกิดข้อผิดพลาด
    }

    /**
     * โหลดคำศัพท์ใหม่ และเริ่มรอบการพิมพ์แบบคลีน
     */
    public void restart() {
        // หากเป็นโหมดจับเวลา จะสุ่มคำเผื่อเอาไว้ล่วงหน้า 
        // เช่น เวลา 30 วินาที อาจจะใช้คำ 120 คำ (คำนวณเผื่อพิมพ์เร็ว)
        int count = Score.getModeTime().equals(mode) ? Math.max(60, amount * 4) : amount;
        currentWords = wordService.randomWords(count);
        prepare();
    }

    /**
     * เล่นซ้ำคำศัพท์เดิม (ไม่ได้สุ่มคำใหม่)
     */
    public void repeat() {
        prepare();
    }

    /**
     * ฟังก์ชันตัวช่วยเตรียมความพร้อมก่อนเริ่มการพิมพ์ในรอบใหม่
     */
    private void prepare() {
        stopTimer();
        running = false;
        engine = new TypingEngine(currentWords); // สร้างระบบพิมพ์อันใหม่พร้อมชุดคำ
        setChromeVisible(true);                  // เปิดให้เห็นปุ่มตั้งค่าต่างๆ (พวกปุ่ม 15 30 60)
        lblCounter.setVisible(false);            // ซ่อนตัวจับเวลา
        typingView.render(engine, false);        // รีเซ็ตการเรนเดอร์คำศัพท์
    }

    /**
     * เมื่อผู้ใช้เริ่มพิมพ์ตัวแรก ให้เริ่มนับเวลา และซ่อนปุ่มการตั้งค่าออก
     */
    private void startTyping() {
        running = true;
        startNanos = System.nanoTime(); // จดบันทึกเวลาที่เริ่มพิมพ์ (ระดับนาโนเพื่อความละเอียด)
        setChromeVisible(false);        // ซ่อนปุ่มต่างๆ ระหว่างการพิมพ์เพื่อไม่ให้เกะกะสายตา
        lblCounter.setVisible(true);

        if (Score.getModeTime().equals(mode)) {
            remaining = amount;
            // เริ่มตัวจับเวลา (Timer) ให้อัปเดตทุกๆ 200ms
            countdown = new Timer(200, e -> {
                // คำนวณเวลาถอยหลัง
                int r = amount - (int) ((System.nanoTime() - startNanos) / 1_000_000_000L);
                if (r != remaining) {
                    remaining = r;
                    updateCounter(); // อัปเดตตัวเลขบนหน้าจอ
                }
                if (r <= 0) finishTyping(); // ถ้าเวลาหมดแล้ว ให้เรียกฟังก์ชันสิ้นสุด
            });
            countdown.start();
        }
        updateCounter();
    }

    /**
     * คำนวณคะแนนตอนสิ้นสุดการพิมพ์ เช่น หมดเวลา หรือพิมพ์ครบจำนวนคำ
     */
    private void finishTyping() {
        if (!running) return;
        running = false;
        stopTimer();

        // คำนวณเวลาที่ใช้ทั้งหมด (ถ้าเป็นโหมดเวลา ก็คือค่าเวลาตามโหมดเลย เช่น 30 วินาที)
        double seconds = Score.getModeTime().equals(mode)
                ? amount
                : (System.nanoTime() - startNanos) / 1_000_000_000.0;
        
        // สร้างออบเจกต์คะแนน
        Score score = engine.buildScore(mode, amount, seconds);

        setChromeVisible(true);
        lblCounter.setVisible(false);

        // ส่งคะแนนไปยัง Callback (เพื่อเปิดหน้าแสดงผลลัพธ์)
        if (onFinished != null) {
            onFinished.accept(score);
        } else {
            // พิมพ์คะแนนออก console หากยังไม่ได้เชื่อม Callback (กรณีทดสอบใน Dev)
            System.out.println("Finished! WPM: " + score.getWpm() + ", ACC: " + score.getAccuracy());
            restart();
        }
    }

    /**
     * หยุดตัวนับเวลาถอยหลัง
     */
    private void stopTimer() {
        if (countdown != null) {
            countdown.stop();
            countdown = null;
        }
    }

    /**
     * อัปเดตข้อความจำนวนคำ / เวลาที่เหลืออยู่ บนหน้าจอ
     */
    private void updateCounter() {
        if (Score.getModeTime().equals(mode)) {
            lblCounter.setText(String.valueOf(Math.max(remaining, 0)));
        } else {
            lblCounter.setText((engine.getCurrentIndex() + 1) + "/" + amount);
        }
    }

    /**
     * เปิด/ปิด การมองเห็นปุ่มต่างๆ บนหน้า UI
     */
    private void setChromeVisible(boolean visible) {
        JComponent[] chrome = {
            btnTimeMode, btnWordsMode,
            btnTime15, btnTime30, btnTime60, btnTime120, btnRestart, jLabel5
        };
        for (JComponent c : chrome) c.setVisible(visible);

        header.setChromeVisible(visible, jPanel1.getBackground());
    }

    public void setOnUserButtonClick(Runnable onUserButtonClick){
        this.onUserButtonClick = onUserButtonClick;
    }

    /**
     * เปลี่ยนสไตล์ของปุ่ม (JToggleButton) ให้มีลักษณะเหมือนแคปซูล 
     * และปรับแต่งให้สลับสีเมื่อถูกเลือก/ไม่ได้ถูกเลือก
     */
    public void styleCapsuleButton(JToggleButton btn) {
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Roboto Mono", Font.BOLD, 15));

        // สลับสีตัวหนังสือเมื่อปุ่มถูกกด/ยกเลิกกด
        btn.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                btn.setForeground(btn.isSelected() ? Color.WHITE : Color.BLACK);
            }
        });
        btn.setForeground(btn.isSelected() ? Color.WHITE : Color.BLACK);

        // จัดการการวาดพื้นหลังของปุ่มแบบ Custom (ขอบมน, สีพื้น)
        btn.setUI(new BasicToggleButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (((AbstractButton) c).isSelected()) {
                    g2.setColor(UiUtil.TEAL); // สีเขียวสำหรับปุ่มที่แอคทีฟ
                } else {
                    g2.setColor(new Color(213, 214, 216)); // สีเทาอ่อนสำหรับปุ่มปกติ
                }
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20); // วาดรูปทรงสี่เหลี่ยมมุมมน
                g2.dispose();
                super.paint(g, c);
            }
        });
    }

    public void setUsername(String username) {
        // Not implemented in HeaderBar yet, but can be skipped or implemented
    }

    /**
     * เมธอดสำหรับสร้าง UI (ส่วนที่สร้างโดยตัวออกแบบ UI ของ NetBeans)
     */
    private void initComponents() {
        buttonGroup1 = new ButtonGroup();
        buttonGroup2 = new ButtonGroup();
        jPanel1 = new JPanel();
        btnTimeMode = new JToggleButton();
        btnWordsMode = new JToggleButton();
        btnTime120 = new JToggleButton();
        btnTime15 = new JToggleButton();
        btnTime30 = new JToggleButton();
        btnTime60 = new JToggleButton();
        Typeping = new JScrollPane();
        TextTypeing = new JTextPane();
        btnRestart = new JButton();
        jLabel5 = new JLabel();

        setPreferredSize(new java.awt.Dimension(1200, 700));
        setLayout(null);

        jPanel1.setBackground(UiUtil.BG_COLOR);
        jPanel1.setLayout(null);

        // ปุ่มโหมดจับเวลา (Time)
        buttonGroup1.add(btnTimeMode);
        btnTimeMode.setIcon(UiUtil.loadIcon("TImeModeIcon_OFF.png")); 
        btnTimeMode.setText("time");
        btnTimeMode.setIconTextGap(8);
        btnTimeMode.setSelectedIcon(UiUtil.loadIcon("TImeModeIcon_ON.png")); 
        btnTimeMode.addActionListener(this::btnTimeModeActionPerformed);
        jPanel1.add(btnTimeMode);
        btnTimeMode.setBounds(313, 150, 110, 38);

        // ปุ่มโหมดคำ (Words)
        buttonGroup1.add(btnWordsMode);
        btnWordsMode.setText("A words");
        btnWordsMode.addActionListener(this::btnWordsModeActionPerformed);
        jPanel1.add(btnWordsMode);
        btnWordsMode.setBounds(433, 150, 120, 38);

        // ปุ่มค่าเป้าหมายต่างๆ
        buttonGroup2.add(btnTime120);
        btnTime120.setText("120");
        btnTime120.addActionListener(this::btnTime120ActionPerformed);
        jPanel1.add(btnTime120);
        btnTime120.setBounds(808, 150, 80, 38);

        buttonGroup2.add(btnTime15);
        btnTime15.setText("15");
        btnTime15.addActionListener(this::btnTime15ActionPerformed);
        jPanel1.add(btnTime15);
        btnTime15.setBounds(583, 150, 65, 38);

        buttonGroup2.add(btnTime30);
        btnTime30.setText("30");
        btnTime30.addActionListener(this::btnTime30ActionPerformed);
        jPanel1.add(btnTime30);
        btnTime30.setBounds(658, 150, 65, 38);

        buttonGroup2.add(btnTime60);
        btnTime60.setText("60");
        btnTime60.addActionListener(this::btnTime60ActionPerformed);
        jPanel1.add(btnTime60);
        btnTime60.setBounds(733, 150, 65, 38);

        // ปุ่มเริ่มใหม่ (Restart)
        btnRestart.setIcon(UiUtil.loadIcon("btnRestart.png")); 
        btnRestart.setBorderPainted(false);
        btnRestart.setContentAreaFilled(false);
        btnRestart.setFocusPainted(false);
        btnRestart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jPanel1.add(btnRestart);
        btnRestart.setBounds(588, 465, 24, 24);

        // ข้อความแนะนําปุ่มลัด (Shortcut Hint)
        jLabel5.setFont(new Font("Menlo", Font.BOLD, 14));
        jLabel5.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel5.setText("<html><font color=\"#00675B\">[ ESC ]</font> <font color=\"#999999\">- restart</font></html>");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(150, 644, 900, 20);

        add(jPanel1);
        jPanel1.setBounds(0, 0, 1200, 700);
    }

    // =========================================================
    // ส่วนของการจัดการ Event การคลิกปุ่มหมวดหมู่/ค่าต่างๆ
    // =========================================================

    private void btnTimeModeActionPerformed(java.awt.event.ActionEvent evt) {
        btnTime15.setText("15");
        btnTime30.setText("30");
        btnTime60.setText("60");
        btnTime120.setText("120");
        applySettings(Score.getModeTime());
    }

    private void btnWordsModeActionPerformed(java.awt.event.ActionEvent evt) {
        btnTime15.setText("10");
        btnTime30.setText("25");
        btnTime60.setText("50");
        btnTime120.setText("100");
        applySettings(Score.getModeWords());
    }

    private void btnTime120ActionPerformed(java.awt.event.ActionEvent evt) {
        applySettings(mode);
    }
    
    private void btnTime15ActionPerformed(java.awt.event.ActionEvent evt) {
        applySettings(mode);
    }

    private void btnTime30ActionPerformed(java.awt.event.ActionEvent evt) {
        applySettings(mode);
    }

    private void btnTime60ActionPerformed(java.awt.event.ActionEvent evt) {
        applySettings(mode);
    }
}
