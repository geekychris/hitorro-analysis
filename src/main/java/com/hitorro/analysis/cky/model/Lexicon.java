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
import com.hitorro.util.Counter;
import com.hitorro.util.CounterMap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Simple default implementation of a lexicon, which scores word,
 * tag pairs with a smoothed estimate of P(tag|word)/P(tag).
 */
public class Lexicon implements LexiconInterface {

    CounterMap<String, String> wordToTagCounters = new CounterMap<String, String>();
    double totalTokens = 0.0;
    double totalWordTypes = 0.0;
    Counter<String> tagCounter = new Counter<String>();
    Counter<String> wordCounter = new Counter<String>();
    Counter<String> typeTagCounter = new Counter<String>();

    /* Builds a lexicon from the observed tags in a list of training trees. */
    public Lexicon(List<Tree<String>> trainTrees) {
        for (Tree<String> trainTree : trainTrees) {
            List<String> words = trainTree.getYield();
            List<String> tags = trainTree.getPreTerminalYield();
            for (int position = 0; position < words.size(); position++) {
                String word = words.get(position);
                String tag = tags.get(position);
                tallyTagging(word, tag);
            }
        }
    }

    public Set<String> getAllTags() {
        return tagCounter.keySet();
    }

    public boolean isKnown(String word) {
        return wordCounter.keySet().contains(word);
    }

    public List<Rule>[] get(String toks[]) {
        List<Rule>[] res = (ArrayList<Rule>[]) Array.newInstance(ArrayList.class, toks.length);
        for (int i = 0; i < toks.length; i++) {
            res[i] = get(toks[i]);
        }
        return res;
    }

    public List<Rule> get(String token) {
        List<Rule> list = new ArrayList<Rule>();
        Counter<String> c = wordToTagCounters.getCounter(token);
        if (c.size() == 0) {
            // deal with missing word
            for (String tag : this.getAllTags()) {
                Rule r = new UnaryRule(tag, token, null);
                r.setScore(scoreTagging(token, tag));
                list.add(r);
            }

        }
        for (String k : c.keySet()) {
            UnaryRule un = new UnaryRule(k, token, null);
            un.score = this.scoreTagging(un);
            list.add(un);
        }
        return list;
    }

    public double scoreTagging(UnaryRule r) {
        return scoreTagging(r.child, r.getName());
    }

    /* Returns a smoothed estimate of P(word|tag) */
    public double scoreTagging(String word, String tag) {
        double p_tag = tagCounter.getCount(tag) / totalTokens;
        double c_word = wordCounter.getCount(word);
        double c_tag_and_word = wordToTagCounters.getCount(word, tag);
        if (c_word < 10) { // rare or unknown
            c_word += 1.0;
            c_tag_and_word += typeTagCounter.getCount(tag) / totalWordTypes;
        }
        double p_word = (1.0 + c_word) / (totalTokens + totalWordTypes);
        double p_tag_given_word = c_tag_and_word / c_word;
        return p_tag_given_word / p_tag * p_word;
    }

    private void tallyTagging(String word, String tag) {
        if (!isKnown(word)) {
            totalWordTypes += 1.0;
            typeTagCounter.incrementCount(tag, 1.0);
        }
        totalTokens += 1.0;
        tagCounter.incrementCount(tag, 1.0);
        wordCounter.incrementCount(word, 1.0);
        wordToTagCounters.incrementCount(word, tag, 1.0);
    }
}
