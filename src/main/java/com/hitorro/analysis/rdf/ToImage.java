/*
 * Copyright (c) 2006-2025 Chris Collins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.hitorro.analysis.rdf;


import com.hitorro.util.core.Log;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Render a complete Jena model to an image (any ImageIO format) or to SVG:
 * vertices are the model's subjects and objects, edges its statements, laid
 * out by {@link RdfGraphLayout}.
 */
public class ToImage {
    private static final int VERTEX_RADIUS = 6;
    private static final Color EDGE = new Color(0x80, 0x80, 0x80);
    private static final Color VERTEX = new Color(0xd0, 0x30, 0x30);
    private static final Color LABEL = Color.black;

    public static boolean render(Model model, File f, String format, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bi.createGraphics();
        try {
            // Fill the background in white
            g2d.setColor(Color.white);
            g2d.fillRect(0, 0, width, height);
            paint(g2d, new RdfGraphLayout(model, width, height));
        } finally {
            g2d.dispose();
        }
        try {
            // Save the image to a file
            ImageIO.write(bi, format, f);
            return true;
        } catch (IOException ex) {
            Log.util.error("Unable to save image %s %e", ex, ex);
            return false;
        }
    }

    public static void toSVG(Model model, File f, int width, int height) throws UnsupportedEncodingException, SVGGraphics2DIOException, IOException, ParserConfigurationException {
        DocumentBuilder dbf = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        DOMImplementation domImpl = dbf.getDOMImplementation();
        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        svgGenerator.setSVGCanvasSize(new Dimension(width, height));
        paint(svgGenerator, new RdfGraphLayout(model, width, height));
        try (Writer out = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8)) {
            svgGenerator.stream(out, true);
        }
    }

    /**
     * Draws the laid-out graph: edges with an arrowhead and the predicate as label,
     * then vertices with their label (Transformers.EDGE and Transformers.NODE).
     */
    static void paint(Graphics2D g, RdfGraphLayout layout) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        FontMetrics fm = g.getFontMetrics();
        for (Statement s : layout.getEdges()) {
            Point2D.Double from = layout.position(s.getSubject());
            Point2D.Double to = layout.position(s.getObject());
            g.setColor(EDGE);
            g.draw(new Line2D.Double(from, to));
            arrowhead(g, from, to);
            g.setColor(LABEL);
            String label = Transformers.EDGE.apply(s);
            g.drawString(label, (float) ((from.x + to.x) / 2 - fm.stringWidth(label) / 2.0), (float) ((from.y + to.y) / 2));
        }
        for (RDFNode v : layout.getVertices()) {
            Point2D.Double p = layout.position(v);
            g.setColor(VERTEX);
            g.fillOval((int) p.x - VERTEX_RADIUS, (int) p.y - VERTEX_RADIUS, 2 * VERTEX_RADIUS, 2 * VERTEX_RADIUS);
            g.setColor(LABEL);
            g.drawString(Transformers.NODE.apply(v), (float) p.x + VERTEX_RADIUS + 2, (float) p.y + fm.getAscent() / 2f);
        }
    }

    private static void arrowhead(Graphics2D g, Point2D.Double from, Point2D.Double to) {
        double angle = Math.atan2(to.y - from.y, to.x - from.x);
        // Tip on the target vertex's rim, not its centre.
        double tipX = to.x - Math.cos(angle) * VERTEX_RADIUS, tipY = to.y - Math.sin(angle) * VERTEX_RADIUS;
        double size = 8, spread = Math.PI / 7;
        Path2D.Double head = new Path2D.Double();
        head.moveTo(tipX, tipY);
        head.lineTo(tipX - size * Math.cos(angle - spread), tipY - size * Math.sin(angle - spread));
        head.lineTo(tipX - size * Math.cos(angle + spread), tipY - size * Math.sin(angle + spread));
        head.closePath();
        g.fill(head);
    }
}
