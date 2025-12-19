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

import org.maltparser.concurrent.ConcurrentMaltParserModel;
import org.maltparser.concurrent.ConcurrentMaltParserService;
import org.maltparser.concurrent.ConcurrentUtils;

import java.io.*;
import java.net.URL;

/**
 * This example shows how to parse sentences from file. The only difference between example ParseSentence1 is that the input is read from
 * file '../data/swemalt-mini/input/sv-stb-dep-mini-test-blind.conll' and written to 'output/out.conll' in the CoNLL data format.
 * <p>
 * To run this example requires that you have created output/swemalt-mini.mco, please read the README file.
 *
 * @author Johan Hall
 */
public class ParseSentenc2 {
    public static void main(String[] args) {
        // Loading the Swedish model swemalt-mini
        ConcurrentMaltParserModel model = null;
        try {
            URL swemaltMiniModelURL = new File("output/swemalt-mini.mco").toURI().toURL();
            model = ConcurrentMaltParserService.initializeParserModel(swemaltMiniModelURL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new File("../data/swemalt-mini/input/sv-stb-dep-mini-test-blind.conll").toURI().toURL().openStream(), "UTF-8"));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("output/sv-stb-dep-mini-test-parsed.conll")), "UTF-8"));
            int sentenceCount = 0;
            while (true) {
                // Reads a sentence from the input file
                String[] inputTokens = ConcurrentUtils.readSentence(reader);

                // If there are no tokens then we have reach the end of file
                if (inputTokens.length == 0) {
                    break;
                }

                // Parse the sentence
                String[] outputTokens = model.parseTokens(inputTokens);

                // Writes the sentence to the output file
                ConcurrentUtils.writeSentence(outputTokens, writer);

                sentenceCount++;
            }
            System.out.println("Parsed " + sentenceCount + " sentences");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

