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

    private static final double MUTATION_RATE   = 0.1; // 10% per bit
    private static final Random RNG             = new Random();

    // ── Fields (Encapsulated) ──────────────────────────────────────────────
    private List<Module> modules;   // The living population

    // ── Constructor ────────────────────────────────────────────────────────
    public CodeBase() {
        modules = new ArrayList<>();
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
            Module parent1 = modules.get(i);
            
            // If it's a LoadedCodeModule and we have at least 2 strong parents, use Crossover
            if (parent1 instanceof LoadedCodeModule && reproduceCount > 1) {
                int partnerIndex = RNG.nextInt(reproduceCount);
                while (partnerIndex == i) {
                    partnerIndex = RNG.nextInt(reproduceCount);
                }
                Module parent2 = modules.get(partnerIndex);
                
                if (parent2 instanceof LoadedCodeModule) {
                    offspring.add(((LoadedCodeModule) parent1).crossover((LoadedCodeModule) parent2));
                } else {
                    offspring.add(parent1.copy());
                }
            } else {
                offspring.add(parent1.copy()); // Polymorphic copy() for non-loaded modules
            }
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
