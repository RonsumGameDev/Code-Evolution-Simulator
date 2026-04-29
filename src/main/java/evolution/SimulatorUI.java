package evolution;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * SIMULATORUI — the main Swing window (JFrame) for the entire application.
 *
 * Layout:
 * ┌─────────────────────────────────────────────────────────┐
 * │  🧬  Conceptual CodeBase Evolution Simulator  [title]   │
 * ├──────────────────┬──────────────────────────────────────┤
 * │  CONTROL PANEL   │         LOG AREA (JTextArea)         │
 * │  [buttons]       │                                      │
 * │  [stats labels]  │                                      │
 * ├──────────────────┴──────────────────────────────────────┤
 * │         LINE GRAPH (fitness over generations)            │
 * ├─────────────────────────────────────────────────────────┤
 * │         BAR  GRAPH (current population)                  │
 * └─────────────────────────────────────────────────────────┘
 *
 * OOP: This class COMPOSES (has-a) all backend objects.
 *      JFrame extended via setters — standard Swing pattern.
 */
public class SimulatorUI extends JFrame {

    // ── Backend objects ────────────────────────────────────────────────────
    private CodeBase         codeBase;
    private EvolutionManager evolutionManager;
    private DatabaseManager  dbManager;

    // ── GUI components ─────────────────────────────────────────────────────
    private JTextArea    logArea;
    private JScrollPane  logScroll;
    private JButton      btnInit;
    private JButton      btnOneGen;
    private JButton      btnMultiGen;
    private JButton      btnReset;
    private JButton      btnLoadCodeBase;  // NEW: Load external codebase
    private JButton      btnLoadGitHub;    // NEW: GitHub conceptual button
    private JTextField   tfGitHubUrl;      // NEW: GitHub URL input
    private JLabel       lblLoadedFiles;   // NEW: Show count of loaded files
    private JLabel       lblTotalLOC;      // NEW: Show total LOC
    private JLabel       lblDbCount;       // NEW: Show DB record count
    private JButton      btnSaveToDb;      // NEW: Save generation to DB
    private JLabel       lblGenCount;
    private JLabel       lblBestFit;
    private JLabel       lblAvgFit;
    private JLabel       lblPopSize;
    private JSpinner     spinnerGens;

    // ── Custom graph panels ────────────────────────────────────────────────
    private LineGraphPanel     lineGraph;
    private BarGraphPanel      barGraph;
    private CodebaseGraphPanel codebaseGraph;

    // ── Theme colors ───────────────────────────────────────────────────────
    private static final Color BG_DARK     = new Color(18, 18, 28);
    private static final Color BG_PANEL    = new Color(28, 28, 42);
    private static final Color BG_CONTROL  = new Color(24, 24, 36);
    private static final Color FG_TEXT     = new Color(210, 215, 230);
    private static final Color FG_ACCENT   = new Color(100, 180, 255);
    private static final Color FG_GREEN    = new Color(80, 210, 120);
    private static final Color FG_GOLD     = new Color(255, 200, 60);
    private static final Color BORDER_COL  = new Color(55, 55, 75);

    // ── Constructor ────────────────────────────────────────────────────────
    public SimulatorUI() {
        dbManager        = new DatabaseManager();
        codeBase         = new CodeBase();
        evolutionManager = new EvolutionManager(codeBase);

        initComponents();
        layoutComponents();
        applyTheme();
        wireListeners();

        appendLog("Welcome to the 🧬 CodeBase Evolution Simulator!\n\n"
                + "Each module is a digital organism with a binary genome.\n"
                + "Evolution works through:\n"
                + "  • Evaluate  → score each module's fitness\n"
                + "  • Select    → remove the weakest 30%\n"
                + "  • Reproduce → duplicate the strongest 30%\n"
                + "  • Mutate    → randomly flip bits in genomes\n\n"
                + "Press [Initialize CodeBase] to begin!\n");
    }

    // ── Component creation ─────────────────────────────────────────────────
    private void initComponents() {
        // Buttons
        btnInit     = createButton("🧫  Initialize CodeBase",  FG_GREEN);
        btnOneGen   = createButton("▶   Run One Generation",   FG_ACCENT);
        btnMultiGen = createButton("⏩  Run Multiple Gens",     FG_ACCENT);
        btnReset    = createButton("↺   Reset",                 new Color(230, 90, 90));
        btnLoadCodeBase = createButton("📂  Load CodeBase",     new Color(100, 200, 255));
        btnLoadGitHub   = createButton("📡  GitHub URL",        new Color(150, 150, 200));
        btnSaveToDb     = createButton("💾  Save to MySQL",     new Color(80, 210, 120));

        // GitHub URL input field
        tfGitHubUrl = new JTextField();
        tfGitHubUrl.setFont(new Font("Monospaced", Font.PLAIN, 11));
        tfGitHubUrl.setBackground(BG_PANEL);
        tfGitHubUrl.setForeground(FG_TEXT);
        tfGitHubUrl.setToolTipText("https://github.com/user/repo");
        tfGitHubUrl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        // Loaded files and LOC labels
        lblLoadedFiles = createStatLabel("Loaded Files: 0");
        lblTotalLOC   = createStatLabel("Total LOC: 0");
        lblDbCount    = createStatLabel("DB Records: " + dbManager.getSavedGenerationsCount());

        // Spinner for multi-gen count
        spinnerGens = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
        spinnerGens.setBackground(BG_PANEL);
        spinnerGens.setForeground(FG_TEXT);
        ((JSpinner.DefaultEditor) spinnerGens.getEditor()).getTextField()
            .setBackground(BG_PANEL);
        ((JSpinner.DefaultEditor) spinnerGens.getEditor()).getTextField()
            .setForeground(FG_TEXT);

        // Stats labels
        lblGenCount = createStatLabel("Generation: 0");
        lblBestFit  = createStatLabel("Best Fitness: —");
        lblAvgFit   = createStatLabel("Avg  Fitness: —");
        lblPopSize  = createStatLabel("Population : —");

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBackground(new Color(14, 14, 22));
        logArea.setForeground(FG_TEXT);
        logArea.setCaretColor(FG_ACCENT);
        logArea.setLineWrap(false);
        logArea.setMargin(new Insets(8, 10, 8, 10));
        logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createLineBorder(BORDER_COL, 1));

        // Graph panels
        lineGraph      = new LineGraphPanel();
        barGraph       = new BarGraphPanel();
        codebaseGraph  = new CodebaseGraphPanel();

        // Initially disable evolution buttons
        btnOneGen.setEnabled(false);
        btnMultiGen.setEnabled(false);
        btnReset.setEnabled(false);
    }

    // ── Layout ─────────────────────────────────────────────────────────────
    private void layoutComponents() {
        setTitle("🧬  Conceptual CodeBase Evolution Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 740);
        setLocationRelativeTo(null);
        setBackground(BG_DARK);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);

        // ── TOP: Title bar ─────────────────────────────────────────────────
        JPanel titleBar = buildTitleBar();
        root.add(titleBar, BorderLayout.NORTH);

        // ── CENTER: controls + log side by side ────────────────────────────
        JPanel centerRow = new JPanel(new BorderLayout(8, 0));
        centerRow.setBackground(BG_DARK);
        centerRow.setBorder(new EmptyBorder(8, 8, 4, 8));

        // Left: control panel
        JPanel controlPanel = buildControlPanel();
        JScrollPane controlScroll = new JScrollPane(controlPanel);
        controlScroll.setPreferredSize(new Dimension(245, 0));
        controlScroll.setBorder(null);
        controlScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        centerRow.add(controlScroll, BorderLayout.WEST);

        // Right: log area
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(BG_DARK);
        JLabel logTitle = new JLabel("  📋 Evolution Log");
        logTitle.setFont(new Font("Monospaced", Font.BOLD, 12));
        logTitle.setForeground(FG_ACCENT);
        logTitle.setBorder(new EmptyBorder(0, 0, 4, 0));
        logPanel.add(logTitle, BorderLayout.NORTH);
        logPanel.add(logScroll, BorderLayout.CENTER);
        centerRow.add(logPanel, BorderLayout.CENTER);

        root.add(centerRow, BorderLayout.CENTER);

        // ── BOTTOM: three graph panels ──────────────────────────────────────
        JPanel graphRow = new JPanel(new GridLayout(1, 3, 8, 0));
        graphRow.setBackground(BG_DARK);
        graphRow.setBorder(new EmptyBorder(4, 8, 8, 8));

        JPanel lgWrap = wrapGraph(lineGraph, "📈  Fitness History (Line Graph)");
        JPanel bgWrap = wrapGraph(barGraph,  "📊  Current Population (Bar Graph)");
        JPanel cbWrap = wrapGraph(codebaseGraph, "📐  Codebase Visualizer (LOC)");
        graphRow.add(lgWrap);
        graphRow.add(bgWrap);
        graphRow.add(cbWrap);

        root.add(graphRow, BorderLayout.SOUTH);

        setContentPane(root);
    }

    /** Wrap a graph panel in a titled container. */
    private JPanel wrapGraph(JPanel graph, String title) {
        JPanel wrap = new JPanel(new BorderLayout(0, 4));
        wrap.setBackground(BG_DARK);
        JLabel lbl = new JLabel("  " + title);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 12));
        lbl.setForeground(FG_GOLD);
        lbl.setBorder(new EmptyBorder(2, 0, 2, 0));
        wrap.add(lbl, BorderLayout.NORTH);
        wrap.add(graph, BorderLayout.CENTER);
        return wrap;
    }

    /** Title bar at the top. */
    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        bar.setBackground(new Color(14, 14, 24));
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL));

        JLabel icon = new JLabel("🧬");
        icon.setFont(new Font("Dialog", Font.PLAIN, 26));

        JLabel title = new JLabel("Conceptual CodeBase Evolution Simulator");
        title.setFont(new Font("Monospaced", Font.BOLD, 16));
        title.setForeground(FG_ACCENT);

        JLabel sub = new JLabel("OOP • Genetic Algorithms • Java Swing");
        sub.setFont(new Font("Monospaced", Font.ITALIC, 11));
        sub.setForeground(new Color(130, 140, 160));

        bar.add(icon);
        bar.add(title);
        bar.add(Box.createHorizontalStrut(20));
        bar.add(sub);
        return bar;
    }

    /** Left control panel with buttons and stats. */
    private JPanel buildControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CONTROL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL, 1),
            new EmptyBorder(14, 12, 14, 12)));

        addSectionLabel(panel, "📂  LOAD CODEBASE");
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnLoadCodeBase);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblLoadedFiles);
        panel.add(Box.createVerticalStrut(4));
        panel.add(lblTotalLOC);
        panel.add(Box.createVerticalStrut(12));

        // GitHub URL section
        addSectionLabel(panel, "📡  GITHUB (OPTIONAL)");
        panel.add(Box.createVerticalStrut(4));
        panel.add(tfGitHubUrl);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnLoadGitHub);

        panel.add(Box.createVerticalStrut(14));
        JSeparator sep0 = new JSeparator();
        sep0.setForeground(BORDER_COL);
        sep0.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(sep0);
        panel.add(Box.createVerticalStrut(14));

        addSectionLabel(panel, "⚙  CONTROLS");
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnInit);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnOneGen);
        panel.add(Box.createVerticalStrut(8));

        // Multi-gen row
        JPanel multiRow = new JPanel(new BorderLayout(6, 0));
        multiRow.setBackground(BG_CONTROL);
        multiRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        multiRow.add(btnMultiGen, BorderLayout.CENTER);
        JPanel spinWrap = new JPanel(new BorderLayout());
        spinWrap.setBackground(BG_CONTROL);
        JLabel xLabel = new JLabel("×");
        xLabel.setForeground(FG_TEXT);
        xLabel.setHorizontalAlignment(SwingConstants.CENTER);
        spinWrap.add(xLabel, BorderLayout.NORTH);
        spinWrap.add(spinnerGens, BorderLayout.CENTER);
        spinWrap.setPreferredSize(new Dimension(48, 36));
        multiRow.add(spinWrap, BorderLayout.EAST);
        panel.add(multiRow);

        panel.add(Box.createVerticalStrut(8));
        panel.add(btnReset);

        // Separator
        panel.add(Box.createVerticalStrut(18));
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COL);
        sep.setBackground(BORDER_COL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(14));

        // Stats
        addSectionLabel(panel, "📊  STATISTICS");
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblGenCount);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblPopSize);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblBestFit);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblAvgFit);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblDbCount);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnSaveToDb);

        // Legend
        panel.add(Box.createVerticalStrut(18));
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(BORDER_COL);
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(sep2);
        panel.add(Box.createVerticalStrut(14));
        addSectionLabel(panel, "🔬  LEGEND");
        panel.add(Box.createVerticalStrut(6));
        addLegendItem(panel, "BasicModule",    new Color(70, 140, 255));
        panel.add(Box.createVerticalStrut(4));
        addLegendItem(panel, "AdvancedModule", new Color(255, 190, 50));
        panel.add(Box.createVerticalStrut(4));
        addLegendItem(panel, "LoadedCodeModule", new Color(100, 200, 255));

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private void addSectionLabel(JPanel panel, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 11));
        lbl.setForeground(FG_GOLD);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
    }

    private void addLegendItem(JPanel panel, String text, Color color) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setBackground(BG_CONTROL);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        JLabel dot = new JLabel("■");
        dot.setForeground(color);
        dot.setFont(new Font("Monospaced", Font.BOLD, 13));
        JLabel lbl = new JLabel(text);
        lbl.setForeground(FG_TEXT);
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
        row.add(dot);
        row.add(lbl);
        panel.add(row);
    }

    // ── Button factory ─────────────────────────────────────────────────────
    private JButton createButton(String text, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Monospaced", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setBackground(new Color(35, 35, 52));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fg.darker(), 1),
            new EmptyBorder(7, 10, 7, 10)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Hover effect
        Color normalBg  = new Color(35, 35, 52);
        Color hoverBg   = new Color(50, 50, 75);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(hoverBg);
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(normalBg);
            }
        });
        return btn;
    }

    private JLabel createStatLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lbl.setForeground(FG_TEXT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // ── Theme application ──────────────────────────────────────────────────
    private void applyTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
    }

    // ── Event listeners ────────────────────────────────────────────────────
    private void wireListeners() {

        // Initialize
        btnInit.addActionListener(e -> {
            codeBase.initialize();
            evolutionManager.reset();
            updateGraphs();
            updateStats();
            appendLog("✅ CodeBase initialized with "
                    + codeBase.size() + " modules.\n\n");
            btnOneGen.setEnabled(true);
            btnMultiGen.setEnabled(true);
            btnReset.setEnabled(true);
            btnInit.setText("🧫  Re-Initialize");
        });

        // One generation
        btnOneGen.addActionListener(e -> {
            String log = evolutionManager.runGeneration();
            appendLog(log);
            updateGraphs();
            updateStats();
        });

        // Multiple generations
        btnMultiGen.addActionListener(e -> {
            int n = (int) spinnerGens.getValue();
            // Run in background to keep UI responsive
            btnOneGen.setEnabled(false);
            btnMultiGen.setEnabled(false);

            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() {
                    return evolutionManager.runGenerations(n);
                }
                @Override
                protected void done() {
                    try {
                        appendLog(get());
                    } catch (Exception ex) {
                        appendLog("Error: " + ex.getMessage() + "\n");
                    }
                    updateGraphs();
                    updateStats();
                    btnOneGen.setEnabled(true);
                    btnMultiGen.setEnabled(true);
                }
            };
            worker.execute();
        });

        // Reset
        btnReset.addActionListener(e -> {
            codeBase = new CodeBase();
            evolutionManager = new EvolutionManager(codeBase);
            dbManager.clearStats();
            logArea.setText("");
            lineGraph.update(null, null);
            barGraph.update(null, 1);
            updateStats();
            btnOneGen.setEnabled(false);
            btnMultiGen.setEnabled(false);
            btnReset.setEnabled(false);
            btnInit.setText("🧫  Initialize CodeBase");
            appendLog("↺  Simulation reset. Press [Initialize CodeBase] to start again.\n\n");
        });

        // Load CodeBase from folder
        btnLoadCodeBase.addActionListener(e -> {
            loadCodeBaseFromFolder();
        });

        // Load CodeBase from GitHub URL (conceptual)
        btnLoadGitHub.addActionListener(e -> {
            handleGitHubLoad();
        });

        // Save to DB
        btnSaveToDb.addActionListener(e -> {
            saveGenerationToDB();
            appendLog("✅ Current generation saved to MySQL.\n\n");
            JOptionPane.showMessageDialog(this, "Generation saved to MySQL successfully!", "Saved to DB", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    // ── UI update helpers ──────────────────────────────────────────────────

    /** Append text to the log and auto-scroll to bottom. */
    private void appendLog(String text) {
        logArea.append(text);
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    /** Refresh both graph panels with latest data. */
    private void updateGraphs() {
        lineGraph.update(
            evolutionManager.getBestFitnessHistory(),
            evolutionManager.getAvgFitnessHistory());

        // Max possible fitness depends on module type
        // AdvancedModule max: genome*3-2; BasicModule max: genome length
        int maxFit = CodeBase.GENOME_LENGTH * 3;
        for (Module m : codeBase.getModules()) {
            maxFit = Math.max(maxFit, m.getFitnessScore());
        }
        barGraph.update(codeBase.getModules(), maxFit);
        codebaseGraph.update(codeBase.getModules());
    }

    /** Update the statistics labels. */
    private void updateStats() {
        lblGenCount.setText("Generation: " + evolutionManager.getGenerationCount());
        lblPopSize .setText("Population : " + codeBase.size());
        lblTotalLOC.setText("Total LOC: " + codeBase.getTotalLinesOfCode());
        lblDbCount .setText("DB Records: " + dbManager.getSavedGenerationsCount());
        if (!codeBase.isEmpty()) {
            lblBestFit.setText(String.format("Best Fitness: %d", evolutionManager.getBestFitness()));
            lblAvgFit .setText(String.format("Avg  Fitness: %.1f", evolutionManager.getAverageFitness()));
        } else {
            lblBestFit.setText("Best Fitness: —");
            lblAvgFit .setText("Avg  Fitness: —");
        }
    }

    private void saveGenerationToDB() {
        if (!codeBase.isEmpty()) {
            dbManager.insertGenerationStats(
                evolutionManager.getGenerationCount(),
                codeBase.size(),
                evolutionManager.getBestFitness(),
                evolutionManager.getAverageFitness(),
                codeBase.getTotalLinesOfCode()
            );
            lblDbCount.setText("DB Records: " + dbManager.getSavedGenerationsCount());
        }
    }

    // ── Codebase loading methods ───────────────────────────────────────────

    /**
     * Open file chooser and load codebases from selected directory.
     * Runs in background thread to prevent UI freezing.
     */
    private void loadCodeBaseFromFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a folder containing Java/Text files");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return; // User cancelled
        }

        File selectedFolder = chooser.getSelectedFile();

        // Run loading in background thread
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                try {
                    // Load modules from folder
                    java.util.List<Module> loadedModules = CodeLoader.loadFromFolder(selectedFolder);

                    // Replace codebase with loaded modules
                    codeBase = new CodeBase();
                    codeBase.loadExternalModules(loadedModules);
                    evolutionManager = new EvolutionManager(codeBase);

                    // Evaluate fitness for all loaded modules
                    for (Module m : codeBase.getModules()) {
                        m.evaluateFitness();
                    }

                    return String.format(
                        "✅ Loaded %d modules from: %s\n\n",
                        loadedModules.size(),
                        selectedFolder.getName()
                    );
                } catch (IllegalArgumentException ex) {
                    return "⚠ Error: " + ex.getMessage() + "\n\n";
                } catch (java.io.IOException ex) {
                    return "⚠ IO Error: " + ex.getMessage() + "\n\n";
                }
            }

            @Override
            protected void done() {
                try {
                    String message = get();
                    appendLog(message);
                    lblLoadedFiles.setText("Loaded Files: " + codeBase.size());
                    updateGraphs();
                    updateStats();
                    btnOneGen.setEnabled(true);
                    btnMultiGen.setEnabled(true);
                    btnReset.setEnabled(true);
                    btnInit.setText("🧫  Reinit Random");
                } catch (Exception ex) {
                    appendLog("⚠ Error loading codebase: " + ex.getMessage() + "\n");
                }
            }
        };
        worker.execute();
    }

    /**
     * Handle GitHub URL input (conceptual/mock implementation).
     */
    private void handleGitHubLoad() {
        String url = tfGitHubUrl.getText().trim();
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter a GitHub repository URL.",
                "GitHub Input",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String message = CodeLoader.getGitHubLoadMessage(url);
        JOptionPane.showMessageDialog(
            this,
            message,
            "GitHub Integration (Conceptual)",
            JOptionPane.INFORMATION_MESSAGE
        );
        appendLog(message + "\n");
    }
}
