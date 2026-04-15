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
package com.hitorro.analysis.sentiment.penn;

import com.hitorro.jsontypesystem.JVS;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.json.keys.BooleanProperty;
import com.hitorro.util.json.keys.StringProperty;


public class SentWordMapper extends BaseMapper<JVS, SentWord> {
    public static StringProperty PosKey = new StringProperty("pos1", "", null);
    public static StringProperty PriorPolarityKey = new StringProperty("priorpolarity", "", null);
    public static StringProperty TypeKey = new StringProperty("type", "", null);
    public static StringProperty WordKey = new StringProperty("word1", "", null);
    public static BooleanProperty StemmedKey = new BooleanProperty("stemmed1", "", false);
    public static SentWordMapper instance = new SentWordMapper();

    @Override
    public SentWord apply(final JVS e) {
        SentWord sw = new SentWord();
        sw.isStemmed = StemmedKey.apply(e);
        sw.pos = PosKey.apply(e);
        sw.type = TypeKey.apply(e);
        sw.word = WordKey.apply(e);
        sw.priorPolarity = PriorPolarityKey.apply(e);
        return sw;
    }
}
