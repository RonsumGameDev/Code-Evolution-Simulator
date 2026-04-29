package evolution;

/**
 * LOADEDCODEMODULE — represents a loaded external code file as a Module.
 *
 * Inherits from Module:
 *   - genome: binary representation derived from file content
 *   - fitnessScore: evaluated based on code characteristics
 *   - mutationRate: inherited mutation behavior
 *
 * Additional fields:
 *   - filePath: original location of the source file
 *   - fileSize: character count (represents code complexity)
 *
 * Fitness Logic (Conceptual):
 *   "Good code" indicators in our simulation:
 *     • Shorter code = simpler (fewer bytes)
 *     • Alternating patterns = well-structured (like AdvancedModule)
 *   Formula: base_score + alternation_bonus + size_penalty
 *
 * OOP: Concrete subclass of Module → demonstrates INHERITANCE.
 *      Unique fitness strategy → demonstrates POLYMORPHISM.
 */
public class LoadedCodeModule extends Module {

    // ── Additional fields specific to loaded code ──────────────────────────
    private String filePath;     // Original file location
    private String displayName;  // Display-friendly name or relative path
    private int    fileSize;     // Original file size (character count)
    private int    linesOfCode;  // Number of source lines in the file

    // ── Constructor ────────────────────────────────────────────────────────
    public LoadedCodeModule(String name, String genome, double mutationRate,
                            String filePath, String displayName,
                            int fileSize, int linesOfCode) {
        super(name, genome, mutationRate);
        this.filePath = filePath;
        this.displayName = displayName;
        this.fileSize = fileSize;
        this.linesOfCode = linesOfCode;
    }

    /**
     * FITNESS RULE (Loaded Code):
     * Balance three factors:
     *   1. Base score: count active genes ('1' bits) — rewards complexity
     *   2. Alternation bonus: reward patterns — indicates structure
     *   3. Size penalty: shorter code is better — simplicity valued
     *
     * Formula Example (genome="10101010", fileSize=250):
     *   - Base score: 4 active genes
     *   - Alternation:  7 position transitions × 1 point = 7
     *   - Size penalty: -1 (files over 200 bytes lose 1 point per 50 bytes)
     *   - Total: 4 + 7 - 1 = 10
     *
     * Biological analogy: Code that is concise yet well-structured
     * (with clear patterns) is considered "healthier" in our ecosystem.
     */
    @Override
    public void evaluateFitness() {
        String genome = getGenome();
        int score = 0;

        // ── 1. BASE SCORE: count active genes (1 bits) ──────────────────
        for (char c : genome.toCharArray()) {
            if (c == '1') score++;
        }

        // ── 2. ALTERNATION BONUS: reward pattern structure ──────────────
        // Each position that differs from previous = structured pattern
        for (int i = 1; i < genome.length(); i++) {
            if (genome.charAt(i) != genome.charAt(i - 1)) {
                score += 1;  // Bonus for alternation
            }
        }

        // ── 3. SIZE PENALTY: shorter code is preferred ────────────────
        // Penalty increases as file size grows beyond 300 characters
        if (fileSize > 300) {
            int excessChars = fileSize - 300;
            int penalty = excessChars / 50;  // -1 for every 50 extra chars
            score = Math.max(1, score - penalty);  // Clamp to minimum of 1
        }

        // ── 4. NORMALIZE: ensure score is reasonable ──────────────────
        // Prevent extremely high scores from dominating
        score = Math.min(score, genome.length() * 4);

        setFitnessScore(score);
    }

    /**
     * Create a copy of this LoadedCodeModule.
     * Polymorphic implementation of Module.copy().
     *
     * @return a new LoadedCodeModule with same genome and properties
     */
    @Override
    public Module copy() {
        return new LoadedCodeModule(
            getName() + "'",
            getGenome(),
            getMutationRate(),
            this.filePath,
            this.displayName,
            this.fileSize,
            this.linesOfCode
        );
    }

    /**
     * Override toString to show source file information.
     * @return detailed string representation including file origin
     */
    @Override
    public String toString() {
        return String.format(
            "[%s | file=%s | LOC=%d | genome=%s | fitness=%d]",
            getName(),
            new java.io.File(filePath).getName(),
            linesOfCode,
            getGenome(),
            getFitnessScore()
        );
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public String getFilePath() { return filePath; }
    public int    getFileSize() { return fileSize; }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public int getLinesOfCode() {
        return linesOfCode;
    }
}
