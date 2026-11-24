package edu.hitsz.application;

import edu.hitsz.rank.Rank;
import edu.hitsz.rank.RankDaoImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class RankTable extends JPanel {
    public static final String MODE_EASY = "easy";
    public static final String MODE_MEDIUM = "medium";
    public static final String MODE_HARD = "hard";
    public static final String MODE_INFI = "infi";

    private static final Map<String, String> MODE_TEXT = createModeTextMap();

    private final JLabel difficultyLabel;
    private final DefaultTableModel model;
    private final JTable table;
    private final RankDaoImpl rankDao;
    private final JComboBox<String> modeSelector;
    private Rank[] cachedRanks = new Rank[0];
    private final Runnable onBack;
    private String currentMode = MODE_EASY;

    public RankTable(Runnable onBack) {
        this.rankDao = new RankDaoImpl();
        this.onBack = onBack;
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        difficultyLabel = new JLabel("难度：-");
        JLabel title = new JLabel("排行榜", SwingConstants.CENTER);
        modeSelector = new JComboBox<>(MODE_TEXT.values().toArray(new String[0]));
        modeSelector.addActionListener(e -> {
            currentMode = textToMode((String) modeSelector.getSelectedItem());
            loadAndFill();
        });
        top.add(difficultyLabel, BorderLayout.WEST);
        top.add(title, BorderLayout.CENTER);
        top.add(modeSelector, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"名次", "姓名", "分数", "时间"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton deleteBtn = new JButton("删除");
        JButton backBtn = new JButton("返回");

        deleteBtn.addActionListener(e -> deleteSelectedRow());
        backBtn.addActionListener(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        buttonPanel.add(deleteBtn);
        buttonPanel.add(backBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        loadAndFill();
    }

    public void setDifficultyText(String difficultyText) {
        difficultyLabel.setText("难度：" + (difficultyText == null ? "-" : difficultyText));
    }

    public void setSelectedMode(String mode) {
        currentMode = normalizeMode(mode);
        modeSelector.setSelectedItem(modeToText(currentMode));
        loadAndFill();
    }

    public void loadAndFill() {
        model.setRowCount(0);
        cachedRanks = rankDao.getAllRanks(currentMode);
        for (Rank r : cachedRanks) {
            String time = new Date(r.getTimestamp()).toString();
            model.addRow(new Object[]{r.getRank(), r.getName(), r.getScore(), time});
        }
    }

    private void deleteSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= cachedRanks.length) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的记录。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, "是否确定删除？", "确认", JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        Rank toDelete = cachedRanks[row];
        rankDao.deleteRank(currentMode, toDelete);
        loadAndFill();
    }

    private static Map<String, String> createModeTextMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(MODE_EASY, "简单模式");
        map.put(MODE_MEDIUM, "普通模式");
        map.put(MODE_HARD, "困难模式");
        map.put(MODE_INFI, "无敌模式");
        return map;
    }

    private String modeToText(String mode) {
        return MODE_TEXT.getOrDefault(mode, MODE_TEXT.get(MODE_EASY));
    }

    private String textToMode(String text) {
        for (Map.Entry<String, String> entry : MODE_TEXT.entrySet()) {
            if (entry.getValue().equals(text)) {
                return entry.getKey();
            }
        }
        return MODE_EASY;
    }

    private String normalizeMode(String mode) {
        if (mode == null) {
            return MODE_EASY;
        }
        String value = mode.trim().toLowerCase();
        if (MODE_TEXT.containsKey(value)) {
            return value;
        }
        return MODE_EASY;
    }
}
