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
package com.hitorro.analysis.lda;

import java.util.Vector;

public class Document {

    //----------------------------------------------------
    //Instance Variables
    //----------------------------------------------------
    public int[] words;
    public String rawStr;
    public int length;

    //----------------------------------------------------
    //Constructors
    //----------------------------------------------------
    public Document() {
        words = null;
        rawStr = "";
        length = 0;
    }

    public Document(int length) {
        this.length = length;
        rawStr = "";
        words = new int[length];
    }

    public Document(int length, int[] words) {
        this.length = length;
        rawStr = "";

        this.words = new int[length];
        for (int i = 0; i < length; ++i) {
            this.words[i] = words[i];
        }
    }

    public Document(int length, int[] words, String rawStr) {
        this.length = length;
        this.rawStr = rawStr;

        this.words = new int[length];
        for (int i = 0; i < length; ++i) {
            this.words[i] = words[i];
        }
    }

    public Document(Vector<Integer> doc) {
        this.length = doc.size();
        rawStr = "";
        this.words = new int[length];
        for (int i = 0; i < length; i++) {
            this.words[i] = doc.get(i);
        }
    }

    public Document(Vector<Integer> doc, String rawStr) {
        this.length = doc.size();
        this.rawStr = rawStr;
        this.words = new int[length];
        for (int i = 0; i < length; ++i) {
            this.words[i] = doc.get(i);
        }
    }
}
