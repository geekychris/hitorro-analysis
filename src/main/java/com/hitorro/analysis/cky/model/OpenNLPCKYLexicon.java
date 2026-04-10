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

import com.hitorro.language.Iso639Table;
import com.hitorro.language.IsoLanguage;
import com.hitorro.language.PartOfSpeech;
import com.hitorro.language.PartOfSpeechSingletonMapper;
import com.hitorro.util.core.events.cache.PoolContainer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class OpenNLPCKYLexicon implements LexiconInterface {
    private IsoLanguage lang;

    public OpenNLPCKYLexicon(String language) {
        lang = Iso639Table.getInstance().getRow(language);
    }

    @Override
    public List<Rule>[] get(final String[] toks) {
        PoolContainer<IsoLanguage, PartOfSpeech> posPool = PartOfSpeechSingletonMapper.singleton.get(Iso639Table.english);
        PartOfSpeech pos = posPool.get();
        try {

            com.hitorro.language.POS p = pos.getPOS(toks);
            List<String>[] tags = p.getTags();
            double probs[][] = p.getProbs();
            List<Rule>[] ret = (ArrayList<Rule>[]) Array.newInstance(ArrayList.class, toks.length);
            for (int i = 0; i < tags.length; i++) {

                for (int j = 0; j < tags[i].size(); j++) {
                    if (ret[j] == null) {
                        ret[j] = new ArrayList<Rule>();
                    }

                    UnaryRule ur = new UnaryRule(tags[i].get(j), toks[j], null);
                    ur.setScore(probs[i][j]);
                    ret[j].add(ur);
                }
            }
            return ret;
        } finally {
            posPool.returnIt(pos);
        }
    }

    @Override
    public Set<String> getAllTags() {
        return null;
    }
}
