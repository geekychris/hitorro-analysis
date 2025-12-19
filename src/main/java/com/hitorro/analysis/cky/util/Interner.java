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

import com.hitorro.util.core.map.MapFactory;

import java.util.Map;

/**
 * Canonicalizes objects.  Given an object, the intern() method returns a
 * canonical representation of that object, that is, an object which equals()
 * the input.  Furthermore, given two objects x and y, it is guaranteed that if
 * x.equals(y), then intern(x) == intern(y).  The default behavior is that the
 * interner is backed by a HashMap and the canonical version of an object x is
 * simply the first object that equals(x) which is passed to the interner.  In
 * this case, it can be true that intern(x) == x.  The backing apply can be
 * specified by passing a MapFactory on construction (though the only standard
 * option which makes much sense is the WeakHashMap, which is slower than a
 * HashMap, but which allows unneeded keys to be reclaimed by the garbage
 * collector).  The source of canonical elements can be changed by specifying an
 * Interner.Factory on construction.
 *
 * @author Dan Klein
 */
public class Interner<T> {
    Map<T, T> canonicalMap;
    CanonicalFactory<T> cf;

    public Interner() {
        this(new MapFactory.HashMapFactory<T, T>(), new IdentityCanonicalFactory<T>());
    }
    public Interner(MapFactory<T, T> mf) {
        this(mf, new IdentityCanonicalFactory<T>());
    }

    public Interner(CanonicalFactory<T> f) {
        this(new MapFactory.HashMapFactory<T, T>(), f);
    }

    public Interner(MapFactory<T, T> mf, CanonicalFactory<T> cf) {
        canonicalMap = mf.buildMap();
        this.cf = cf;
    }

    /**
     * Returns a canonical representation of the given object.  If the object has
     * no canonical representation, one is built using the interner's
     * CanonicalFactory.  The default is that new objects will be their own
     * canonical instances.
     *
     * @param object
     * @return a canonical representation of that object
     */
    public T intern(T object) {
        T canonical = canonicalMap.get(object);
        if (canonical == null) {
            canonical = cf.build(object);
            canonicalMap.put(canonical, canonical);
        }
        return canonical;
    }

    /**
     * The source of canonical objects when a non-interned object is presented to
     * the interner.  The default implementation is an identity apply.
     */
    public interface CanonicalFactory<T> {
        T build(T object);
    }

    static class IdentityCanonicalFactory<T> implements CanonicalFactory<T> {
        public T build(T object) {
            return object;
        }
    }
}
