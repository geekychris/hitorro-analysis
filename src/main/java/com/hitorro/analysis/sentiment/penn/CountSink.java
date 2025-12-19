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

import com.fasterxml.jackson.databind.JsonNode;
import com.hitorro.jsontypesystem.JVS;
import com.hitorro.util.CounterMap;
import com.hitorro.util.core.iterator.sinks.Sink;
import com.hitorro.util.core.params.JsonKeyMap;
import com.hitorro.util.json.keys.propaccess.PropaccessError;

import java.util.List;

/**
 *
 */
public class CountSink implements Sink<JVS> {
    private CounterMap<String, String> counter = new CounterMap();

    public CounterMap<String, String> getCounters() {
        return counter;
    }

    @Override
    public boolean init(JsonNode node) {
        return true;
    }

    @Override
    public boolean start() {
        counter.clear();
        return true;
    }

    @Override
    public boolean add(final JVS o) {
        try {
            List<JsonKeyMap> l = o.getSubMaps("");
            for (JsonKeyMap entry : l) {
                counter.incrementCount(entry.getKey(), entry.getValue().textValue(), 1.0);
            }
        } catch (PropaccessError propaccessError) {
            return false;
        }

        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public void close() {
        stop();
    }
}
