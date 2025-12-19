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
package com.hitorro.analysis.lda;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class Dictionary {
    public Map<String, Integer> word2id;
    public Map<Integer, String> id2word;

    //--------------------------------------------------
    // constructors
    //--------------------------------------------------

    public Dictionary() {
        word2id = new HashMap<String, Integer>();
        id2word = new HashMap<Integer, String>();
    }

    //---------------------------------------------------
    // get/set methods
    //---------------------------------------------------

    public String getWord(int id) {
        return id2word.get(id);
    }

    public Integer getID(String word) {
        return word2id.get(word);
    }

    //----------------------------------------------------
    // checking methods
    //----------------------------------------------------

    /**
     * check if this dictionary contains a specified word
     */
    public boolean contains(String word) {
        return word2id.containsKey(word);
    }

    public boolean contains(int id) {
        return id2word.containsKey(id);
    }
    //---------------------------------------------------
    // manupulating methods
    //---------------------------------------------------

    /**
     * add a word into this dictionary return the corresponding id
     */
    public int addWord(String word) {
        if (!contains(word)) {
            int id = word2id.size();

            word2id.put(word, id);
            id2word.put(id, word);

            return id;
        } else {
            return getID(word);
        }
    }

    //---------------------------------------------------
    // I/O methods
    //---------------------------------------------------

    /**
     * read dictionary from file
     */
    public boolean readWordMap(String wordMapFile) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(wordMapFile), "UTF-8"));
            String line;

            //read the number of words
            line = reader.readLine();
            int nwords = Integer.parseInt(line);

            //read apply
            for (int i = 0; i < nwords; ++i) {
                line = reader.readLine();
                StringTokenizer tknr = new StringTokenizer(line, " \t\n\r");

                if (tknr.countTokens() != 2) {
                    continue;
                }

                String word = tknr.nextToken();
                String id = tknr.nextToken();
                int intID = Integer.parseInt(id);

                id2word.put(intID, word);
                word2id.put(word, intID);
            }

            reader.close();
            return true;
        } catch (Exception e) {
            System.out.println("Error while reading dictionary:" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean writeWordMap(String wordMapFile) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(wordMapFile), "UTF-8"));

            //write number of words
            writer.write(word2id.size() + "\n");

            //write word to id
            Iterator<String> it = word2id.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Integer value = word2id.get(key);

                writer.write(key + " " + value + "\n");
            }

            writer.close();
            return true;
        } catch (Exception e) {
            System.out.println("Error while writing word apply " + e.getMessage());
            e.printStackTrace();
            return false;
        }


    }
}
