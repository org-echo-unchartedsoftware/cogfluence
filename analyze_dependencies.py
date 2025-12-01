#!/usr/bin/env python3
"""
Analyze Maven dependencies across all pom.xml files in the project
"""
import os
import xml.etree.ElementTree as ET
from collections import defaultdict
import json

def parse_pom(pom_path):
    """Parse a pom.xml file and extract dependencies"""
    try:
        tree = ET.parse(pom_path)
        root = tree.getroot()
        
        # Maven namespace
        ns = {'mvn': 'http://maven.apache.org/POM/4.0.0'}
        
        dependencies = []
        
        # Find all dependencies
        for dep in root.findall('.//mvn:dependency', ns):
            group_id = dep.find('mvn:groupId', ns)
            artifact_id = dep.find('mvn:artifactId', ns)
            version = dep.find('mvn:version', ns)
            scope = dep.find('mvn:scope', ns)
            
            if group_id is not None and artifact_id is not None:
                dep_info = {
                    'groupId': group_id.text,
                    'artifactId': artifact_id.text,
                    'version': version.text if version is not None else 'managed',
                    'scope': scope.text if scope is not None else 'compile',
                    'pom': pom_path
                }
                dependencies.append(dep_info)
        
        return dependencies
    except Exception as e:
        print(f"Error parsing {pom_path}: {e}")
        return []

def main():
    # Find all pom.xml files
    pom_files = []
    for root, dirs, files in os.walk('.'):
        for file in files:
            if file == 'pom.xml':
                pom_files.append(os.path.join(root, file))
    
    print(f"Found {len(pom_files)} pom.xml files")
    
    # Parse all dependencies
    all_deps = []
    for pom in pom_files:
        deps = parse_pom(pom)
        all_deps.extend(deps)
    
    # Group by artifact
    dep_groups = defaultdict(list)
    for dep in all_deps:
        key = f"{dep['groupId']}:{dep['artifactId']}"
        dep_groups[key].append(dep)
    
    # Create summary
    summary = {
        'total_pom_files': len(pom_files),
        'total_dependencies': len(all_deps),
        'unique_dependencies': len(dep_groups),
        'dependencies': {}
    }
    
    # Categorize dependencies
    categories = {
        'logging': ['slf4j', 'log4j', 'logback'],
        'testing': ['junit', 'mockito', 'hamcrest'],
        'json': ['jackson', 'gson', 'json'],
        'guice': ['guice', 'inject'],
        'servlet': ['servlet', 'jakarta.servlet', 'javax.servlet'],
        'restlet': ['restlet'],
        'commons': ['commons-'],
        'guava': ['guava'],
        'xml': ['xml', 'jaxb', 'woodstox'],
        'cache': ['ehcache'],
        'other': []
    }
    
    categorized = defaultdict(list)
    
    for key, deps in sorted(dep_groups.items()):
        dep_info = {
            'key': key,
            'groupId': deps[0]['groupId'],
            'artifactId': deps[0]['artifactId'],
            'versions': list(set([d['version'] for d in deps])),
            'scopes': list(set([d['scope'] for d in deps])),
            'usage_count': len(deps),
            'used_in': list(set([d['pom'] for d in deps]))
        }
        
        # Categorize
        categorized_flag = False
        for cat, keywords in categories.items():
            if cat == 'other':
                continue
            for keyword in keywords:
                if keyword.lower() in key.lower():
                    categorized[cat].append(dep_info)
                    categorized_flag = True
                    break
            if categorized_flag:
                break
        
        if not categorized_flag:
            categorized['other'].append(dep_info)
        
        summary['dependencies'][key] = dep_info
    
    # Print summary
    print("\n" + "="*80)
    print("DEPENDENCY ANALYSIS SUMMARY")
    print("="*80)
    print(f"Total POM files: {summary['total_pom_files']}")
    print(f"Total dependency declarations: {summary['total_dependencies']}")
    print(f"Unique dependencies: {summary['unique_dependencies']}")
    print("\n" + "="*80)
    print("DEPENDENCIES BY CATEGORY")
    print("="*80)
    
    for cat in sorted(categorized.keys()):
        deps = categorized[cat]
        print(f"\n{cat.upper()} ({len(deps)} dependencies):")
        print("-" * 80)
        for dep in sorted(deps, key=lambda x: x['key']):
            print(f"  {dep['key']}")
            print(f"    Versions: {', '.join(dep['versions'])}")
            print(f"    Scopes: {', '.join(dep['scopes'])}")
            print(f"    Used in {dep['usage_count']} module(s)")
    
    # Save detailed report
    with open('dependency-analysis.json', 'w') as f:
        json.dump(summary, f, indent=2)
    
    print("\n" + "="*80)
    print("Detailed report saved to: dependency-analysis.json")
    print("="*80)
    
    # Identify replaceable dependencies
    print("\n" + "="*80)
    print("POTENTIALLY REPLACEABLE DEPENDENCIES")
    print("="*80)
    print("\nThe following dependencies could potentially be replaced with custom implementations:")
    
    replaceable = {
        'commons-codec': 'Base64, URL encoding, hex encoding',
        'commons-io': 'File utilities, stream utilities',
        'commons-lang3': 'String utilities, array utilities',
        'guava': 'Collections, caching, primitives (partial)',
    }
    
    for key, deps_list in dep_groups.items():
        for replaceable_key, description in replaceable.items():
            if replaceable_key in key.lower():
                print(f"\n  {key}")
                print(f"    Functions: {description}")
                print(f"    Used in {len(deps_list)} module(s)")
                break

if __name__ == '__main__':
    main()
