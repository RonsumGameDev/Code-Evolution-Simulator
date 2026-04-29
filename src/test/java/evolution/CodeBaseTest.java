package evolution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CodeBaseTest {

    private CodeBase codeBase;

    @BeforeEach
    void setUp() {
        codeBase = new CodeBase();
    }

    @Test
    void testInitialize() {
        codeBase.initialize();
        assertEquals(CodeBase.INITIAL_SIZE, codeBase.size(), "CodeBase size should equal INITIAL_SIZE after initialization.");
        assertFalse(codeBase.isEmpty(), "CodeBase should not be empty after initialization.");
    }

    @Test
    void testLoadExternalModules() {
        List<Module> modules = new ArrayList<>();
        modules.add(new BasicModule("TestMod", "10101010", 0.1));
        
        codeBase.loadExternalModules(modules);
        
        assertEquals(1, codeBase.size(), "CodeBase size should be 1 after loading 1 module.");
        assertEquals("TestMod", codeBase.getModules().get(0).getName(), "The loaded module should match the one provided.");
    }

    @Test
    void testLoadExternalModulesThrowsExceptionOnEmpty() {
        List<Module> emptyList = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> {
            codeBase.loadExternalModules(emptyList);
        });
    }

    @Test
    void testRemoveWeakest() {
        codeBase.initialize();
        for (Module m : codeBase.getModules()) {
            m.evaluateFitness();
        }
        int initialSize = codeBase.size();
        codeBase.removeWeakest();
        assertTrue(codeBase.size() < initialSize, "CodeBase size should decrease after removeWeakest.");
    }
}
