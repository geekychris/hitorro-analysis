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

/**
 * Created with IntelliJ IDEA. User: chris Date: 4/26/12 Time: 10:54 PM To change this template use File | Settings |
 * File Templates.
 */

import java.util.*;

/**
 * Concatenates an iterator over iterators into one long iterator.
 *
 * @author Dan Klein
 */
public class ConcatenationIterator<E> implements Iterator<E> {

    Iterator<Iterator<E>> sourceIterators;
    Iterator<E> currentIterator;
    Iterator<E> lastIteratorToReturn;

    public ConcatenationIterator(Iterator<Iterator<E>> sourceIterators) {
        this.sourceIterators = sourceIterators;
        this.currentIterator = (new ArrayList<E>()).iterator();
        this.lastIteratorToReturn = null;
        advance();
    }

    public ConcatenationIterator(Collection<Iterator<E>> iteratorCollection) {
        this(iteratorCollection.iterator());
    }

    public static void main(String[] args) {
        List<String> list0 = Collections.emptyList();
        List<String> list1 = Arrays.asList("a b c d".split(" "));
        List<String> list2 = Arrays.asList("e f".split(" "));
        List<Iterator<String>> iterators = new ArrayList<Iterator<String>>();
        iterators.add(list1.iterator());
        iterators.add(list0.iterator());
        iterators.add(list2.iterator());
        iterators.add(list0.iterator());
        Iterator<String> iterator = new ConcatenationIterator<String>(iterators);
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    public boolean hasNext() {
        if (currentIterator.hasNext()) {
            return true;
        }
        return false;
    }

    public E next() {
        if (currentIterator.hasNext()) {
            while (true) {
                try {
                    E e = currentIterator.next();
                    lastIteratorToReturn = currentIterator;
                    advance();
                    return e;
                } catch (Exception e) {
                    System.err.println("bad file");
                    if (!sourceIterators.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    currentIterator = sourceIterators.next();
                }
            }
        }
        throw new NoSuchElementException();
    }

    private void advance() {
        while (!currentIterator.hasNext() && sourceIterators.hasNext()) {
            currentIterator = sourceIterators.next();
        }
    }

    public void remove() {
        if (lastIteratorToReturn == null) {
            throw new IllegalStateException();
        }
        currentIterator.remove();
    }
}

