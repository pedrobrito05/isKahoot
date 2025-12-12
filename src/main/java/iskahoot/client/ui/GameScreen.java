package iskahoot.client.ui;

import iskahoot.model.Answer;
import iskahoot.model.Connection;
import iskahoot.model.Question;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameScreen {

    private final Connection connection;
    private final JFrame frame;
    private final String username;
    private Timer questionTimer;

    public GameScreen(String username, Connection connection) {
        this.username = username;
        this.connection = connection;

        frame = new JFrame("IsKahoot - " + username);
        frame.setSize(700, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        showWaitingScreen("Aguardando o início do jogo...");
        frame.setVisible(true);
    }

    // Tela de espera
    public void showWaitingScreen(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 26));
        label.setForeground(Color.WHITE);

        panel.add(label, BorderLayout.CENTER);

        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }

    // Mostra nova pergunta
    public void updateQuestion(Question question) {

        if (questionTimer != null && questionTimer.isRunning()) {
            questionTimer.stop();
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.BLACK);

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

        // === Opções ===
        for (int i = 0; i < question.getOptions().size(); i++) {
            final int index = i;
            String option = question.getOptions().get(i);

            JButton btn = new JButton(option);
            btn.setFont(new Font("Dialog", Font.BOLD, 16));
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(true);
            btn.setBackground(Color.MAGENTA);

            btn.addActionListener(e -> {
                selectedIndex[0] = index;
                for (int j = 0; j < optionButtons.size(); j++) {
                    optionButtons.get(j).setBackground(j == index ? Color.ORANGE : Color.MAGENTA);
                }
            });

            optionButtons.add(btn);
            optionsPanel.add(btn);
        }

        // Botão de enviar
        JButton submitBtn = new JButton("Enviar Resposta");
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitBtn.setFont(new Font("Dialog", Font.BOLD, 18));
        submitBtn.setOpaque(true);
        submitBtn.setBorderPainted(false);
        submitBtn.setContentAreaFilled(true);
        submitBtn.setBackground(Color.GREEN);

        submitBtn.addActionListener(e -> {
            if (selectedIndex[0] != -1) {
                if (questionTimer != null) questionTimer.stop();
                sendAnswer(selectedIndex[0], question);
                showWaitingScreen("Resposta enviada! Aguardando outros jogadores...");
            }
        });

        panel.add(questionLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(timerLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(optionsPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(submitBtn);

        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();

        // === TIMER ===
        final int[] timeLeft = {30};
        questionTimer = new Timer(1000, evt -> {
            timeLeft[0]--;
            timerLabel.setText(String.valueOf(timeLeft[0]));

            if (timeLeft[0] <= 0) {
                questionTimer.stop();

                if (selectedIndex[0] == -1) {
                    sendTimeoutAnswer();
                } else {
                    sendAnswer(selectedIndex[0], question);
                }

                showWaitingScreen("Tempo esgotado! Aguardando próxima pergunta...");
            }
        });

        questionTimer.start();
    }

    private void sendAnswer(int index, Question q) {
        System.out.println(username + " escolheu: " + q.getOptions().get(index));
        try {
            connection.send(new Answer(index, 0));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void sendTimeoutAnswer() {
        try {
            connection.send(new Answer(-1, 30_000));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Tela final
    public void showGameOver(String message) {
        if (questionTimer != null && questionTimer.isRunning()) {
            questionTimer.stop();
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLUE);

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 30));
        label.setForeground(Color.WHITE);

        JButton closeBtn = new JButton("Sair");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 20));
        closeBtn.addActionListener(e -> System.exit(0));

        panel.add(label, BorderLayout.CENTER);
        panel.add(closeBtn, BorderLayout.SOUTH);

        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }

}
