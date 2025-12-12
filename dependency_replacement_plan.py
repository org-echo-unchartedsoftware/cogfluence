#!/usr/bin/env python3
"""
Analyze dependencies and create a plan for replacement/removal
"""
import os
import re
from collections import defaultdict

print("=== Dependency Replacement Analysis ===\n")

# Check for org.json usage
json_files = []
for root, dirs, files in os.walk('.'):
    if 'target' in root or '.git' in root:
        continue
    for file in files:
        if file.endswith('.java'):
            filepath = os.path.join(root, file)
            try:
                with open(filepath, 'r', encoding='utf-8', errors='ignore') as f:
                    content = f.read()
                    if 'import org.json.' in content:
                        json_files.append(filepath)
            except:
                pass

print(f"1. org.json library:")
print(f"   - Used in {len(json_files)} files")
print(f"   - Strategy: Replace with custom lightweight JSON implementation")
print(f"   - Feasibility: MEDIUM - will require creating JSON parser/writer\n")

# Check for commons-codec usage
codec_files = []
for root, dirs, files in os.walk('.'):
    if 'target' in root or '.git' in root:
        continue
    for file in files:
        if file.endswith('.java'):
            filepath = os.path.join(root, file)
            try:
                with open(filepath, 'r', encoding='utf-8', errors='ignore') as f:
                    content = f.read()
                    if 'org.apache.commons.codec' in content:
                        codec_files.append(filepath)
            except:
                pass

print(f"2. commons-codec library:")
print(f"   - Used in {len(codec_files)} files")
print(f"   - Strategy: Replace with Java standard Base64 (java.util.Base64)")
print(f"   - Feasibility: HIGH - Java 8+ has built-in Base64\n")

# Check for commons-io usage
io_files = []
for root, dirs, files in os.walk('.'):
    if 'target' in root or '.git' in root:
        continue
    for file in files:
        if file.endswith('.java'):
            filepath = os.path.join(root, file)
            try:
                with open(filepath, 'r', encoding='utf-8', errors='ignore') as f:
                    content = f.read()
                    if 'org.apache.commons.io' in content:
                        io_files.append(filepath)
            except:
                pass

print(f"3. commons-io library:")
print(f"   - Used in {len(io_files)} files")
print(f"   - Strategy: Replace with custom utility methods")
print(f"   - Feasibility: MEDIUM - need to implement file utilities\n")

# Check for commons-lang usage
lang_files = []
for root, dirs, files in os.walk('.'):
    if 'target' in root or '.git' in root:
        continue
    for file in files:
        if file.endswith('.java'):
            filepath = os.path.join(root, file)
            try:
                with open(filepath, 'r', encoding='utf-8', errors='ignore') as f:
                    content = f.read()
                    if 'org.apache.commons.lang' in content:
                        lang_files.append(filepath)
            except:
                pass

print(f"4. commons-lang library:")
print(f"   - Used in {len(lang_files)} files")
print(f"   - Strategy: Replace with custom string/array utilities")
print(f"   - Feasibility: MEDIUM - need to implement string utilities\n")

print("\n=== Recommended Approach ===")
print("1. Start with commons-codec (easiest - use Java 8 Base64)")
print("2. Create custom JSON implementation to replace org.json")
print("3. Create utility classes to replace commons-io and commons-lang")
print("4. Keep framework dependencies (Guice, Restlet, SLF4J) - too deeply integrated")
print("5. Test each replacement incrementally")

