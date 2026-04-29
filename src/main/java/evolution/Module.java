package evolution;

import java.util.Random;

/**
 * MODULE — represents a single "Organism" in our biological analogy.
 *
 * Biological analogy:
 *   - genome     → the binary string of code ("101101")
 *   - fitnessScore → how "fit" / healthy this organism is
 *   - mutate()   → like DNA point-mutations (bit flips)
 *   - evaluateFitness() → natural selection metric
 *
 * OOP: This is an ABSTRACT class demonstrating ABSTRACTION.
 * Subclasses must override evaluateFitness() → POLYMORPHISM.
 */
public abstract class Module {

    // ── Encapsulation: all fields are private ──────────────────────────────
    private String genome;        // DNA: binary string, e.g. "10110101"
    private int    fitnessScore;  // How fit this module is (0–genome.length)
    private String name;          // Human-readable identifier

    protected static final Random RNG = new Random();
    protected double mutationRate;  // Probability of each bit flipping

    // ── Constructor ────────────────────────────────────────────────────────
    public Module(String name, String genome, double mutationRate) {
        this.name         = name;
        this.genome       = genome;
        this.mutationRate = mutationRate;
        this.fitnessScore = 0;
    }

    // ── Abstract method → forces subclasses to define fitness logic ────────
    /**
     * POLYMORPHISM hook: each Module subclass computes fitness differently.
     * Base rule: count the number of '1' bits in the genome.
     */
    public abstract void evaluateFitness();

    // ── Mutation: random bit-flip (DNA point mutation) ─────────────────────
    /**
     * Simulates a biological mutation.
     * Each character in the genome has a mutationRate chance of flipping
     * (0→1 or 1→0), mimicking a nucleotide substitution.
     */
    public void mutate() {
        StringBuilder sb = new StringBuilder(genome);
        for (int i = 0; i < sb.length(); i++) {
            if (RNG.nextDouble() < mutationRate) {
                // Flip the bit
                sb.setCharAt(i, sb.charAt(i) == '0' ? '1' : '0');
            }
        }
        genome = sb.toString();
    }

    /**
     * Returns a deep copy of this module (used during reproduction).
     * Subclasses override this to return the correct type.
     */
    public abstract Module copy();

    // ── Getters & Setters (Encapsulation) ──────────────────────────────────
    public String getGenome()               { return genome; }
    public int    getFitnessScore()         { return fitnessScore; }
    public String getName()                 { return name; }
    public double getMutationRate()         { return mutationRate; }

    protected void setFitnessScore(int score) { this.fitnessScore = score; }
    protected void setGenome(String genome)   { this.genome = genome; }

    /**
     * Provides source code line count for modules that represent files.
     * Default modules do not expose LOC, so this returns 0.
     */
    public int getLinesOfCode() {
        return 0;
    }

    @Override
    public String toString() {
        return String.format("[%s | genome=%s | fitness=%d]", name, genome, fitnessScore);
    }
}
