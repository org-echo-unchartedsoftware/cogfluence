import os
import re
from collections import defaultdict

# Dependencies that could potentially be replaced with simple implementations
replaceable_deps = {
    'commons-codec': ['Base64', 'Hex encoding/decoding'],
    'commons-io': ['File operations', 'Stream utilities'],
    'commons-lang': ['String utilities', 'Array utilities'],
    'joda-time': ['Date/Time operations - can use Java 8+ time API'],
}

# Scan for imports
import_counts = defaultdict(int)

for root, dirs, files in os.walk('.'):
    # Skip target directories
    if 'target' in root or '.git' in root:
        continue
    
    for file in files:
        if file.endswith('.java'):
            filepath = os.path.join(root, file)
            try:
                with open(filepath, 'r', encoding='utf-8', errors='ignore') as f:
                    content = f.read()
                    
                    # Check for specific imports
                    if 'org.apache.commons.codec' in content:
                        import_counts['commons-codec'] += 1
                    if 'org.apache.commons.io' in content:
                        import_counts['commons-io'] += 1
                    if 'org.apache.commons.lang' in content:
                        import_counts['commons-lang'] += 1
                    if 'org.joda.time' in content:
                        import_counts['joda-time'] += 1
                    if 'com.google.common' in content:
                        import_counts['guava'] += 1
                    if 'org.json' in content:
                        import_counts['org.json'] += 1
            except:
                pass

print("=== Dependency Usage Analysis ===\n")
for dep, count in sorted(import_counts.items(), key=lambda x: x[1]):
    print(f"{dep}: used in {count} files")
    if dep in replaceable_deps:
        print(f"  -> Could replace: {', '.join(replaceable_deps[dep])}")
    print()
