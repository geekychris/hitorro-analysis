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

import java.io.*;

public class RelationalFeature implements Serializable {

    public String name;
    public String[][] values;

    public RelationalFeature(int size, String declaration, BufferedReader br) throws IOException {
        values = new String[size][size];
        String[] declist = declaration.split(" ");
        name = declist[2];
        for (int i = 0; i < size; i++) {
            values[i] = br.readLine().substring(2).split(" ");
        }
    }

    public String getFeature(int firstIndex, int secondIndex) {
        if (firstIndex == 0 || secondIndex == 0) {
            return name + "=NULL";
        } else
        //System.out.println(values.length + "** " + name+"="+values[firstIndex-1][secondIndex-1]);
        {
            return name + "=" + values[firstIndex - 1][secondIndex - 1];
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(name);
        out.writeObject(values);
    }


    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        name = (String) in.readObject();
        values = (String[][]) in.readObject();
    }


}
