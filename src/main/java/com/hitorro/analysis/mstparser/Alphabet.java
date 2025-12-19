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

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Alphabet implements Serializable {
    private static final long serialVersionUID = 1;
    private static final int CURRENT_SERIAL_VERSION = 0;
    TObjectIntHashMap map;
    int numEntries;
    boolean growthStopped = false;


    public Alphabet(int capacity) {
        this.map = new TObjectIntHashMap(capacity);
        //this.apply.setDefaultValue(-1);

        numEntries = 0;
    }

    public Alphabet() {
        this(10000);
    }

    /**
     * Return -1 if entry isn't present.
     */
    public int lookupIndex(Object entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Can't lookup \"null\" in an Alphabet.");
        }

        int ret;
        if (!map.contains(entry)) {
            ret = -1;
        } else {
            ret = map.get(entry);
        }
        if (ret == -1 && !growthStopped) {
            ret = numEntries;
            map.put(entry, ret);
            numEntries++;
        }

        return ret;
    }

    public Object[] toArray() {
        return map.keys();
    }

    public boolean contains(Object entry) {
        return map.contains(entry);
    }

    public int size() {
        return numEntries;
    }

    public void stopGrowth() {
        growthStopped = true;
        map.compact();
    }


    // Serialization 

    public void allowGrowth() {
        growthStopped = false;
    }

    public boolean growthStopped() {
        return growthStopped;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(CURRENT_SERIAL_VERSION);
        out.writeInt(numEntries);
        out.writeObject(map);
        out.writeBoolean(growthStopped);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int version = in.readInt();
        numEntries = in.readInt();
        map = (TObjectIntHashMap) in.readObject();
        growthStopped = in.readBoolean();
    }

}
