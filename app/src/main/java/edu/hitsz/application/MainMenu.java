package edu.hitsz.application;

import edu.hitsz.game.EasyGame;
import edu.hitsz.game.HardGame;
import edu.hitsz.game.InfiGame;
import edu.hitsz.game.MediumGame;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridLayout;

public class MainMenu extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private Game currentGame;
    private final RankTable rankPanel;
    private String currentDifficultyText = "-";
    private String currentRankMode = RankTable.MODE_EASY;

    private static final String CARD_START = "START";
    private static final String CARD_GAME = "GAME";
    private static final String CARD_RANK = "RANK";

    public MainMenu() {
        super("Aircraft War");
        setSize(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel startMenu = buildStartMenu();
        cardPanel.add(startMenu, CARD_START);

        rankPanel = new RankTable(this::showStartMenu);
        cardPanel.add(rankPanel, CARD_RANK);

        add(cardPanel);
        setLocationRelativeTo(null);
        cardLayout.show(cardPanel, CARD_START);
    }

    private JPanel buildStartMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(Box.createVerticalStrut(80));

        JButton easyBtn = new JButton("简单模式");
        JButton middleBtn = new JButton("普通模式");
        JButton hardBtn = new JButton("困难模式");
        JButton infiBtn = new JButton("无敌模式");
        JButton rankBtn = new JButton("排行榜");

        easyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        middleBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        hardBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        infiBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        rankBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(easyBtn);
        panel.add(Box.createVerticalStrut(25));
        panel.add(middleBtn);
        panel.add(Box.createVerticalStrut(25));
        panel.add(hardBtn);
        panel.add(Box.createVerticalStrut(25));
        panel.add(infiBtn);
        panel.add(Box.createVerticalStrut(40));
        panel.add(rankBtn);

        easyBtn.addActionListener(e -> startGame(new EasyGame("Player"), "EASY", "简单模式", RankTable.MODE_EASY));
        middleBtn.addActionListener(e -> startGame(new MediumGame("Player"), "MEDIUM", "普通模式", RankTable.MODE_MEDIUM));
        hardBtn.addActionListener(e -> startGame(new HardGame("Player"), "HARD", "困难模式", RankTable.MODE_HARD));
        infiBtn.addActionListener(e -> startGame(new InfiGame("Player"), "INFI", "无敌模式", RankTable.MODE_INFI));
        rankBtn.addActionListener(e -> showRankPanel());

        return panel;
    }

    private void startGame(Game game, String difficultyName, String difficultyText, String rankMode) {
        currentDifficultyText = difficultyText;
        currentRankMode = rankMode;

        if (currentGame != null) {
            try {
                currentGame.stopGame();
            } catch (Exception ignored) {
            }
            cardPanel.remove(currentGame);
        }

        ImageManager.setBackgroundByDifficulty(difficultyName);

        currentGame = game;
        currentGame.setOnGameOver(this::handleGameOver);

        cardPanel.add(currentGame, CARD_GAME);
        revalidate();
        repaint();
        cardLayout.show(cardPanel, CARD_GAME);
        currentGame.action();
    }

    private void handleGameOver() {
        promptAndSaveRank();
        showRankPanel();
    }

    private void showRankPanel() {
        rankPanel.setDifficultyText(currentDifficultyText);
        rankPanel.setSelectedMode(currentRankMode);
        cardLayout.show(cardPanel, CARD_RANK);
    }

    private void promptAndSaveRank() {
        if (currentGame == null) {
            return;
        }

        JTextField nameField = new JTextField("", 16);
        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        inputPanel.add(new JLabel("本局得分：" + currentGame.getCurrentScore()));
        inputPanel.add(nameField);

        int option = JOptionPane.showConfirmDialog(
                this,
                inputPanel,
                "游戏结束，请输入姓名（取消则不记录）",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (option == JOptionPane.OK_OPTION) {
            currentGame.saveCurrentRank(nameField.getText(), currentRankMode);
        }
    }

    private void showStartMenu() {
        cardLayout.show(cardPanel, CARD_START);
    }
}
