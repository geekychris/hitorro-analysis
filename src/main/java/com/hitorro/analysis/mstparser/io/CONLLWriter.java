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

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * A writer to create files in CONLL format. <p/> <p> Created: Sat Nov 10 15:25:10 2001 </p>
 *
 * @author Jason Baldridge
 * @version $Id: CONLLWriter.java 94 2007-01-17 17:05:12Z jasonbaldridge $
 * @see mstparser.io.DependencyWriter
 */
public class CONLLWriter extends DependencyWriter {

    public CONLLWriter(boolean labeled) {
        this.labeled = labeled;
    }

    public void write(DependencyInstance instance) throws IOException {
        DecimalFormat df = null;
        if (instance.confidenceScores != null) {
            df = new DecimalFormat();
            df.setMaximumFractionDigits(3);
        }
        for (int i = 0; i < instance.length(); i++) {
            writer.write(Integer.toString(i + 1));
            writer.write('\t');
            writer.write(instance.forms[i]);
            writer.write('\t');
            writer.write(instance.forms[i]);
            writer.write('\t');
            //writer.write(instance.cpostags[i]);                 writer.write('\t');
            writer.write(instance.postags[i]);
            writer.write('\t');
            writer.write(instance.postags[i]);
            writer.write('\t');
            writer.write("-");
            writer.write('\t');
            writer.write(Integer.toString(instance.heads[i]));
            writer.write('\t');
            writer.write(instance.deprels[i]);
            writer.write('\t');
            writer.write("-\t-");
            if (instance.confidenceScores != null) {
                writer.write('\t');
                writer.write(df.format(instance.confidenceScores[i]));
            }
            writer.newLine();
        }
        writer.newLine();

    }


}
