package evolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * CODEBASE — represents the entire POPULATION of modules (organisms).
 *
 * Biological analogy:
 *   - The gene pool / ecosystem that holds all living organisms.
 *   - We can remove the weakest (natural selection) and inspect members.
 *
 * OOP: Encapsulates a list of Modules, exposing controlled operations.
 */
public class CodeBase {

    // ── Constants ──────────────────────────────────────────────────────────
    public  static final int    GENOME_LENGTH   = 8;   // Bits per genome
    public  static final int    INITIAL_SIZE    = 10;  // Starting population
    private static final double MUTATION_RATE   = 0.1; // 10% per bit
    private static final Random RNG             = new Random();

    // ── Fields (Encapsulated) ──────────────────────────────────────────────
    private List<Module> modules;   // The living population

    // ── Constructor ────────────────────────────────────────────────────────
    public CodeBase() {
        modules = new ArrayList<>();
    }

    /**
     * INITIALIZE: Seed the population with random modules.
     * Mixes BasicModules and AdvancedModules for diversity.
     *
     * Biological analogy: Genesis — first generation of organisms
     * arising from random genetic sequences.
     */
    public void initialize() {
        modules.clear();
        for (int i = 0; i < INITIAL_SIZE; i++) {
            String genome = randomGenome();
            Module m;
            // Mix of basic and advanced organisms
            if (i % 3 == 0) {
                m = new AdvancedModule("Adv-" + (i + 1), genome, MUTATION_RATE);
            } else {
                m = new BasicModule("Mod-" + (i + 1), genome, MUTATION_RATE);
            }
            modules.add(m);
        }
    }

    /**
     * LOAD EXTERNAL MODULES: Replace population with loaded codebases.
     * This allows simulation of real code evolution.
     *
     * @param loadedModules List<Module> containing LoadedCodeModules
     * @throws IllegalArgumentException if loadedModules is empty
     */
    public void loadExternalModules(java.util.List<Module> loadedModules) {
        if (loadedModules == null || loadedModules.isEmpty()) {
            throw new IllegalArgumentException("Cannot load empty module list");
        }
        modules.clear();
        modules.addAll(loadedModules);
    }

    /**
     * Generate a random binary string of GENOME_LENGTH bits.
     * Biological analogy: random DNA sequence at birth.
     */
    private String randomGenome() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < GENOME_LENGTH; i++) {
            sb.append(RNG.nextInt(2)); // 0 or 1
        }
        return sb.toString();
    }

    /**
     * NATURAL SELECTION: Remove the bottom 30% weakest modules.
     * Biological analogy: survival of the fittest —
     * organisms below a fitness threshold do not survive.
     */
    public void removeWeakest() {
        if (modules.isEmpty()) return;

        // Sort ascending by fitness (weakest first)
        modules.sort(Comparator.comparingInt(Module::getFitnessScore));

        // Remove bottom 30%
        int removeCount = Math.max(1, modules.size() * 3 / 10);
        modules.subList(0, removeCount).clear();
    }

    /**
     * REPRODUCTION: The top 30% fittest modules reproduce (are duplicated).
     * Biological analogy: strong organisms pass on their genes to offspring.
     * Offspring are copies with slight mutations applied later.
     */
    public void reproduceStrongest() {
        if (modules.isEmpty()) return;

        // Sort descending by fitness (strongest first)
        modules.sort(Comparator.comparingInt(Module::getFitnessScore).reversed());

        int reproduceCount = Math.max(1, modules.size() * 3 / 10);
        List<Module> offspring = new ArrayList<>();

        for (int i = 0; i < reproduceCount; i++) {
            offspring.add(modules.get(i).copy()); // Polymorphic copy()
        }
        modules.addAll(offspring);
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public List<Module> getModules() { return modules; }

    public int size() { return modules.size(); }

    public boolean isEmpty() { return modules.isEmpty(); }

    /**
     * Computes the total LOC across modules that expose line count.
     */
    public int getTotalLinesOfCode() {
        return modules.stream().mapToInt(Module::getLinesOfCode).sum();
    }

    /**
     * Computes the average LOC across modules that expose line count.
     * Modules without LOC are counted as zero.
     */
    public double getAverageLinesOfCode() {
        if (modules.isEmpty()) {
            return 0.0;
        }
        return modules.stream().mapToInt(Module::getLinesOfCode).average().orElse(0.0);
    }
}
