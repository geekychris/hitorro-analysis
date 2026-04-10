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
package com.hitorro.analysis.cky.parser;


public abstract class ObjectBlockCache<E> {
    int index = 1;
    private int bits = 12;
    private int blockSize = 1 << bits;
    private int cacheSets = 1024;
    private int cacheSetIncrement = 1024;
    private E[][] pages = getArrayArray(cacheSets);

    public ObjectBlockCache() {

    }

    public void reset() {
        index = 1;
    }

    public final E getRow(int ind) {
        int pageNumber = ind >> bits;
        int offset = ind % blockSize;
        if (pageNumber >= cacheSets) {
            return null;
        }
        return pages[pageNumber][offset];
    }

    public final E getRow() {
        //return CellRow.get();
        int pageNumber = index >> bits;
        int offset = index % blockSize;
        if (pageNumber >= cacheSets) {
            // too small
            E arr2[][] = getArrayArray(pages.length + cacheSetIncrement);
            System.arraycopy(pages, 0, arr2, 0, pages.length);
            cacheSets = pages.length + cacheSetIncrement;
            pages = arr2;
        }
        E page[] = pages[pageNumber];
        if (page == null) {
            page = createBlock(pageNumber << bits);
            pages[pageNumber] = page;
        }
        E cr = page[offset];
        index++;
        return cr;
    }

    private final E[] createBlock(int index) {
        E arr[] = getArray(blockSize);
        for (int i = 0; i < blockSize; i++) {
            arr[i] = getNew(index + i);
        }
        return arr;
    }

    public abstract E getNew(int index);

    public abstract E[] getArray(int size);

    public abstract E[][] getArrayArray(int size);

}
