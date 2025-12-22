#!/usr/bin/env python3
"""
Analyze dependencies and categorize them for potential replacement with in-repo implementations.
"""
import json
import os

# Dependencies that can be replaced with simple implementations
REPLACEABLE_DEPS = {
    # JSON processing - can be replaced with built-in org.json or custom implementation
    "com.fasterxml.jackson.core:jackson-core": {
        "complexity": "medium",
        "reason": "JSON parsing - can use org.json or custom implementation",
        "priority": "medium"
    },
    "com.fasterxml.jackson.core:jackson-databind": {
        "complexity": "high",
        "reason": "Object mapping - complex but replaceable with custom serialization",
        "priority": "low"
    },
    "com.fasterxml.jackson.core:jackson-annotations": {
        "complexity": "low",
        "reason": "Annotations - can be replaced with custom annotations",
        "priority": "high"
    },
    
    # Apache Commons - utility libraries that can be reimplemented
    "commons-io:commons-io": {
        "complexity": "low",
        "reason": "File I/O utilities - simple to reimplement",
        "priority": "high"
    },
    "commons-lang:commons-lang": {
        "complexity": "low",
        "reason": "String utilities - simple to reimplement",
        "priority": "high"
    },
    "org.apache.commons:commons-lang3": {
        "complexity": "low",
        "reason": "String utilities - simple to reimplement",
        "priority": "high"
    },
    "commons-logging:commons-logging": {
        "complexity": "low",
        "reason": "Logging facade - can use SLF4J or custom implementation",
        "priority": "medium"
    },
    
    # Date/Time libraries
    "joda-time:joda-time": {
        "complexity": "low",
        "reason": "Date/time utilities - Java 8+ has java.time package",
        "priority": "high"
    },
    
    # XML processing
    "com.fasterxml.woodstox:woodstox-core": {
        "complexity": "medium",
        "reason": "XML processing - can use built-in javax.xml",
        "priority": "medium"
    },
    "xalan:xalan": {
        "complexity": "medium",
        "reason": "XSLT processor - can use built-in javax.xml.transform",
        "priority": "medium"
    },
    "xerces:xercesImpl": {
        "complexity": "medium",
        "reason": "XML parser - can use built-in javax.xml.parsers",
        "priority": "medium"
    },
}

# Dependencies that are too complex or critical to replace
KEEP_DEPS = {
    # Dependency injection frameworks
    "com.google.inject:guice",
    "com.google.inject.extensions:guice-assistedinject",
    "com.google.inject.extensions:guice-multibindings",
    "com.google.inject.extensions:guice-servlet",
    
    # Web frameworks and servers
    "org.eclipse.jetty:jetty-server",
    "org.restlet:org.restlet",
    "org.restlet:org.restlet.ext.servlet",
    "org.restlet:org.restlet.ext.jackson",
    "org.restlet:org.restlet.ext.json",
    
    # Big data frameworks
    "org.apache.spark:spark-core_2.12",
    "org.apache.hadoop:hadoop-client",
    "org.apache.hadoop:hadoop-core",
    
    # Database drivers
    "com.mysql:mysql-connector-j",
    "net.sourceforge.jtds:jtds",
    "org.hsqldb:hsqldb",
    
    # Security frameworks
    "org.apache.shiro:shiro-core",
    "org.apache.shiro:shiro-guice",
    "org.apache.shiro:shiro-web",
    
    # Testing frameworks
    "junit:junit",
    "org.jmock:jmock",
    "org.seleniumhq.selenium:selenium-java",
    
    # Logging frameworks
    "org.slf4j:slf4j-api",
    "org.apache.logging.log4j:log4j-core",
    "org.apache.logging.log4j:log4j-api",
    
    # Search engines
    "org.apache.solr:solr-solrj",
}

def main():
    # Read the dependency analysis
    with open('external_deps_analysis.txt', 'r') as f:
        content = f.read()
    
    print("=" * 80)
    print("DEPENDENCY REPLACEMENT ANALYSIS")
    print("=" * 80)
    print()
    
    print("HIGH PRIORITY REPLACEMENTS (Simple implementations):")
    print("-" * 80)
    for dep, info in sorted(REPLACEABLE_DEPS.items(), key=lambda x: (x[1]['priority'], x[0])):
        if info['priority'] == 'high':
            print(f"\n{dep}")
            print(f"  Complexity: {info['complexity']}")
            print(f"  Reason: {info['reason']}")
    
    print("\n\n" + "=" * 80)
    print("MEDIUM PRIORITY REPLACEMENTS:")
    print("-" * 80)
    for dep, info in sorted(REPLACEABLE_DEPS.items(), key=lambda x: x[0]):
        if info['priority'] == 'medium':
            print(f"\n{dep}")
            print(f"  Complexity: {info['complexity']}")
            print(f"  Reason: {info['reason']}")
    
    print("\n\n" + "=" * 80)
    print("DEPENDENCIES TO KEEP (Too complex or critical):")
    print("-" * 80)
    for dep in sorted(KEEP_DEPS):
        print(f"  - {dep}")
    
    print("\n\n" + "=" * 80)
    print("SUMMARY:")
    print("-" * 80)
    print(f"Total replaceable dependencies: {len(REPLACEABLE_DEPS)}")
    print(f"High priority: {sum(1 for d in REPLACEABLE_DEPS.values() if d['priority'] == 'high')}")
    print(f"Medium priority: {sum(1 for d in REPLACEABLE_DEPS.values() if d['priority'] == 'medium')}")
    print(f"Low priority: {sum(1 for d in REPLACEABLE_DEPS.values() if d['priority'] == 'low')}")
    print(f"Dependencies to keep: {len(KEEP_DEPS)}")

if __name__ == "__main__":
    main()
