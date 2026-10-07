package ui.pages;

import javax.swing.*;

import ui.UiUtil;
import ui.components.HeaderBar;

public class Result extends JPanel{
    public Result() {
        setLayout(null);
        setBackground(UiUtil.BG_COLOR);

        // 1. HeaderBar
        HeaderBar header = new HeaderBar();
        add(header);

        // TODO: นิว
    }
}
