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
package com.hitorro.analysis.mstparser;

import com.hitorro.analysis.cky.ling.Tree;
import com.hitorro.util.core.string.Fmt;

import java.util.ArrayList;

/**
 *
 */
public class DependencyResult {
    public DependencyInstance instance;
    public String posNoRoot[];
    public String labels[];
    public int heads[];
    public double confidenceScores[];

    public DependencyResult(DependencyInstance instance, String posNoRoot[], String labels[], int heads[], double confidenceScores[]) {
        this.instance = instance;
        this.posNoRoot = posNoRoot;
        this.labels = labels;
        this.confidenceScores = confidenceScores;
        // recode heads to start from 0
        this.heads = heads;
        for (int i = 0; i < heads.length; i++) {
            heads[i] = heads[i] - 1;
        }
    }

    public Tree<String> getTree() {
        // start with the root node (-1)
        int root = getIndex(0, -1);
        if (root == -1) {
            return new Tree<String>("EMPTY");
        }
        ArrayList<Tree<String>> c1 = new ArrayList<Tree<String>>();
        Tree<String> rootN = new Tree<String>("", c1);
        setLabel(rootN, root);

        ArrayList<Tree<String>> children = new ArrayList<Tree<String>>();
        rootN.setChildren(children);
        fillChildren(children, root);
        return rootN;
    }

    /**
     * Look for all the listChildren that point to this root position.
     *
     * @param children
     * @param root
     * @return
     */
    private void fillChildren(ArrayList<Tree<String>> children, int root) {
        int ind = getIndex(0, root);
        while (ind != -1) {
            ArrayList<Tree<String>> c1 = new ArrayList<Tree<String>>();
            Tree<String> curr = new Tree<String>("", c1);
            setLabel(curr, ind);
            children.add(curr);
            fillChildren(c1, ind);
            ind = getIndex(ind + 1, root);
        }
    }

    private void setLabel(Tree<String> tree, final int root) {
        String label = Fmt.S("%s %s(%s)", instance.forms[root + 1], labels[root], instance.postags[root + 1]);
        tree.setLabel(label);
    }

    private int getIndex(int start, int lookingFor) {
        for (int i = start; i < heads.length; i++) {
            if (heads[i] == lookingFor) {
                return i;
            }
        }
        return -1;
    }

}
