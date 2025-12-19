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
import com.hitorro.analysis.cky.ling.transformers.AnnotateTreeMapper;
import com.hitorro.analysis.cky.ling.transformers.StandardTreeNormalizer;
import com.hitorro.analysis.cky.ling.transformers.VeritcalMarkovizationMapper;
import com.hitorro.analysis.cky.parser.PCFGParserTester;
import com.hitorro.util.core.events.cache.HashCache;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.json.keys.FileProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class GrammarSingletonMapper extends BaseMapper<String, Grammar> {
    public static final FileProperty Path = new FileProperty("cky.grammarpath", "location of where the cky stuff is ", "${ht_data}/parser/");
    public static String EventName = "GrammarSingleton";
    public static final HashCache<String, Grammar> singleton = new HashCache<String, Grammar>(EventName, new GrammarSingletonMapper());

    /**
     * do the standard cleaning, 2nd order vertical markovization and then binarize
     */
    public static BaseMapper<Tree<String>, Tree<String>> chain = StandardTreeNormalizer.instance.combine(VeritcalMarkovizationMapper.instance).
            combine(AnnotateTreeMapper.instance);

    public static List<Tree<String>> readMASCTrees(File basePath, int low, int high) {
        AbstractIterator<Tree<String>> iter = PCFGParserTester.readMASCTrees(basePath, low, high);
        return iter.map(chain).toCollection(new ArrayList<Tree<String>>(), -1);
    }

    public Grammar apply(String key) {
        if (StringUtil.nullOrEmptyString(key)) {
            key = "masc/train";
        }
        File path = Path.apply();
        path = new File(path, key);

        // Convert this into a apply train
        List<Tree<String>> trainTrees = readMASCTrees(path, 0, 38);

        Grammar grammar = new Grammar(trainTrees);
        return grammar;
    }
}
