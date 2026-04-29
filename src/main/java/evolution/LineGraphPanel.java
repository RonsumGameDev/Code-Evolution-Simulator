package evolution;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * LINEGRAPHPANEL — custom Swing panel that plots fitness over generations.
 *
 * Draws two lines:
 *   • Blue  → Best fitness per generation
 *   • Green → Average fitness per generation
 *
 * Uses paintComponent(Graphics g) for all drawing (no external libraries).
 *
 * OOP: Extends JPanel → INHERITANCE from Swing.
 */
public class LineGraphPanel extends JPanel {

    // ── Margins ────────────────────────────────────────────────────────────
    private static final int MARGIN_LEFT   = 60;
    private static final int MARGIN_RIGHT  = 20;
    private static final int MARGIN_TOP    = 30;
    private static final int MARGIN_BOTTOM = 45;

    // ── Colors ─────────────────────────────────────────────────────────────
    private static final Color COLOR_BEST  = new Color(70, 130, 255);   // Blue
    private static final Color COLOR_AVG   = new Color(60, 200, 100);   // Green
    private static final Color COLOR_GRID  = new Color(60, 60, 70);
    private static final Color COLOR_BG    = new Color(20, 20, 30);
    private static final Color COLOR_AXIS  = new Color(180, 180, 200);
    private static final Color COLOR_TEXT  = new Color(210, 210, 230);

    // ── Data ───────────────────────────────────────────────────────────────
    private List<Integer> bestHistory;
    private List<Double>  avgHistory;

    // ── Constructor ────────────────────────────────────────────────────────
    public LineGraphPanel() {
        setBackground(COLOR_BG);
        setBorder(BorderFactory.createLineBorder(new Color(60, 60, 80), 1));
        setPreferredSize(new Dimension(500, 220));
    }

    /** Update graph data and repaint. */
    public void update(List<Integer> bestHistory, List<Double> avgHistory) {
        this.bestHistory = bestHistory;
        this.avgHistory  = avgHistory;
        repaint();
    }

    // ── Paint ──────────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Plot area dimensions
        int plotW = w - MARGIN_LEFT - MARGIN_RIGHT;
        int plotH = h - MARGIN_TOP  - MARGIN_BOTTOM;

        // ── Title ──────────────────────────────────────────────────────────
        g2.setColor(COLOR_TEXT);
        g2.setFont(new Font("Monospaced", Font.BOLD, 13));
        g2.drawString("Fitness Over Generations", MARGIN_LEFT, 20);

        // ── Legend ─────────────────────────────────────────────────────────
        g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g2.setColor(COLOR_BEST);
        g2.fillRect(w - 160, 10, 12, 12);
        g2.setColor(COLOR_TEXT);
        g2.drawString("Best", w - 144, 21);

        g2.setColor(COLOR_AVG);
        g2.fillRect(w - 90, 10, 12, 12);
        g2.setColor(COLOR_TEXT);
        g2.drawString("Avg", w - 74, 21);

        // ── Axes ───────────────────────────────────────────────────────────
        g2.setColor(COLOR_AXIS);
        g2.setStroke(new BasicStroke(1.5f));
        // Y-axis
        g2.drawLine(MARGIN_LEFT, MARGIN_TOP, MARGIN_LEFT, MARGIN_TOP + plotH);
        // X-axis
        g2.drawLine(MARGIN_LEFT, MARGIN_TOP + plotH,
                    MARGIN_LEFT + plotW, MARGIN_TOP + plotH);

        // ── Axis Labels ────────────────────────────────────────────────────
        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2.setColor(COLOR_TEXT);
        // Y label
        Graphics2D g2r = (Graphics2D) g2.create();
        g2r.rotate(-Math.PI / 2, 14, MARGIN_TOP + plotH / 2);
        g2r.drawString("Fitness", 14, MARGIN_TOP + plotH / 2);
        g2r.dispose();
        // X label
        g2.drawString("Generation", MARGIN_LEFT + plotW / 2 - 30, h - 6);

        // ── No data guard ──────────────────────────────────────────────────
        if (bestHistory == null || bestHistory.isEmpty()) {
            g2.setColor(new Color(120, 120, 140));
            g2.setFont(new Font("Monospaced", Font.ITALIC, 12));
            g2.drawString("Run a generation to see data...",
                          MARGIN_LEFT + plotW / 2 - 110,
                          MARGIN_TOP  + plotH / 2);
            return;
        }

        int n       = bestHistory.size();
        int maxFit  = bestHistory.stream().mapToInt(Integer::intValue).max().orElse(1);
        maxFit      = Math.max(maxFit, 1); // avoid divide-by-zero

        // ── Grid lines ─────────────────────────────────────────────────────
        g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
                                     BasicStroke.JOIN_MITER, 1,
                                     new float[]{4, 4}, 0));
        g2.setColor(COLOR_GRID);
        int gridLines = 5;
        for (int i = 1; i <= gridLines; i++) {
            int yLine = MARGIN_TOP + plotH - (i * plotH / gridLines);
            g2.drawLine(MARGIN_LEFT, yLine, MARGIN_LEFT + plotW, yLine);
            // Y-axis tick values
            g2.setColor(COLOR_TEXT);
            g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
            int val = (int)((double) i / gridLines * maxFit);
            g2.drawString(String.valueOf(val), MARGIN_LEFT - 28, yLine + 4);
            g2.setColor(COLOR_GRID);
        }

        // ── Helper: map data → screen coordinates ─────────────────────────
        // xFor(i) and yFor(v) are computed inline below

        // ── Draw BEST fitness line ─────────────────────────────────────────
        g2.setStroke(new BasicStroke(2.2f));
        g2.setColor(COLOR_BEST);
        for (int i = 1; i < n; i++) {
            int x1 = MARGIN_LEFT + (i - 1) * plotW / Math.max(n - 1, 1);
            int x2 = MARGIN_LEFT +  i      * plotW / Math.max(n - 1, 1);
            int y1 = MARGIN_TOP  + plotH - bestHistory.get(i - 1) * plotH / maxFit;
            int y2 = MARGIN_TOP  + plotH - bestHistory.get(i)     * plotH / maxFit;
            g2.drawLine(x1, y1, x2, y2);
        }
        // Dots
        for (int i = 0; i < n; i++) {
            int x = MARGIN_LEFT + i * plotW / Math.max(n - 1, 1);
            int y = MARGIN_TOP  + plotH - bestHistory.get(i) * plotH / maxFit;
            g2.fillOval(x - 3, y - 3, 7, 7);
        }

        // ── Draw AVG fitness line ──────────────────────────────────────────
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND,
                                     BasicStroke.JOIN_ROUND, 1,
                                     new float[]{6, 3}, 0));
        g2.setColor(COLOR_AVG);
        for (int i = 1; i < n; i++) {
            int x1 = MARGIN_LEFT + (i - 1) * plotW / Math.max(n - 1, 1);
            int x2 = MARGIN_LEFT +  i      * plotW / Math.max(n - 1, 1);
            int y1 = MARGIN_TOP  + plotH - (int)(avgHistory.get(i - 1) * plotH / maxFit);
            int y2 = MARGIN_TOP  + plotH - (int)(avgHistory.get(i)     * plotH / maxFit);
            g2.drawLine(x1, y1, x2, y2);
        }

        // ── X-axis tick labels ─────────────────────────────────────────────
        g2.setStroke(new BasicStroke(1f));
        g2.setColor(COLOR_TEXT);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
        int step = Math.max(1, n / 8);
        for (int i = 0; i < n; i += step) {
            int x = MARGIN_LEFT + i * plotW / Math.max(n - 1, 1);
            g2.drawString(String.valueOf(i + 1), x - 4, MARGIN_TOP + plotH + 16);
        }
    }
}
