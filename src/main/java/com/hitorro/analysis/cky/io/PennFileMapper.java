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
package com.hitorro.analysis.cky.io;

import com.hitorro.analysis.cky.ling.PennTreeReader;
import com.hitorro.analysis.cky.ling.Tree;
import com.hitorro.obj.core.Log;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.core.iterator.Mapper;
import com.hitorro.util.io.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 */
public class PennFileMapper implements Mapper<File, AbstractIterator<Tree<String>>> {
    public static PennFileMapper instance = new PennFileMapper();

    @Override
    public AbstractIterator<Tree<String>> apply(final File file) {
        try {
            return new PennTreeReader(FileUtil.getBufferedReaderFromFile(file));
        } catch (FileNotFoundException e) {
            Log.util.error("Couldnt open a PennTreeReader %s %e from file %s", e, e, file);
        }
        return null;
    }
}
