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
package com.hitorro.analysis.classifier.gender;


import gnu.trove.map.hash.TObjectFloatHashMap;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.io.csv.SpaceReader;
import com.hitorro.util.io.csv.csvconsumer.CSVConsumer;
import com.hitorro.util.json.keys.BasefileProperty;

/**
 *
 */
public class CensusGender {
    public BasefileProperty CensusDir = new BasefileProperty("analysis.isQuestionClassifier.census.dir", "", "file://${ht_data}/classifiers/census_names");
    protected int part;
    TObjectFloatHashMap<String> maleHash = new TObjectFloatHashMap<String>();
    TObjectFloatHashMap<String> femaleHash = new TObjectFloatHashMap<String>();
    TObjectFloatHashMap<String> lastHash = new TObjectFloatHashMap<String>();

    public CensusGender() {
    }

    public void init() {
        BaseFile male = CensusDir.apply().getChild("dist.male.first");

        BaseFile female = CensusDir.apply().getChild("dist.female.first");

        BaseFile last = CensusDir.apply().getChild("dist.all.last");
        SpaceReader maleSp = new SpaceReader(male);
        SpaceReader femaleSp = new SpaceReader(female);
        SpaceReader lastSp = new SpaceReader(last);
        part = 0;
        GenderConsumer gc = new GenderConsumer(this);
        maleSp.consume(gc);
        part = 1;
        femaleSp.consume(gc);
        part = 2;
        lastSp.consume(gc);
    }

    void add(String name, float percent) {
        if (part == 0) {
            maleHash.put(name.toLowerCase(), percent);
        } else if (part == 1) {

            femaleHash.put(name.toLowerCase(), percent);
        } else {
            lastHash.put(name.toLowerCase(), percent);
        }
    }
}

class GenderConsumer implements CSVConsumer {
    private CensusGender cg;

    public GenderConsumer(CensusGender cg) {
        this.cg = cg;
    }

    @Override
    public void line(final int rowCount, final String[] line) {
        cg.add(line[0], Float.parseFloat(line[1]));
    }
}