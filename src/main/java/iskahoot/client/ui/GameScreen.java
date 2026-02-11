package iskahoot.client.ui;

import iskahoot.model.Answer;
import iskahoot.model.Connection;
import iskahoot.model.Question;
import iskahoot.model.Leaderboard;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameScreen {

    private final Connection connection;
    private final JFrame frame;
    private final String username;

    private JPanel rootPanel;
    private JPanel questionPanel;
    private JPanel leaderboardPanel;

    private Timer questionTimer;

    public GameScreen(String username, Connection connection) {
        this.username = username;
        this.connection = connection;

        frame = new JFrame("IsKahoot - " + username);
        frame.setSize(900, 450);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initLayout();
        showWaitingScreen("Aguardando o início do jogo...");

        frame.setVisible(true);
    }

    // ===============================
    // Layout base (feito UMA vez)
    // ===============================
    private void initLayout() {
        rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(Color.BLACK);

        questionPanel = new JPanel();
        questionPanel.setBackground(Color.BLACK);

        leaderboardPanel = new JPanel();
        leaderboardPanel.setPreferredSize(new Dimension(250, 0));
        leaderboardPanel.setBackground(new Color(30, 30, 30));

        rootPanel.add(questionPanel, BorderLayout.CENTER);
        rootPanel.add(leaderboardPanel, BorderLayout.EAST);

        frame.setContentPane(rootPanel);
    }

    // ===============================
    // Tela de espera (SEM esconder LB)
    // ===============================
    public void showWaitingScreen(String message) {
        stopTimer();

        questionPanel.removeAll();
        questionPanel.setLayout(new BorderLayout());

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 26));
        label.setForeground(Color.WHITE);

        questionPanel.add(label, BorderLayout.CENTER);

        questionPanel.revalidate();
        questionPanel.repaint();
    }

    // ===============================
    // Mostra pergunta
    // ===============================
    public void updateQuestion(Question question) {
        stopTimer();

        questionPanel.removeAll();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        questionPanel.setBackground(Color.BLACK);

        JLabel questionLabel = new JLabel(question.getText());
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 22));
        questionLabel.setForeground(Color.WHITE);

        JLabel timerLabel = new JLabel("30");
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        timerLabel.setForeground(Color.RED);

        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        optionsPanel.setBackground(Color.BLACK);

        List<JButton> optionButtons = new ArrayList<>();
        final int[] selectedIndex = {-1};

        for (int i = 0; i < question.getOptions().size(); i++) {
            final int index = i;
            JButton btn = new JButton(question.getOptions().get(i));
            btn.setFont(new Font("Dialog", Font.BOLD, 16));
            btn.setBackground(Color.MAGENTA);
            btn.setOpaque(true);
            btn.setBorderPainted(false);

            btn.addActionListener(e -> {
                selectedIndex[0] = index;
                for (int j = 0; j < optionButtons.size(); j++) {
                    optionButtons.get(j)
                            .setBackground(j == index ? Color.ORANGE : Color.MAGENTA);
                }
            });

            optionButtons.add(btn);
            optionsPanel.add(btn);
        }

        JButton submitBtn = new JButton("Enviar Resposta");
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitBtn.setFont(new Font("Dialog", Font.BOLD, 18));
        submitBtn.setBackground(Color.GREEN);
        submitBtn.setOpaque(true);
        submitBtn.setBorderPainted(false);

        submitBtn.addActionListener(e -> {
            if (selectedIndex[0] != -1) {
                stopTimer();
                sendAnswer(selectedIndex[0], question);
                showWaitingScreen("Resposta enviada! Aguardando outros jogadores...");
            }
        });

        questionPanel.add(questionLabel);
        questionPanel.add(Box.createVerticalStrut(10));
        questionPanel.add(timerLabel);
        questionPanel.add(Box.createVerticalStrut(15));
        questionPanel.add(optionsPanel);
        questionPanel.add(Box.createVerticalStrut(20));
        questionPanel.add(submitBtn);

        questionPanel.revalidate();
        questionPanel.repaint();

        startTimer(timerLabel, selectedIndex, question);
    }

    // ===============================
    // Leaderboard (sempre visível)
    // ===============================
    public void updateLeaderboard(Leaderboard leaderboard) {
        leaderboardPanel.removeAll();
        leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.Y_AXIS));
        leaderboardPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel title = new JLabel("Leaderboard");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.ORANGE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        leaderboardPanel.add(title);
        leaderboardPanel.add(Box.createVerticalStrut(15));

        leaderboard.getScores().entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(entry -> {
                    JLabel lbl = new JLabel(entry.getKey() + ": " + entry.getValue());
                    lbl.setFont(new Font("Arial", Font.BOLD, 16));
                    lbl.setForeground(Color.WHITE);
                    lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                    leaderboardPanel.add(lbl);
                    leaderboardPanel.add(Box.createVerticalStrut(8));
                });

        leaderboardPanel.revalidate();
        leaderboardPanel.repaint();
    }

    // ===============================
    // Timer
    // ===============================
    private void startTimer(JLabel timerLabel, int[] selectedIndex, Question question) {
        final int[] timeLeft = {30};

        questionTimer = new Timer(1000, e -> {
            timeLeft[0]--;
            timerLabel.setText(String.valueOf(timeLeft[0]));

            if (timeLeft[0] <= 0) {
                stopTimer();
                if (selectedIndex[0] == -1)
                    sendTimeoutAnswer();
                else
                    sendAnswer(selectedIndex[0], question);

                showWaitingScreen("Tempo esgotado! Aguardando próxima pergunta...");
            }
        });

        questionTimer.start();
    }

    private void stopTimer() {
        if (questionTimer != null && questionTimer.isRunning())
            questionTimer.stop();
    }

    // ===============================
    // Envio de respostas
    // ===============================
    private void sendAnswer(int index, Question q) {
        try {
            connection.send(new Answer(index, 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendTimeoutAnswer() {
        try {
            connection.send(new Answer(-1, 30_000));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===============================
    // Fim de jogo
    // ===============================
    public void showGameOver(String message) {
        stopTimer();

        questionPanel.removeAll();
        questionPanel.setLayout(new BorderLayout());

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 30));
        label.setForeground(Color.WHITE);

        JButton closeBtn = new JButton("Sair");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 20));
        closeBtn.addActionListener(e -> System.exit(0));

        questionPanel.add(label, BorderLayout.CENTER);
        questionPanel.add(closeBtn, BorderLayout.SOUTH);

        questionPanel.revalidate();
        questionPanel.repaint();
    }
}
