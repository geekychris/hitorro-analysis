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

import java.io.IOException;

/**
 * A writer to create files in MST format. <p/> <p> Created: Sat Nov 10 15:25:10 2001 </p>
 *
 * @author Jason Baldridge
 * @version $Id: MSTWriter.java 94 2007-01-17 17:05:12Z jasonbaldridge $
 * @see mstparser.io.DependencyWriter
 */
public class MSTWriter extends DependencyWriter {

    public MSTWriter(boolean labeled) {
        this.labeled = labeled;
    }

    public void write(DependencyInstance instance) throws IOException {
        writer.write(Util.join(instance.forms, '\t') + "\n");
        writer.write(Util.join(instance.postags, '\t') + "\n");
        if (labeled) {
            writer.write(Util.join(instance.deprels, '\t') + "\n");
        }
        writer.write(Util.join(instance.heads, '\t') + "\n");
        if (instance.confidenceScores != null) {
            writer.write(Util.join(instance.confidenceScores, '\t', 3) + "\n");
        }
        writer.write("\n");
    }
}
