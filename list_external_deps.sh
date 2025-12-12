#!/bin/bash
echo "=== External Dependencies Analysis ==="
echo ""
echo "Analyzing pom.xml files for external dependencies..."
echo ""

# Find all external dependencies (not internal project dependencies)
find . -name "pom.xml" -type f | while read pom; do
    echo "File: $pom"
    grep -A 2 "<dependency>" "$pom" | grep -E "(groupId|artifactId|version)" | grep -v "project.groupId" | grep -v "project.parent.groupId" | head -20
    echo ""
done
