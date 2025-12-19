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

import com.hitorro.util.core.iterator.AbstractIterator;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.NoSuchElementException;

/**
 *
 */
public abstract class TreeReader extends AbstractIterator<Tree<String>> {
    public static String ROOT_LABEL = "ROOT";

    protected PushbackReader in;
    protected Tree<String> nextTree;

    public boolean hasNext() {
        return (nextTree != null);
    }

    public Tree<String> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Tree<String> tree = nextTree;
        nextTree = readRootTree();
        return tree;
    }

    public Tree<String> readRootTree() {
        throw new RuntimeException("readRootTree() undefined.");
    }

    public int peek() throws IOException {
        int ch = in.read();
        in.unread(ch);
        return ch;
    }

    public String readLabel() throws IOException {
        readWhiteSpace();
        return readText();
    }

    public Tree<String> readLeaf() throws IOException {
        String label = readText();
        return new Tree<String>(label);
    }

    public String readText() throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch = in.read();
        while (!isWhiteSpace(ch) && !isLeftParen(ch) && !isRightParen(ch)) {
            sb.append((char) ch);
            ch = in.read();
        }
        in.unread(ch);
        //      System.out.println("Read text: ["+sb+"]");
        return sb.toString().intern();
    }

    public void readLeftParen() throws IOException {
        //      System.out.println("Read left.");
        readWhiteSpace();
        int ch = in.read();
        if (!isLeftParen(ch)) {
            throw new RuntimeException("Format error reading tree.");
        }
    }

    public void readRightParen() throws IOException {
        //      System.out.println("Read right.");
        readWhiteSpace();
        int ch = in.read();
        if (!isRightParen(ch)) {
            throw new RuntimeException("Format error reading tree.");
        }
    }

    public void readWhiteSpace() throws IOException {
        int ch = in.read();
        while (isWhiteSpace(ch)) {
            ch = in.read();
        }
        in.unread(ch);
    }

    public boolean isWhiteSpace(int ch) {
        return (ch == ' ' || ch == '\t' || ch == '\f' || ch == '\r' || ch == '\n');
    }

    public boolean isLeftParen(int ch) {
        return ch == '(';
    }

    public boolean isRightParen(int ch) {
        return ch == ')';
    }

    public boolean isSemicolon(int ch) {
        return ch == ';';
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
