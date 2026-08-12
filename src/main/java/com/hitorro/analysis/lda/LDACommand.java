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

import com.hitorro.jsontypesystem.JVS;
import com.hitorro.util.commandandcontrol.Command;
import com.hitorro.util.commandandcontrol.CommandSession;
import com.hitorro.util.commandandcontrol.Response;
import com.hitorro.util.commandandcontrol.ano.CommandArgument;
import com.hitorro.util.commandandcontrol.ano.CommandDef;
import com.hitorro.util.json.keys.BooleanProperty;
import com.hitorro.util.json.keys.DoubleProperty;
import com.hitorro.util.json.keys.IntegerProperty;
import com.hitorro.util.json.keys.StringProperty;


@CommandDef(command = "lda.chunkParser", description = "")
public class LDACommand extends Command {
    @CommandArgument(required = false)
    public static BooleanProperty EstKey = new BooleanProperty("est", "Specify whether we want to estimate model from scratch", false);

    @CommandArgument(required = false)
    public static BooleanProperty WithRawDataKey = new BooleanProperty("-withrawdata", "Specify whether we include raw data in the input", false);
    @CommandArgument(required = false)
    public static BooleanProperty EstCKey = new BooleanProperty("estc", "Specify whether we want to continue the last estimation", false);
    @CommandArgument(required = false)
    public static BooleanProperty InfKey = new BooleanProperty("inf", "Specify whether we want to do inference", false);

    @CommandArgument(required = false)
    public static StringProperty DirKey = new StringProperty("dir", "Specify directory", "");
    @CommandArgument(required = false)
    public static StringProperty DFileKey = new StringProperty("dfile", "Specify data file", "");
    @CommandArgument(required = false)
    public static StringProperty ModelKey = new StringProperty("model", "Specify the model name", "");
    @CommandArgument(required = false)
    public static StringProperty WordMapKey = new StringProperty("wordmap", "Specify the word apply file", "wordmap.txt");
    @CommandArgument(required = false)
    public static DoubleProperty AlphaKey = new DoubleProperty("alpha", "Specify Alpha", -1.0);
    @CommandArgument(required = false)
    public static DoubleProperty BetaKey = new DoubleProperty("beta", "Specify Beta", -1.0);
    @CommandArgument(required = false)
    public static IntegerProperty NTopicsKey = new IntegerProperty("ntopics", "Specify number of topics", 100);
    @CommandArgument(required = false)
    public static IntegerProperty NItersKey = new IntegerProperty("niters", "number of iterations", 1000);
    @CommandArgument(required = false)
    public static IntegerProperty SaveStepKey = new IntegerProperty("savestep", "Specify the number of steps to save the model since the last save", 100);
    @CommandArgument(required = false)
    public static IntegerProperty TWordsKey = new IntegerProperty("twords", "Specify the number of most likely words to be printed for each topic", 100);

    @Override
    public boolean execute(final String rawValue, final com.fasterxml.jackson.databind.JsonNode args, final Response response, final CommandSession session) {
        LDACmdOption opts = new LDACmdOption();
        opts.est = EstKey.apply(args);
        opts.estc = EstCKey.apply(args);
        opts.inf = InfKey.apply(args);
        opts.withrawdata = WithRawDataKey.apply(args);
        opts.dir = DirKey.apply(args);
        opts.dfile = DFileKey.apply(args);
        opts.modelName = ModelKey.apply(args);
        opts.wordMapFileName = WordMapKey.apply(args);
        opts.alpha = AlphaKey.apply(args);
        opts.beta = BetaKey.apply(args);
        opts.K = NTopicsKey.apply(args);
        opts.niters = NItersKey.apply(args);
        opts.savestep = SaveStepKey.apply(args);
        opts.twords = TWordsKey.apply(args);

        return true;
    }
}
