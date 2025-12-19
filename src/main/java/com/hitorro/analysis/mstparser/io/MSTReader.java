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
import java.io.FileReader;
import java.io.IOException;

/**
 * A reader for files in MST format. <p/> <p> Created: Sat Nov 10 15:25:10 2001 </p>
 *
 * @author Jason Baldridge
 * @version $Id: MSTReader.java 94 2007-01-17 17:05:12Z jasonbaldridge $
 * @see DependencyReader
 */
public class MSTReader extends DependencyReader {


    public DependencyInstance getNext() throws IOException {

        String line = inputReader.readLine();
        String pos_line = inputReader.readLine();
        String deprel_line = labeled ? inputReader.readLine() : pos_line;
        String heads_line = inputReader.readLine();
        String conf_line = confScores ? inputReader.readLine() : "";
        inputReader.readLine(); // blank line

        if (line == null) {
            inputReader.close();
            return null;
        }

        String[] forms = line.split("\t");
        String[] pos = pos_line.split("\t");
        String[] deprels = deprel_line.split("\t");
        int[] heads = Util.stringsToInts(heads_line.split("\t"));

        return getInstance(confScores, labeled, conf_line, forms, pos, deprels, heads);
    }


    protected boolean fileContainsLabels(String file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));
        in.readLine();
        in.readLine();
        in.readLine();
        if (confScores) {
            in.readLine();
        }
        String line = in.readLine();
        in.close();

        if (line.trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

}
