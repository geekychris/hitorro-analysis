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
package com.hitorro.analysis.cky.model;

import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.io.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

/**
 *
 */
public class StanfordTSVParse {
    public static void parse(RuleReceiver receiver, File file, boolean unaryFile) throws FileNotFoundException, UnsupportedEncodingException {
        Iterator<String> iter = FileUtil.getLineReaderIteratorFromFile(file, "UTF-8");

        while (iter.hasNext()) {
            String s = iter.next();
            s = s.trim();
            if (s.length() == 0 || s.startsWith("#")) {
                continue;
            }
            String parts[] = StringUtil.tokenizeFromSingleChar(s, "\t");

            String num = parts[0];
            double prob;
            if (parts.length < 3) {
                continue;
            }
            if (num.indexOf('.') == -1) {
                prob = (double) Integer.parseInt(num);
            } else {
                prob = Double.parseDouble(num);
            }
            String v1;
            if (!unaryFile) {
                // split now on spaces
                String arg[] = StringUtil.tokenizeFromSingleChar(parts[2], " ");
                v1 = arg[0];
                if (arg.length == 1) {
                    receiver.unaryRule(prob, parts[1], v1);
                } else if (arg.length >= 2) {
                    receiver.binaryRule(prob, parts[1], v1, arg[1]);
                } else {
                    int i = 0;
                }
            } else {
                receiver.unaryRule(prob, parts[1], parts[2]);
            }
        }

    }
}
