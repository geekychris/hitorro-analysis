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
 * @author Dan Klein
 */
public class CollectionUtils {
    public static <E extends Comparable<E>> List<E> sort(Collection<E> c) {
        List<E> list = new ArrayList<E>(c);
        Collections.sort(list);
        return list;
    }

    public static <E> List<E> sort(Collection<E> c, Comparator<E> r) {
        List<E> list = new ArrayList<E>(c);
        Collections.sort(list, r);
        return list;
    }

    public static <K, V> void addToValueList(Map<K, List<V>> map, K key, V value) {
        List<V> valueList = map.get(key);
        if (valueList == null) {
            valueList = new ArrayList<V>();
            map.put(key, valueList);
        }
        valueList.add(value);
    }

    public static <K, V> void addToValueHashset(Map<K, HashSet<V>> map, K key, V value) {
        HashSet<V> valueList = map.get(key);
        if (valueList == null) {
            valueList = new HashSet<V>();
            map.put(key, valueList);
        }
        valueList.add(value);
    }

    public final static <K, V> List<V> getValueList(Map<K, List<V>> map, K key) {
        List<V> valueList = map.get(key);
        if (valueList == null) {
            return Collections.emptyList();
        }
        return valueList;
    }
}
