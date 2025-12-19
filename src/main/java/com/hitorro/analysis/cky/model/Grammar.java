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
package com.hitorro.analysis.cky.model;

import com.hitorro.analysis.cky.ling.Tree;
import com.hitorro.analysis.cky.parser.CellRow;
import com.hitorro.analysis.cky.util.CollectionUtils;
import com.hitorro.util.Counter;
import com.hitorro.util.core.Console;
import com.hitorro.util.core.IDObjectMap;
import com.hitorro.util.core.string.StringUtil;

import java.util.*;

/**
 * Simple implementation of a PCFG grammar, offering the ability to
 * look up rules by their child symbols.  Rule probability estimates
 * are just relative frequency estimates off of training trees.
 */
public class Grammar {
    public static final Rule[] empty = new Rule[0];
    public Map<String, String[]> leftToRightDependencyArr = new HashMap<String, String[]>();
    public IDObjectMap<Rule> idMap = new IDObjectMap();
    Map<String, List<UnaryRule>> unaryRulesByChild =
            new HashMap<String, List<UnaryRule>>();
    Map<String, UnaryRule[]> unaryRulesByChildArr =
            new HashMap<String, UnaryRule[]>();
    Map<String, List<Rule>> combinedRules =
            new HashMap<String, List<Rule>>();
    Map<String, Rule[]> combinedRulesArr = new HashMap<String, Rule[]>();
    Map<String, HashSet<String>> leftToRightDependency = new HashMap<String, HashSet<String>>();

    /* A builds PCFG using the observed counts of binary and unary
     * productions in the training trees to estimate the probabilities
     * for those rules.
     **/
    public Grammar(List<Tree<String>> trainTrees) {
        Counter<UnaryRule> unaryRuleCounter = new Counter<UnaryRule>();
        Counter<BinaryRule> binaryRuleCounter = new Counter<BinaryRule>();
        Counter<String> symbolCounter = new Counter<String>();
        for (Tree<String> trainTree : trainTrees) {
            tallyTree(trainTree, symbolCounter, unaryRuleCounter, binaryRuleCounter);
        }
        for (UnaryRule unaryRule : unaryRuleCounter.keySet()) {
            double unaryProbability =
                    unaryRuleCounter.getCount(unaryRule) /
                            symbolCounter.getCount(unaryRule.getName());
            unaryRule.setScore(unaryProbability);
            addUnary(unaryRule);
        }
        for (BinaryRule binaryRule : binaryRuleCounter.keySet()) {
            double binaryProbability =
                    binaryRuleCounter.getCount(binaryRule) /
                            symbolCounter.getCount(binaryRule.getName());
            binaryRule.setScore(binaryProbability);
            addBinary(binaryRule);
        }

        finalizeRightDependents();
    }

    public final static String getKey(CellRow l, CellRow r) {
        return StringUtil.strcat(l.getRule().getName(), "#", r.getRule().getName());
    }

    public static String getKey(Rule l, Rule r) {
        return StringUtil.strcat(l.getName(), "#", r.getName());
    }

    public static String getKey(String l, String r) {
        return StringUtil.strcat(l, "#", r);
    }

    private void finalizeRightDependents() {
        for (String key : leftToRightDependency.keySet()) {
            HashSet<String> h = leftToRightDependency.get(key);
            String arr[] = new String[h.size()];
            int i = 0;
            for (String s : h) {
                arr[i++] = s;
            }
            leftToRightDependencyArr.put(key, arr);
        }
        leftToRightDependency.clear();

        for (Map.Entry<String, List<UnaryRule>> entry : unaryRulesByChild.entrySet()) {
            UnaryRule arr[] = new UnaryRule[entry.getValue().size()];
            int ind = 0;

            for (UnaryRule u : entry.getValue()) {
                if (u == null) {
                    Console.println();
                }
                arr[ind++] = u;
            }
            unaryRulesByChildArr.put(entry.getKey(), arr);
            if (ind < entry.getValue().size()) {
                Console.println();
            }

        }

        for (Map.Entry<String, List<Rule>> entry : combinedRules.entrySet()) {

            Rule arr[] = new Rule[entry.getValue().size()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = entry.getValue().get(i);
            }
            combinedRulesArr.put(entry.getKey(), arr);
        }
    }

    public final List<Rule> get(String key) {
        return combinedRules.get(key);
    }

    public final Rule[] getArr(String key) {
        return unaryRulesByChildArr.get(key);
    }


    public final List<UnaryRule> getUnaryRulesByChild(String child) {
        return CollectionUtils.getValueList(unaryRulesByChild, child);
    }

    public final UnaryRule[] getUnaryRulesByChildArr(String id) {
        return unaryRulesByChildArr.get(id);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<String> ruleStrings = new ArrayList<String>();

        for (String child : unaryRulesByChild.keySet()) {
            for (UnaryRule unaryRule : getUnaryRulesByChild(child)) {
                ruleStrings.add(unaryRule.toString());
            }
        }
        for (String ruleString : CollectionUtils.sort(ruleStrings)) {
            sb.append(ruleString);
            sb.append("\n");
        }
        return sb.toString();
    }

    private void addBinary(BinaryRule binaryRule) {
        String key = getKey(binaryRule.getLeftChild(), binaryRule.getRightChild());
        CollectionUtils.addToValueList(combinedRules, key, binaryRule);
        CollectionUtils.addToValueHashset(leftToRightDependency, binaryRule.getLeftChild(), binaryRule.getRightChild());
    }

    private void addUnary(UnaryRule unaryRule) {
        CollectionUtils.addToValueList(unaryRulesByChild,
                unaryRule.getChild(), unaryRule);

        CollectionUtils.addToValueList(combinedRules, unaryRule.getChild(), unaryRule);
    }


    private void tallyTree(Tree<String> tree, Counter<String> symbolCounter,
                           Counter<UnaryRule> unaryRuleCounter,
                           Counter<BinaryRule> binaryRuleCounter) {
        if (tree.isLeaf()) {
            return;
        }
        if (tree.isPreTerminal()) {
            return;
        }
        if (tree.getChildren().size() == 1) {
            UnaryRule unaryRule = makeUnaryRule(tree);
            symbolCounter.incrementCount(tree.getLabel(), 1.0);
            unaryRuleCounter.incrementCount(unaryRule, 1.0);
        }
        if (tree.getChildren().size() == 2) {
            BinaryRule binaryRule = makeBinaryRule(tree);
            symbolCounter.incrementCount(tree.getLabel(), 1.0);
            binaryRuleCounter.incrementCount(binaryRule, 1.0);
        }
        if (tree.getChildren().size() < 1 || tree.getChildren().size() > 2) {
            throw new RuntimeException("Attempted to construct a Grammar with an illegal tree: " + tree);
        }
        for (Tree<String> child : tree.getChildren()) {
            tallyTree(child, symbolCounter, unaryRuleCounter, binaryRuleCounter);
        }
    }

    private UnaryRule makeUnaryRule(Tree<String> tree) {
        return new UnaryRule(tree.getLabel(), tree.getChildren().get(0).getLabel(), this);
    }

    private BinaryRule makeBinaryRule(Tree<String> tree) {
        return new BinaryRule(tree.getLabel(), tree.getChildren().get(0).getLabel(),
                tree.getChildren().get(1).getLabel(), this);
    }
}
