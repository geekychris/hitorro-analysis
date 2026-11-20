package com.hitorro.analysis;
import tools.jackson.databind.*; import tools.jackson.databind.node.*;
import com.hitorro.jsontypesystem.JVS;
import com.hitorro.analysis.access.vector.*; import com.hitorro.analysis.access.sentenceenum.SentenceEnumeratorRetriever;
import com.hitorro.analysis.sentiment.penn.*; import com.hitorro.analysis.cky.CKYMap;
import com.hitorro.obj.core.TypeFieldMapper; import com.hitorro.obj.core.solr.RetrievalContext; import com.hitorro.obj.core.solr.collection.CollectionConfig;
import com.hitorro.obj.core.solr.retrievalmodules.summarizer.SummarizerRetriever;
import com.hitorro.util.core.iterator.AbstractIterator; import com.hitorro.util.core.iterator.CollectionIterator; import com.hitorro.util.core.iterator.sinks.BaseSink;
import com.hitorro.util.json.keys.propaccess.Propaccess;
import org.apache.solr.client.solrj.SolrQuery;
import java.io.*; import java.nio.charset.StandardCharsets; import java.nio.file.*; import java.util.*;
/**
 * Pins what hitorro-analysis produces from JSON to what 3.0.2 did on Jackson 2.18: the sentiment
 * lexicon rows, SentWord mapping and CountSink counting of loosely typed values, the term vectors the
 * vectorizer puts on each result and its aggregate (mapping, freq), the summarizer and CKY mappers
 * (both fail on 3.0.2 too: pinned as they are). {@code jackson2-analysis.txt} needs nothing;
 * {@code jackson2-analysis-data.txt} needs HiTorro's data directory (HT_DATA, or ../data in the
 * hitorro-all layout) and is skipped without it. Both are the output of the same code run on the
 * 3.0.2 sources (generator: ~/hitorro-j3/tools/Jackson2AnalysisGolden.java.txt in the migration
 * workspace). StringNode is mapped back to TextNode before comparing.
 */
class Jackson2FormatGoldenTest {
  static final Map<String, String> out = new LinkedHashMap<>();
  static void p(String k, Object v){ out.put(k, String.valueOf(v).replace("\\","\\\\").replace("\n","\\n").replace("\r","\\r")); }
  static boolean jackson(Throwable t){ return t.getClass().getName().startsWith("com.fasterxml.jackson") || t.getClass().getName().startsWith("tools.jackson"); }
  static String desc(Throwable t){ String s = (t instanceof IOException ? "IOE:" : "RTE:") + (jackson(t) ? "jackson" : t.getClass().getSimpleName());
    if (!jackson(t) && t.getCause() != null) s += "<" + (jackson(t.getCause()) ? "jackson" : t.getCause().getClass().getSimpleName()) + ">"; return s; }
  static String tryIt(java.util.concurrent.Callable<Object> c){ try { return String.valueOf(c.call()); } catch(Throwable t){ if (System.getenv("AG_TRACE") != null) t.printStackTrace(); return "!"+desc(t); } }
  static String sig(JsonNode n){ if (n == null) return "null"; if (n.isObject()) { StringBuilder sb = new StringBuilder("{"); Iterator<String> it = n.propertyNames().iterator(); while (it.hasNext()) { String k = it.next(); sb.append(k).append(':').append(sig(n.get(k))).append(it.hasNext() ? "," : ""); } return sb.append('}').toString(); }
    if (n.isArray()) { StringBuilder sb = new StringBuilder("["); for (int i = 0; i < n.size(); i++) sb.append(i > 0 ? "," : "").append(sig(n.get(i))); return sb.append(']').toString(); }
    return n.getClass().getSimpleName(); }
  // JsonNode.toString() fails past 500 levels on Jackson 3 (2.x: 1000), so print trees with the mapper
  static final ObjectMapper W = com.hitorro.util.json.JsonMappers.json();
  static String str(JsonNode n){ if (n == null) return "null"; try { return W.writeValueAsString(n); } catch (Exception e) { throw new RuntimeException(e); } }
  static String jvs(JVS j){ return j == null ? "null" : str(j.getJsonNode()) + " | " + sig(j.getJsonNode()); }
  static JVS parse(ObjectMapper om, String s) { try { return new JVS(om.readTree(s)); } catch (Exception e) { throw new RuntimeException(e); } }

  static class RC extends RetrievalContext {
    RC() { super(new CollectionConfig(), "en", new SolrQuery()); setCollectionName("coll"); }
    @Override public String getTranslatedField(String s) { return s; }
    @Override public TypeFieldMapper.FieldType getFieldType(Propaccess a) { return TypeFieldMapper.FieldType.TypeField; }
  }
  static List<JVS> rows(ObjectMapper om, String... docs) { List<JVS> l = new ArrayList<>(); for (String d : docs) { JVS r = new JVS(); r.setJVSChild(JVS.docKey, parse(om, d)); l.add(r); } return l; }
  static final String[] DOCS = {
    "{\"id\":{\"id\":\"d:1\"},\"title\":\"Good news about the climate\",\"body\":\"The climate report is good. Scientists are happy and hopeful about the excellent results. Bad weather is terrible.\"}",
    "{\"id\":{\"id\":\"d:2\"},\"title\":\"Technology\",\"body\":\"Technology changes fast. New phones are great and wonderful.\"}",
    "{\"id\":{\"id\":\"d:3\"},\"title\":7,\"body\":null}",
    "{\"id\":{\"id\":\"d:4\"},\"title\":\"\u00e9t\u00e9 \uD83D\uDE00\",\"body\":[\"array\",\"body\"]}",
  };
  static final String TFF = "type=weaksubj len=1 word1=abandoned pos1=adj stemmed1=n priorpolarity=negative\n"
    + "type=strongsubj len=1 word1=excellent pos1=adj stemmed1=n priorpolarity=positive\n"
    + "type=strongsubj len=1 word1=good pos1=anypos stemmed1=y priorpolarity=positive\n"
    + "type=strongsubj len=1 word1=terrible pos1=adj stemmed1=n priorpolarity=negative\n"
    + "type=weaksubj len=1 word1=happy pos1=adj stemmed1=y priorpolarity=positive\n"
    + "type=weaksubj len=1 word1=hopeful pos1=adj stemmed1=n priorpolarity=positive\n";

  /** withData: the parts that need HiTorro's data directory (HT_DATA: iso639.psv, stemmers, models). */
  public static Map<String, String> compute(Path dir, boolean withData) throws Exception {
    out.clear();
    java.lang.reflect.Field dp = com.hitorro.util.core.params.GlobalProperties.class.getDeclaredField("defaultProperties"); dp.setAccessible(true);
    Object oldProps = dp.get(null);
    try { computeAux(dir, withData); } finally { dp.set(null, oldProps); }
    return out;
  }
  static void computeAux(Path dir, boolean withData) throws Exception {
    ObjectMapper om = com.hitorro.util.json.JsonMappers.json();
    Path tff = dir.resolve("subj.tff"); Files.writeString(tff, TFF);
    com.hitorro.util.core.params.GlobalProperties.setDefaultProperties(om.readTree("{\"sentiment\":{\"penndir\":\"file://" + tff.toAbsolutePath() + "\"}}"));
    // ---- sentiment lexicon: raw rows, word mapping, counting
    p("sentiment.raw", tryIt(() -> { AbstractIterator<ObjectNode> it = SentimentUtil.getRawValues(); List<String> l = new ArrayList<>(); while (it.hasNext()) { ObjectNode n = it.next(); l.add(str(n) + " | " + sig(n)); } return l; }));
    String[] words = {"{\"pos1\":\"adj\",\"priorpolarity\":\"negative\",\"type\":\"weaksubj\",\"word1\":\"abandoned\",\"stemmed1\":\"n\"}",
      "{\"pos1\":5,\"priorpolarity\":null,\"type\":true,\"word1\":[\"x\"],\"stemmed1\":\"true\"}", "{\"stemmed1\":true}", "{}"};
    for (int i = 0; i < words.length; i++) { String w = words[i]; p("sentword." + i, tryIt(() -> { SentWord sw = SentWordMapper.instance.apply(parse(om, w)); StringBuilder b = new StringBuilder(); for (String fn : new String[]{"isStemmed", "pos", "type", "word", "priorPolarity"}) { java.lang.reflect.Field f = SentWord.class.getDeclaredField(fn); f.setAccessible(true); b.append(f.get(sw)).append("|"); } return b; })); }
    p("countsink", tryIt(() -> { CountSink cs = new CountSink(); cs.start();
      for (String w : words) cs.add(parse(om, w)); cs.add(parse(om, "{\"a\":{\"b\":1},\"n\":2,\"s\":\"x\",\"z\":null}"));
      TreeMap<String, String> m = new TreeMap<>(); for (String k : cs.getCounters().keySet()) { TreeMap<String, Double> c = new TreeMap<>(); for (String w : cs.getCounters().getCounter(k).keySet()) c.put(String.valueOf(w), cs.getCounters().getCounter(k).getCount(w)); m.put(String.valueOf(k), String.valueOf(c)); } return m; }));
    p("sentenum", tryIt(() -> { JVS q = parse(om, "{\"sentenum\":{}}"); SentenceEnumeratorRetriever s = new SentenceEnumeratorRetriever(); boolean part = s.getParticipate(q, new RC());
      AbstractIterator<JVS> it = s.appendRetriever(new CollectionIterator<>(rows(om, DOCS[0])), q, new RC()); return part + " " + jvs(it.next()); }));
    if (!withData) return;
    // ---- vectorizer: term vector rows put on each result and the aggregate (mapping, freq)
    String[] vq = {"{\"vectorize\":{\"title\":\"doc.title\",\"body\":\"doc.body\",\"analyzer\":\"htstandard, case\"},\"lang\":\"en\"}",
      "{\"vectorize\":{\"title\":\"doc.title\",\"body\":\"doc.body\"}}", "{\"vectorize\":{\"title\":5,\"body\":true,\"analyzer\":\"\"}}", "{}"};
    for (int i = 0; i < vq.length; i++) { String q = vq[i]; p("vectorize." + i, tryIt(() -> { JVS query = parse(om, q); RC rc = new RC(); VectorizerRetriever vr = new VectorizerRetriever();
      if (!vr.getParticipate(query, rc)) return "noparticipate";
      AbstractIterator<JVS> it = vr.appendRetriever(new CollectionIterator<>(rows(om, DOCS)), query, rc); List<String> l = new ArrayList<>(); while (it.hasNext()) l.add(jvs(it.next()));
      List<String> aggs = new ArrayList<>(); rc.addAggregates(new BaseSink<JVS>() { public boolean init(JsonNode n) { return true; } public boolean start() { return true; } public boolean add(JVS j) { aggs.add(jvs(j)); return true; } public boolean stop() { return true; } });
      return "rows=" + l + " aggs=" + aggs; })); }
    // ---- summarizer: sentence spans with phrase hits put on each result
    String[] sq = {"{\"summarize\":{\"title\":\"title\",\"body\":\"body\",\"size\":50,\"queries\":[\"climate\",\"phones\"]}}",
      "{\"summarize\":{\"title\":\"title\",\"body\":\"body\",\"size\":\"20\",\"queries\":\"report\"}}", "{\"summarize\":{}}", "{\"summarize\":5}", "{}"};
    for (int i = 0; i < sq.length; i++) { String q = sq[i]; p("summarize." + i, tryIt(() -> { JVS query = parse(om, q); RC rc = new RC(); SummarizerRetriever sr = new SummarizerRetriever();
      if (!sr.getParticipate(query, rc)) return "noparticipate";
      AbstractIterator<JVS> it = sr.appendRetriever(new CollectionIterator<>(rows(om, DOCS)), query, rc); List<String> l = new ArrayList<>(); while (it.hasNext()) l.add(jvs(it.next())); return l; })); }
    // ---- CKY parse mapper over JSON values
    p("cky", tryIt(() -> { CKYMap m = new CKYMap(new String[][]{{"s", "parse"}, {"arr", "parses"}, {"n", "np"}});
      com.hitorro.util.core.iterator.JsonValueSource src = new com.hitorro.util.core.iterator.JsonValueSource(om.readTree("{\"s\":\"the dog runs\",\"arr\":[\"a cat\",\"the bird sings\"],\"n\":5}"));
      return str(m.apply(src).getNode()); }));
  }
  static Map<String, String> load(String name) throws IOException {
    Map<String, String> m = new LinkedHashMap<>();
    try (InputStream in = Jackson2FormatGoldenTest.class.getResourceAsStream(name)) {
      for (String line : new String(in.readAllBytes(), StandardCharsets.UTF_8).split("\n")) {
        if (line.isEmpty()) continue;
        int i = line.indexOf('=');
        m.put(line.substring(0, i), line.substring(i + 1));
      }
    }
    return m;
  }
  static Map<String, String> actual(Path dir, boolean withData) throws Exception {
    Map<String, String> a = new LinkedHashMap<>();
    compute(dir, withData).forEach((k, v) -> a.put(k, v.replace("StringNode", "TextNode")));
    return a;
  }

  @org.junit.jupiter.api.Test
  void matchesJackson2Output(@org.junit.jupiter.api.io.TempDir Path dir) throws Exception {
    Map<String, String> expected = load("jackson2-analysis.txt");
    org.assertj.core.api.Assertions.assertThat(expected).hasSize(7);
    org.assertj.core.api.Assertions.assertThat(actual(dir, false)).containsExactlyEntriesOf(expected);
  }

  @org.junit.jupiter.api.Test
  void matchesJackson2OutputWithData(@org.junit.jupiter.api.io.TempDir Path dir) throws Exception {
    String data = System.getProperty("HT_DATA", System.getenv("HT_DATA"));
    if (data == null && Files.isRegularFile(Path.of("../data/iso639.psv"))) data = Path.of("../data").toAbsolutePath().normalize().toString();
    org.junit.jupiter.api.Assumptions.assumeTrue(data != null && Files.isRegularFile(Path.of(data, "iso639.psv")),
        "needs HiTorro's data directory (HT_DATA or ../data)");
    String old = System.getProperty("HT_DATA");
    System.setProperty("HT_DATA", data);
    try {
      Map<String, String> expected = load("jackson2-analysis-data.txt");
      org.assertj.core.api.Assertions.assertThat(expected).hasSize(17);
      org.assertj.core.api.Assertions.assertThat(actual(dir, true)).containsExactlyEntriesOf(expected);
    } finally {
      if (old == null) System.clearProperty("HT_DATA"); else System.setProperty("HT_DATA", old);
    }
  }
}
