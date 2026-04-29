# Capstone Project Diagrams

This document contains the UML Class Diagram and Database Schema Diagram for the Evolution Simulator project.

## UML Class Diagram

This class diagram outlines the object-oriented structure of the simulation, showing how the GUI (`SimulatorUI`) manages the state (`EvolutionManager`, `CodeBase`) which in turn contains the individual organisms (`Module`).

```mermaid
classDiagram
    class Main {
        +main(args: String[]) void
    }
    
    class SimulatorUI {
        -codeBase: CodeBase
        -evolutionManager: EvolutionManager
        -dbManager: DatabaseManager
        +SimulatorUI()
        -initComponents() void
        -layoutComponents() void
        -wireListeners() void
        -updateGraphs() void
        -updateStats() void
        -saveGenerationToDB() void
    }

    class DatabaseManager {
        -DB_URL: String
        +DatabaseManager()
        -initializeDatabase() void
        +getConnection() Connection
        +insertGenerationStats(generation: int, population: int, bestFitness: int, avgFitness: double, totalLoc: int) void
        +clearStats() void
        +getSavedGenerationsCount() int
    }

    class CodeBase {
        -modules: List~Module~
        +initialize() void
        +loadExternalModules(modules: List~Module~) void
        +removeWeakest() void
        +reproduceStrongest() void
        +getTotalLinesOfCode() int
        +getAverageLinesOfCode() double
        +getModules() List~Module~
    }

    class EvolutionManager {
        -codeBase: CodeBase
        -generationCount: int
        -bestFitnessHistory: List~Integer~
        -avgFitnessHistory: List~Double~
        +EvolutionManager(codeBase: CodeBase)
        +reset() void
        +runGeneration() String
        +runGenerations(n: int) String
    }

    class Module {
        <<abstract>>
        #name: String
        #genome: String
        #fitnessScore: int
        +evaluateFitness() void
        +copy() Module
        +mutate() void
    }

    class BasicModule {
        +evaluateFitness() void
        +copy() Module
    }

    class AdvancedModule {
        +evaluateFitness() void
        +copy() Module
    }
    
    class LoadedCodeModule {
        -linesOfCode: int
        -fileContent: String
        +evaluateFitness() void
        +copy() Module
    }

    Main --> SimulatorUI : launches
    SimulatorUI --> CodeBase : has-a
    SimulatorUI --> EvolutionManager : has-a
    SimulatorUI --> DatabaseManager : uses
    EvolutionManager --> CodeBase : manipulates
    CodeBase *-- Module : contains
    Module <|-- BasicModule : extends
    Module <|-- AdvancedModule : extends
    Module <|-- LoadedCodeModule : extends
```

## Database Schema Diagram

This diagram visualizes the SQLite database table used for persisting generation statistics.

```mermaid
erDiagram
    EVOLUTION_STATS {
        INTEGER id PK "AUTOINCREMENT"
        INTEGER generation "NOT NULL"
        INTEGER population "NOT NULL"
        INTEGER best_fitness "NOT NULL"
        REAL avg_fitness "NOT NULL"
        INTEGER total_loc "NOT NULL"
    }
```
