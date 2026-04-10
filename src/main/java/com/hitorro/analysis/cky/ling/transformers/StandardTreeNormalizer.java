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


public class StandardTreeNormalizer extends BaseMapper<Tree<String>, Tree<String>> {
    public static StandardTreeNormalizer instance = new StandardTreeNormalizer();

    EmptyNodeStripper emptyNodeStripper = new EmptyNodeStripper();
    XOverXRemover<String> xOverXRemover = new XOverXRemover<String>();
    FunctionNodeStripper functionNodeStripper = new FunctionNodeStripper();

    public Tree<String> apply(Tree<String> tree) {
        tree = functionNodeStripper.apply(tree);
        tree = emptyNodeStripper.apply(tree);
        tree = xOverXRemover.apply(tree);
        return tree;
    }
}
