package evolution;

/**
 * ADVANCEDMODULE — an "evolved" organism with a more complex fitness function.
 *
 * Biological analogy: a more sophisticated species that rewards
 * alternating gene patterns (like regulatory gene sequences).
 * An alternating genome "10101010" scores maximum fitness because
 * each gene is neatly switched on/off in a disciplined pattern —
 * analogous to well-structured, alternating code logic.
 *
 * OOP: Second concrete subclass of Module → demonstrates INHERITANCE.
 * Different evaluateFitness() implementation → demonstrates POLYMORPHISM.
 */
public class AdvancedModule extends Module {

    public AdvancedModule(String name, String genome, double mutationRate) {
        super(name, genome, mutationRate);
    }

    /**
     * FITNESS RULE (Advanced):
     * Reward alternating patterns in the genome (e.g. "1010" or "0101").
     * Each position that alternates from the previous adds 2 points.
     * Each '1' still adds 1 point as a base bonus.
     *
     * Biological analogy: structured, rhythmic gene expression
     * indicates a more sophisticated and robust organism.
     */
    @Override
    public void evaluateFitness() {
        String genome = getGenome();
        int score = 0;

        for (int i = 0; i < genome.length(); i++) {
            // Base score: count active genes
            if (genome.charAt(i) == '1') score++;

            // Bonus: reward alternation (like structured code patterns)
            if (i > 0 && genome.charAt(i) != genome.charAt(i - 1)) {
                score += 2;
            }
        }
        setFitnessScore(score);
    }

    /** Returns a new AdvancedModule with the same genome. */
    @Override
    public Module copy() {
        return new AdvancedModule(getName() + "'", getGenome(), getMutationRate());
    }
}
