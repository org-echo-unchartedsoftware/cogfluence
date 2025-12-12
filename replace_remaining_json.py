#!/usr/bin/env python3
import os

files_to_update = [
    './influent-server/src/main/java/influent/server/spi/impl/graphml/GraphMLExportDataService.java',
    './influent-server/src/main/java/influent/server/spi/impl/graphml/GraphMLImportDataService.java',
    './tile-service/src/main/java/com/oculusinfo/tile/rest/layer/LayerServiceImpl.java'
]

for filepath in files_to_update:
    if os.path.exists(filepath):
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        content = content.replace(
            'import org.json.XML;',
            'import software.uncharted.influent.util.json.XML;'
        )
        content = content.replace(
            'import org.json.JSONTokener;',
            'import software.uncharted.influent.util.json.JSONTokener;'
        )
        
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        
        print(f"Updated: {filepath}")

print("Done!")
