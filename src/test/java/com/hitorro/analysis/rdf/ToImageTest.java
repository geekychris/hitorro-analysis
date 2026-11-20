package com.hitorro.analysis.rdf;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ToImageTest {

    private static Model sample() {
        Model m = ModelFactory.createDefaultModel();
        m.setNsPrefix("ex", "http://example.org/");
        Property knows = m.createProperty("http://example.org/knows");
        Property name = m.createProperty("http://example.org/name");
        Resource alice = m.createResource("http://example.org/alice");
        Resource bob = m.createResource("http://example.org/bob");
        Resource carol = m.createResource("http://example.org/carol");
        alice.addProperty(knows, bob).addProperty(name, "Alice");
        bob.addProperty(knows, carol);
        carol.addProperty(knows, alice);
        return m;
    }

    @Test
    void layoutPlacesEveryVertexInsideTheArea() {
        RdfGraphLayout layout = new RdfGraphLayout(sample(), 400, 300);
        // alice, bob, carol and the "Alice" literal; four statements.
        assertThat(layout.getVertices()).hasSize(4);
        assertThat(layout.getEdges()).hasSize(4);
        for (var v : layout.getVertices()) {
            Point2D.Double p = layout.position(v);
            assertThat(p.x).isBetween(0.0, 400.0);
            assertThat(p.y).isBetween(0.0, 300.0);
        }
    }

    @Test
    void layoutIsDeterministicAndSpreadsVerticesOut() {
        RdfGraphLayout a = new RdfGraphLayout(sample(), 400, 300);
        RdfGraphLayout b = new RdfGraphLayout(sample(), 400, 300);
        for (int i = 0; i < a.getVertices().size(); i++) {
            assertThat(a.position(a.getVertices().get(i))).isEqualTo(b.position(b.getVertices().get(i)));
            for (int j = i + 1; j < a.getVertices().size(); j++) {
                assertThat(a.position(a.getVertices().get(i)).distance(a.position(a.getVertices().get(j)))).isGreaterThan(20.0);
            }
        }
    }

    @Test
    void emptyModelRenders(@TempDir Path dir) throws Exception {
        File png = dir.resolve("empty.png").toFile();
        assertThat(ToImage.render(ModelFactory.createDefaultModel(), png, "png", 100, 80)).isTrue();
        assertThat(ImageIO.read(png).getWidth()).isEqualTo(100);
    }

    @Test
    void rendersPngWithTheGraphDrawn(@TempDir Path dir) throws Exception {
        File png = dir.resolve("graph.png").toFile();
        assertThat(ToImage.render(sample(), png, "png", 400, 300)).isTrue();
        BufferedImage img = ImageIO.read(png);
        assertThat(img.getWidth()).isEqualTo(400);
        assertThat(img.getHeight()).isEqualTo(300);
        int nonWhite = 0;
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                if ((img.getRGB(x, y) & 0xffffff) != 0xffffff) {
                    nonWhite++;
                }
            }
        }
        assertThat(nonWhite).isGreaterThan(200);
    }

    @Test
    void rendersSvgWithLabels(@TempDir Path dir) throws Exception {
        File svg = dir.resolve("graph.svg").toFile();
        ToImage.toSVG(sample(), svg, 400, 300);
        String text = Files.readString(svg.toPath());
        assertThat(text).contains("<svg").contains("ex:alice").contains("ex:knows").contains("Alice");
    }
}
