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

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA. User: chris Date: 4/28/12 Time: 1:19 PM To change this template use File | Settings |
 * File Templates.
 */
public class PennTreebankReader {

    public static Collection<Tree<String>> readTrees(String path, int lowFileNum, int highFileNumber) {
        return new TreeCollection(path, lowFileNum, highFileNumber);
    }

//   public static Collection<Tree<String>> readTrees(String path) {
//     return readTrees(path, -1, Integer.MAX_VALUE);
//   }

    public static Collection<Tree<String>> readTrees(String path) {
        return new TreeCollection(path);
    }

    public static void main(String[] args) {
        Collection<Tree<String>> trees = readTrees(args[0]);
        for (Tree<String> tree : trees) {
            tree = (new Trees.StandardTreeNormalizer()).transformTree(tree);
            System.out.println(Trees.PennTreeRenderer.render(tree));
        }
    }

    static class TreeCollection extends AbstractCollection<Tree<String>> {

        List<File> files;

        public TreeCollection(String path, int lowFileNum, int highFileNum) {
            FileFilter fileFilter = new NumberRangeFileFilter(".mrg", lowFileNum, highFileNum, true);
            this.files = getFilesUnder(path, fileFilter);
        }

        public TreeCollection(String path) {
            FileFilter fileFilter = new FileFilter() {
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

        private List<File> getFilesUnder(String path, FileFilter fileFilter) {
            File root = new File(path);
            List<File> files = new ArrayList<File>();
            addFilesUnder(root, files, fileFilter);
            return files;
        }

        private void addFilesUnder(File root, List<File> files, FileFilter fileFilter) {
            if (!fileFilter.accept(root)) {
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

        static class TreeIteratorIterator implements Iterator<Iterator<Tree<String>>> {
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
                        nextTreeIterator = new Trees.PennTreeReader(new BufferedReader(new FileReader(file)));
                    } catch (FileNotFoundException e) {
                    }
                }
            }
        }

    }

}

