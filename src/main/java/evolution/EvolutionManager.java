package evolution;

import java.util.Comparator;
import java.util.List;

/**
 * EVOLUTIONMANAGER — the engine that drives one full generation cycle.
 *
 * Biological analogy: The forces of nature (environment + time)
 * that cause a population to evolve across generations.
 *
 * Each generation cycle:
 *   1. Evaluate  → score every module's fitness
 *   2. Select    → remove the weakest (natural selection)
 *   3. Reproduce → duplicate the strongest (sexual/asexual reproduction)
 *   4. Mutate    → randomly alter some genes (DNA mutation)
 *
 * OOP: Encapsulates all evolution logic; uses polymorphism
 * by calling evaluateFitness() on each Module (resolved at runtime).
 */
public class EvolutionManager {

    // ── Fields ─────────────────────────────────────────────────────────────
    private CodeBase codeBase;        // The population being evolved
    private int      generationCount; // How many generations have passed

    // ── Statistics history (for the line graph) ───────────────────────────
    private java.util.List<Integer> bestFitnessHistory;
    private java.util.List<Double>  avgFitnessHistory;

    // ── Constructor ────────────────────────────────────────────────────────
    public EvolutionManager(CodeBase codeBase) {
        this.codeBase          = codeBase;
        this.generationCount   = 0;
        this.bestFitnessHistory = new java.util.ArrayList<>();
        this.avgFitnessHistory  = new java.util.ArrayList<>();
    }

    /**
     * RUN ONE GENERATION: execute all four evolution steps.
     * Returns a human-readable log string for display in the GUI.
     *
     * Biological analogy: one tick of evolutionary time.
     */
    public String runGeneration() {
        if (codeBase.isEmpty()) {
            return "⚠ CodeBase is empty. Please initialize first.\n";
        }

        generationCount++;
        StringBuilder log = new StringBuilder();
        log.append("═══════════════════════════════════════\n");
        log.append("  🧬 GENERATION ").append(generationCount).append("\n");
        log.append("═══════════════════════════════════════\n");

        // ── STEP 1: EVALUATE ────────────────────────────────────────────
        // Polymorphic call: each Module subclass uses its own fitness formula
        log.append("► [1] Evaluating fitness...\n");
        for (Module m : codeBase.getModules()) {
            m.evaluateFitness();  // POLYMORPHISM in action
        }

        // ── STEP 2: SELECT ──────────────────────────────────────────────
        // Remove weakest members (natural selection)
        log.append("► [2] Applying natural selection (removing weakest 30%)...\n");
        codeBase.removeWeakest();

        // ── STEP 3: REPRODUCE ───────────────────────────────────────────
        // Duplicate strongest members (reproduction)
        log.append("► [3] Reproducing strongest 30%...\n");
        codeBase.reproduceStrongest();

        // ── STEP 4: MUTATE ──────────────────────────────────────────────
        // Apply random mutations to all members
        log.append("► [4] Applying mutations...\n");
        for (Module m : codeBase.getModules()) {
            m.mutate();
            m.evaluateFitness(); // Re-evaluate after mutation
        }

        // ── STATS ────────────────────────────────────────────────────────
        int    best = getBestFitness();
        double avg  = getAverageFitness();
        bestFitnessHistory.add(best);
        avgFitnessHistory.add(avg);

        // ── LOG: print top and bottom modules ────────────────────────────
        log.append("\n  Population (").append(codeBase.size()).append(" modules):\n");
        List<Module> sorted = new java.util.ArrayList<>(codeBase.getModules());
        sorted.sort(Comparator.comparingInt(Module::getFitnessScore).reversed());
        
        int displayLimit = 5;
        if (sorted.size() <= displayLimit * 2) {
            for (Module m : sorted) {
                log.append("    ").append(m).append("\n");
            }
        } else {
            log.append("    --- Top ").append(displayLimit).append(" Fittest ---\n");
            for (int i = 0; i < displayLimit; i++) {
                log.append("    ").append(sorted.get(i)).append("\n");
            }
            log.append("    ... (").append(sorted.size() - displayLimit * 2).append(" modules omitted) ...\n");
            log.append("    --- Bottom ").append(displayLimit).append(" Weakest ---\n");
            for (int i = sorted.size() - displayLimit; i < sorted.size(); i++) {
                log.append("    ").append(sorted.get(i)).append("\n");
            }
        }

        // ── LOG: summary ─────────────────────────────────────────────────
        Module bestModule = getBestModule();
        log.append("\n  ✦ Best  module : ").append(bestModule).append("\n");
        log.append(String.format("  ✦ Best  fitness: %d%n", best));
        log.append(String.format("  ✦ Avg   fitness: %.2f%n", avg));
        log.append("\n");

        return log.toString();
    }

    /** Run multiple generations and return combined log. */
    public String runGenerations(int count) {
        StringBuilder combined = new StringBuilder();
        for (int i = 0; i < count; i++) {
            combined.append(runGeneration());
        }
        return combined.toString();
    }

    /** Reset the simulation state (but not the codebase — caller does that). */
    public void reset() {
        generationCount = 0;
        bestFitnessHistory.clear();
        avgFitnessHistory.clear();
    }

    // ── Stat helpers ───────────────────────────────────────────────────────

    public int getBestFitness() {
        return codeBase.getModules().stream()
                .mapToInt(Module::getFitnessScore).max().orElse(0);
    }

    public double getAverageFitness() {
        return codeBase.getModules().stream()
                .mapToInt(Module::getFitnessScore).average().orElse(0.0);
    }

    public Module getBestModule() {
        return codeBase.getModules().stream()
                .max(Comparator.comparingInt(Module::getFitnessScore)).orElse(null);
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public int                  getGenerationCount()     { return generationCount; }
    public java.util.List<Integer> getBestFitnessHistory()  { return bestFitnessHistory; }
    public java.util.List<Double>  getAvgFitnessHistory()   { return avgFitnessHistory; }
    public CodeBase             getCodeBase()            { return codeBase; }
}
