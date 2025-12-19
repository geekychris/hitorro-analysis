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

import com.hitorro.util.Counter;
import com.hitorro.util.CounterMap;
import com.hitorro.util.PriorityQueue;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods related to Counters and CounterMaps.
 *
 * @author Dan Klein
 */
public class Counters {

    public static <E> Counter<E> normalize(Counter<E> counter) {
        Counter<E> normalizedCounter = new Counter<E>();
        double total = counter.totalCount();
        for (E key : counter.keySet()) {
            normalizedCounter.setCount(key, counter.getCount(key) / total);
        }
        return normalizedCounter;
    }

    public static <K, V> CounterMap<K, V> conditionalNormalize(CounterMap<K, V> counterMap) {
        CounterMap<K, V> normalizedCounterMap = new CounterMap<K, V>();
        for (K key : counterMap.keySet()) {
            Counter<V> normalizedSubCounter = normalize(counterMap.getCounter(key));
            for (V value : normalizedSubCounter.keySet()) {
                double count = normalizedSubCounter.getCount(value);
                normalizedCounterMap.setCount(key, value, count);
            }
        }
        return normalizedCounterMap;
    }

    public static <E> String toBiggestValuesFirstString(Counter<E> c) {
        return c.asPriorityQueue().toString();
    }

    public static <E> String toBiggestValuesFirstString(Counter<E> c, int k) {
        PriorityQueue<E> pq = c.asPriorityQueue();
        PriorityQueue<E> largestK = new PriorityQueue<E>();
        while (largestK.size() < k && pq.hasNext()) {
            double firstScore = pq.getPriority();
            E first = pq.next();
            largestK.add(first, firstScore);
        }
        return largestK.toString();
    }

    public static <E> List<E> sortedKeys(Counter<E> counter) {
        List<E> sortedKeyList = new ArrayList<E>();
        PriorityQueue<E> pq = counter.asPriorityQueue();
        while (pq.hasNext()) {
            sortedKeyList.add(pq.next());
        }
        return sortedKeyList;
    }

}
