package evolution;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * CODEBASEGRAPHPANEL — visualizes module size (LOC) distribution.
 *
 * This panel shows each module as a bar whose height reflects the
 * number of lines of code in that module. It is especially useful
 * for loaded external codebases where LOC is meaningful.
 */
public class CodebaseGraphPanel extends JPanel {

    private static final int MARGIN_LEFT   = 50;
    private static final int MARGIN_RIGHT  = 20;
    private static final int MARGIN_TOP    = 30;
    private static final int MARGIN_BOTTOM = 50;

    private static final Color COLOR_BG       = new Color(20, 20, 30);
    private static final Color COLOR_AXIS     = new Color(180, 180, 200);
    private static final Color COLOR_GRID     = new Color(55, 55, 65);
    private static final Color COLOR_TEXT     = new Color(210, 210, 230);
    private static final Color COLOR_FILL     = new Color(120, 180, 240);
    private static final Color COLOR_FILL_ALT = new Color(90, 210, 130);

    private List<Module> modules;
    private int          maxLoc = 1;

    public CodebaseGraphPanel() {
        setBackground(COLOR_BG);
        setBorder(BorderFactory.createLineBorder(new Color(60, 60, 80), 1));
        setPreferredSize(new Dimension(360, 220));
    }

    public void update(List<Module> modules) {
        this.modules = modules;
        this.maxLoc = Math.max(1, modules.stream().mapToInt(Module::getLinesOfCode).max().orElse(1));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int plotW = w - MARGIN_LEFT - MARGIN_RIGHT;
        int plotH = h - MARGIN_TOP - MARGIN_BOTTOM;

        g2.setColor(COLOR_TEXT);
        g2.setFont(new Font("Monospaced", Font.BOLD, 13));
        g2.drawString("Codebase Visualizer (LOC)", MARGIN_LEFT, 20);

        g2.setColor(COLOR_AXIS);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(MARGIN_LEFT, MARGIN_TOP, MARGIN_LEFT, MARGIN_TOP + plotH);
        g2.drawLine(MARGIN_LEFT, MARGIN_TOP + plotH,
                    MARGIN_LEFT + plotW, MARGIN_TOP + plotH);

        Graphics2D g2r = (Graphics2D) g2.create();
        g2r.rotate(-Math.PI / 2, 14, MARGIN_TOP + plotH / 2);
        g2r.setColor(COLOR_TEXT);
        g2r.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2r.drawString("Lines of Code", 14, MARGIN_TOP + plotH / 2);
        g2r.dispose();

        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2.setColor(COLOR_TEXT);
        g2.drawString("Modules", MARGIN_LEFT + plotW / 2 - 20, h - 10);

        if (modules == null || modules.isEmpty()) {
            g2.setColor(new Color(120, 120, 140));
            g2.setFont(new Font("Monospaced", Font.ITALIC, 12));
            g2.drawString("Load a codebase to see LOC distribution...",
                          MARGIN_LEFT + 10,
                          MARGIN_TOP + plotH / 2);
            return;
        }

        int gridLines = 4;
        g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
                                     BasicStroke.JOIN_MITER, 1,
                                     new float[]{4, 4}, 0));
        for (int i = 1; i <= gridLines; i++) {
            int yLine = MARGIN_TOP + plotH - i * plotH / gridLines;
            g2.setColor(COLOR_GRID);
            g2.drawLine(MARGIN_LEFT, yLine, MARGIN_LEFT + plotW, yLine);
            g2.setColor(COLOR_TEXT);
            g2.drawString(String.valueOf((int)((double) i / gridLines * maxLoc)),
                          MARGIN_LEFT - 30, yLine + 4);
        }

        int n = modules.size();
        int maxDisplay = 40;
        int displayCount = Math.min(n, maxDisplay);
        int barW = Math.max(10, plotW / (Math.max(displayCount, 1) * 2));
        int spacing = plotW / Math.max(displayCount, 1);

        if (n > maxDisplay) {
            g2.setColor(new Color(255, 100, 100));
            g2.setFont(new Font("Monospaced", Font.ITALIC, 10));
            g2.drawString("Showing first " + maxDisplay + " of " + n + " files", MARGIN_LEFT, MARGIN_TOP - 4);
        }

        for (int i = 0; i < displayCount; i++) {
            Module m = modules.get(i);
            int loc = m.getLinesOfCode();
            int barH = (int)((double) loc / maxLoc * plotH);
            barH = Math.max(barH, loc > 0 ? 3 : 2);

            int x = MARGIN_LEFT + i * spacing + (spacing - barW) / 2;
            int y = MARGIN_TOP + plotH - barH;

            Color fill = (i % 2 == 0) ? COLOR_FILL : COLOR_FILL_ALT;
            g2.setPaint(new GradientPaint(x, y, fill.brighter(), x, y + barH, fill.darker()));
            g2.fillRoundRect(x, y, barW, barH, 4, 4);

            g2.setColor(fill.darker());
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(x, y, barW, barH, 4, 4);

            if (loc > 0) {
                g2.setColor(COLOR_TEXT);
                g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
                String label = String.valueOf(loc);
                int lx = x + barW / 2 - g2.getFontMetrics().stringWidth(label) / 2;
                g2.drawString(label, lx, y - 4);
            }

            String displayName = m.getName();
            if (m instanceof LoadedCodeModule) {
                displayName = new File(((LoadedCodeModule) m).getFilePath()).getName();
            }
            if (displayName.length() > 10) {
                displayName = displayName.substring(0, 9) + "…";
            }
            g2.setColor(COLOR_TEXT);
            g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
            int tx = x + barW / 2 - g2.getFontMetrics().stringWidth(displayName) / 2;
            g2.drawString(displayName, tx, MARGIN_TOP + plotH + 16);
        }
    }
}
