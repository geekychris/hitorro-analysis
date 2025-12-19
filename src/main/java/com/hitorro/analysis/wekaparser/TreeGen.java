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
package com.hitorro.analysis.wekaparser;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/*
class foo extends model
{
    public static final int A = 0;
    public static final int B = 1;
    public static final int C = 2;

    public double score (double d[])
    {
        double total = 0;
        if (d[A] > 0.34)
        {
            if (d[B] >= 0.11)
            {
               total += -0.55;
            }
            else
            {
                total += 0.88;
            }
            if (d[C] < 0.111)
            {
                total +=0.1234;
            }
            else
            {
                total += 0.0111;
            }
        }
        else
        {
            total += 0.000001;
        }
        return total;
    }
}
 */
public class TreeGen implements Opcodes {

    public static byte[] dump() throws Exception {

        ClassWriter cw = new ClassWriter(0);
        MethodVisitor cv;

        cw.visit(50, ACC_SUPER, "com/hitorro/util/typesystem/valuesource/foo", "com/hitorro/util/typesystem/valuesource/model", null, new String[]{"AsmFieldGenerator.java"});

        //visitField(int access,                java.lang.String name, java.lang.String desc, java.lang.String signature, java.lang.Object value)
        //ACC_PUBLIC + ACC_FINAL + ACC_STATIC   "A",                   "I"

        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "A", "I", null, null);

        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "B", "I", null, null);

        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "C", "I", null
                , null);

        {
            cv = cw.visitMethod(0, "<init>", "()V", null, null);
            cv.visitVarInsn(ALOAD, 0);
            cv.visitMethodInsn(INVOKESPECIAL, "com/hitorro/util/typesystem/valuesource/model", "<init>", "()V");
            cv.visitInsn(RETURN);
            cv.visitMaxs(1, 1);
        }
        {
            cv = cw.visitMethod(ACC_PUBLIC, "score", "([D)D", null, null);
            cv.visitInsn(DCONST_0);
            cv.visitVarInsn(DSTORE, 2);
            cv.visitVarInsn(ALOAD, 1);
            cv.visitInsn(ICONST_0);
            cv.visitInsn(DALOAD);
            cv.visitLdcInsn(new Double("0.34"));
            cv.visitInsn(DCMPL);

            Label l0 = new Label();
            cv.visitJumpInsn(IFLE, l0);
            cv.visitVarInsn(ALOAD, 1);
            cv.visitInsn(ICONST_1);
            cv.visitInsn(DALOAD);
            cv.visitLdcInsn(new Double("0.11"));
            cv.visitInsn(DCMPL);

            Label l1 = new Label();
            cv.visitJumpInsn(IFLT, l1);
            cv.visitVarInsn(DLOAD, 2);
            cv.visitLdcInsn(new Double("-0.55"));
            cv.visitInsn(DADD);
            cv.visitVarInsn(DSTORE, 2);

            Label l2 = new Label();
            cv.visitJumpInsn(GOTO, l2);
            cv.visitLabel(l1);
            cv.visitVarInsn(DLOAD, 2);
            cv.visitLdcInsn(new Double("0.88"));
            cv.visitInsn(DADD);
            cv.visitVarInsn(DSTORE, 2);
            cv.visitLabel(l2);
            cv.visitVarInsn(ALOAD, 1);
            cv.visitInsn(ICONST_2);
            cv.visitInsn(DALOAD);
            cv.visitLdcInsn(new Double("0.111"));
            cv.visitInsn(DCMPG);
            Label l3 = new Label();
            cv.visitJumpInsn(IFGE, l3);
            cv.visitVarInsn(DLOAD, 2);
            cv.visitLdcInsn(new Double("0.1234"));
            cv.visitInsn(DADD);
            cv.visitVarInsn(DSTORE, 2);
            Label l4 = new Label();
            cv.visitJumpInsn(GOTO, l4);
            cv.visitLabel(l3);
            cv.visitVarInsn(DLOAD, 2);
            cv.visitLdcInsn(new Double("0.0111"));
            cv.visitInsn(DADD);
            cv.visitVarInsn(DSTORE, 2);
            cv.visitJumpInsn(GOTO, l4);
            cv.visitLabel(l0);
            cv.visitVarInsn(DLOAD, 2);
            cv.visitLdcInsn(new Double("1.0E-6"));
            cv.visitInsn(DADD);
            cv.visitVarInsn(DSTORE, 2);
            cv.visitLabel(l4);
            cv.visitVarInsn(DLOAD, 2);
            cv.visitInsn(DRETURN);
            cv.visitMaxs(4, 4);
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}