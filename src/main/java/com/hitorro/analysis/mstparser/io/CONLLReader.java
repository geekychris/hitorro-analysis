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
import com.hitorro.analysis.mstparser.RelationalFeature;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A reader for files in CoNLL format. <p/> <p> Created: Sat Nov 10 15:25:10 2001 </p>
 *
 * @author Jason Baldridge
 * @version $Id: CONLLReader.java 112 2007-03-23 19:19:28Z jasonbaldridge $
 * @see mstparser.DependencyReader
 */
public class CONLLReader extends DependencyReader {

    protected boolean discourseMode = false;

    public CONLLReader(boolean discourseMode) {
        this.discourseMode = discourseMode;
    }

    public DependencyInstance getNext() throws IOException {

        ArrayList<String[]> lineList = new ArrayList<String[]>();

        String line = inputReader.readLine();
        while (line != null && !line.equals("") && !line.startsWith("*")) {
            lineList.add(line.split("\t"));
            line = inputReader.readLine();
            //System.out.println("## "+line);
        }

        int length = lineList.size();

        if (length == 0) {
            inputReader.close();
            return null;
        }

        String[] forms = new String[length + 1];
        String[] lemmas = new String[length + 1];
        String[] cpos = new String[length + 1];
        String[] pos = new String[length + 1];
        String[][] feats = new String[length + 1][];
        String[] deprels = new String[length + 1];
        int[] heads = new int[length + 1];
        double[] confscores = confScores ? new double[length + 1] : null;

        forms[0] = "<root>";
        lemmas[0] = "<root-LEMMA>";
        cpos[0] = "<root-CPOS>";
        pos[0] = "<root-POS>";
        deprels[0] = "<no-type>";
        heads[0] = -1;
        if (confScores) {
            confscores[0] = 1;
        }

        for (int i = 0; i < length; i++) {
            String[] info = lineList.get(i);
            forms[i + 1] = normalize(info[1]);
            lemmas[i + 1] = normalize(info[2]);
            cpos[i + 1] = info[3];
            pos[i + 1] = info[4];
            feats[i + 1] = info[5].split("\\|");
            deprels[i + 1] = labeled ? info[7] : "<no-type>";
            heads[i + 1] = Integer.parseInt(info[6]);
            if (confScores) {
                confscores[i + 1] = Double.parseDouble(info[10]);
            }
        }

        feats[0] = new String[feats[1].length];
        for (int i = 0; i < feats[1].length; i++) {
            feats[0][i] = "<root-feat>" + i;
        }

        // The following stuff is for discourse and can be safely
        // ignored if you are doing sentential parsing. (In theory it
        // could be useful for sentential parsing.)
        if (discourseMode) {
            String[][] extended_feats = new String[feats[0].length][length + 1];
            for (int i = 0; i < extended_feats.length; i++) {
                for (int j = 0; j < length + 1; j++) {
                    extended_feats[i][j] = feats[j][i];
                }
            }

            feats = extended_feats;
        }

        ArrayList<RelationalFeature> rfeats =
                new ArrayList<RelationalFeature>();

        while (line != null && !line.equals("")) {
            rfeats.add(new RelationalFeature(length, line, inputReader));
            line = inputReader.readLine();
        }

        RelationalFeature[] rfeatsList = new RelationalFeature[rfeats.size()];
        rfeats.toArray(rfeatsList);

        // End of discourse stuff.

        return new DependencyInstance(forms, lemmas, cpos, pos, feats, deprels, heads, rfeatsList, confscores);

    }


    protected boolean fileContainsLabels(String file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));
        String line = in.readLine();
        in.close();

        if (line.trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

}
