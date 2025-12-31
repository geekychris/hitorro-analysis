# Dependency Cleanup Plan

## Analysis Results

Maven dependency:analyze found the following issues:

### ✅ Unused Declared Dependencies (Safe to Remove)

1. **org.junit.jupiter:junit-jupiter** - Unused (we use junit-jupiter-api directly)
2. **org.mockito:mockito-junit-jupiter** - Unused
3. **org.apache.jena:jena-arq** - Unused (only jena-core is used)
4. **org.apache.jena:jena-tdb** - Unused
5. **net.sf.jung:jung2** (POM type) - Unused
6. **net.sf.jung:jung-graph-impl** - Unused
7. **org.apache.xmlgraphics:batik-rasterizer** - Unused
8. **org.apache.xmlgraphics:batik-swing** - Unused

### ❌ Used But Undeclared (Need to ADD)

These are being used but pulled in transitively - should declare explicitly:

1. **org.junit.jupiter:junit-jupiter-api** (currently pulled transitively)
2. **org.ow2.asm:asm** (for bytecode generation)
3. **org.junit.jupiter:junit-jupiter-params** (for parameterized tests)
4. **net.sourceforge.collections:collections-generic** (for JUNG)
5. **org.apache.opennlp:opennlp-tools** (for NLP)
6. **com.fasterxml.jackson.core:jackson-databind** (for JSON)
7. **xml-apis:xml-apis** (for XML/SVG)
8. **com.hitorro:hitorro-base** (internal dependency)
9. **com.hitorro:hitorro-util** (internal dependency)
10. **org.apache.lucene:lucene-core** (for text analysis)

### ⚠️  Issues Found

1. **Duplicate trove4j declaration** (lines 34 and 139)
2. **Test-scoped dependency in compile scope**: junit-jupiter-api should be test scope

## Cleanup Steps (Incremental)

### Step 1: Remove Duplicate
- Remove duplicate trove4j entry

### Step 2: Remove Unused JENA Dependencies
- Keep: jena-core (used)
- Remove: jena-arq, jena-tdb (unused)

### Step 3: Remove Unused JUNG Dependencies  
- Keep: jung-api, jung-visualization, jung-algorithms (used)
- Remove: jung2 (POM), jung-graph-impl (unused)

### Step 4: Remove Unused Batik Dependencies
- Keep: batik-svggen (used for SVG generation)
- Remove: batik-rasterizer, batik-swing (unused)

### Step 5: Remove Unused Test Dependencies
- Remove: junit-jupiter (use junit-jupiter-api instead)
- Remove: mockito-junit-jupiter (unused)

### Step 6: Add Missing Direct Dependencies
- Add all "used undeclared" dependencies explicitly

## Expected Benefits

- **Smaller JAR**: Removing unused dependencies reduces fat JAR size
- **Faster builds**: Less dependencies to download and compile against
- **Clearer dependencies**: Explicit declaration of what's actually used
- **Better maintenance**: Easier to understand project requirements
