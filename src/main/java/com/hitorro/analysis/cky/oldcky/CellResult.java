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

import com.hitorro.util.core.ListUtil;
import com.hitorro.util.core.string.Fmt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class CellResult {
    public String rows[];
    int i;
    int j;
    Map<String, CellRow> map = new HashMap<String, CellRow>();

    public String toString() {
        return Fmt.S("[%s,%s], count: %s", i, j, map.size());
    }


    public int computeRows() {
        rows = new String[map.size()];
        int i = 0;
        for (CellRow key : map.values()) {
            rows[i++] = key.getShort();
        }
        return i;
    }

    public String getPacked(int row) {
        if (rows.length > row) {
            return rows[row];
        }
        return "";
    }


    public void set(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public void clear() {
        map.clear();
    }

    /**
     * @param rules
     * @param probIn
     */
    void add(ArrayList<Rule> rules, double probIn) {
        for (Rule r : rules) {
            CellRow cr = new CellRow(r, r.getProb() * probIn, null, null);
            add(cr);
        }
    }

    /**
     * Take account of unary rules by looking for unaries and resolving them.
     *
     * @param parser
     * @return
     */
    public int resolveUnaries(CKYParser parser) {
        boolean doLoop = true;
        int found = 0;
        ArrayList<CellRow> addList = new ArrayList<CellRow>();

        while (doLoop) {
            doLoop = false;
            for (CellRow cr : map.values()) {
                ArrayList<Rule> rules = parser.rules.get(cr.rule.from);
                //
                if (ListUtil.nullOrEmpty(rules)) {
                    continue;
                }
                for (Rule rule : rules) {
                    // replacement rules
                    addList.add(new CellRow(rule, rule.prob * cr.prob, cr, null));
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

    /**
     * Go through all the possible pairs of rules and see if the rule exists.  If it does then create a new result
     * with the left.prob * right.prob * rule.prob as the new probability.
     *
     * @param left
     * @param right
     * @param parser
     * @return
     */
    public int addIntersect(CellResult left, CellResult right, CKYParser parser) {
        int found = 0;
        for (CellRow l : left.map.values()) {
            for (CellRow r : right.map.values()) {
                String key = Rule.key(l.rule.from, r.rule.from);

                //Console.println("Looking for intersection %s", key);
                ArrayList<Rule> res = parser.rules.get(key);
                if (ListUtil.nullOrEmpty(res)) {
                    continue;
                }
                for (Rule rule : res) {
                    double val = rule.getProb() * l.prob * r.prob;
                    found += add(new CellRow(rule, val, l, r));

                }
            }
        }
        if (found > 0) {
            resolveUnaries(parser);
        }
        return found;
    }

    private int add(final CellRow r) {
        CellRow existing = map.get(r.rule.from);

        if (existing != null) {
            if (existing.prob < r.prob) {
                // new rule replaces
                map.put(r.rule.from, r);
                return 1;
            }
        } else {
            map.put(r.rule.from, r);
            return 1;
        }
        return 0;
    }
}
