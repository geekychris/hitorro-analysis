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

import java.util.ArrayList;
import java.util.List;


public class ModelFeatures implements ModelBufferVisitor {
    private String scheme;
    private String relation;
    private int instances;
    private int attributeCount;
    private List<String> attributes = new ArrayList();

    public void visit(List<String> buff) {
        setScheme(getStringFromKV(buff, 2));
        setRelation(getStringFromKV(buff, 3));
        setInstances(getIntFromKV(buff, 4));
        setAttributeCount(getIntFromKV(buff, 5));
        for (int i = 0; i < getAttributeCount(); i++) {
            getAttributes().add(getStringFromV(buff, i + 6));
        }
    }

    private String getStringFromKV(List<String> buff, int index) {
        String s = buff.get(index);
        int i = s.indexOf(":");
        if (i == -1) {
            return s;
        }
        return s.substring(i + 1).trim();
    }

    private String getStringFromV(List<String> buff, int index) {
        String s = buff.get(index);
        return s.trim();
    }

    private int getIntFromKV(List<String> buff, int index) {
        String s = getStringFromKV(buff, index);
        return Integer.parseInt(s);
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(final String scheme) {
        this.scheme = scheme;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(final String relation) {
        this.relation = relation;
    }

    public int getInstances() {
        return instances;
    }

    public void setInstances(final int instances) {
        this.instances = instances;
    }

    public int getAttributeCount() {
        return attributeCount;
    }

    public void setAttributeCount(final int attributeCount) {
        this.attributeCount = attributeCount;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(final List<String> attributes) {
        this.attributes = attributes;
    }
}