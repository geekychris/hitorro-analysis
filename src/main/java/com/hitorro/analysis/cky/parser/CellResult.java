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

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import com.hitorro.analysis.cky.model.Grammar;
import com.hitorro.analysis.cky.model.Rule;
import com.hitorro.analysis.cky.model.UnaryRule;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class CellResult {
    public String rows[];
    int i;
    int j;
    private TObjectIntHashMap<String> map = new TObjectIntHashMap<String>();
    private int values[] = null;
    private CellRowCache cache;

    public CellResult(CellRowCache crc) {
        this.cache = crc;
    }

    public String toString() {
        return "[" + i + "," + i + "], count: " + map.size();
    }

    public final int[] getValues() {
        if (values == null) {
            values = new int[map.size()];
            int i = 0;
            for (TObjectIntIterator it = map.iterator(); it.hasNext(); ) {
                it.advance();
                values[i++] = it.value();
            }
            return values;
        }
        return values;
    }

    public final void set(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public final void clear() {
        map.clear();
        values = null;
    }

    /**
     * @param rules
     */
    final void add(List<Rule> rules) {

        for (Rule r : rules) {
            CellRow cr = cache.getRow();
            cr.set(r, r.getScore(), null, null);
            add(cr);
        }
    }

    /**
     * Take account of unary rules by looking for unaries and resolving them.
     *
     * @param parser
     * @return
     */
    //66% of time
    public final int resolveUnaries(CKYParser parser, LinkedList<CellRow> addList) {
        boolean doLoop = true;
        int found = 0;
        addList.clear();
        UnaryRule[] rules;
        Grammar g = parser.grammar;
        CellRowCache crc = parser.rowCache;
        while (doLoop) {
            doLoop = false;
            for (TObjectIntIterator it = map.iterator(); it.hasNext(); ) {
                it.advance();
                // 9% time
                CellRow cr = crc.getRow(it.value());
                if (cr != null) {
                    // this is  17% of time
                    rules = g.getUnaryRulesByChildArr(cr.getRule().getName());
                    //
                    if (rules != null) {
                        for (Rule rule : rules) {
                            // replacement rules
                            double d = rule.getScore() * cr.getProb();
                            //15% of time (the apply.get)
                            CellRow existing = cache.getRow(map.get(rule.getName()));

                            if (existing != null) {
                                if (existing.getProb() < d) {
                                    CellRow c = crc.getRow();
                                    c.set(rule, d, cr, null);
                                    addList.add(c);
                                }
                            } else {
                                CellRow c = crc.getRow();
                                c.set(rule, d, cr, null);
                                addList.add(c);
                            }
                        }
                    }
                }
            }
            for (CellRow cr2 : addList) {
                // takes care of dupes (highest probability wins)
                int tmpFound = add(cr2);
                if (tmpFound > 0) {
                    doLoop = true;
                    found += tmpFound;
                }
            }
            addList.clear();
        }
        return found;
    }

    public final int add(final CellRow r) {
        CellRow existing = cache.getRow(map.get(r.getRule().getName()));

        if (existing != null) {
            if (existing.getProb() < r.getProb()) {
                // new rule replaces
                map.put(r.getRule().getName(), r.getIndex());
                return 1;
            }
        } else {
            map.put(r.getRule().getName(), r.getIndex());
            return 1;
        }
        return 0;
    }

    public final TObjectIntHashMap<String> getMap() {
        return map;
    }
}
