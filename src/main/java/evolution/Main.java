package evolution;

import javax.swing.SwingUtilities;

/**
 * MAIN — Entry point for the CodeBase Evolution Simulator.
 *
 * Launches the Swing GUI on the Event Dispatch Thread (EDT),
 * which is the correct thread-safe way to start Swing applications.
 *
 * To compile and run:
 *   javac -d out src/evolution/*.java
 *   java  -cp out evolution.Main
 */
public class Main {
    public static void main(String[] args) {
        // All Swing GUI work must happen on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            SimulatorUI ui = new SimulatorUI();
            ui.setVisible(true);
        });
    }
}
