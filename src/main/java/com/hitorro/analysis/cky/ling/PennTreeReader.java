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
package com.hitorro.analysis.cky.ling;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class PennTreeReader extends TreeReader {

    public PennTreeReader(Reader in) {
        this.in = new PushbackReader(in);
        nextTree = readRootTree();
    }

    public Tree<String> readRootTree() {
        try {
            readWhiteSpace();
            if (!isLeftParen(peek())) {
                return null;
            }
            return readTree(true);
        } catch (IOException e) {
            throw new RuntimeException("Error reading tree.");
        }
    }

    private Tree<String> readTree(boolean isRoot) throws IOException {
        readLeftParen();
        String label = readLabel();
        if (label.length() == 0 && isRoot) {
            label = ROOT_LABEL;
        }
        List<Tree<String>> children = readChildren();
        readRightParen();
        return new Tree<String>(label, children);
    }

    private List<Tree<String>> readChildren() throws IOException {
        readWhiteSpace();
        if (!isLeftParen(peek())) {
            return Collections.singletonList(readLeaf());
        }
        return readChildList();
    }

    private List<Tree<String>> readChildList() throws IOException {
        List<Tree<String>> children = new ArrayList<Tree<String>>();
        readWhiteSpace();
        while (!isRightParen(peek())) {
            children.add(readTree(false));
            readWhiteSpace();
        }
        return children;
    }

    @Override
    public void close() throws Exception {
        in.close();
    }
}
