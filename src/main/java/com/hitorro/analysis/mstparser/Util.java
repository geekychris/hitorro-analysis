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

import java.text.DecimalFormat;

/**
 * Utility methods that may be generally useful.
 *
 * @author Jason Baldridge
 * @created August 27, 2006
 */
public class Util {

    // Assumes input is a String[] containing integers as strings.
    public static int[] stringsToInts(String[] stringreps) {
        int[] nums = new int[stringreps.length];
        for (int i = 0; i < stringreps.length; i++) {
            nums[i] = Integer.parseInt(stringreps[i]);
        }
        return nums;
    }

    // Assumes input is a String[] containing doubles as strings.
    public static double[] stringsToDoubles(String[] stringreps) {
        double[] nums = new double[stringreps.length];
        for (int i = 0; i < stringreps.length; i++) {
            nums[i] = Double.parseDouble(stringreps[i]);
        }
        return nums;
    }

    public static String join(String[] a, char sep) {
        StringBuffer sb = new StringBuffer();
        sb.append(a[0]);
        for (int i = 1; i < a.length; i++) {
            sb.append(sep).append(a[i]);
        }
        return sb.toString();
    }

    public static String join(int[] a, char sep) {
        StringBuffer sb = new StringBuffer();
        sb.append(a[0]);
        for (int i = 1; i < a.length; i++) {
            sb.append(sep).append(a[i]);
        }
        return sb.toString();
    }

    public static String join(double[] a, char sep, int fractionDigits) {
        StringBuffer sb = new StringBuffer();
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(fractionDigits);
        sb.append(df.format(a[0]));
        for (int i = 1; i < a.length; i++) {
            sb.append(sep).append(df.format(a[i]));
        }
        return sb.toString();
    }

}
