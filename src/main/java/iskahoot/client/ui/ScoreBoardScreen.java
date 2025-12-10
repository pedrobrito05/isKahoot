package iskahoot.client.ui;

import iskahoot.objects.Team;
import iskahoot.server.Game;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

//Ecrã do placar (resultados).
public class ScoreBoardScreen extends JFrame{
    private JTable table;
    private DefaultTableModel model;
    public ScoreBoardScreen(Game game) {
        setTitle("Scoreboard");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 60)); // azul escuro
        add(mainPanel);

        // Título
        JLabel title = new JLabel("Scoreboard", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        mainPanel.add(title, BorderLayout.NORTH);

        // Tabela
        String[] columns = {"Posição", "Jogador", "Pontuação"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabela não editável
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 16));
        table.setForeground(Color.BLACK);

        // Header estilizado
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(100, 100, 200));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 16));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        mainPanel.add(scroll, BorderLayout.CENTER);

        // Botão inferior
        JButton nextBtn = new JButton("➡ Próxima Pergunta");
        nextBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        nextBtn.setBackground(new Color(70, 150, 240));
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setFocusPainted(false);
        nextBtn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(null);
        buttonPanel.add(nextBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        addPlayerScore(game);


    }

    // Método para adicionar linhas ao scoreboard
    public void addPlayerScore(Game game) {

        for(Team team: game.getTeams()){
            model.addRow(new Object[]{1, team.getTeamName(), team.getScore()});
        }
    }

    public static void main(String[] args) {
        Game game = new Game ("a", 2,2, null);
        ScoreBoardScreen sc=new ScoreBoardScreen(game);
        sc.setVisible(true);
    }
}
