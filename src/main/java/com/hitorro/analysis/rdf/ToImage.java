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


import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
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
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Render a complete Jena model to the
 */
public class ToImage {

    public static boolean render(Model model, File f, String format, int width, int height) {
        Graph<RDFNode, Statement> g = new JenaJungGraph(model);

        Layout<RDFNode, Statement> layout = new FRLayout(g);


        layout.setSize(new Dimension(width, height));
        VisualizationImageServer<RDFNode, Statement> viz =
                new VisualizationImageServer(layout, new Dimension(width, height));
        RenderContext context = viz.getRenderContext();
        context.setEdgeLabelTransformer(Transformers.EDGE);
        context.setVertexLabelTransformer(Transformers.NODE);


        viz.setPreferredSize(new Dimension(width, height));
        Image img = viz.getImage(new Point(width / 2, height / 2), new Dimension(width, height));

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bi.createGraphics();
        // Fill the background in white
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, width, height);

        // Draw the image
        g2d.setColor(Color.white);
        g2d.drawImage(img, 0, 0, width, height, Color.blue, null);

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

        Graph<RDFNode, Statement> g = new JenaJungGraph(model);

        Layout<RDFNode, Statement> layout = new FRLayout(g);


        layout.setSize(new Dimension(width, height));
        VisualizationImageServer<RDFNode, Statement> viz =
                new VisualizationImageServer(layout, new Dimension(width, height));
        RenderContext context = viz.getRenderContext();
        context.setEdgeLabelTransformer(Transformers.EDGE);
        context.setVertexLabelTransformer(Transformers.NODE);


        viz.setPreferredSize(new Dimension(width, height));

        DocumentBuilder dbf = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        DOMImplementation domImpl = dbf.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        viz.paint(svgGenerator); // Hrm, is this right??

        Writer out = new FileWriter(f);
        svgGenerator.stream(out, true);
    }
}
