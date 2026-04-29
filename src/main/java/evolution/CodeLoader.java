package evolution;

import java.io.*;
import java.util.*;

/**
 * CODELOADER — Handles loading external codebases from the file system.
 *
 * Purpose:
 *   - Read .java and .txt files from a selected directory
 *   - Convert file content into Module objects with genomes
 *   - Support error handling for invalid directories
 *
 * Design:
 *   - Files are recursively scanned from a root directory
 *   - Each file becomes a LoadedCodeModule with a genome derived from content
 *   - Simple, deterministic genome conversion (no external dependencies)
 *
 * OOP: Utility class pattern — encapsulates file I/O and conversion logic.
 */
public class CodeLoader {

    // ── Constants ──────────────────────────────────────────────────────────
    private static final double MUTATION_RATE = 0.1;  // 10% mutation per bit
    private static final int MIN_FILE_SIZE = 10;  // Minimum bytes to process
    private static final String[] SUPPORTED_EXTENSIONS = {
        ".java", ".py", ".js", ".ts", ".cpp", ".c", ".h", ".cs", ".go", ".rb", ".php", ".rs", ".swift", ".kt", ".sh"
    };

    /**
     * Load all supported files from a directory recursively.
     *
     * @param rootDirectory the folder to scan
     * @return List of LoadedCodeModule objects (one per file)
     * @throws IOException if directory doesn't exist or is not readable
     * @throws IllegalArgumentException if directory is empty
     */
    public static List<Module> loadFromFolder(File rootDirectory)
            throws IOException, IllegalArgumentException {

        if (!rootDirectory.exists()) {
            throw new IOException("Directory does not exist: " + rootDirectory.getAbsolutePath());
        }
        if (!rootDirectory.isDirectory()) {
            throw new IOException("Path is not a directory: " + rootDirectory.getAbsolutePath());
        }

        List<Module> modules = new ArrayList<>();
        List<File> supportedFiles = findSupportedFiles(rootDirectory);

        if (supportedFiles.isEmpty()) {
            throw new IllegalArgumentException(
                "No supported code files found in: " + rootDirectory.getAbsolutePath());
        }

        // Convert each file to a Module
        for (File file : supportedFiles) {
            try {
                String fileContent = readFileAsString(file);
                String genome = generateGenome(fileContent);
                String relativePath = rootDirectory.toPath().relativize(file.toPath()).toString();
                String moduleName = relativePath.replace(File.separatorChar, '_').replace('.', '_');
                int linesOfCode = countLines(fileContent);

                Module module = new LoadedCodeModule(
                    moduleName,
                    genome,
                    MUTATION_RATE,
                    file.getAbsolutePath(),
                    relativePath,
                    fileContent.length(),
                    linesOfCode
                );
                modules.add(module);
            } catch (IOException e) {
                System.err.println("⚠ Failed to load file: " + file.getAbsolutePath());
                System.err.println("  Reason: " + e.getMessage());
                // Continue with other files
            }
        }

        return modules;
    }

    /**
     * Recursively find all .java and .txt files in directory.
     * @param directory root folder to search
     * @return List of File objects matching supported extensions
     */
    private static List<File> findSupportedFiles(File directory) {
        List<File> results = new ArrayList<>();
        File[] files = directory.listFiles();

        if (files == null) {
            return results; // Empty directory
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // Recursively search subdirectories
                results.addAll(findSupportedFiles(file));
            } else if (isSupportedFile(file)) {
                results.add(file);
            }
        }
        return results;
    }

    /**
     * Check if a file has a supported extension.
     * @param file the file to check
     * @return true if file extension is in SUPPORTED_EXTENSIONS
     */
    private static boolean isSupportedFile(File file) {
        String name = file.getName().toLowerCase();
        for (String ext : SUPPORTED_EXTENSIONS) {
            if (name.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Read entire file content as a String.
     * @param file the file to read
     * @return file contents as string
     * @throws IOException if file cannot be read
     */
    private static String readFileAsString(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    /**
     * Count lines in file content for LOC metrics.
     * @param content the text content of a file
     * @return number of lines
     */
    private static int countLines(String content) {
        if (content == null || content.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == '\n') {
                count++;
            }
        }
        return count;
    }

    /**
     * Convert file content into a binary genome string.
     *
     * Approach:
     *   1. Use file length as basis (shorter files = fewer genes)
     *   2. Convert each byte to binary (8 bits)
     *   3. XOR fold to compress into reasonable size
     *   4. Result: deterministic, repeatable genome from any file
     *
     * Example: file with 100 bytes might produce 64-bit genome
     *
     * @param fileContent the file's text content
     * @return binary string genome (e.g., "1010110101")
     */
    public static String generateGenome(String fileContent) {
        if (fileContent == null || fileContent.isEmpty()) {
            return generateDefaultGenome();
        }

        // Convert content to bytes and get hash-like values
        byte[] bytes = fileContent.getBytes();
        int length = Math.max(MIN_FILE_SIZE, bytes.length);

        // Target genome length: proportional to file size, capped at 32 bits
        int targetLength = Math.min(32, Math.max(8, length / 16));

        // XOR fold: compress bytes into target length
        byte[] folded = new byte[targetLength];
        for (int i = 0; i < bytes.length; i++) {
            folded[i % targetLength] ^= bytes[i];
        }

        // Convert bytes to binary string
        StringBuilder genome = new StringBuilder();
        for (byte b : folded) {
            // Use only lower 4 bits for simpler patterns
            for (int i = 3; i >= 0; i--) {
                genome.append((b >> i) & 1);
            }
        }

        return genome.toString();
    }

    /**
     * Generate a default genome for empty files.
     * @return simple binary string
     */
    private static String generateDefaultGenome() {
        return "00000000"; // 8 bits of zeros
    }

    /**
     * Simulate GitHub URL loading (conceptual).
     * Returns a mock message since real API integration is out of scope.
     *
     * @param githubUrl the GitHub repository URL
     * @return information message
     */
    public static String getGitHubLoadMessage(String githubUrl) {
        if (githubUrl == null || githubUrl.trim().isEmpty()) {
            return "⚠ Please enter a valid GitHub URL.";
        }

        return String.format(
            "📡 GitHub Integration (Conceptual):\n" +
            "   URL: %s\n" +
            "   Status: In production version, this would:\n" +
            "     • Clone repository\n" +
            "     • Extract .java files\n" +
            "     • Create modules from source\n" +
            "   For now: Use 'Load CodeBase' to load a local folder.\n",
            githubUrl
        );
    }
}
