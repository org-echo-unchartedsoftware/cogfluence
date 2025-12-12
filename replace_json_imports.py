#!/usr/bin/env python3
import os
import re

# Count of files modified
modified_count = 0

for root, dirs, files in os.walk('.'):
    # Skip target directories and git
    if 'target' in root or '.git' in root:
        continue
    
    for file in files:
        if file.endswith('.java'):
            filepath = os.path.join(root, file)
            try:
                with open(filepath, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                original_content = content
                
                # Replace org.json imports with custom implementation
                content = content.replace(
                    'import org.json.JSONObject;',
                    'import software.uncharted.influent.util.json.JSONObject;'
                )
                content = content.replace(
                    'import org.json.JSONArray;',
                    'import software.uncharted.influent.util.json.JSONArray;'
                )
                content = content.replace(
                    'import org.json.JSONException;',
                    'import software.uncharted.influent.util.json.JSONException;'
                )
                
                # Also handle cases where multiple imports are on same line
                content = re.sub(
                    r'import\s+org\.json\.(JSONObject|JSONArray|JSONException);',
                    r'import software.uncharted.influent.util.json.\1;',
                    content
                )
                
                if content != original_content:
                    with open(filepath, 'w', encoding='utf-8') as f:
                        f.write(content)
                    modified_count += 1
                    print(f"Modified: {filepath}")
            except Exception as e:
                print(f"Error processing {filepath}: {e}")

print(f"\nTotal files modified: {modified_count}")
