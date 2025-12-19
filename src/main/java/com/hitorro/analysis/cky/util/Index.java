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
package com.hitorro.analysis.cky.util;

import java.util.*;

/**
 * Maintains a two-way apply between a setPhrases of objects and contiguous integers from
 * 0 to the number of objects.  Use get(i) to look up object i, and
 * indexOf(object) to look up the index of an object.
 *
 * @author Dan Klein
 */
public class Index<E> extends AbstractList<E> {
    List<E> objects;
    Map<E, Integer> indexes;

    public Index() {
        objects = new ArrayList<E>();
        indexes = new HashMap<E, Integer>();
    }

    public Index(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    /**
     * Return the object with the given index
     *
     * @param index
     */
    public E get(int index) {
        return objects.get(index);
    }

    /**
     * Returns the number of objects indexed.
     */
    public int size() {
        return objects.size();
    }

    /**
     * Returns the index of the given object, or -1 if the object is not present
     * in the indexer.
     *
     * @param o
     */
    public int indexOf(Object o) {
        Integer index = indexes.get(o);
        if (index == null) {
            return -1;
        }
        return index;
    }

    /**
     * Constant time override for contains.
     */
    public boolean contains(Object o) {
        return indexes.keySet().contains(o);
    }

    /**
     * Add an element to the indexer.  If the element is already in the indexer,
     * the indexer is unchanged (and false is returned).
     *
     * @param e
     */
    public boolean add(E e) {
        if (contains(e)) {
            return false;
        }
        objects.add(e);
        indexes.put(e, size() - 1);
        return true;
    }

}
