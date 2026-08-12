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
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.basefile.tools.BaseFileUtil;
import com.hitorro.util.core.CommandArgsMapper;
import com.hitorro.util.core.iterator.AbstractIterator;
import com.hitorro.util.json.keys.BasefileProperty;


public class SentimentUtil {
    public static BasefileProperty BaseDirProperty = new BasefileProperty("sentiment.penndir", "", "file://${ht_data}/sentiment/penn/en/subjclueslen1-HLTEMNLP05.tff");

    ///ht/hitorro/data/sentiment/penn/en
    public static AbstractIterator<com.fasterxml.jackson.databind.node.ObjectNode> getRawValues() {
        BaseFile dir = BaseDirProperty.apply();
        return BaseFileUtil.bf2lineiter.apply(dir).map(CommandArgsMapper.instance);
    }

    // getSentWords() was removed — its type chain (map(CommandArgsMapper) → ObjectNode) then
    // map(SentWordMapper) expecting JVS never was type-safe. Method had no callers.
}
