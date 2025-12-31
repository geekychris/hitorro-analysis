# POM Properties Refactoring

## Overview

All versions have been moved to properties for centralized version management. This makes it easy to update library versions in one place.

## Properties Organization

### Build Configuration
```xml
<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
<maven.compiler.source>19</maven.compiler.source>
<maven.compiler.target>19</maven.compiler.target>
```

### Hitorro Internal Versions
```xml
<hitorro.version>3.0.0</hitorro.version>
```
Used for:
- `hitorro-text-core`
- `hitorro-objretrieval`

### External Library Versions
```xml
<trove4j.version>3.0.3</trove4j.version>
<jena.version>4.10.0</jena.version>
<jung.version>2.0.1</jung.version>
<batik.version>1.18</batik.version>
<maltparser.version>1.9.2</maltparser.version>
```

### Testing Library Versions
```xml
<junit.version>5.11.4</junit.version>
<mockito.version>5.14.2</mockito.version>
<assertj.version>3.27.3</assertj.version>
```

### Maven Plugin Versions
```xml
<maven-compiler-plugin.version>3.13.0</maven-compiler-plugin.version>
<maven-jar-plugin.version>3.4.2</maven-jar-plugin.version>
<maven-assembly-plugin.version>3.7.1</maven-assembly-plugin.version>
<maven-surefire-plugin.version>3.5.2</maven-surefire-plugin.version>
```

## Benefits

### 1. **Single Source of Truth**
All versions are defined in one place at the top of the POM, making it easy to see what versions are being used.

### 2. **Easy Updates**
To update a library version, you only need to change it in one place:

**Before (hardcoded):**
```xml
<dependency>
    <groupId>net.sf.trove4j</groupId>
    <artifactId>trove4j</artifactId>
    <version>3.0.3</version>
</dependency>
```

**After (property):**
```xml
<!-- In properties section -->
<trove4j.version>3.0.3</trove4j.version>

<!-- In dependencies section -->
<dependency>
    <groupId>net.sf.trove4j</groupId>
    <artifactId>trove4j</artifactId>
    <version>${trove4j.version}</version>
</dependency>
```

### 3. **Consistency**
All JUNG libraries use the same version property:
```xml
<jung.version>2.0.1</jung.version>

<dependency>
    <artifactId>jung-api</artifactId>
    <version>${jung.version}</version>
</dependency>
<dependency>
    <artifactId>jung-visualization</artifactId>
    <version>${jung.version}</version>
</dependency>
<dependency>
    <artifactId>jung-algorithms</artifactId>
    <version>${jung.version}</version>
</dependency>
```

This ensures all JUNG modules stay in sync.

### 4. **Documentation**
Properties serve as documentation, showing at a glance:
- What libraries are used
- What versions are current
- What can be upgraded together

### 5. **Build Flexibility**
Versions can be overridden from command line:
```bash
mvn clean package -Djunit.version=5.12.0
```

### 6. **Profile Support**
Different profiles can use different versions:
```xml
<profiles>
    <profile>
        <id>latest</id>
        <properties>
            <jena.version>5.6.0</jena.version>
        </properties>
    </profile>
</profiles>
```

## Quick Reference

### To Update a Library

1. **Find the property** in the `<properties>` section
2. **Change the version number**
3. **Rebuild**: `mvn clean test`

Example - Updating JUnit:
```xml
<!-- Change from -->
<junit.version>5.11.4</junit.version>

<!-- To -->
<junit.version>5.12.0</junit.version>
```

### To Update Multiple Related Libraries

For libraries that should stay in sync (like JUNG modules), update the single property:

```xml
<!-- Update once -->
<jung.version>2.1.1</jung.version>

<!-- All JUNG dependencies automatically updated -->
```

### To Update All Hitorro Libraries

Update the single internal version:
```xml
<hitorro.version>3.1.0</hitorro.version>
```

This updates:
- `hitorro-text-core`
- `hitorro-objretrieval`

## Verification

### Check Property Resolution
```bash
mvn help:effective-pom | grep "<version>"
```

### List All Properties
```bash
mvn help:evaluate -Dexpression=project.properties
```

### Check Specific Property
```bash
mvn help:evaluate -Dexpression=junit.version -q -DforceStdout
```

## Testing

All tests pass after refactoring:
```
✅ Tests run: 81
✅ Failures: 0
✅ Errors: 0
✅ Skipped: 0
```

## Comparison

| Aspect | Before | After |
|--------|--------|-------|
| **Version Locations** | Scattered throughout POM | All in properties section |
| **JUNG Version Updates** | Update 3 places | Update 1 place |
| **Hitorro Version Updates** | Update 2 places | Update 1 place |
| **Plugin Version Updates** | Update 4 places | Update 1 place each |
| **Version Visibility** | Hard to see all versions | Easy to see in properties |
| **Maintenance** | Error-prone | Easy and safe |

## Best Practices

### Property Naming Convention

Used consistent naming:
- Library properties: `<library-name>.version`
- Plugin properties: `<maven-plugin-name>.version`
- Internal properties: `<project-name>.version`

Examples:
- ✅ `junit.version`
- ✅ `maven-compiler-plugin.version`
- ✅ `hitorro.version`

### Grouping

Properties are grouped by purpose:
1. Build configuration
2. Internal versions
3. External library versions
4. Testing library versions
5. Maven plugin versions

This makes it easy to find and update related versions.

## Future Enhancements

### Optional: Version Management Plugin

Consider adding the Versions Maven Plugin for easier updates:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>versions-maven-plugin</artifactId>
    <version>2.17.1</version>
</plugin>
```

Commands:
```bash
# Check for property updates
mvn versions:display-property-updates

# Update properties interactively
mvn versions:update-properties

# Set specific property
mvn versions:set-property -Dproperty=junit.version -DnewVersion=5.12.0
```

---

**Date**: December 19, 2025  
**Status**: ✅ Complete  
**Tests**: ✅ 81/81 Passing
