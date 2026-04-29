# User Guide: External CodeBase Loading Feature

## Quick Start

### Loading a Codebase
1. **Click** the "📂 Load CodeBase" button in the left panel
2. **Select** a folder containing `.java` or `.txt` files
3. **Wait** for the loading to complete (shows in log)
4. **Ready to evolve!** Evolution buttons are now enabled

### What Happens
- Each file becomes a **Module** (digital organism)
- **Genome**: Generated from file content
- **Fitness**: Based on code characteristics
- **Evolution**: Modules compete and improve

## Feature Overview

### Load CodeBase Button (📂)
- Opens file chooser dialog
- Scans folder recursively for Java/text files
- Converts each to evolutionary module
- Shows results in simulation log

### Loaded Files Display
- Shows count of modules: `Loaded Files: 4`
- Updates after successful load
- Indicates population ready for evolution

### GitHub URL Field (📡) [Conceptual]
- Accepts GitHub repository URLs
- Shows integration concept message
- Ready for future production feature
- Example: `https://github.com/torvalds/linux`

## Understanding the Simulation

### How Files Become Organisms

**Transformation Pipeline**:
```
Java/Text File
    ↓
Read Content
    ↓
Generate Genome (binary string)
    ↓
Create LoadedCodeModule
    ↓
Evaluate Initial Fitness
    ↓
Add to Population
```

### Fitness Scoring

Each module's "health" based on:
```
Fitness = Active Genes + Structure Pattern - File Size Penalty

Where:
  Active Genes    = Number of '1' bits (represents complexity)
  Structure       = Alternating patterns (indicates organization)
  File Size       = Penalty for very large code (>300 chars)
```

**Example**:
```
File: Calculator.java (450 bytes)
  → Genome: "10101101010110..."
  → Active genes: 12 points
  → Alternation: 8 points  
  → Size penalty: -3 points
  → Fitness: 17
```

## Workflow Examples

### Example 1: Simple Evolution
```
Step 1: Click "Load CodeBase"
        → Select sample_codebase/ folder
        → 4 files loaded (StringUtils, ArrayUtils, Calculator, DESIGN.txt)

Step 2: Click "▶ Run One Generation"
        → Modules evaluated for fitness
        → Bottom 30% removed (natural selection)
        → Top 30% duplicated (reproduction)
        → Random mutations applied
        → Fitness recalculated

Step 3: Repeat Step 2
        → Watch bar graph update
        → Line graph shows fitness trend
        → Statistics update in real-time

Step 4: Run Multiple Generations
        → Set spinner to "10"
        → Click "⏩ Run Multiple Gens"
        → Processing happens in background
        → UI stays responsive
```

### Example 2: Compare Random vs Loaded
```
Comparison A - Random Generation:
  Initialize CodeBase
    → 10 random modules created
    → Fitness: 1-8 points range

Comparison B - Loaded Codebase:
  Load CodeBase
    → Real files converted to modules
    → Fitness: 5-25 points range
    → More variation due to real code

Observation:
  → Real code shows different evolutionary patterns
  → Fitness converges faster with richer variation
```

## Common Tasks

### View a Module's Details
1. Check simulation log for module information
2. Look for entries like: `[StringUtils | genome=1010110101 | fitness=15]`
3. Bar graph shows visual fitness representation

### Experiment with Different Codebases
1. Create folders with different code types:
   - Simple scripts (10-50 lines)
   - Complex libraries (100+ lines)  
   - Documentation files
2. Load each separately
3. Compare evolutionary patterns

### Find Best Performing Code
1. Turn on detailed logging
2. Run 100 generations
3. Check line graph for fitness trends
4. Loaded modules with high fitness = "well-designed" in simulation

## Tips & Tricks

### Maximize Diversity
- Load folders with mixed file types (.java + .txt)
- Different file sizes create varied genomes
- More diversity = more interesting evolution

### Speed Up Processing
- Small folders (< 50 files) load instantly
- Large projects may take 5-10 seconds
- Loading happens in background (UI stays responsive)

### Compare Strategies
```
Strategy 1: Load Real Code
  Pros: Realistic patterns, interesting evolution
  Cons: Less predictable results

Strategy 2: Random Generation  
  Pros: Pure genetic algorithm demonstration
  Cons: Abstract, less engaging

Strategy 3: Hybrid
  Load folder → Run 10 gens → Reset → Initialize Random → Run 10 gens
  (Compare side-by-side)
```

### Debug Module Fitness
1. Load a single-file folder
2. Run one generation
3. Check log for detailed fitness breakdown
4. Understand what drives fitness for that code

## Troubleshooting

### "No .java or .txt files found"
- **Cause**: Selected folder has no supported files
- **Solution**: Place `.java` or `.txt` files in folder
- **Note**: Searches subdirectories recursively

### Loading Takes Long Time
- **Cause**: Folder has many files (1000+)
- **Solution**: Wait for completion (it's running)
- **Alternative**: Use smaller subset of files

### Modules Disappear After Loading
- **Cause**: You clicked Reset button after loading
- **Solution**: Click "Load CodeBase" again
- **Remember**: Reset clears the population

### GitHub Button Shows Message
- **Cause**: Real API integration not yet implemented
- **Solution**: Use "Load CodeBase" for now
- **Future**: GitHub feature coming in v2.0

## Conceptual Understanding

### What the Simulation Represents

This is a **CONCEPTUAL** model where:
- 🧬 **Genome** = Abstract code representation (not AST or semantics)
- 💪 **Fitness** = Simplified code quality metrics
- 🔄 **Evolution** = How algorithms find "optimal" design

### What It DOESN'T Do

This is NOT:
- ❌ Actual code transformation
- ❌ Semantic analysis of code
- ❌ Real bug detection or fixing
- ❌ Production-grade refactoring tool

### Educational Value

This simulation teaches:
- ✅ How genetic algorithms work in theory
- ✅ Population dynamics over time
- ✅ Selection pressure and adaptation
- ✅ Mutation and inheritance mechanics
- ✅ How evolution finds local optima

## Advanced Features

### Custom Fitness Evaluation
The LoadedCodeModule fitness formula:
- File size matters: Shorter code is preferred (simplicity)
- Pattern matters: Regular patterns rewarded (structure)
- Content matters: '1' bits represent complexity

### Genome Understanding
Files → Genomes via:
```
1. Read file bytes
2. XOR-fold to uniform length
3. Convert to binary
4. Result: 32-128 bit genome
```

Each byte contributes equally → deterministic → reproducible

## Integration with Evolution Simulator

### Compatibility
- Loaded modules work with all existing evolution operators:
  - ✅ Fitness evaluation (specialized for code)
  - ✅ Mutation (random bit flipping)
  - ✅ Selection (top 30% survive)
  - ✅ Reproduction (copies with variations)

### Graph Display
- **Bar Graph**: Shows fitness of each loaded module
- **Line Graph**: Tracks average + best fitness over time
- **Both update** in real-time as evolution progresses

### Statistics
- Generation count
- Population size
- Best fitness
- Average fitness

All these adapt to loaded codebases automatically!

## Next Steps

1. **Try it out**: Load the sample_codebase/ folder
2. **Run generations**: Watch modules evolve
3. **Experiment**: Try different folders
4. **Analyze**: Understand what drives fitness
5. **Customize**: Modify LoadedCodeModule fitness function

## Feedback & Ideas

If you want to extend this feature:
- Real GitHub API integration
- Advanced genome generation (AST-based)
- Code metrics integration
- Export evolved genomes
- Version history tracking

The foundation is ready for these enhancements!
