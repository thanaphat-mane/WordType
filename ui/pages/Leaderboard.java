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
 * หน้า Leaderboard
 * <p>
 * หน้าตา (UI): ใช้ ScoreRow / ActionRoundButton / HeaderBar และตำแหน่งตามดีไซน์เดิมของโปรเจกต์
 * วิธีทำงาน: เลือก mode + จำนวน -> ตารางอันดับหน้าละ 5 แถว, แถวสีเขียวล่างสุด = สถิติที่ดีที่สุดของผู้ใช้ที่ล็อกอิน
 * <p>
 * MainFrame เรียก refresh() (โหลดตามตัวกรองเดิม) หรือ open(mode, amount) (เปิดตามโหมดที่เพิ่งเล่น)
 * ก่อนสลับมาหน้านี้ทุกครั้ง
 */
public class Leaderboard extends JPanel {

    private static final int PAGE_SIZE = 5;

    // เรขาคณิตของตาราง (ตามดีไซน์เดิม)
    private static final int START_X = 150;
    private static final int START_Y = 200;
    private static final int ROW_HEIGHT = 46;
    private static final int GAP = 8;
    private static final int ROW_WIDTH = 900;
    private static final int PINNED_Y = START_Y + (ROW_HEIGHT + GAP) * PAGE_SIZE + 15;

    private static final Color TEAL = UiUtil.TEAL;
    private static final Color GREY = Color.decode("#8E9094");
    private static final Color PILL_BG = new Color(213, 214, 216);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yy", Locale.ENGLISH);

    private final ScoreService scoreService;

    // ปุ่มเลือก mode / จำนวน
    private final JToggleButton btnTimeMode = new Pill("time");
    private final JToggleButton btnWordsMode = new Pill("A words");
    private final JToggleButton[] amountButtons = {
        new Pill("15"), new Pill("30"), new Pill("60"), new Pill("120")
    };

    private final List<ScoreRow> rowViews = new ArrayList<>();
    private ScoreRow pinnedView;
    private final JLabel lblEmpty = new JLabel("no scores yet", SwingConstants.CENTER);
    private final JLabel lblPageNum = new JLabel("1 / 1", SwingConstants.CENTER);

    private String mode = Score.getModeTime();
    private int amount = 30;
    private int page = 0;
    private String currentUser;
    private List<Score> ranking = new ArrayList<>();
    private boolean loading = false; // กันไม่ให้ reload ซ้ำตอนเราตั้งปุ่มเองด้วยโค้ด

    private Runnable onBack;

    public Leaderboard(ScoreService scoreService) {
        this.scoreService = scoreService;

        setLayout(null);
        setBackground(UiUtil.BG_COLOR);

        // HeaderBar สลับหน้าเองผ่าน MainFrame ไม่ต้องผูก callback
        add(new HeaderBar());

        // ---------- ปุ่มเลือก mode / จำนวน (ช่วง x 342-828 y 125 สูง 38 เท่าตำแหน่ง ModeSelector เดิม) ----------
        Icon timeOff = icon("TImeModeIcon_OFF.png");
        Icon timeOn = icon("TImeModeIcon_ON.png");
        if (timeOff != null) btnTimeMode.setIcon(timeOff);
        if (timeOn != null) btnTimeMode.setSelectedIcon(timeOn);
        btnTimeMode.setIconTextGap(8);
        btnTimeMode.setBounds(342, 125, 98, 38);
        btnWordsMode.setBounds(450, 125, 98, 38);

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(btnTimeMode);
        modeGroup.add(btnWordsMode);
        add(btnTimeMode);
        add(btnWordsMode);

        int[] amountX = { 581, 643, 705, 767 };
        ButtonGroup amountGroup = new ButtonGroup();
        for (int i = 0; i < amountButtons.length; i++) {
            amountGroup.add(amountButtons[i]);
            amountButtons[i].setBounds(amountX[i], 125, 52, 38);
            amountButtons[i].addActionListener(e -> onFilterChanged());
            add(amountButtons[i]);
        }

        btnTimeMode.addActionListener(e -> {
            setAmountLabels(true);
            onFilterChanged();
        });
        btnWordsMode.addActionListener(e -> {
            setAmountLabels(false);
            onFilterChanged();
        });
        btnTimeMode.setSelected(true);
        amountButtons[1].setSelected(true);

        // ---------- หัวตาราง ----------
        int headerY = 180;
        Font headerFont = new Font("Roboto Mono", Font.PLAIN, 13);
        addHeaderLabel("rank", 185, headerY, 32, headerFont);
        addHeaderLabel("player", 290, headerY, 50, headerFont);
        addHeaderLabel("wpm", 620, headerY, 24, headerFont);
        addHeaderLabel("acc", 770, headerY, 24, headerFont);
        addHeaderLabel("date", 920, headerY, 32, headerFont);

        // ---------- ข้อความเมื่อยังไม่มีข้อมูล ----------
        lblEmpty.setFont(new Font("Roboto Mono", Font.PLAIN, 16));
        lblEmpty.setForeground(GREY);
        lblEmpty.setBounds(START_X, START_Y + 20, ROW_WIDTH, 30);
        add(lblEmpty);

        // ---------- Pagination ----------
        ActionRoundButton btnPrev = new ActionRoundButton("<");
        btnPrev.setBounds(518, 550, 32, 32);
        onClick(btnPrev, () -> changePage(-1));
        add(btnPrev);

        lblPageNum.setFont(new Font("Roboto Mono", Font.BOLD, 16));
        lblPageNum.setForeground(Color.BLACK);
        lblPageNum.setBounds(565, 556, 70, 21);
        add(lblPageNum);

        ActionRoundButton btnNext = new ActionRoundButton(">");
        btnNext.setBounds(650, 550, 32, 32);
        onClick(btnNext, () -> changePage(1));
        add(btnNext);

        // ---------- คีย์ลัด ----------
        JLabel lblShortcut = new JLabel("<html><font color='#006664'>[ ESC ]</font> <font color='#000000'>- back to home</font></html>");
        lblShortcut.setFont(new Font("Menlo", Font.PLAIN, 14));
        lblShortcut.setBounds(508, 610, 184, 18);
        lblShortcut.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblShortcut);

        bindKey(KeyEvent.VK_ESCAPE, "back", () -> {
            if (onBack != null) onBack.run();
        });
        bindKey(KeyEvent.VK_LEFT, "prev", () -> changePage(-1));
        bindKey(KeyEvent.VK_RIGHT, "next", () -> changePage(1));
    }

    // ==================================================================
    // API สำหรับ MainFrame
    // ==================================================================

    /** กด ESC -> กลับหน้า Home (MainFrame ส่ง showHome มาให้) */
    public void setOnBack(Runnable r) {
        this.onBack = r;
    }

    /** โหลดใหม่ตามตัวกรองที่เลือกค้างอยู่ (ดึงข้อมูลและชื่อผู้ใช้ล่าสุดทุกครั้ง) */
    public void refresh() {
        currentUser = readCurrentUser();
        page = 0;
        reload();
    }

    /** เปิดหน้านี้ โดยเลือก mode/amount ที่ผู้ใช้เพิ่งเล่น */
    public void open(String mode, int amount) {
        loading = true;
        this.mode = mode;
        this.amount = amount;
        boolean timeMode = Score.getModeTime().equals(mode);
        (timeMode ? btnTimeMode : btnWordsMode).setSelected(true);
        setAmountLabels(timeMode);
        for (JToggleButton b : amountButtons) {
            if (Integer.parseInt(b.getText()) == amount) b.setSelected(true);
        }
        loading = false;
        refresh();
    }

    // ==================================================================
    // Logic
    // ==================================================================

    private String readCurrentUser() {
        User u = InMemorySession.getInstance().getCurrentUser();
        return (u == null || u.getUsername() == null || u.getUsername().isEmpty()) ? null : u.getUsername();
    }

    private void onFilterChanged() {
        if (loading) return;
        mode = btnTimeMode.isSelected() ? Score.getModeTime() : Score.getModeWords();
        for (JToggleButton b : amountButtons) {
            if (b.isSelected()) amount = Integer.parseInt(b.getText());
        }
        page = 0;
        reload();
    }

    private void setAmountLabels(boolean timeMode) {
        String[] labels = timeMode ? new String[] { "15", "30", "60", "120" }
                                   : new String[] { "10", "25", "50", "100" };
        for (int i = 0; i < amountButtons.length; i++) amountButtons[i].setText(labels[i]);
    }

    private void changePage(int delta) {
        int next = page + delta;
        if (next < 0 || next >= totalPages()) return;
        page = next;
        render();
    }

    private int totalPages() {
        return Math.max(1, (ranking.size() + PAGE_SIZE - 1) / PAGE_SIZE);
    }

    private void reload() {
        ranking = scoreService.leaderboard(mode, amount);
        page = Math.max(0, Math.min(page, totalPages() - 1));
        render();
    }

    // ==================================================================
    // Rendering
    // ==================================================================

    private void render() {
        // ลบแถวเก่า (ScoreRow แก้ค่าไม่ได้ จึงสร้างใหม่ทุกครั้ง)
        for (ScoreRow r : rowViews) remove(r);
        rowViews.clear();
        if (pinnedView != null) {
            remove(pinnedView);
            pinnedView = null;
        }

        int from = page * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, ranking.size());
        for (int i = from; i < to; i++) {
            Score s = ranking.get(i);
            boolean isMe = s.getUsername().equals(currentUser);
            ScoreRow row = toRow(i + 1, s, isMe ? s.getUsername() + " (you)" : s.getUsername(), false);
            row.setBounds(START_X, START_Y + (ROW_HEIGHT + GAP) * (i - from), ROW_WIDTH, ROW_HEIGHT);
            add(row);
            rowViews.add(row);
        }

        // แถวเขียว = ที่ดีที่สุดของผู้ใช้ที่ login อยู่ (ซ่อนถ้าไม่ได้ล็อกอิน/ยังไม่มีสถิติ)
        if (currentUser != null) {
            for (int i = 0; i < ranking.size(); i++) {
                if (ranking.get(i).getUsername().equals(currentUser)) {
                    pinnedView = toRow(i + 1, ranking.get(i), currentUser + " (your best)", true);
                    pinnedView.setBounds(START_X, PINNED_Y, ROW_WIDTH, ROW_HEIGHT);
                    add(pinnedView);
                    break;
                }
            }
        }

        lblEmpty.setVisible(ranking.isEmpty());
        lblPageNum.setText((page + 1) + " / " + totalPages());

        revalidate();
        repaint();
    }

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
    // Helpers
    // ==================================================================

    /** โหลดไอคอน ถ้าไม่เจอ (UiUtil คืน null) หรือโหลดพลาดให้คืน null แทนที่จะทำให้หน้าล่ม */
    private static Icon icon(String name) {
        try {
            return UiUtil.loadIcon(name);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private void addHeaderLabel(String text, int x, int y, int w, Font font) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(GREY);
        l.setBounds(x, y, w, 17);
        add(l);
    }

    /** ผูกการคลิกด้วย MouseListener ใช้ได้กับทุก Component (ไม่ขึ้นกับว่า ActionRoundButton สืบทอดจากอะไร) */
    private void onClick(JComponent c, Runnable action) {
        c.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });
    }

    private void bindKey(int keyCode, String name, Runnable action) {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyCode, 0), name);
        getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // ทำงานเฉพาะตอนหน้านี้แสดงอยู่ (กันชนกับหน้าอื่นที่ใช้ปุ่มเดียวกัน)
                if (isShowing()) action.run();
            }
        });
    }

    // ==================================================================
    // ปุ่มแคปซูลตามดีไซน์: ปกติพื้นเทา ตัวอักษรดำ / ถูกเลือกพื้นเขียว ตัวอักษรขาว
    // ==================================================================

    private static class Pill extends JToggleButton {
        Pill(String text) {
            super(text);
            setFont(new Font("Roboto Mono", Font.BOLD, 13));
            setMargin(new java.awt.Insets(0, 0, 0, 0));
            setFocusable(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public Color getForeground() {
            return isSelected() ? Color.WHITE : Color.BLACK;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isSelected() ? TEAL : PILL_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}