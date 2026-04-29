# Developer Guide: CodeBase Loading Implementation

## Architecture Overview

```
┌──────────────────────────────────────────────────────────────┐
│                   Evolution Simulator UI                      │
│              (SimulatorUI with Load Controls)                 │
└──────────────────────┬───────────────────────────────────────┘
                       │
        ┌──────────────┼──────────────┐
        │              │              │
        ▼              ▼              ▼
    FileChooser   CodeLoader    LoadedCodeModule
    (User I/O)   (File → Genome)  (Fitness Eval)
        │              │              │
        └──────────────┼──────────────┘
                       │
                       ▼
                  CodeBase
              (Population Management)
                       │
                       ▼
              EvolutionManager
                (Generation Loop)
```

## Class Hierarchy

```
java.lang.Object
├── CodeLoader (Utility class, all static)
├── Module (Abstract)
│   ├── BasicModule
│   ├── AdvancedModule
│   └── LoadedCodeModule ← NEW
└── CodeBase
    ├── loadExternalModules() ← UPDATED
    └── [existing methods]
```

## Detailed Component Documentation

### CodeLoader Class

**Location**: `src/evolution/CodeLoader.java`

**Visibility**: All public static methods for utility class pattern

**Key Methods**:

```java
public static List<Module> loadFromFolder(File rootDirectory)
    throws IOException, IllegalArgumentException
```
- **Purpose**: Main entry point for folder loading
- **Algorithm**:
  1. Validate directory exists and is readable
  2. Recursively find all .java and .txt files
  3. Read each file's content
  4. Generate genome for each
  5. Create LoadedCodeModule for each
  6. Return list of modules
- **Error Handling**:
  - Throws `IOException` if directory invalid
  - Throws `IllegalArgumentException` if no files found
  - Continues on individual file read failures with warning
- **Performance**: O(n*m) where n=files, m=avg_file_bytes
- **Thread Safety**: Not thread-safe; use SwingWorker in UI

```java
private static List<File> findSupportedFiles(File directory)
```
- **Recursive directory traversal**
- **Supported extensions**: `.java`, `.txt` (case-insensitive)
- **Returns**: Unsorted list of matching files
- **Properties**: Files from subdirectories mixed with root level

```java
public static String generateGenome(String fileContent)
```
- **Algorithm**: XOR-folding compression
- **Input**: File content as string (includes newlines)
- **Output**: Binary string, length = 8 to 32 bits
- **Properties**:
  - Deterministic: Same input = same output always
  - Fast: Linear time complexity
  - Uniform: All files → similar genome lengths
  - Lossy: Cannot reverse to get original file
  
**Genome Generation Details**:
```
Step 1: Convert string to bytes
  "hello" → [104, 101, 108, 108, 111]

Step 2: Target length calculation
  targetLen = min(32, max(8, fileSize / 16))
  Example: 500 bytes → 32 bits target

Step 3: XOR-fold bytes into target length
  for each byte: folded[i % targetLen] ^= byte
  
Step 4: Convert each byte to 4-bit binary
  folded = [156, 42, ...]
  156 = 0b10011100 → lower 4 bits → "1100"
  Result: "1100..." (32+ bits total)
```

**Design Rationale**:
- XOR preserves bit entropy without bias
- Folding compresses without loss of diversity
- Binary representation enables genetic operators
- Uniform length simplifies fitness comparison

### LoadedCodeModule Class

**Location**: `src/evolution/LoadedCodeModule.java`

**Extends**: `Module` (abstract superclass)

**Primary Responsibility**: Implement code-specific fitness evaluation

**Constructors**:
```java
public LoadedCodeModule(String name, String genome, 
                       double mutationRate, String filePath, 
                       int fileSize)
```
- Sets all inherited Module fields
- Stores original file path for reference
- Stores file size for fitness calculation

**Fitness Evaluation**:
```java
public void evaluateFitness()
```
- **Formula Components**:
  1. **Base Score**: Count '1' bits in genome
     - Range: 0 to genome.length()
     - Represents: Code complexity/richness
  
  2. **Alternation Bonus**: +1 for each position where bit differs from previous
     - Range: 0 to genome.length()-1
     - Represents: Code structure/patterns
  
  3. **Size Penalty**: -1 for each 50 bytes over 300 byte mark
     - Range: 0 or negative
     - Represents: Preference for concise code
  
  4. **Normalization**: Cap at genome.length() * 4
     - Range: 1 to MAX
     - Represents: Prevent extreme outliers

- **Example Calculation**:
  ```
  Genome: "10101101" (8 bits)
  File size: 250 bytes
  
  Base: count('1') = 5
  Alternation: 1↔0: 7 transitions = 7
  Size penalty: 250 < 300 = 0
  Total: 5 + 7 + 0 = 12
  Capped: min(12, 8*4) = 12
  ```

- **Edge Cases**:
  - Empty genome: triggers minimum fitness (1)
  - Large files: significant penalty kicks in
  - Highly alternating: rewards well-structured code
  - Uniform genomes: lower fitness (low complexity)

**Polymorphic Methods**:
```java
public Module copy()
```
- Creates new LoadedCodeModule with same properties
- Called during reproduction phase in evolution
- Copies all fields including filePath and fileSize

```java
public String toString()
```
- Overrides Module.toString()
- Shows filename (not full path for brevity)
- Useful for debugging and logging

**Key Design Differences from BasicModule/AdvancedModule**:
| Aspect | BasicModule | AdvancedModule | LoadedCodeModule |
|--------|-------------|---|---|
| Base Score | Count '1' | Count '1' | Count '1' |
| Bonus | None | Alternation | Alternation |
| Penalty | None | None | File size |
| Domain | Abstract | Abstract | Real code |
| Fitness Range | 0-8 | 0-24 | 1-? |

### CodeBase Updates

**New Method**:
```java
public void loadExternalModules(java.util.List<Module> loadedModules)
    throws IllegalArgumentException
```
- Location: Before `randomGenome()` method
- **Purpose**: Replace population with external modules
- **Implementation**:
  - Clear existing modules: `modules.clear()`
  - Add all new modules: `modules.addAll(loadedModules)`
- **Validation**: Throws if list is null or empty
- **Use Case**: Called after CodeLoader.loadFromFolder()
- **Note**: EvolutionManager.reset() NOT called (keeps history)

### SimulatorUI Enhancements

**New Fields** (in declaration section):
```java
private JButton      btnLoadCodeBase;  // Folder loading
private JButton      btnLoadGitHub;    // URL input
private JTextField   tfGitHubUrl;      // URL field
private JLabel       lblLoadedFiles;   // Module count display
```

**Updated Methods**:

1. **initComponents()**
   - Initialize new button/field objects
   - Set colors, fonts, sizes
   - Set initial states (enabled/disabled)

2. **buildControlPanel()**
   - Add "📂 LOAD CODEBASE" section at top
   - Add "📡 GITHUB (OPTIONAL)" section below
   - Add legend item for LoadedCodeModule

3. **wireListeners()**
   - btnLoadCodeBase → loadCodeBaseFromFolder()
   - btnLoadGitHub → handleGitHubLoad()

4. **New Helper Methods**:

   ```java
   private void loadCodeBaseFromFolder()
   ```
   - Opens JFileChooser for directory selection
   - Runs CodeLoader.loadFromFolder() in SwingWorker
   - Updates UI on completion:
     - Updates codeBase reference
     - Creates new EvolutionManager
     - Enables evolution buttons
     - Updates statistics
     - Shows results in log
   
   ```java
   private void handleGitHubLoad()
   ```
   - Validates URL from text field
   - Calls CodeLoader.getGitHubLoadMessage()
   - Shows JOptionPane dialog
   - Logs message to simulation

## Extension Points

### Customize Fitness Function
**Location**: `LoadedCodeModule.evaluateFitness()`

**Example Modification** - Only reward alternating patterns:
```java
@Override
public void evaluateFitness() {
    String genome = getGenome();
    int score = 0;
    
    // Only alternation bonus (ignore base score)
    for (int i = 1; i < genome.length(); i++) {
        if (genome.charAt(i) != genome.charAt(i - 1)) {
            score += 2;  // Bigger bonus
        }
    }
    
    setFitnessScore(score);
}
```

### Customize Genome Generation
**Location**: `CodeLoader.generateGenome()`

**Example Modification** - Length-based genome:
```java
public static String generateGenome(String fileContent) {
    int length = fileContent.length();
    int genomeLength = Math.min(32, Math.max(8, length / 20));
    
    StringBuilder genome = new StringBuilder();
    for (int i = 0; i < genomeLength; i++) {
        // Alternate based on length
        genome.append((i % 2 == 0) ? '1' : '0');
    }
    return genome.toString();
}
```

### Add Real File Format Support
**Location**: `CodeLoader.isSupportedFile()`

**Add Python and C++ support**:
```java
private static final String[] SUPPORTED_EXTENSIONS = {
    ".java", ".txt", ".py", ".cpp", ".c", ".js", ".ts"
};
```
No other changes needed!

### Implement GitHub API Integration
**Location**: `CodeLoader.getGitHubLoadMessage()` → rename and modify

**Pseudocode**:
```java
public static List<Module> loadFromGitHub(String repoUrl)
    throws IOException {
    
    // 1. Parse URL
    String[] parts = repoUrl.split("/");
    String owner = parts[parts.length-2];
    String repo = parts[parts.length-1];
    
    // 2. Clone repository (requires ProcessBuilder)
    Process process = new ProcessBuilder(
        "git", "clone", repoUrl, "/tmp/" + repo).start();
    process.waitFor();
    
    // 3. Load from cloned folder
    File clonedFolder = new File("/tmp/" + repo);
    return loadFromFolder(clonedFolder);
}
```

## Testing Guide

### Unit Tests (if added)

```java
class CodeLoaderTests {
    
    @BeforeEach
    void setup() {
        testFolder = new File("test_codebase");
        testFolder.mkdirs();
    }
    
    @Test
    void testLoadEmptyFolder() {
        assertThrows(IllegalArgumentException.class, 
            () -> CodeLoader.loadFromFolder(testFolder));
    }
    
    @Test
    void testLoadSingleFile() throws Exception {
        // Create sample.java
        File sample = new File(testFolder, "sample.java");
        sample.createNewFile();
        
        List<Module> modules = CodeLoader.loadFromFolder(testFolder);
        assertEquals(1, modules.size());
    }
    
    @Test
    void testGenomeDeterministic() {
        String content = "test code";
        String genome1 = CodeLoader.generateGenome(content);
        String genome2 = CodeLoader.generateGenome(content);
        assertEquals(genome1, genome2);
    }
    
    @Test
    void testLoadedCodeModuleFitness() {
        LoadedCodeModule module = new LoadedCodeModule(
            "test", "10101010", 0.1, "/path", 100);
        module.evaluateFitness();
        assertTrue(module.getFitnessScore() > 0);
    }
}
```

### Integration Tests

```java
// Test complete workflow
void testCompleteLoadAndEvolve() {
    // 1. Load
    List<Module> loaded = CodeLoader.loadFromFolder(sampleFolder);
    
    // 2. Add to CodeBase
    CodeBase cb = new CodeBase();
    cb.loadExternalModules(loaded);
    
    // 3. Evolve
    EvolutionManager em = new EvolutionManager(cb);
    em.runGeneration();
    em.runGeneration();
    
    // 4. Verify
    assertTrue(em.getBestFitness() >= 0);
}
```

## Performance Considerations

### Time Complexity
| Operation | Complexity | Notes |
|-----------|-----------|-------|
| findSupportedFiles() | O(f) | f = total files |
| readFileAsString() | O(b) | b = file bytes |
| generateGenome() | O(b) | XOR all bytes |
| loadFromFolder() | O(f*b) | Process all files |
| evaluateFitness() | O(g) | g = genome length |

### Memory Usage
| Component | Memory | Notes |
|-----------|--------|-------|
| Per file | O(b) | Entire content loaded once |
| Genome | O(32bytes) | Fixed size after generation |
| Module object | ~1KB | Handles + field storage |
| Population | O(n*1KB) | n = num modules |

### Optimization Tips
1. **Lazy Loading**: Don't load file contents if not needed
2. **Streaming**: Process large files in chunks
3. **Caching**: Store previously loaded folders
4. **Parallel**: Use File. listFiles() in parallel stream
5. **Filtering**: Skip non-text binary files early

## Common Bugs & Fixes

### Bug: IOException on Windows paths
**Cause**: Path separator differences
**Fix**: Use `File.separator` instead of hardcoded "/"
```java
String path = folder + File.separator + filename;
```

### Bug: UI freezes during large folder load
**Cause**: File I/O on EDT (Event Dispatch Thread)
**Fix**: Already handled with SwingWorker in code ✓

### Bug: Fitness always same for all modules
**Cause**: Wrong fitness formula implementation
**Debug**: Add logging in evaluateFitness()
```java
System.out.println("Base:" + base + ", Alt:" + alt + ", Penalty:" + penalty);
```

### Bug: Genome not matching expected pattern
**Cause**: XOR-fold produces different results
**Debug**: Print intermediate values in generateGenome()
```java
System.out.println("File bytes: " + Arrays.toString(bytes));
System.out.println("Target length: " + targetLength);
System.out.println("Genome: " + genome);
```

## Code Review Checklist

- [ ] CodeLoader.loadFromFolder handles empty folder?
- [ ] CodeLoader handles unreadable files gracefully?
- [ ] CodeLoader.generateGenome is deterministic?
- [ ] LoadedCodeModule.evaluateFitness returns valid score?
- [ ] LoadedCodeModule.copy creates independent clone?
- [ ] CodeBase.loadExternalModules validates input?
- [ ] SimulatorUI runs loading in SwingWorker?
- [ ] UI updates happen on EDT?
- [ ] Error messages are user-friendly?
- [ ] All new classes properly commented?

## Deployment Checklist

- [ ] All classes compile without warnings
- [ ] Sample codebase folder included
- [ ] Documentation files present (this file, USER_GUIDE.md)
- [ ] Update project README with new feature
- [ ] Test on Windows, Mac, Linux
- [ ] Verify file chooser works on all platforms
- [ ] Test with empty folders and single files

## Future Enhancement Ideas

1. **Semantic Genome Generation**
   - Parse AST instead of raw bytes
   - Genome represents code structure
   - Fitness mirrors actual code quality metrics

2. **Multi-language Support**
   - Add C++, Python, Go, Rust support
   - Language-specific parsing
   - Unified evolutionary framework

3. **Code Metrics Integration**
   - Cyclomatic complexity
   - Lines of code (LOC)
   - Test coverage
   - Code duplication percentage

4. **GitHub Integration**
   - Real API via GitHub4J library
   - Clone and auto-update
   - Track evolution across commits
   - PR suggestions based on evolution

5. **Evolved Code Export**
   - Save best genomes as runnable code
   - Code generation from genome
   - Performance benchmarking

6. **Visual Analysis**
   - 3D fitness landscape visualization
   - Phylogenetic tree of evolution
   - Heatmap of fitness improvements

7. **Machine Learning Integration**
   - Predict fitness without evaluation
   - Learn fitness patterns
   - Guided evolution based on patterns

## References

- **Genetic Algorithms**: Holland, "Adaptation in Natural and Artificial Systems" (1975)
- **XOR Folding**: Hashing technique, see hash function literature
- **Swing UI Patterns**: Oracle Java Swing documentation
- **SwingWorker**: For background threads in Swing
- **File I/O Best Practices**: Java NIO documentation

---

For questions or clarifications, refer to well-commented code sections and in-line documentation.
