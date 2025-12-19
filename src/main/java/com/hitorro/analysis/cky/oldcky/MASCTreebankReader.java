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
package com.hitorro.analysis.cky.oldcky;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * Taken from Stanford Class
 *
 * @author Dan Klein
 * @author Paul Baumstarck (adapted from PennTreebankReader 2008.4)
 */
public class MASCTreebankReader {

    public static Collection<Tree<String>> readTrees(String path,
                                                     int lowFileNum, int highFileNumber) {
        return new TreeCollection(path, lowFileNum, highFileNumber);
    }

    // public static Collection<Tree<String>> readTrees(String path) {
    // return readTrees(path, -1, Integer.MAX_VALUE);
    // }

    public static Collection<Tree<String>> readTrees(String path) {
        return new TreeCollection(path);
    }

    public static void main(String[] args) {
        Collection<Tree<String>> trees = readTrees(args[0], 0, 1000);
        System.out.println("Number of trees: " + trees.size());
        for (Tree<String> tree : trees) {
            tree = (new Trees.StandardTreeNormalizer()).transformTree(tree);
            System.out.println(Trees.PennTreeRenderer.render(tree));
        }
    }

    static class TreeCollection extends AbstractCollection<Tree<String>> {

        List<File> files;

        public TreeCollection(String path, int lowFileNum, int highFileNum) {
            this.files = getFilesUnder(path);
        }

        public TreeCollection(String path) {

            this.files = getFilesUnder(path);
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

        private List<File> getFilesUnder(String path) {
            File root = new File(path);
            List<File> files = new ArrayList<File>();
            addFilesUnder(root, files);
            return files;
        }

        private void addFilesUnder(File root, List<File> files) {
            if (root.isFile()) {
                files.add(root);
                return;
            }

            if (root.isDirectory()) {
                File[] children = root.listFiles();
                for (int i = 0; i < children.length; i++) {
                    File child = children[i];
                    if (!child.getName().endsWith("mrg")) {
                        continue;
                    }
                    addFilesUnder(child, files);
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
                        //System.out.println(file.getName());
                        nextTreeIterator = new Trees.PennTreeReader(new BufferedReader(new FileReader(file)));
                    } catch (FileNotFoundException e) {
                        //System.out.println("Exception "+e.toString());
                    }
                }
            }
        }

    }

}
