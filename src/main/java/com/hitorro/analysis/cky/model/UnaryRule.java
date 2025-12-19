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

/**
 * A unary grammar rule with score representing its probability.
 */
public class UnaryRule extends Rule {
    String child;


    public UnaryRule(String parent, String child, Grammar g) {
        this.name = parent;
        this.child = child;

        if (g != null) {
            this.parentId = g.idMap.getOrAdd(this, parent);
        }
    }

    public final String getChild() {
        return child;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UnaryRule)) {
            return false;
        }

        final UnaryRule unaryRule = (UnaryRule) o;

        if (child != null ? !child.equals(unaryRule.child) : unaryRule.child != null) {
            return false;
        }
        if (name != null ? !name.equals(unaryRule.name) : unaryRule.name != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 29 * result + (child != null ? child.hashCode() : 0);
        return result;
    }

    public String toString() {
        return name + " -> " + child + " %% " + score;
    }

    public final String toShort() {
        return name + "->" + child;
    }
}
