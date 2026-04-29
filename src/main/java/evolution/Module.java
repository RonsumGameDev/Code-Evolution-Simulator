package evolution;

import java.util.Random;

/**
 * MODULE — represents a single "Organism" in our biological analogy.
 *
 * OOP: This is an ABSTRACT class demonstrating ABSTRACTION.
 * Subclasses must override evaluateFitness() and mutate() → POLYMORPHISM.
 */
public abstract class Module {

    private int    fitnessScore;
    private String name;

    protected static final Random RNG = new Random();
    protected double mutationRate;

    public Module(String name, double mutationRate) {
        this.name         = name;
        this.mutationRate = mutationRate;
        this.fitnessScore = 0;
    }

    /**
     * POLYMORPHISM hook: each Module subclass computes fitness differently.
     */
    public abstract void evaluateFitness();

    /**
     * POLYMORPHISM hook: each Module subclass mutates differently.
     */
    public abstract void mutate();

    /**
     * Returns a deep copy of this module.
     */
    public abstract Module copy();

    public int    getFitnessScore()         { return fitnessScore; }
    public String getName()                 { return name; }
    public double getMutationRate()         { return mutationRate; }

    protected void setFitnessScore(int score) { this.fitnessScore = score; }

    public int getLinesOfCode() {
        return 0;
    }

    @Override
    public String toString() {
        return String.format("[%s | fitness=%d]", name, fitnessScore);
    }
}
