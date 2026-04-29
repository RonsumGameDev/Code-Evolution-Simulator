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
    void testLoadExternalModules() {
        List<Module> modules = new ArrayList<>();
        modules.add(new LoadedCodeModule("TestMod", 0.1, "path", "display", 100, 10, "class Test {}"));
        
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
        List<Module> modules = new ArrayList<>();
        modules.add(new LoadedCodeModule("Mod1", 0.1, "path1", "disp1", 100, 10, "class Mod1 {}"));
        modules.add(new LoadedCodeModule("Mod2", 0.1, "path2", "disp2", 100, 10, "class Mod2 {}"));
        modules.add(new LoadedCodeModule("Mod3", 0.1, "path3", "disp3", 100, 10, "class Mod3 {}"));
        codeBase.loadExternalModules(modules);
        
        for (Module m : codeBase.getModules()) {
            m.evaluateFitness();
        }
        int initialSize = codeBase.size();
        codeBase.removeWeakest();
        assertTrue(codeBase.size() < initialSize, "CodeBase size should decrease after removeWeakest.");
    }
}
