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
public class BioIETreeReader extends TreeReader {

    public BioIETreeReader(Reader in) {
        this.in = new PushbackReader(in);
        nextTree = readRootTree();
    }

    public Tree<String> readRootTree() {
        try {
            for (; ; ) {
                readCommentsAndWhiteSpace();
                if (!isLeftParen(peek())) {
                    return null;
                }
                in.read(); // (
                String str = readText();
                if (str.equals("SENT")) {
                    break; // read the sentence
                } else if (str.equals("SEC")) {
                    // read in the section, "("-less tree, then throw it away
                    readTree(false);
                } else {
                    //System.out.println("Failed: "+str);
                    return null; // reading error
                }
            }
            // We have stripped off "(SENT", so read a "("-less tree.
            return new Tree<String>(ROOT_LABEL, Collections.singletonList(readTree(false)));
        } catch (IOException e) {
            throw new RuntimeException("Error reading tree." + e.toString());
        }
    }

    private Tree<String> readTree(boolean matchparen) throws IOException {
        if (matchparen) {
            readLeftParen();
        }
        String label = readColonizedLabel();
        List<Tree<String>> children = readChildren();
        readRightParen();
        return new Tree<String>(label, children);
    }

    public String readColonizedLabel() throws IOException {
        readWhiteSpace();
        String ret = readText();
        int i = ret.indexOf(":");
        if (i == -1) {
            return ret;
        } else {
            return ret.substring(0, i);
        }
    }

    private List<Tree<String>> readChildren() throws IOException {
        readWhiteSpace();
        if (!isLeftParen(peek())) {
            return Collections.singletonList(readLeaf());
        } else {
            return readChildList();
        }
    }

    private List<Tree<String>> readChildList() throws IOException {
        List<Tree<String>> children = new ArrayList<Tree<String>>();
        readWhiteSpace();
        while (!isRightParen(peek())) {
            children.add(readTree(true));
            readWhiteSpace();
        }
        return children;
    }

    private void readCommentsAndWhiteSpace() throws IOException {
        int ch;
        for (; ; ) {
            readWhiteSpace();
            if (!isSemicolon(peek())) {
                return;
            }
            // read a line
            ch = in.read();
            while (ch != '\n') {
                ch = in.read();
            }
        }
    }

    @Override
    public void close() throws Exception {
        in.close();
    }
}
