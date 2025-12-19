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
package com.hitorro.analysis.cky.ling.transformers;

import com.hitorro.analysis.cky.ling.Tree;
import com.hitorro.util.core.iterator.mappers.BaseMapper;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FunctionNodeStripper extends BaseMapper<Tree<String>, Tree<String>> {
    public Tree<String> apply(Tree<String> tree) {
        String transformedLabel = tree.getLabel();
        int cutIndex = transformedLabel.indexOf('-');
        int cutIndex2 = transformedLabel.indexOf('=');
        if (cutIndex2 > 0 && (cutIndex2 < cutIndex || cutIndex == -1)) {
            cutIndex = cutIndex2;
        }
        cutIndex2 = transformedLabel.indexOf('^');
        if (cutIndex2 > 0 && (cutIndex2 < cutIndex || cutIndex == -1)) {
            cutIndex = cutIndex2;
        }
        cutIndex2 = transformedLabel.indexOf(':');
        if (cutIndex2 > 0 && (cutIndex2 < cutIndex || cutIndex == -1)) {
            cutIndex = cutIndex2;
        }

        if (cutIndex > 0 && !tree.isLeaf()) {
            transformedLabel = new String(transformedLabel.substring(0,
                    cutIndex));
        }
        if (tree.isLeaf()) {
            return new Tree<String>(transformedLabel);
        }
        List<Tree<String>> transformedChildren = new ArrayList<Tree<String>>();
        for (Tree<String> child : tree.getChildren()) {
            transformedChildren.add(apply(child));
        }
        return new Tree<String>(transformedLabel, transformedChildren);
    }
}
