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

    private void populateCodeBase() {
        java.util.List<Module> modules = new java.util.ArrayList<>();
        modules.add(new LoadedCodeModule("Mod1", 0.1, "path1", "disp1", 100, 10, "class Mod1 {}"));
        modules.add(new LoadedCodeModule("Mod2", 0.1, "path2", "disp2", 100, 10, "class Mod2 {}"));
        codeBase.loadExternalModules(modules);
    }

    @Test
    void testRunGeneration() {
        populateCodeBase();
        String result = evolutionManager.runGeneration();
        assertNotNull(result, "runGeneration should return a log string.");
        assertEquals(1, evolutionManager.getGenerationCount(), "Generation count should increment to 1.");
        assertEquals(1, evolutionManager.getBestFitnessHistory().size(), "History should contain 1 entry.");
    }

    @Test
    void testRunGenerations() {
        populateCodeBase();
        evolutionManager.runGenerations(5);
        assertEquals(5, evolutionManager.getGenerationCount(), "Generation count should be 5 after running 5 generations.");
        assertEquals(5, evolutionManager.getAvgFitnessHistory().size(), "History should contain 5 entries.");
    }
}
