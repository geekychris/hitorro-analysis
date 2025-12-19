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

import com.hitorro.analysis.cky.ling.Tree;
import com.hitorro.analysis.cky.model.Grammar;
import com.hitorro.analysis.cky.model.LexiconInterface;
import com.hitorro.analysis.cky.model.Rule;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 428ms avg   P: 75.81   R: 70.55   F1: 73.09   EX: 19.39
 * 422ms avg   P: 75.81   R: 70.55   F1: 73.09   EX: 19.39
 * 405ms avg   P: 75.81   R: 70.55   F1: 73.09   EX: 19.39
 * 403ms avg   P: 75.81   R: 70.55   F1: 73.09   EX: 19.39
 * <p>
 * 2nd order markov all non terminal tags
 * 522ms avg   P: 74.54   R: 72.21   F1: 73.36   EX: 18.18
 */
public class CKYParser {
    public CellRowCache rowCache = new CellRowCache();
    Grammar grammar;
    LexiconInterface lexicon;
    private CellResult results[][] = null;
    private int n;

    public CKYParser(Grammar grammar, LexiconInterface lexicon) {
        this.grammar = grammar;
        this.lexicon = lexicon;
    }

    public Tree<String> getBacktrace(String token) {
        int cr = results[0][n].getMap().get(token);
        if (cr == 0) {
            return null;
        }
        return rowCache.getRow(cr).getTree();
    }

    public void printCounts() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(results[i][j].getMap().size());
                System.out.print(" ,");
            }
            System.out.println();
        }
    }

    public final boolean parse(String tokens[]) {
        rowCache.reset();
        n = tokens.length;
        results = fill(results, tokens.length + 1);
        // fill 0,0->0,l-1 with POS from the input tokens (this would ultimately be a toks to POS tags
        boolean allThere = true;
        LinkedList<CellRow> addList = new LinkedList<CellRow>();
        List<Rule>[] rules = lexicon.get(tokens);
        for (int i = 0; i < tokens.length; i++) {
            int j = i + 1;
            List<Rule> r = rules[i];

            CellResult cr = results[i][j];
            cr.clear();

            cr.add(r);
            cr.resolveUnaries(this, addList);
        }

        StringBuilder sb = new StringBuilder();
        int found1;

        CellResult lm;
        CellResult mr;
        Map<String, String[]> leftDepend = grammar.leftToRightDependencyArr;
        int wordsMinusSpan;
        int end;
        int lRows[];
        String rightKeys[];
        for (int span = 2; span <= n; span++) {
            wordsMinusSpan = n - span;
            for (int begin = 0; begin <= wordsMinusSpan; begin++) {
                end = begin + span;
                CellResult cr = results[begin][end];
                cr.clear();
                for (int split = begin + 1; split <= end - 1; split++) {
                    lm = results[begin][split];
                    mr = results[split][end];

                    found1 = 0;
                    lRows = lm.getValues();

                    for (int lRow : lRows) {
                        CellRow lRowRow = rowCache.getRow(lRow);
                        rightKeys = leftDepend.get(lRowRow.getRule().getName());

                        if (rightKeys != null) {
                            found1 = compute(sb, found1, mr, cr, lRowRow, rightKeys);
                        }
                    }
                    if (found1 > 0) {
                        cr.resolveUnaries(this, addList);
                    }
                }
            }

        }

        return true;
    }

    private final int compute(StringBuilder sb, int found1, CellResult mr, CellResult cr, CellRow lRow, String[] rightKeys) {
        int r;
        int rightSize = rightKeys.length;
        String rKey;
        List<Rule> res;
        Rule lRule = lRow.getRule();
        String key;
        for (int ri = 0; ri < rightSize; ri++) {
            rKey = rightKeys[ri];
            r = mr.getMap().get(rKey);
            // see if the right node actually contains that relationship
            if (r != 0) {
                // combine parent
                sb.setLength(0);
                sb.append(lRule.getName());
                sb.append("#");
                sb.append(rKey);
                key = sb.toString();

                //System.out.println("Looking for intersection " +  key);
                res = grammar.get(key);
                if (res != null) {
                    CellRow rowTemp = rowCache.getRow(r);
                    double d = lRow.getProb() * rowTemp.getProb();
                    for (Rule rule : res) {
                        double val = rule.getScore() * d;
                        CellRow row = rowCache.getRow();
                        row.set(rule, val, lRow, rowTemp);
                        found1 += cr.add(row);

                    }
                }
            }
        }
        return found1;
    }

    private final CellResult[][] fill(CellResult[][] old, int size) {
        if (old != null && old.length >= size) {
            return old;
        }
        CellResult[][] res = new CellResult[size][size];

        // some point recoup the cells
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                CellResult c = new CellResult(this.rowCache);
                c.i = i;
                c.j = j;
                res[i][j] = c;
            }
        }
        return res;
    }
}
