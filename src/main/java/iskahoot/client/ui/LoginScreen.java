package iskahoot.client.ui;

import javax.swing.*;
import java.awt.*;

public class LoginScreen {
    private final JFrame frame;

    public LoginScreen() {
        this.frame = new JFrame("IsKahoot - Login");
    }

    public void show() {
        // Painel com GridBagLayout (similar ao GridPane)
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos de input
        JTextField roomField = new JTextField("Sala1");
        JTextField teamField = new JTextField("EquipaA");
        JTextField userField = new JTextField();

        JButton connectBtn = new JButton("Ligar");

        connectBtn.addActionListener(e -> {
            String room = roomField.getText();
            String team = teamField.getText();
            String username = userField.getText();

            System.out.println("Sala: " + room);
            System.out.println("Equipa: " + team);
            System.out.println("Username: " + username);

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username não pode estar vazio!",
                        "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Abre GameScreen
            GameScreen gameScreen = new GameScreen(username);
            gameScreen.show();
            frame.dispose(); // Fecha login
        });

        // Adiciona componentes ao layout

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Sala:"), gbc);
        gbc.gridx = 1; panel.add(roomField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Equipa:"), gbc);
        gbc.gridx = 1; panel.add(teamField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; panel.add(userField, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        connectBtn.setPreferredSize(new Dimension(100, 30));
        panel.add(connectBtn, gbc);

        // Configuração da janela
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}