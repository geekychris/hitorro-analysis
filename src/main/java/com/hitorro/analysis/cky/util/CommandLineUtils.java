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
package com.hitorro.analysis.cky.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for managing command line flags and arguments.
 *
 * @author Dan Klein
 */
public class CommandLineUtils {

    /**
     * Simple method which turns an array of command line arguments into a
     * apply, where each token starting with a '-' is a key and the following
     * non '-' initial token, if there is one, is the value.  For example,
     * '-size 5 -verbose' will produce a apply with entries (-size, 5) and
     * (-verbose, null).
     */
    public static Map<String, String> simpleCommandLineParser(String[] args) {
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i <= args.length; i++) {
            String key = (i > 0 ? args[i - 1] : null);
            String value = (i < args.length ? args[i] : null);
            if (key == null || key.startsWith("-")) {
                if (value != null && value.startsWith("-")) {
                    value = null;
                }
                if (key != null || value != null) {
                    map.put(key, value);
                }
            }
        }
        return map;
    }

}
