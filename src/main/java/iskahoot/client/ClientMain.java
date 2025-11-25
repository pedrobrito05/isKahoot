package iskahoot.client;

import javax.swing.SwingUtilities;
import iskahoot.client.ui.LoginScreen;

public class ClientMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().show();
        });
    }
}