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
import com.hitorro.analysis.cky.util.ConcatenationIterator;
import com.hitorro.obj.core.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * @author Dan Klein
 * @author Paul Baumstarck (adapted from PennTreebankReader 2008.4)
 */
public class MASCTreebankReader extends AbstractCollection<Tree<String>> {

    private List<File> files;

    public MASCTreebankReader(File path, int lowFileNum, int highFileNum) {
        NumberRangeFileFilter fileFilter = new NumberRangeFileFilter(".mrg", lowFileNum, highFileNum);
        this.files = getFilesUnder(path, fileFilter);
    }

    public MASCTreebankReader(File path) {
        NumberRangeFileFilter fileFilter = new NumberRangeFileFilter() {
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return true;
                }
                String name = pathname.getName();
                return name.endsWith(".mrg");
            }
        };
        this.files = getFilesUnder(path, fileFilter);
    }

    public static Collection<Tree<String>> readTrees(File path,
                                                     int lowFileNum, int highFileNumber) {
        return new MASCTreebankReader(path, lowFileNum, highFileNumber);
    }

    public static Collection<Tree<String>> readTrees(File path) {
        return new MASCTreebankReader(path);
    }

    public Iterator<Tree<String>> iterator() {
        return new ConcatenationIterator<Tree<String>>(new TreeIteratorIterator(files));
    }

    public int size() {
        int size = 0;
        Iterator i = iterator();
        while (i.hasNext()) {
            size++;
            i.next();
        }
        return size;
    }

    private List<File> getFilesUnder(File root,
                                     NumberRangeFileFilter fileFilter) {
        List<File> files = new ArrayList<File>();
        addFilesUnder(root, files, fileFilter);
        return files;
    }

    private void addFilesUnder(File root, List<File> files,
                               NumberRangeFileFilter fileFilter) {
        if (!fileFilter.acceptSequential(root)) {
            return;
        }
        if (root.isFile()) {
            files.add(root);
            return;
        }
        if (root.isDirectory()) {
            File[] children = root.listFiles();
            for (int i = 0; i < children.length; i++) {
                File child = children[i];
                addFilesUnder(child, files, fileFilter);
            }
        }
    }

    static class TreeIteratorIterator implements
            Iterator<Iterator<Tree<String>>> {
        Iterator<File> fileIterator;
        Iterator<Tree<String>> nextTreeIterator;

        TreeIteratorIterator(List<File> files) {
            this.fileIterator = files.iterator();
            advance();
        }

        public boolean hasNext() {
            return nextTreeIterator != null;
        }

        public Iterator<Tree<String>> next() {
            Iterator<Tree<String>> currentTreeIterator = nextTreeIterator;
            advance();
            return currentTreeIterator;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void advance() {
            nextTreeIterator = null;
            while (nextTreeIterator == null && fileIterator.hasNext()) {
                try {
                    File file = fileIterator.next();
                    System.out.println(file.getName());
                    nextTreeIterator = new PennTreeReader(new BufferedReader(new FileReader(file)));
                } catch (FileNotFoundException e) {
                    Log.util.error("Unable to read file %s %e", e, e);
                }
            }
        }
    }

}

