#!/bin/bash
echo "Building the application..."
export PATH=$(pwd)/apache-maven-3.9.6/bin:$PATH
mvn clean package
echo "Copying the fat JAR..."
cp target/evolution-simulator-1.0.0-SNAPSHOT.jar EvolutionSimulator.jar
echo "Build complete! You can now run ./run.sh"
