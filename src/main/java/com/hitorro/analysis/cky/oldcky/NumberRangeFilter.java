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

import java.io.File;
import java.io.FileFilter;

/**
 * Accepts files based on the (last) number in their filename.  Optionally restricts based on extensions, as well.
 *
 * @author Dan Klein
 */
class NumberRangeFileFilter implements FileFilter {
    int highFileNum;
    int lowFileNum;
    String extension;
    boolean recurse;
    int i;

    public NumberRangeFileFilter() {
    }

    public NumberRangeFileFilter(String extension, int lowFileNum, int highFileNum, boolean recurse) {
        this.i = -1; // initialize file counter to zero
        this.highFileNum = highFileNum;
        this.lowFileNum = lowFileNum;
        this.extension = extension;
        this.recurse = recurse;
    }

    public boolean accept(File pathname) {
        if (pathname.isDirectory()) {
            return recurse;
        }
        String name = pathname.getName();
        if (!name.endsWith(extension)) {
            return false;
        }
        int lastNumberIndex = getLastNumberIndex(name);
        if (lastNumberIndex == -1) {
            return false;
        }
        int numEndLoc = lastNumberIndex + 1;
        int numStartLoc = getLastNonNumberIndex(name, lastNumberIndex) + 1;
        int fileNum = Integer.parseInt(name.substring(numStartLoc, numEndLoc));
        if (fileNum >= lowFileNum && fileNum <= highFileNum) {
            return true;
        }
        return false;
    }

    public boolean acceptSequential(File pathname) {
        if (pathname.isDirectory()) {
            return recurse;
        }
        String name = pathname.getName();
        if (!name.endsWith(extension)) {
            return false;
        }
        ++i;
        return i >= lowFileNum && i <= highFileNum;
    }

    private int getLastNonNumberIndex(String name, int lastNumberIndex) {
        int index = lastNumberIndex - 1;
        while (index >= 0 && Character.isDigit(name.charAt(index))) {
            index--;
        }
        if (index < -1) {
            return -1;
        }
        return index;
    }

    private int getLastNumberIndex(String name) {
        int index = name.length() - 1;
        while (index >= 0 && !Character.isDigit(name.charAt(index))) {
            index--;
        }
        return index;
    }
}
