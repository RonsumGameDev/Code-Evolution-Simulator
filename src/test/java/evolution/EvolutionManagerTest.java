package evolution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvolutionManagerTest {

    private CodeBase codeBase;
    private EvolutionManager evolutionManager;

    @BeforeEach
    void setUp() {
        codeBase = new CodeBase();
        evolutionManager = new EvolutionManager(codeBase);
    }

    @Test
    void testReset() {
        evolutionManager.runGeneration();
        evolutionManager.reset();
        assertEquals(0, evolutionManager.getGenerationCount(), "Generation count should be 0 after reset.");
        assertTrue(evolutionManager.getBestFitnessHistory().isEmpty(), "Best fitness history should be empty after reset.");
        assertTrue(evolutionManager.getAvgFitnessHistory().isEmpty(), "Avg fitness history should be empty after reset.");
    }

    @Test
    void testRunGeneration() {
        codeBase.initialize();
        String result = evolutionManager.runGeneration();
        assertNotNull(result, "runGeneration should return a log string.");
        assertEquals(1, evolutionManager.getGenerationCount(), "Generation count should increment to 1.");
        assertEquals(1, evolutionManager.getBestFitnessHistory().size(), "History should contain 1 entry.");
    }

    @Test
    void testRunGenerations() {
        codeBase.initialize();
        evolutionManager.runGenerations(5);
        assertEquals(5, evolutionManager.getGenerationCount(), "Generation count should be 5 after running 5 generations.");
        assertEquals(5, evolutionManager.getAvgFitnessHistory().size(), "History should contain 5 entries.");
    }
}
