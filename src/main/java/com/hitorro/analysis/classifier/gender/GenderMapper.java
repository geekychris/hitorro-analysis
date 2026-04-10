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

import com.fasterxml.jackson.databind.JsonNode;
import com.hitorro.util.basefile.fs.BaseFile;
import com.hitorro.util.core.events.EventListener;
import com.hitorro.util.core.events.LocalEventHub;
import com.hitorro.util.core.iterator.mappers.BaseMapper;
import com.hitorro.util.core.iterator.mappers.StringToTokensMapper;
import com.hitorro.util.core.longword.BiDirectionalKeyLongMap;
import com.hitorro.util.core.longword.NamedBitsOfLong;
import com.hitorro.util.core.longword.WordBits;
import com.hitorro.util.core.map.FPHashLongMap;
import com.hitorro.util.json.keys.BasefileProperty;
import com.hitorro.util.startupframework.ServiceContext;

import java.io.IOException;
import java.text.ParseException;


public class GenderMapper extends BaseMapper<String[], String> implements EventListener {
    public static GenderMapper defaultMapper = new GenderMapper(true);
    public static BaseMapper GenderCompositeMapper = StringToTokensMapper.spaceMapper.combine(GenderMapper.defaultMapper);
    public static BasefileProperty OutKey = new BasefileProperty("gendertable", "", "file://${ht_data}/classifiers/gender");
    private WordBits bits;
    private FPHashLongMap fpHashLongMap;
    private NamedBitsOfLong nbol;
    private NamedBitsOfLong lastNameNbol;
    private NamedBitsOfLong institutionNbol;
    public GenderMapper(boolean init) {
        if (init) {
            LocalEventHub.get().addEventListener(this, ServiceContext.ServerUp);
        }
    }

    public boolean event(String topic, String subTopic, Object args) {
        return true;
    }

    @Override
    public String eventName() {
        return "gendermapper";
    }

    @Override
    public boolean runAsync() {
        return false;
    }

    /**
     * a token could be one of the following: lastname institution first name unknown
     *
     * @param parts
     * @return
     */
    @Override
    public String apply(final String[] parts) {
        String ret = nbol.mapFromLong(0);
        boolean set = false;

        for (String part : parts) {
            long l = fpHashLongMap.get(part, 0);
            if (l != 0) {
                if (institutionNbol.isSet(l)) {
                    // this is an institution, we cant classify it!, give up
                    return "U";
                }
                if (lastNameNbol.isSet(l)) {
                    if (set != true) {
                        if (nbol.get(l) != 0) {
                            // we are not sure, it looks like its a last name and a first time
                            // if we dont get anything better, we can use this as a hit
                            ret = nbol.mapFromLong(l);
                        }
                    }
                    // this is potentially a last name, we cant consider it.
                    continue;
                }
                // we have something that is either male or female (gender.csv doesnt contain U)
                ret = nbol.mapFromLong(l);
                set = true;
            }
        }
        return ret;
    }

    public String initPass(final JsonNode map) {
        bits = new WordBits();
        BiDirectionalKeyLongMap mlm = new BiDirectionalKeyLongMap(true);
        mlm.add("M", 1);
        mlm.add("F", 2);
        mlm.add("U", 0);
        bits.add("sex", 2, mlm);
        nbol = bits.get("sex");
        bits.add("lastname", 1);
        lastNameNbol = bits.get("lastname");
        bits.add("institution", 1);
        institutionNbol = bits.get("institution");

        BaseFile bOut = OutKey.apply(map);

        try {
            fpHashLongMap = FPHashLongMap.loadFromDirectory(bits, bOut, 10, true, true);
        } catch (IOException e) {
            return e.toString();
        } catch (ParseException e) {
            return e.toString();
        }
        return null;
    }

    @Override
    public boolean init(final JsonNode node) {
        return false;
    }
}
