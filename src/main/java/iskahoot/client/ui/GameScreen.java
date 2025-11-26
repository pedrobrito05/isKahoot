package iskahoot.client.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import iskahoot.model.Question;
import iskahoot.model.QuestionLoader;
import iskahoot.model.Quiz;

public class GameScreen {

    private final JFrame frame;
    private final String username;
    private final Quiz quiz;
    private int currentQuestionIndex = 0;
    private int points = 0;

    public GameScreen(String username) {
        this.username = username;
        this.frame = new JFrame("IsKahoot - GameScreen");

        // Carrega perguntas do JSON local
        quiz = QuestionLoader.loadFromFile("/questions.json");

        if (quiz == null || quiz.questions == null || quiz.questions.isEmpty()) {
            throw new RuntimeException("Erro: Quiz não carregado.");
        }
    }

    // Método para iniciar a UI
    public void show() {
        showQuestion(quiz.questions.get(currentQuestionIndex));
    }

    public void showQuestion(Question question) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.black);

        JLabel questionLabel = new JLabel(question.getText());
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 20));
        questionLabel.setForeground(Color.white);

        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        optionsPanel.setBackground(Color.black);

        List<JButton> optionButtons = new java.util.ArrayList<>();
        final int[] selectedIndex = {-1};

        for (int i = 0; i < question.getOptions().size(); i++) {
            final int index = i;
            String option = question.getOptions().get(i);
            JButton btn = new JButton(option);
            btn.setFocusPainted(false);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(true);
            btn.setBackground(Color.MAGENTA);
            btn.setFont(new Font("Dialog", Font.BOLD, 16));

            btn.addActionListener(e -> {
                selectedIndex[0] = index;
                for (int j = 0; j < optionButtons.size(); j++) {
                    optionButtons.get(j).setBackground(j == index ? Color.ORANGE : Color.MAGENTA);
                }
            });

            optionButtons.add(btn);
            optionsPanel.add(btn);
        }

        JButton submitBtn = new JButton("Enviar Resposta");
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitBtn.setFocusPainted(false);
        submitBtn.setOpaque(true);
        submitBtn.setBorderPainted(false);
        submitBtn.setContentAreaFilled(true);
        submitBtn.setBackground(Color.green);
        submitBtn.setFont(new Font("Dialog", Font.BOLD, 16));

        submitBtn.addActionListener(e -> {
            if (selectedIndex[0] != -1) {
                int selIndex = selectedIndex[0];
                System.out.println(username + " escolheu: " + question.getOptions().get(selIndex));

                if (selIndex == question.getCorrectIndex()) {
                    System.out.println("Resposta correta! +" + question.getPoints() + " pontos.");
                    points += question.getPoints();
                } else {
                    System.out.println("Resposta incorreta. Resposta certa: " +
                            question.getOptions().get(question.getCorrectIndex()));
                }

                currentQuestionIndex++;
                if (currentQuestionIndex < quiz.questions.size()) {
                    showQuestion(quiz.questions.get(currentQuestionIndex));
                } else {
                    JOptionPane.showMessageDialog(frame, "Fim do quiz!\nFez: " + points + " pontos",
                            "Fim", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                }
            } else {
                System.out.println(username + " não selecionou nenhuma opção.");
            }
        });

        panel.add(questionLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(optionsPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(submitBtn);

        frame.setContentPane(panel);
        frame.setSize(700, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
