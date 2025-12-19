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
package com.hitorro.analysis.rdf;


import com.hitorro.obj.core.Log;
import com.hitorro.util.core.Env;
import com.hitorro.util.core.events.cache.HashCache;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.core.string.Fmt;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.*;

/**
 *
 */
public class JenaModelCacheMapper extends BaseMapper<String, Model> {
    public static String EventName = "Jenamodel";
    public static HashCache<String, Model> instance = new HashCache(EventName, new JenaModelCacheMapper());

    public Model apply(final String key) {
        File data = Env.getData();
        File rdfFile = new File(data, Fmt.S("rdfknowledge/%s.rdf", key));

        InputStream in = null;
        try {
            in = new FileInputStream(rdfFile);
            // Create an empty in-memory model and populate it from the graph
            Model model = ModelFactory.createMemModelMaker().createDefaultModel();
            model.read(in, null);
            in.close();
            return model;
        } catch (FileNotFoundException e) {
            Log.util.error("Unable to find file %s %s %e", rdfFile, e, e);
            return null;
        } catch (IOException e) {
            Log.util.error("Unable to find file %s %s %e", rdfFile, e, e);
            return null;
        }
    }
}
