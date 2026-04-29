package evolution;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * BARGRAPHPANEL — custom Swing panel that shows current population as bars.
 *
 * Each bar = one Module.
 * Bar height = fitness score.
 * Color distinguishes BasicModule (blue) from AdvancedModule (gold).
 *
 * OOP: Extends JPanel → INHERITANCE.
 *      Uses instanceof check → POLYMORPHISM awareness.
 */
public class BarGraphPanel extends JPanel {

    // ── Margins ────────────────────────────────────────────────────────────
    private static final int MARGIN_LEFT   = 50;
    private static final int MARGIN_RIGHT  = 20;
    private static final int MARGIN_TOP    = 30;
    private static final int MARGIN_BOTTOM = 40;

    // ── Colors ─────────────────────────────────────────────────────────────
    private static final Color COLOR_BASIC    = new Color(70, 140, 255);   // Blue
    private static final Color COLOR_ADVANCED = new Color(255, 190, 50);   // Gold
    private static final Color COLOR_BASIC_B  = new Color(40, 90, 180);    // Dark blue border
    private static final Color COLOR_ADV_B    = new Color(190, 130, 20);   // Dark gold border
    private static final Color COLOR_BG       = new Color(20, 20, 30);
    private static final Color COLOR_AXIS     = new Color(180, 180, 200);
    private static final Color COLOR_GRID     = new Color(55, 55, 65);
    private static final Color COLOR_TEXT     = new Color(210, 210, 230);

    // ── Data ───────────────────────────────────────────────────────────────
    private List<Module> modules;
    private int          maxPossibleFitness = 1;

    public BarGraphPanel() {
        setBackground(COLOR_BG);
        setBorder(BorderFactory.createLineBorder(new Color(60, 60, 80), 1));
        setPreferredSize(new Dimension(500, 220));
    }

    /** Refresh the displayed population. */
    public void update(List<Module> modules, int maxFitness) {
        this.modules           = modules;
        this.maxPossibleFitness = Math.max(maxFitness, 1);
        repaint();
    }

    // ── Paint ──────────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int w     = getWidth();
        int h     = getHeight();
        int plotW = w - MARGIN_LEFT - MARGIN_RIGHT;
        int plotH = h - MARGIN_TOP  - MARGIN_BOTTOM;

        // ── Title ──────────────────────────────────────────────────────────
        g2.setColor(COLOR_TEXT);
        g2.setFont(new Font("Monospaced", Font.BOLD, 13));
        g2.drawString("Current Population Fitness", MARGIN_LEFT, 20);

        // ── Legend ─────────────────────────────────────────────────────────
        g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g2.setColor(COLOR_BASIC);
        g2.fillRect(w - 200, 10, 12, 12);
        g2.setColor(COLOR_TEXT);
        g2.drawString("BasicModule", w - 184, 21);

        g2.setColor(COLOR_ADVANCED);
        g2.fillRect(w - 80, 10, 12, 12);
        g2.setColor(COLOR_TEXT);
        g2.drawString("Advanced", w - 64, 21);

        // ── Axes ───────────────────────────────────────────────────────────
        g2.setColor(COLOR_AXIS);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(MARGIN_LEFT, MARGIN_TOP, MARGIN_LEFT, MARGIN_TOP + plotH);
        g2.drawLine(MARGIN_LEFT, MARGIN_TOP + plotH,
                    MARGIN_LEFT + plotW, MARGIN_TOP + plotH);

        // Y label (rotated)
        Graphics2D g2r = (Graphics2D) g2.create();
        g2r.setColor(COLOR_TEXT);
        g2r.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2r.rotate(-Math.PI / 2, 12, MARGIN_TOP + plotH / 2);
        g2r.drawString("Fitness", 12, MARGIN_TOP + plotH / 2);
        g2r.dispose();

        // X label
        g2.setColor(COLOR_TEXT);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2.drawString("Modules", MARGIN_LEFT + plotW / 2 - 20, h - 6);

        // ── No data guard ──────────────────────────────────────────────────
        if (modules == null || modules.isEmpty()) {
            g2.setColor(new Color(120, 120, 140));
            g2.setFont(new Font("Monospaced", Font.ITALIC, 12));
            g2.drawString("Initialize to see population...",
                          MARGIN_LEFT + plotW / 2 - 110,
                          MARGIN_TOP  + plotH / 2);
            return;
        }

        // ── Grid lines ─────────────────────────────────────────────────────
        int gridLines = 4;
        g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
                                     BasicStroke.JOIN_MITER, 1,
                                     new float[]{4, 4}, 0));
        for (int i = 1; i <= gridLines; i++) {
            int yLine = MARGIN_TOP + plotH - i * plotH / gridLines;
            g2.setColor(COLOR_GRID);
            g2.drawLine(MARGIN_LEFT, yLine, MARGIN_LEFT + plotW, yLine);
            g2.setColor(COLOR_TEXT);
            g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
            int val = (int)((double) i / gridLines * maxPossibleFitness);
            g2.drawString(String.valueOf(val), MARGIN_LEFT - 28, yLine + 4);
        }

        // ── Bars ───────────────────────────────────────────────────────────
        int n       = modules.size();
        int maxDisplay = 40;
        int displayCount = Math.min(n, maxDisplay);
        int barW    = Math.max(4, plotW / (displayCount + 1) - 2);
        int spacing = plotW / (displayCount + 1);

        if (n > maxDisplay) {
            g2.setColor(new Color(255, 100, 100));
            g2.setFont(new Font("Monospaced", Font.ITALIC, 10));
            g2.drawString("Showing first " + maxDisplay + " of " + n + " modules", MARGIN_LEFT, MARGIN_TOP - 4);
        }

        for (int i = 0; i < displayCount; i++) {
            Module m    = modules.get(i);
            int fitness = m.getFitnessScore();
            int barH    = (int)((double) fitness / maxPossibleFitness * plotH);
            barH        = Math.max(barH, 2); // always visible

            int x = MARGIN_LEFT + (i + 1) * spacing - barW / 2;
            int y = MARGIN_TOP  + plotH - barH;

            // Fill color by type
            Color fill   = (m instanceof AdvancedModule) ? COLOR_ADVANCED : COLOR_BASIC;
            Color border = (m instanceof AdvancedModule) ? COLOR_ADV_B    : COLOR_BASIC_B;

            // Gradient effect
            GradientPaint gp = new GradientPaint(
                x, y,         fill.brighter(),
                x, y + barH,  fill.darker());
            g2.setPaint(gp);
            g2.fillRoundRect(x, y, barW, barH, 4, 4);

            g2.setColor(border);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(x, y, barW, barH, 4, 4);

            // Fitness value on top of bar
            g2.setColor(COLOR_TEXT);
            g2.setFont(new Font("Monospaced", Font.BOLD, 9));
            String label = String.valueOf(fitness);
            int lx = x + barW / 2 - g2.getFontMetrics().stringWidth(label) / 2;
            if (y > MARGIN_TOP + 14) {
                g2.drawString(label, lx, y - 3);
            }
        }
    }
}
