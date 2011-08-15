/*
 * Copyright 2011 William Bernardet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.japi.checker;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import com.googlecode.japi.checker.model.ClassData;
import com.googlecode.japi.checker.model.FieldData;
import com.googlecode.japi.checker.model.InnerClassData;
import com.googlecode.japi.checker.model.MethodData;

public class ClassDumper implements ClassVisitor {
    public ClassData clazz;
    
    public void visit(int version, int access, String name, String signature,
            String superName, String[] interfaces) {
        System.out.println("class " + name + " extends " + superName + " {");
        clazz = new ClassData(access, name, signature, superName, interfaces, version);
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return null;
    }

    public void visitAttribute(Attribute attribute) {
    }

    public void visitEnd() {
        System.out.println("}");
    }

    public FieldVisitor visitField(int access, String name, String desc,
            String signature, Object value) {
        System.out.println("    -(field) " + name + " " + signature + " " + desc);
        clazz.add(new FieldData(access, name, desc, signature, value));
        return null;
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        System.out.println("    +(ic) " + name + " " + outerName + " " + innerName + " " + access);
        //clazz = new ClassData(access, name, innerName);
        clazz.add(new InnerClassData(access, name, outerName, innerName));
    }

    public MethodVisitor visitMethod(int access, String name, String descriptor,
            String signature, String[] exceptions) {
        System.out.println("    +(m) " + name + " " + descriptor + " " + signature + " " + exceptions);
        clazz.add(new MethodData(access, name, descriptor, signature, exceptions));
        return null;
    }

    public void visitOuterClass(String owner, String name, String desc) {
        System.out.println("    *(oc) " + name + " " + desc);
    }

    public void visitSource(String source, String debug) {
    }

    public ClassData getClazz() {
        return clazz;
    }
}
