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
package com.hitorro.analysis.wekaparser;

import com.hitorro.util.core.Console;
import com.hitorro.util.core.Log;
import com.hitorro.util.core.classes.ClassUtil;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.io.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class ModelReader<T extends Node> {
    private static Map<String, Class> impls = new HashMap();

    static {
        addImpl(AdTreeVisitor.class);
    }

    private List<String> buffList = new ArrayList();
    private ModelFeatures mf;
    private TreeVisitor<T> tv;
    private T root;

    public ModelReader() {

    }

    public static void main(String args[]) throws FileNotFoundException {
        File f = new File("/com/hitorro/platform/treeexamples/spam.adtree");
        ModelReader<ADNode> mr = getModelReader(f);
        ADNode node = mr.getRoot();
        Console.println();
        StringBuilder sb = new StringBuilder();
        Console.println(node.getCode());
    }

    private static ModelReader getModelReader(final File f) throws FileNotFoundException {
        ModelReader fr = new ModelReader();
        fr.read(f);
        ModelFeatures mbv = new ModelFeatures();
        fr.extract(mbv);
        fr.mf = mbv;
        fr.tv = getTreeVisitor(mbv);
        if (fr.tv == null) {
            return null;
        }

        fr.extract(fr.tv);
        fr.root = fr.tv.getRoot();
        Console.println("%s", fr.tv.toString());
        return fr;
    }

    private static void addImpl(Class c) {
        TreeVisitor tv = (TreeVisitor) ClassUtil.getInstanceSwallowError(c, TreeVisitor.class);
        if (tv != null) {
            impls.put(tv.getImplements(), c);
        }
    }

    private static TreeVisitor getTreeVisitor(ModelFeatures mf) {
        String scheme = mf.getScheme();
        if (StringUtil.nullOrEmptyString(scheme)) {
            Log.decisiontrees.error("no scheme defined for decision tree");
            return null;
        }
        String clazz = StringUtil.getNthPart(scheme, " ", 0);
        Class c = impls.get(clazz);
        if (c == null) {
            Log.decisiontrees.error("no tree implementation for scheme: %s", clazz);
            return null;
        }
        return (TreeVisitor) ClassUtil.getInstanceSwallowError(c, TreeVisitor.class);
    }

    public ModelFeatures getModelFeatures() {
        return mf;
    }

    public T getRoot() {
        return root;
    }

    public TreeVisitor getTree() {
        return tv;
    }

    public boolean read(File file) throws FileNotFoundException {
        Iterator<String> iter = FileUtil.getLineReaderIteratorFromFile(file);
        buffList.clear();
        while (iter.hasNext()) {
            buffList.add(iter.next());
        }

        return true;
    }

    public void extract(ModelBufferVisitor mbv) {
        mbv.visit(buffList);

    }

}


