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
package com.hitorro.analysis.mstparser;


import gnu.trove.list.TLinkableAdapter;

/**
 * A simple class holding a feature index and value that can be used in a TLinkedList. <p/> <p> Created: Sat Nov 10
 * 15:25:10 2001 </p>
 *
 * @author Jason Baldridge
 * @version $Id: TLinkedList.java,v 1.5 2005/03/26 17:52:56 ericdf Exp $
 * @see FeatureVector
 */

public final class Feature extends TLinkableAdapter {

    public int index;
    public double value;

    public Feature(int i, double v) {
        index = i;
        value = v;
    }

    public final Feature clone() {
        return new Feature(index, value);
    }

    public final Feature negation() {
        return new Feature(index, -value);
    }

    public final String toString() {
        return index + "=" + value;
    }

}
