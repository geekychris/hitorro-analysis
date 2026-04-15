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

import com.hitorro.analysis.mstparser.io.DependencyReader;
import com.hitorro.language.IsoLanguage;
import com.hitorro.util.core.Log;
import com.hitorro.util.core.events.cache.PoolContainer;
import com.hitorro.util.core.events.cache.PooledObjectIntf;
import com.hitorro.util.json.keys.FileProperty;

import java.io.File;
import java.io.IOException;


public class MSTParser implements PooledObjectIntf<IsoLanguage> {
    public static FileProperty ModelDirKey = new FileProperty("mstparser.root", "root directory for models", "${ht_data}/mstparser/model/");
    public static final String LAB = "LAB";
    private int generation;
    private DependencyParser dp;
    private DependencyPipe pipe;
    private ParserOptions options;
    private PoolContainer pc;

    public boolean init(IsoLanguage val) throws IOException {
        options = new ParserOptions(new String[]{"test", "test-file://publicsource/mstparser/chris.lab"});
        options.format = "MST";


        pipe = options.secondOrder ?
                new DependencyPipe2O(options) : new DependencyPipe(options);

        dp = new DependencyParser(pipe, options);
        File dir = ModelDirKey.apply();
        File langDir = new File(dir, val.getTwo());
        File model = new File(langDir, "dep.model");

        try {
            dp.loadModel(model);
            pipe.labeled = true;
        } catch (Exception e) {
            Log.util.error("%s %e", e, e);
            throw new IOException(e);
        }

        pipe.closeAlphabets();
        return true;
    }

    public DependencyResult get(String formsIn[], String posIn[], boolean computeConfidence) {
        boolean confScores = false;
        // must be true else LAB isnt even used.
        boolean labeled = true;
        String conf_line = "";


        String deprelsIn[] = new String[formsIn.length];
        int headsIn[] = new int[formsIn.length];
        for (int i = 0; i < formsIn.length; i++) {
            deprelsIn[i] = LAB;
        }
        DependencyInstance instance = DependencyReader.getInstance(confScores, labeled, conf_line, formsIn, posIn, deprelsIn, headsIn);

        String[] forms = instance.forms;
        String[] formsNoRoot = new String[forms.length - 1];
        String[] posNoRoot = new String[formsNoRoot.length];
        String[] labels = new String[formsNoRoot.length];
        int[] heads = new int[formsNoRoot.length];

        dp.decode(instance,
                options.testK,
                dp.getParams(),
                formsNoRoot,
                posNoRoot,
                labels,
                heads);
        double[] confidenceScores = null;
        ConfidenceEstimator confEstimator = null;

        if (computeConfidence) {
            confEstimator =
                    ConfidenceEstimator.resolveByName("KDFix*0.05*50",
                            dp);
        }

        if (confEstimator != null) {
            confidenceScores =
                    confEstimator.estimateConfidence(instance);
        }
        return new DependencyResult(instance, posNoRoot, labels, heads, confidenceScores);
    }

    @Override
    public int getGenerationId() {
        return generation;
    }

    @Override
    public void setGenerationId(final int id) {
        generation = id;
    }

    @Override
    public void passivate() {
    }

    @Override
    public void activate() {
    }

    @Override
    public boolean reInit(final IsoLanguage key) {
        return false;
    }

    @Override
    public void setPoolContainer(final PoolContainer pc) {
        this.pc = pc;
    }

    @Override
    public void close() {
        pc.returnIt(this);
    }
}
