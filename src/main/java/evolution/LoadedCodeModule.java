package evolution;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import java.util.List;

/**
 * LOADEDCODEMODULE — represents a loaded external code file as a Module.
 * Now performs ACTUAL codebase evolution via JavaParser AST operations.
 */
public class LoadedCodeModule extends Module {

    private String filePath;
    private String displayName;
    private int    fileSize;
    private int    linesOfCode;
    private String fileContent;

    public LoadedCodeModule(String name, double mutationRate,
                            String filePath, String displayName,
                            int fileSize, int linesOfCode, String fileContent) {
        super(name, mutationRate);
        this.filePath = filePath;
        this.displayName = displayName;
        this.fileSize = fileSize;
        this.linesOfCode = linesOfCode;
        this.fileContent = fileContent;
    }

    /**
     * FITNESS RULE (1A: Static Analysis & Maintainability):
     * - Base score starts high.
     * - Subtract points for complexity (if, for, while).
     * - Subtract points for length (LOC).
     * - Add points for having modular methods.
     */
    @Override
    public void evaluateFitness() {
        if (!filePath.endsWith(".java")) {
            setFitnessScore(Math.max(1, 100 - (linesOfCode / 10)));
            return;
        }
        try {
            CompilationUnit cu = StaticJavaParser.parse(fileContent);
            int complexity = 0;
            int methodCount = cu.findAll(MethodDeclaration.class).size();
            
            complexity += cu.findAll(IfStmt.class).size();
            complexity += cu.findAll(ForStmt.class).size();
            complexity += cu.findAll(WhileStmt.class).size();
            
            int score = 1000 - (complexity * 15) - (linesOfCode * 2) + (methodCount * 10);
            setFitnessScore(Math.max(1, score));
        } catch (Exception e) {
            // Syntax error penalty
            setFitnessScore(1);
        }
    }

    /**
     * MUTATION RULE (2C: Refactoring):
     * - Safely renames random variables via AST.
     */
    @Override
    public void mutate() {
        if (!filePath.endsWith(".java")) return;
        try {
            CompilationUnit cu = StaticJavaParser.parse(fileContent);
            
            List<VariableDeclarator> vars = cu.findAll(VariableDeclarator.class);
            if (!vars.isEmpty() && RNG.nextDouble() < getMutationRate()) {
                VariableDeclarator var = vars.get(RNG.nextInt(vars.size()));
                var.setName(var.getNameAsString() + "_mut" + RNG.nextInt(10));
            }
            
            this.fileContent = cu.toString();
            this.linesOfCode = this.fileContent.split("\n").length;
            this.fileSize = this.fileContent.length();
        } catch (Exception e) {
            // Can't mutate broken AST
        }
    }

    /**
     * REPRODUCTION RULE (3B: Crossover):
     * - Swaps method bodies between two fit modules to create an offspring.
     */
    public LoadedCodeModule crossover(LoadedCodeModule other) {
        if (!filePath.endsWith(".java") || !other.filePath.endsWith(".java")) {
            return (LoadedCodeModule) this.copy();
        }
        try {
            CompilationUnit cuThis = StaticJavaParser.parse(this.fileContent);
            CompilationUnit cuOther = StaticJavaParser.parse(other.fileContent);
            
            List<MethodDeclaration> methodsThis = cuThis.findAll(MethodDeclaration.class);
            List<MethodDeclaration> methodsOther = cuOther.findAll(MethodDeclaration.class);
            
            if (!methodsThis.isEmpty() && !methodsOther.isEmpty()) {
                MethodDeclaration mThis = methodsThis.get(RNG.nextInt(methodsThis.size()));
                MethodDeclaration mOther = methodsOther.get(RNG.nextInt(methodsOther.size()));
                
                mThis.setBody(mOther.getBody().orElse(null));
            }
            
            String childContent = cuThis.toString();
            return new LoadedCodeModule(
                getName() + "_child",
                getMutationRate(),
                this.filePath,
                this.displayName,
                childContent.length(),
                childContent.split("\n").length,
                childContent
            );
        } catch (Exception e) {
            return (LoadedCodeModule) this.copy();
        }
    }

    @Override
    public Module copy() {
        return new LoadedCodeModule(
            getName() + "'",
            getMutationRate(),
            this.filePath,
            this.displayName,
            this.fileSize,
            this.linesOfCode,
            this.fileContent
        );
    }

    @Override
    public String toString() {
        return String.format(
            "[%s | file=%s | LOC=%d | fitness=%d]",
            getName(),
            new java.io.File(filePath).getName(),
            linesOfCode,
            getFitnessScore()
        );
    }

    public String getFilePath() { return filePath; }
    public int    getFileSize() { return fileSize; }
    public String getDisplayName() { return displayName; }
    @Override
    public int getLinesOfCode() { return linesOfCode; }
    public String getFileContent() { return fileContent; }
}
