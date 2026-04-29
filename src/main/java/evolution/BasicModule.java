package evolution;

/**
 * BASICMODULE — the standard organism in our simulation.
 *
 * Biological analogy: a common organism whose fitness is simply
 * the count of active genes ('1' bits) in its genome.
 *
 * OOP: Concrete subclass of Module → demonstrates INHERITANCE.
 * Overrides evaluateFitness() → demonstrates POLYMORPHISM.
 */
public class BasicModule extends Module {

    public BasicModule(String name, String genome, double mutationRate) {
        super(name, genome, mutationRate);
    }

    /**
     * FITNESS RULE (Basic):
     * Count the number of '1' characters in the genome.
     * More active genes → higher fitness.
     *
     * Biological analogy: more beneficial alleles → stronger organism.
     */
    @Override
    public void evaluateFitness() {
        int count = 0;
        for (char c : getGenome().toCharArray()) {
            if (c == '1') count++;
        }
        setFitnessScore(count);
    }

    /** Returns a new BasicModule with the same genome and mutationRate. */
    @Override
    public Module copy() {
        return new BasicModule(getName() + "'", getGenome(), getMutationRate());
    }
}
