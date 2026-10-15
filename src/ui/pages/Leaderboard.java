package ui.pages;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import model.Score;
import model.User;
import service.InMemorySession;
import service.ScoreService;
import ui.UiUtil;
import ui.components.ActionRoundButton;
import ui.components.HeaderBar;
import ui.components.ScoreRow;

/**
 * คลาสหน้าจอแสดงรายการจัดอันดับคะแนน (Leaderboard Page) สำหรับเกม WordType
 * <p>
 * หน้าจอนี้ทำหน้าที่ดึงข้อมูลคะแนนจาก {@link ScoreService} และนำมาแสดงผลในรูปแบบตารางอันดับ
 * โดยมีคุณสมบัติต่าง ๆ ดังนี้:
 * <ul>
 *   <li><b>การกรองข้อมูล (Filter):</b> สามารถสลับดูคะแนนระหว่างโหมดจับเวลา (Time Mode) และโหมดจำนวนคำ (Words Mode)
 *       รวมถึงเลือกระยะเวลา (15, 30, 60, 120 วินาที) หรือจำนวนคำ (10, 25, 50, 100 คำ) ได้</li>
 *   <li><b>การแบ่งหน้า (Pagination):</b> แสดงผลหน้าละ 5 รายการ เพื่อความสวยงามและอ่านง่าย พร้อมปุ่มเลื่อนหน้าซ้าย-ขวา</li>
 *   <li><b>แถวคะแนนพิเศษ (Pinned Row):</b> ตรึงแถวคะแนนสถิติที่ดีที่สุดของผู้ใช้งานที่เข้าสู่ระบบอยู่ในขณะนั้นไว้ด้านล่างสุด
 *       พร้อมเน้นด้วยสีเขียว (Highlight) เพื่อให้เปรียบเทียบกับอันดับของผู้อื่นได้ทันที</li>
 *   <li><b>คีย์ลัด (Keyboard Shortcuts):</b>
 *       <ul>
 *         <li>ปุ่ม <code>ESC</code>: ย้อนกลับไปยังหน้าแรก (Home)</li>
 *         <li>ปุ่ม <code>ลูกศรซ้าย (&larr;)</code>: เลื่อนไปยังหน้าก่อนหน้า</li>
 *         <li>ปุ่ม <code>ลูกศรขวา (&rarr;)</code>: เลื่อนไปยังหน้าถัดไป</li>
 *       </ul>
 *   </li>
 *   <li><b>การเชื่อมต่อกับ MainFrame:</b> รองรับเมธอด {@link #refresh()} เพื่อรีเฟรชข้อมูลตามตัวกรองเดิม
 *       และเมธอด {@link #open(String, int)} เพื่อเปิดหน้าแสดงผลตามโหมดและจำนวนที่ผู้ใช้เพิ่งเล่นเสร็จ</li>
 * </ul>
 * 
 * @author Warayu S. / Ui-Integration Team
 */
public class Leaderboard extends JPanel {

    // ==================================================================
    // ค่าคงที่สำหรับการจัดวางเลย์เอาต์และเรขาคณิตของตาราง (Layout & Geometry)
    // ==================================================================

    /** จำนวนแถวคะแนนสูงสุดที่จะแสดงผลในแต่ละหน้า */
    private static final int PAGE_SIZE = 5;

    /** พิกัดแนวนอน (แกน X) จุดเริ่มต้นของการวางตารางคะแนน */
    private static final int START_X = 150;

    /** พิกัดแนวตั้ง (แกน Y) จุดเริ่มต้นของแถวแรกในตารางคะแนน */
    private static final int START_Y = 200;

    /** ความสูงของแต่ละแถวในตารางคะแนน (พิกเซล) */
    private static final int ROW_HEIGHT = 46;

    /** ระยะห่างแนวตั้งระหว่างแต่ละแถวของตารางคะแนน (พิกเซล) */
    private static final int GAP = 8;

    /** ความกว้างของแต่ละแถวคะแนน (พิกเซล) */
    private static final int ROW_WIDTH = 900;

    /**
     * พิกัดแนวตั้ง (แกน Y) ของแถวตรึงพิเศษ (Pinned Row) สำหรับแสดงสถิติของผู้ใช้ปัจจุบัน
     * โดยคำนวณให้อยู่ถัดลงมาจากพื้นที่แสดงผล 5 แถวปกติ พร้อมเว้นระยะห่างด้านบนอีก 15 พิกเซล
     */
    private static final int PINNED_Y = START_Y + (ROW_HEIGHT + GAP) * PAGE_SIZE + 15;

    // ==================================================================
    // ค่าคงที่สำหรับชุดสีและรูปแบบวันที่ (Colors & Formats)
    // ==================================================================

    /** สีเขียวหัวเป็ดหลักของแอปพลิเคชัน (Teal) */
    private static final Color TEAL = UiUtil.TEAL;

    /** สีเทากลาง (#8E9094) สำหรับข้อความหัวตารางและข้อความรอง */
    private static final Color GREY = Color.decode("#8E9094");

    /** สีเทาอ่อนของพื้นหลังปุ่ม Pill ขณะที่ไม่ได้ถูกเลือก */
    private static final Color PILL_BG = new Color(213, 214, 216);

    /** รูปแบบการจัดฟอร์แมตวันที่สำหรับแสดงในตาราง เช่น "16 Oct 26" */
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yy", Locale.ENGLISH);

    // ==================================================================
    // เซอร์วิสและคอมโพเนนต์ส่วนติดต่อผู้ใช้ (Services & UI Components)
    // ==================================================================

    /** เซอร์วิสสำหรับจัดการและเข้าถึงข้อมูลคะแนนจัดอันดับ */
    private final ScoreService scoreService;

    // --- ปุ่มสลับโหมดการเล่น (Mode Toggle Buttons รูปแบบแคปซูล) ---
    /** ปุ่มสลับเลือกโหมดจับเวลา (Time Mode) */
    private final JToggleButton btnTimeMode = new Pill("time");

    /** ปุ่มสลับเลือกโหมดจำนวนคำ (Words Mode) */
    private final JToggleButton btnWordsMode = new Pill("A words");

    /** รายการปุ่มเลือกปริมาณ (เวลาเป็นวินาที หรือจำนวนคำ ขึ้นอยู่กับโหมดที่เลือก) */
    private final JToggleButton[] amountButtons = {
        new Pill("15"), new Pill("30"), new Pill("60"), new Pill("120")
    };

    /** รายการเก็บคอมโพเนนต์ ScoreRow ที่กำลังแสดงผลอยู่ในหน้าปัจจุบัน */
    private final List<ScoreRow> rowViews = new ArrayList<>();

    /** คอมโพเนนต์ ScoreRow สำหรับแถวตรึงพิเศษ (Pinned Row) ของผู้ใช้ที่เข้าสู่ระบบ */
    private ScoreRow pinnedView;

    /** ป้ายข้อความแสดงเมื่อยังไม่มีข้อมูลคะแนนในหมวดที่เลือก ("no scores yet") */
    private final JLabel lblEmpty = new JLabel("no scores yet", SwingConstants.CENTER);

    /** ป้ายข้อความแสดงเลขหน้าปัจจุบันและจำนวนหน้าทั้งหมด เช่น "1 / 3" */
    private final JLabel lblPageNum = new JLabel("1 / 1", SwingConstants.CENTER);

    // ==================================================================
    // ตัวแปรสถานะภายในของหน้าจัดอันดับ (State Variables)
    // ==================================================================

    /** โหมดที่กำลังเลือกดู (ค่าเริ่มต้นคือ โหมดเวลา "time") */
    private String mode = Score.getModeTime();

    /** ปริมาณเวลาหรือจำนวนคำที่กำลังเลือกดู (ค่าเริ่มต้นคือ 30) */
    private int amount = 30;

    /** หมายเลขหน้าปัจจุบัน (Zero-based index โดยเริ่มนับหน้าแรกที่ 0) */
    private int page = 0;

    /** ชื่อผู้ใช้ที่กำลังเข้าสู่ระบบอยู่ในปัจจุบัน (null หากยังไม่ได้ล็อกอิน) */
    private String currentUser;

    /** รายการคะแนนทั้งหมดที่ดึงมาจาก ScoreService ตามโหมดและปริมาณที่เลือก */
    private List<Score> ranking = new ArrayList<>();

    /**
     * แฟล็กป้องกันการโหลดข้อมูลซ้ำซ้อน (Event Loop)
     * เมื่อโค้ดโปรแกรมทำการตั้งค่าสถานะปุ่มเอง จะตั้งค่านี้เป็น true เพื่อไม่ให้ทริกเกอร์ onFilterChanged()
     */
    private boolean loading = false;

    /** ออบเจกต์คำสั่ง Runnable สำหรับส่งต่อไปยังหน้า Home เมื่อผู้ใช้กดย้อนกลับ */
    private Runnable onBack;

    /**
     * คอนสตรักเตอร์สำหรับสร้างหน้าจอ Leaderboard
     * ทำหน้าที่กำหนดค่าเลย์เอาต์ สร้างและจัดวางคอมโพเนนต์ทั้งหมด รวมถึงลงทะเบียน Event Listeners และคีย์ลัด
     *
     * @param scoreService เซอร์วิสสำหรับดึงข้อมูลคะแนนจัดอันดับ
     */
    public Leaderboard(ScoreService scoreService) {
        this.scoreService = scoreService;

        // --- 1. การตั้งค่าเลย์เอาต์และพื้นหลังของ Panel ---
        // ใช้ null layout เพื่อจัดวางตำแหน่งและขนาดของคอมโพเนนต์แบบสัมบูรณ์ (Absolute Positioning) ตามดีไซน์
        setLayout(null);
        setBackground(UiUtil.BG_COLOR);

        // --- 2. แถบหัวกระดาษ (HeaderBar) ---
        // HeaderBar มีปุ่มโลโก้/โปรไฟล์ที่สลับหน้าเองผ่าน MainFrame ได้ ไม่จำเป็นต้องผูก callback เพิ่มเติม
        add(new HeaderBar());

        // --- 3. ปุ่มเลือกโหมดและปริมาณ (Mode & Amount Selectors) ---
        // วางตำแหน่งที่พิกัด Y = 125 ความสูง 38 พิกเซล เพื่อให้ตรงกับตำแหน่งเดิมของ ModeSelector ในหน้า Home
        Icon timeOff = icon("TImeModeIcon_OFF.png");
        Icon timeOn = icon("TImeModeIcon_ON.png");
        if (timeOff != null) btnTimeMode.setIcon(timeOff);
        if (timeOn != null) btnTimeMode.setSelectedIcon(timeOn);
        btnTimeMode.setIconTextGap(8); // เว้นระยะห่างระหว่างไอคอนนาฬิกากับข้อความ
        btnTimeMode.setBounds(342, 125, 98, 38);
        btnWordsMode.setBounds(450, 125, 98, 38);

        // รวมกลุ่มปุ่มโหมดให้อยู่ใน ButtonGroup เพื่อให้เลือกได้เพียงโหมดเดียวในแต่ละครั้ง
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(btnTimeMode);
        modeGroup.add(btnWordsMode);
        add(btnTimeMode);
        add(btnWordsMode);

        // กำหนดพิกัด X ให้กับปุ่มปริมาณทั้ง 4 ปุ่ม โดยเว้นระยะห่างเท่าๆ กัน
        int[] amountX = { 581, 643, 705, 767 };
        ButtonGroup amountGroup = new ButtonGroup();
        for (int i = 0; i < amountButtons.length; i++) {
            amountGroup.add(amountButtons[i]);
            amountButtons[i].setBounds(amountX[i], 125, 52, 38);
            // ผูก ActionListener เมื่อคลิกเปลี่ยนปริมาณ ให้รีเฟรชข้อมูลตามตัวกรองใหม่
            amountButtons[i].addActionListener(e -> onFilterChanged());
            add(amountButtons[i]);
        }

        // เมื่อกดสลับเป็น Time Mode: ปรับตัวเลขปริมาณเป็น (15, 30, 60, 120) และโหลดข้อมูลใหม่
        btnTimeMode.addActionListener(e -> {
            setAmountLabels(true);
            onFilterChanged();
        });
        // เมื่อกดสลับเป็น Words Mode: ปรับตัวเลขปริมาณเป็น (10, 25, 50, 100) และโหลดข้อมูลใหม่
        btnWordsMode.addActionListener(e -> {
            setAmountLabels(false);
            onFilterChanged();
        });

        // กำหนดค่าเริ่มต้น: เลือกโหมดจับเวลา (Time Mode) และปริมาณ 30 วินาที
        btnTimeMode.setSelected(true);
        amountButtons[1].setSelected(true);

        // --- 4. หัวตารางจัดอันดับ (Table Headers) ---
        // วางชื่อคอลัมน์ต่าง ๆ ให้ตรงกับคอลัมน์ของแถว ScoreRow ด้านล่าง
        int headerY = 180;
        Font headerFont = new Font("Roboto Mono", Font.PLAIN, 13);
        addHeaderLabel("rank", 185, headerY, 32, headerFont);
        addHeaderLabel("player", 290, headerY, 50, headerFont);
        addHeaderLabel("wpm", 620, headerY, 24, headerFont);
        addHeaderLabel("acc", 770, headerY, 24, headerFont);
        addHeaderLabel("date", 920, headerY, 32, headerFont);

        // --- 5. ป้ายข้อความแจ้งเตือนเมื่อไม่มีข้อมูลคะแนน (Empty State Label) ---
        lblEmpty.setFont(new Font("Roboto Mono", Font.PLAIN, 16));
        lblEmpty.setForeground(GREY);
        lblEmpty.setBounds(START_X, START_Y + 20, ROW_WIDTH, 30);
        add(lblEmpty);

        // --- 6. แถบควบคุมการเปลี่ยนหน้า (Pagination Controls) ---
        // ปุ่มเลื่อนไปหน้าก่อนหน้า (<)
        ActionRoundButton btnPrev = new ActionRoundButton("<");
        btnPrev.setBounds(518, 550, 32, 32);
        onClick(btnPrev, () -> changePage(-1));
        add(btnPrev);

        // ป้ายแสดงสถานะหน้าปัจจุบัน / หน้าทั้งหมด
        lblPageNum.setFont(new Font("Roboto Mono", Font.BOLD, 16));
        lblPageNum.setForeground(Color.BLACK);
        lblPageNum.setBounds(565, 556, 70, 21);
        add(lblPageNum);

        // ปุ่มเลื่อนไปหน้าถัดไป (>)
        ActionRoundButton btnNext = new ActionRoundButton(">");
        btnNext.setBounds(650, 550, 32, 32);
        onClick(btnNext, () -> changePage(1));
        add(btnNext);

        // --- 7. คำใบ้คีย์ลัดและการผูกปุ่มคีย์บอร์ด (Keyboard Shortcuts) ---
        JLabel lblShortcut = new JLabel("<html><font color='#006664'>[ ESC ]</font> <font color='#000000'>- back to home</font></html>");
        lblShortcut.setFont(new Font("Menlo", Font.PLAIN, 14));
        lblShortcut.setBounds(508, 610, 184, 18);
        lblShortcut.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblShortcut);

        // ผูกปุ่มลัด ESC เพื่อกลับสู่หน้า Home
        bindKey(KeyEvent.VK_ESCAPE, "back", () -> {
            if (onBack != null) onBack.run();
        });
        // ผูกปุ่มลูกศรซ้าย เพื่อเปลี่ยนไปหน้าก่อนหน้า
        bindKey(KeyEvent.VK_LEFT, "prev", () -> changePage(-1));
        // ผูกปุ่มลูกศรขวา เพื่อเปลี่ยนไปหน้าถัดไป
        bindKey(KeyEvent.VK_RIGHT, "next", () -> changePage(1));
    }

    // ==================================================================
    // API สำหรับ MainFrame เรียกใช้งาน (Public APIs)
    // ==================================================================

    /**
     * กำหนดคำสั่งคอลแบ็กสำหรับการย้อนกลับไปหน้าแรก (Home)
     * มักถูกเรียกใช้โดย MainFrame เพื่อส่งต่อเมธอดแสดงหน้า Home เช่น {@code () -> showHome()}
     *
     * @param r คำสั่ง Runnable ที่จะทำงานเมื่อกดปุ่ม ESC
     */
    public void setOnBack(Runnable r) {
        this.onBack = r;
    }

    /**
     * รีเฟรชและโหลดข้อมูลคะแนนจัดอันดับใหม่ตามตัวกรองที่เลือกค้างอยู่
     * มีการอัปเดตชื่อผู้ใช้ปัจจุบันจากเซสชัน และรีเซ็ตหมายเลขหน้ากลับไปยังหน้าแรก (หน้า 0)
     */
    public void refresh() {
        currentUser = readCurrentUser();
        page = 0;
        reload();
    }

    /**
     * เปิดหน้า Leaderboard โดยระบุโหมดและปริมาณที่ต้องการดูโดยตรง
     * มักถูกเรียกหลังเล่นเกมจบ เพื่อให้เปิดมายังกระดานคะแนนของโหมดที่เพิ่งเล่นทันที
     *
     * @param mode   โหมดการเล่นที่ต้องการเปิดดู เช่น {@link Score#getModeTime()} หรือ {@link Score#getModeWords()}
     * @param amount ปริมาณเวลา (วินาที) หรือจำนวนคำที่ต้องการเปิดดู
     */
    public void open(String mode, int amount) {
        loading = true; // ตั้งค่าสถานะเพื่อป้องกันไม่ให้ Event Listener ยิงการ reload ซ้ำ
        this.mode = mode;
        this.amount = amount;
        
        // เลือกปุ่มโหมดตามค่าที่ส่งเข้ามา
        boolean timeMode = Score.getModeTime().equals(mode);
        (timeMode ? btnTimeMode : btnWordsMode).setSelected(true);
        setAmountLabels(timeMode);

        // เลือกปุ่มปริมาณที่มีตัวเลขตรงกับค่า amount
        for (JToggleButton b : amountButtons) {
            if (Integer.parseInt(b.getText()) == amount) b.setSelected(true);
        }
        loading = false;
        
        // โหลดข้อมูลคะแนนมาแสดงผล
        refresh();
    }

    // ==================================================================
    // ลอจิกการทำงานภายใน (Internal Logic)
    // ==================================================================

    /**
     * ดึงชื่อผู้ใช้ที่กำลังเข้าสู่ระบบอยู่ในปัจจุบันจากเซสชันหน่วยความจำ
     *
     * @return ชื่อผู้ใช้งาน (Username) หรือ {@code null} หากยังไม่มีการเข้าสู่ระบบ
     */
    private String readCurrentUser() {
        User u = InMemorySession.getInstance().getCurrentUser();
        return (u == null || u.getUsername() == null || u.getUsername().isEmpty()) ? null : u.getUsername();
    }

    /**
     * จัดการเหตุการณ์เมื่อตัวกรอง (โหมดหรือปริมาณ) ถูกเปลี่ยนแปลงโดยผู้ใช้
     * จะอ่านค่าตัวเลือกใหม่จากปุ่มบนหน้าจอ แล้วสั่งโหลดข้อมูลใหม่จากหน้าแรก
     */
    private void onFilterChanged() {
        // หากอยู่ในช่วงที่โค้ดโปรแกรมกำลังเซ็ตค่าปุ่มเอง ให้ข้ามการทำงาน
        if (loading) return;

        // ตรวจสอบโหมดที่ถูกเลือก
        mode = btnTimeMode.isSelected() ? Score.getModeTime() : Score.getModeWords();

        // ตรวจสอบปริมาณที่ถูกเลือก
        for (JToggleButton b : amountButtons) {
            if (b.isSelected()) amount = Integer.parseInt(b.getText());
        }

        // รีเซ็ตหน้ากลับไปหน้าแรก และโหลดข้อมูลคะแนนใหม่
        page = 0;
        reload();
    }

    /**
     * ปรับเปลี่ยนข้อความบนปุ่มปริมาณให้เหมาะสมกับโหมดที่เลือก
     * <ul>
     *   <li>โหมดเวลา (Time Mode): ตัวเลือกคือ 15, 30, 60, 120 วินาที</li>
     *   <li>โหมดจำนวนคำ (Words Mode): ตัวเลือกคือ 10, 25, 50, 100 คำ</li>
     * </ul>
     *
     * @param timeMode {@code true} หากเป็นโหมดจับเวลา, {@code false} หากเป็นโหมดจำนวนคำ
     */
    private void setAmountLabels(boolean timeMode) {
        String[] labels = timeMode ? new String[] { "15", "30", "60", "120" }
                                   : new String[] { "10", "25", "50", "100" };
        for (int i = 0; i < amountButtons.length; i++) amountButtons[i].setText(labels[i]);
    }

    /**
     * เปลี่ยนหน้าแสดงผลของตารางคะแนนตามผลต่างที่ระบุ
     *
     * @param delta ค่าความเปลี่ยนแปลงของหน้า (-1 สำหรับย้อนกลับ, +1 สำหรับไปข้างหน้า)
     */
    private void changePage(int delta) {
        int next = page + delta;
        // ตรวจสอบไม่ให้เลื่อนออกนอกขอบเขตหน้าที่มีอยู่จริง
        if (next < 0 || next >= totalPages()) return;
        page = next;
        render();
    }

    /**
     * คำนวณจำนวนหน้าทั้งหมดจากจำนวนรายการคะแนนที่ดึงมาได้
     * มีการการันตีว่าจะคืนค่าอย่างน้อย 1 หน้าเสมอ แม้จะไม่มีข้อมูลคะแนนเลยก็ตาม
     *
     * @return จำนวนหน้าทั้งหมด (อย่างน้อย 1)
     */
    private int totalPages() {
        return Math.max(1, (ranking.size() + PAGE_SIZE - 1) / PAGE_SIZE);
    }

    /**
     * เรียกดึงข้อมูลคะแนนจัดอันดับใหม่จาก {@link ScoreService}
     * และปรับแต่งค่าหน้าปัจจุบันให้อยู่ในช่วงที่ถูกต้อง ก่อนสั่งเรนเดอร์หน้าจอ
     */
    private void reload() {
        ranking = scoreService.leaderboard(mode, amount);
        page = Math.max(0, Math.min(page, totalPages() - 1));
        render();
    }

    // ==================================================================
    // การเรนเดอร์และวาดคอมโพเนนต์บนหน้าจอ (Rendering)
    // ==================================================================

    /**
     * แสดงผลตารางคะแนนและองค์ประกอบต่าง ๆ บนหน้าจอใหม่ทั้งหมด
     * <ol>
     *   <li>ลบคอมโพเนนต์แถวเก่าและแถวตรึงพิเศษเดิมออกจาก Panel</li>
     *   <li>สร้างคอมโพเนนต์ {@link ScoreRow} ใหม่สำหรับรายการคะแนนในหน้าปัจจุบัน (รอบละ 5 แถว)</li>
     *   <li>หากผู้ใช้ปัจจุบันล็อกอินอยู่ จะค้นหาสถิติที่ดีที่สุดของผู้ใช้ แล้วนำมาแสดงในแถวตรึงด้านล่างสุด (Pinned Row)</li>
     *   <li>อัปเดตการแสดงผลป้ายแจ้งเตือนเมื่อไม่มีข้อมูล และป้ายเลขหน้า</li>
     *   <li>สั่ง {@link #revalidate()} และ {@link #repaint()} เพื่อให้ Swing วาดหน้าจอใหม่ทันที</li>
     * </ol>
     */
    private void render() {
        // ลบแถวเดิมออกทั้งหมด (เนื่องจาก ScoreRow ไม่รองรับการแก้ค่า จึงสร้างใหม่เพื่อความถูกต้อง)
        for (ScoreRow r : rowViews) remove(r);
        rowViews.clear();
        if (pinnedView != null) {
            remove(pinnedView);
            pinnedView = null;
        }

        // คำนวณช่วงดัชนีของคะแนนที่จะนำมาแสดงในหน้าปัจจุบัน
        int from = page * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, ranking.size());

        // สร้างแถวคะแนนปกติของหน้านี้
        for (int i = from; i < to; i++) {
            Score s = ranking.get(i);
            boolean isMe = s.getUsername().equals(currentUser);
            // หากแถวนี้เป็นคะแนนของผู้ใช้ปัจจุบัน ให้ต่อท้ายชื่อด้วย "(you)"
            ScoreRow row = toRow(i + 1, s, isMe ? s.getUsername() + " (you)" : s.getUsername(), false);
            row.setBounds(START_X, START_Y + (ROW_HEIGHT + GAP) * (i - from), ROW_WIDTH, ROW_HEIGHT);
            add(row);
            rowViews.add(row);
        }

        // แถวสีเขียวตรึงพิเศษด้านล่างสุด: สถิติที่ดีที่สุดของผู้ใช้งานที่เข้าสู่ระบบอยู่
        // จะแสดงเฉพาะเมื่อเข้าสู่ระบบแล้ว และผู้ใช้มีคะแนนบันทึกอยู่ในหมวดนี้
        if (currentUser != null) {
            for (int i = 0; i < ranking.size(); i++) {
                if (ranking.get(i).getUsername().equals(currentUser)) {
                    // แถวนี้จะถูกเน้นด้วยพื้นหลังสีเขียว (highlight = true) และมีข้อความ "(your best)"
                    pinnedView = toRow(i + 1, ranking.get(i), currentUser + " (your best)", true);
                    pinnedView.setBounds(START_X, PINNED_Y, ROW_WIDTH, ROW_HEIGHT);
                    add(pinnedView);
                    break;
                }
            }
        }

        // แสดงป้าย "no scores yet" เมื่อยังไม่มีข้อมูลคะแนนในหมวดนี้
        lblEmpty.setVisible(ranking.isEmpty());

        // อัปเดตข้อความแสดงเลขหน้าปัจจุบัน เช่น "1 / 3"
        lblPageNum.setText((page + 1) + " / " + totalPages());

        // สั่งให้ Swing อัปเดตผังและวาดหน้าจอใหม่
        revalidate();
        repaint();
    }

    /**
     * แปลงออบเจกต์คะแนน {@link Score} ให้กลายเป็นคอมโพเนนต์แถวคะแนน {@link ScoreRow}
     * พร้อมทั้งจัดรูปแบบข้อความ เช่น ปัดเศษ WPM, ปัดเศษ Accuracy พร้อมใส่เครื่องหมาย %, และจัดรูปแบบวันที่
     *
     * @param rank        อันดับของคะแนน (1-based index)
     * @param s           ออบเจกต์คะแนนที่ต้องการแสดงผล
     * @param displayName ข้อความชื่อผู้เล่นที่จะแสดง (อาจต่อท้ายด้วย "(you)" หรือ "(your best)")
     * @param highlight   กำหนดว่าต้องการเน้นสีพื้นหลังแถวเป็นสีเขียวเป็นพิเศษหรือไม่
     * @return อินสแตนซ์ของ {@link ScoreRow} ที่พร้อมนำไปจัดวางบนหน้าจอ
     */
    private ScoreRow toRow(int rank, Score s, String displayName, boolean highlight) {
        return new ScoreRow(
                "#" + rank,
                displayName,
                String.valueOf(Math.round(s.getWpm())),
                Math.round(s.getAccuracy()) + "%",
                s.getDate().format(DATE_FMT),
                highlight);
    }

    // ==================================================================
    // เมธอดตัวช่วยอื่น ๆ (Helper Methods)
    // ==================================================================

    /**
     * โหลดรูปภาพไอคอนจากชื่อไฟล์อย่างปลอดภัย
     * หากเกิดข้อผิดพลาดในการโหลดไฟล์ จะคืนค่า {@code null} แทนที่จะโยนข้อผิดพลาดทำให้หน้าจอหยุดทำงาน
     *
     * @param name ชื่อไฟล์ไอคอนในโฟลเดอร์รูปภาพ
     * @return ออบเจกต์ {@link Icon} หรือ {@code null} หากไม่พบไฟล์
     */
    private static Icon icon(String name) {
        try {
            return UiUtil.loadIcon(name);
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * สร้างและจัดวางป้ายข้อความหัวตาราง (Table Header Label) ลงบนหน้าจอ
     *
     * @param text ข้อความหัวตาราง เช่น "rank", "player", "wpm", "acc", "date"
     * @param x    พิกัดแนวนอน (แกน X)
     * @param y    พิกัดแนวตั้ง (แกน Y)
     * @param w    ความกว้างของป้ายข้อความ
     * @param font รูปแบบฟอนต์ที่ใช้สำหรับข้อความ
     */
    private void addHeaderLabel(String text, int x, int y, int w, Font font) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(GREY);
        l.setBounds(x, y, w, 17);
        add(l);
    }

    /**
     * ผูกเหตุการณ์คลิกเมาส์เข้ากับคอมโพเนนต์ใด ๆ โดยใช้ {@link MouseAdapter}
     * ช่วยให้สามารถรับการคลิกบนคอมโพเนนต์ได้ง่ายโดยไม่ต้องสนใจว่าคอมโพเนนต์นั้นสืบทอดมาจากคลาสใด
     *
     * @param c      คอมโพเนนต์ที่ต้องการรับการคลิก
     * @param action คำสั่ง Runnable ที่ต้องการให้ทำงานเมื่อมีการคลิกเมาส์
     */
    private void onClick(JComponent c, Runnable action) {
        c.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });
    }

    /**
     * ผูกปุ่มลัดบนคีย์บอร์ดเข้ากับคำสั่งการทำงาน โดยใช้ระบบ InputMap และ ActionMap ของ Swing
     * กำหนดเงื่อนไขการรับคีย์ลัดเมื่อหน้าต่างแอปพลิเคชันมีโฟกัส (WHEN_IN_FOCUSED_WINDOW)
     * และจะทำงานเฉพาะตอนที่หน้า Leaderboard กำลังแสดงผลอยู่บนหน้าจอเท่านั้น (isShowing())
     *
     * @param keyCode รหัสปุ่มบนคีย์บอร์ดจาก {@link KeyEvent} (เช่น KeyEvent.VK_ESCAPE)
     * @param name    ชื่ออ้างอิงของ Action
     * @param action  คำสั่ง Runnable ที่ต้องการให้ทำงานเมื่อผู้ใช้กดปุ่ม
     */
    private void bindKey(int keyCode, String name, Runnable action) {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyCode, 0), name);
        getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // ทำงานเฉพาะตอนหน้านี้แสดงอยู่ เพื่อป้องกันการชนกับหน้าอื่นที่อาจใช้ปุ่มเดียวกัน
                if (isShowing()) action.run();
            }
        });
    }

    // ==================================================================
    // คลาสปุ่มแคปซูลแบบกำหนดเอง (Custom Pill Toggle Button)
    // ==================================================================

    /**
     * คอมโพเนนต์ปุ่มสลับรูปทรงแคปซูล (Pill Toggle Button)
     * ตกแต่งสไตล์มินิมอลตามแบบของโปรเจกต์:
     * <ul>
     *   <li>สถานะปกติ (ไม่ได้เลือก): พื้นหลังสีเทาอ่อน (#D5D6D8) ตัวอักษรสีดำ</li>
     *   <li>สถานะถูกเลือก (Selected): พื้นหลังสีเขียวหลัก (Teal) ตัวอักษรสีขาว</li>
     *   <li>ขอบมนสวยงามด้วยการวาดแบบกำหนดเอง (Custom Painting)</li>
     * </ul>
     */
    private static class Pill extends JToggleButton {

        /**
         * คอนสตรักเตอร์สำหรับสร้างปุ่มทรงแคปซูล
         *
         * @param text ข้อความที่จะแสดงบนปุ่ม
         */
        Pill(String text) {
            super(text);
            setFont(new Font("Roboto Mono", Font.BOLD, 13));
            setMargin(new java.awt.Insets(0, 0, 0, 0));
            setFocusable(false);             // ไม่ต้องแสดงกรอบโฟกัสของปุ่ม
            setFocusPainted(false);          // ปิดการวาดเอฟเฟกต์โฟกัส
            setBorderPainted(false);         // ปิดการวาดเส้นขอบเริ่มต้นของ Swing
            setContentAreaFilled(false);     // ปิดการวาดพื้นหลังเริ่มต้นของ Swing (เพราะเราจะวาดเอง)
            setCursor(new Cursor(Cursor.HAND_CURSOR)); // เปลี่ยนเคอร์เซอร์เป็นรูปมือเมื่อชี้
        }

        /**
         * กำหนดสีของข้อความบนปุ่มตามสถานะการเลือก
         *
         * @return สีขาวหากปุ่มกำลังถูกเลือก หรือสีดำหากปุ่มไม่ได้ถูกเลือก
         */
        @Override
        public Color getForeground() {
            return isSelected() ? Color.WHITE : Color.BLACK;
        }

        /**
         * วาดภาพพื้นหลังของปุ่มเป็นรูปสี่เหลี่ยมขอบมน (Rounded Rectangle)
         * พร้อมเปิดระบบลบรอยหยัก (Anti-Aliasing) เพื่อให้ขอบโค้งมนเรียบเนียน
         *
         * @param g กราฟิกคอนเทกซ์ที่ใช้ในการวาด
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            // เปิดระบบลบรอยหยักเพื่อความคมชัดและนุ่มนวลของขอบมน
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // เลือกสีพื้นหลัง: สีเขียว (TEAL) ถ้าถูกเลือก หรือสีเทาอ่อน (PILL_BG) ถ้าไม่ได้เลือก
            g2.setColor(isSelected() ? TEAL : PILL_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

            g2.dispose(); // คืนทรัพยากรกราฟิก
            super.paintComponent(g); // ให้ Swing วาดข้อความและไอคอนบนปุ่มต่อตามปกติ
        }
    }
}