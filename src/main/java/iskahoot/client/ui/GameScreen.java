package iskahoot.client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GameScreen {
    private final JFrame frame;
    private final String username;

    public GameScreen(String username) {
        this.username = username;
        this.frame = new JFrame("IsKahoot - GameScreen");
    }

    public void show() {

        // Painel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Pergunta
        JLabel questionLabel = new JLabel("Qual é a capital de Portugal?");
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Agrupamento de opções
        ButtonGroup optionsGroup = new ButtonGroup();
        JRadioButton option1 = new JRadioButton("Lisboa");
        JRadioButton option2 = new JRadioButton("Porto");
        JRadioButton option3 = new JRadioButton("Braga");
        JRadioButton option4 = new JRadioButton("Faro");

        optionsGroup.add(option1);
        optionsGroup.add(option2);
        optionsGroup.add(option3);
        optionsGroup.add(option4);

        // Botão enviar
        JButton submitBtn = new JButton("Enviar Resposta");
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ButtonModel selected = optionsGroup.getSelection();
                if (selected != null) {
                    String text = null;
                    if (option1.isSelected()) text = option1.getText();
                    else if (option2.isSelected()) text = option2.getText();
                    else if (option3.isSelected()) text = option3.getText();
                    else if (option4.isSelected()) text = option4.getText();

                    System.out.println(username + " escolheu: " + text);
                } else {
                    System.out.println(username + " não selecionou nenhuma opção.");
                }
            }
        });

        // Adicionar ao painel
        panel.add(questionLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(option1);
        panel.add(option2);
        panel.add(option3);
        panel.add(option4);
        panel.add(Box.createVerticalStrut(15));
        panel.add(submitBtn);

        // Configurar frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}