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
package com.hitorro.analysis.classifier.gender;

import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.basefile.tools.BaseFileUtil;
import com.hitorro.util.core.string.StringUtil;
import com.hitorro.util.io.csv.CSVFileWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ExclusiveListCensusGender extends CensusGender {
    private HashMap<String, String> master;
    private HashMap<String, String> last;

    public ExclusiveListCensusGender() {
        master = new HashMap();
        last = new HashMap();
    }

    void add(String name, float percent) {
        if (part == 0) {
            master.put(name.toLowerCase(), "m");
        } else if (part == 1) {
            String sex = master.remove(name.toLowerCase());
            if (StringUtil.nullOrEmptyString(sex)) {
                master.put(name.toLowerCase(), "f");
            }
        } else {
            last.put(name.toLowerCase(), "1");
        }
    }

    public void writeSex(BaseFile bf) throws IOException {
        OutputStream os = BaseFileUtil.bf2outputstream.apply(bf);
        List<String> l = new ArrayList();
        l.add("key");
        l.add("sex");
        CSVFileWriter writer = new CSVFileWriter(os, l);
        for (Map.Entry<String, String> e : master.entrySet()) {
            writer.writeRow(new String[]{e.getKey(), e.getValue()});
        }
        os.flush();
        os.close();
    }

    public void writeLastName(BaseFile bf) throws IOException {
        OutputStream os = BaseFileUtil.bf2outputstream.apply(bf);
        List<String> l = new ArrayList();
        l.add("key");
        l.add("lastname");
        CSVFileWriter writer = new CSVFileWriter(os, l);
        for (Map.Entry<String, String> e : last.entrySet()) {
            writer.writeRow(new String[]{e.getKey(), e.getValue()});
        }
        os.flush();
        os.close();
    }
}
