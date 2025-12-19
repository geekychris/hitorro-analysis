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
package com.hitorro.analysis.mstparser.io;

import com.hitorro.analysis.mstparser.DependencyInstance;
import com.hitorro.analysis.mstparser.Util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A class that defines common behavior and abstract methods for readers for different formats. <p/> <p> Created: Sat
 * Nov 10 15:25:10 2001 </p>
 *
 * @author Jason Baldridge
 * @version $Id: DependencyReader.java 112 2007-03-23 19:19:28Z jasonbaldridge $
 */
public abstract class DependencyReader {

    protected BufferedReader inputReader;
    protected boolean labeled = true;
    protected boolean confScores = false;

    public static DependencyReader createDependencyReader(String format,
                                                          boolean discourseMode)
            throws IOException {

        if (format.equals("MST")) {
            return new MSTReader();
        } else if (format.equals("CONLL")) {
            return new CONLLReader(discourseMode);
        } else {
            System.out.println("!!!!!!!  Not a supported format: " + format);
            System.out.println("********* Assuming CONLL format. **********");
            return new CONLLReader(discourseMode);
        }
    }

    public static DependencyReader createDependencyReader(String format)
            throws IOException {

        return createDependencyReader(format, false);
    }

    public static DependencyReader createDependencyReaderWithConfidenceScores(
            String format) throws IOException {
        DependencyReader reader = createDependencyReader(format);
        reader.confScores = true;
        return reader;
    }

    public static DependencyInstance getInstance(boolean confScores, boolean labeled, final String conf_line, final String[] forms, final String[] pos, final String[] deprels, final int[] heads) {
        // called in demo case, called in api case?
        String[] forms_new = new String[forms.length + 1];
        String[] pos_new = new String[pos.length + 1];
        String[] deprels_new = new String[deprels.length + 1];
        int[] heads_new = new int[heads.length + 1];

        forms_new[0] = "<root>";
        pos_new[0] = "<root-POS>";
        deprels_new[0] = "<no-type>";
        heads_new[0] = -1;
        for (int i = 0; i < forms.length; i++) {
            forms_new[i + 1] = normalize(forms[i]);
            pos_new[i + 1] = pos[i];
            deprels_new[i + 1] = labeled ? deprels[i] : "<no-type>";
            heads_new[i + 1] = heads[i];
        }

        return getInstance(confScores, conf_line, forms, forms_new, pos_new, deprels_new, heads_new);
    }

    private static DependencyInstance getInstance(boolean confScores, final String conf_line, final String[] forms, final String[] forms_new, final String[] pos_new, final String[] deprels_new, final int[] heads_new) {
        double[] confs_new = null;
        if (confScores) {
            double[] confs = Util.stringsToDoubles(conf_line.split("\t"));
            confs_new = new double[confs.length + 1];
            confs_new[0] = 1;
            for (int i = 0; i < forms.length; i++) {
                confs_new[i + 1] = confs[i];
            }
        }

        DependencyInstance instance =
                new DependencyInstance(forms_new, pos_new, deprels_new, heads_new, confs_new);

        // set up the course pos tags as just the first letter of the fine-grained ones
        String[] cpostags = new String[pos_new.length];
        cpostags[0] = "<root-CPOS>";
        for (int i = 1; i < pos_new.length; i++) {
            cpostags[i] = pos_new[i].substring(0, 1);
        }
        instance.cpostags = cpostags;

        // set up the lemmas as just the first 5 characters of the forms
        String[] lemmas = new String[forms_new.length];
        cpostags[0] = "<root-LEMMA>";
        for (int i = 1; i < forms_new.length; i++) {
            int formLength = forms_new[i].length();
            lemmas[i] = formLength > 5 ? forms_new[i].substring(0, 5) : forms_new[i];
        }
        instance.lemmas = lemmas;
        instance.feats = new String[0][0];

        return instance;
    }

    protected static String normalize(String s) {
        if (s.matches("[0-9]+|[0-9]+\\.[0-9]+|[0-9]+[0-9,]+")) {
            return "<num>";
        }

        return s;
    }

    public boolean startReading(String file) throws IOException {
        labeled = fileContainsLabels(file);
        inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
        return labeled;
    }

    public boolean isLabeled() {
        return labeled;
    }

    public abstract DependencyInstance getNext() throws IOException;

    protected abstract boolean fileContainsLabels(String filename) throws IOException;
}
