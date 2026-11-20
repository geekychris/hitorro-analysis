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

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A Jena model as a directed graph (subjects and objects are vertices, statements
 * are edges) laid out with the Fruchterman-Reingold force-directed algorithm, the
 * one JUNG's FRLayout implements. Replaces the JUNG dependency, which brought in
 * the unmaintained collections-generic.
 */
public final class RdfGraphLayout {
    private static final int ITERATIONS = 200;

    private final List<RDFNode> vertices = new ArrayList<>();
    private final List<Statement> edges = new ArrayList<>();
    private final Map<RDFNode, Point2D.Double> positions = new LinkedHashMap<>();

    /**
     * Lays the model out in a width x height area. The layout is deterministic:
     * the same model always gives the same picture.
     */
    public RdfGraphLayout(Model model, int width, int height) {
        Map<RDFNode, Integer> index = new LinkedHashMap<>();
        StmtIterator it = model.listStatements();
        try {
            while (it.hasNext()) {
                Statement s = it.next();
                edges.add(s);
                index.putIfAbsent(s.getSubject(), index.size());
                index.putIfAbsent(s.getObject(), index.size());
            }
        } finally {
            it.close();
        }
        vertices.addAll(index.keySet());
        layout(index, width, height);
    }

    public List<RDFNode> getVertices() {
        return Collections.unmodifiableList(vertices);
    }

    public List<Statement> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public Point2D.Double position(RDFNode vertex) {
        return positions.get(vertex);
    }

    private void layout(Map<RDFNode, Integer> index, int width, int height) {
        int n = vertices.size();
        if (n == 0) {
            return;
        }
        double[] x = new double[n], y = new double[n];
        Random random = new Random(42);
        for (int i = 0; i < n; i++) {
            x[i] = random.nextDouble() * width;
            y[i] = random.nextDouble() * height;
        }
        // Ideal edge length; temperature caps how far a vertex moves per step
        // and cools linearly to zero.
        double k = Math.sqrt((double) width * height / n);
        double temperature = Math.max(width, height) / 10.0;
        int[][] ends = new int[edges.size()][];
        for (int e = 0; e < ends.length; e++) {
            Statement s = edges.get(e);
            ends[e] = new int[]{index.get(s.getSubject()), index.get(s.getObject())};
        }
        for (int iter = 0; iter < ITERATIONS; iter++) {
            double[] dx = new double[n], dy = new double[n];
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    double ddx = x[i] - x[j], ddy = y[i] - y[j];
                    double dist = Math.max(0.01, Math.hypot(ddx, ddy));
                    double force = k * k / dist;
                    dx[i] += ddx / dist * force;
                    dy[i] += ddy / dist * force;
                    dx[j] -= ddx / dist * force;
                    dy[j] -= ddy / dist * force;
                }
            }
            for (int[] e : ends) {
                if (e[0] == e[1]) {
                    continue;
                }
                double ddx = x[e[0]] - x[e[1]], ddy = y[e[0]] - y[e[1]];
                double dist = Math.max(0.01, Math.hypot(ddx, ddy));
                double force = dist * dist / k;
                dx[e[0]] -= ddx / dist * force;
                dy[e[0]] -= ddy / dist * force;
                dx[e[1]] += ddx / dist * force;
                dy[e[1]] += ddy / dist * force;
            }
            for (int i = 0; i < n; i++) {
                double disp = Math.max(0.01, Math.hypot(dx[i], dy[i]));
                double step = Math.min(disp, temperature);
                x[i] = clamp(x[i] + dx[i] / disp * step, width);
                y[i] = clamp(y[i] + dy[i] / disp * step, height);
            }
            temperature *= 1.0 - 1.0 / ITERATIONS;
        }
        for (int i = 0; i < n; i++) {
            positions.put(vertices.get(i), new Point2D.Double(x[i], y[i]));
        }
    }

    private static double clamp(double v, int max) {
        double margin = Math.min(20, max / 10.0);
        return Math.max(margin, Math.min(max - margin, v));
    }
}
