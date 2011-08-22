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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
    private Logger logger = Logger.getLogger(ClassDumper.class.getName());
    private ClassData clazz; // current main class being parsed.
    private Map<String, ClassData> classes = new HashMap<String, ClassData>();
    
    public void visit(int version, int access, String name, String signature,
            String superName, String[] interfaces) {
        logger.fine("class " + name + " extends " + superName + " {");
        clazz = new ClassData(null, access, name, signature, superName, interfaces, version);
        classes.put(name, clazz);
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return null;
    }

    public void visitAttribute(Attribute attribute) {
    }

    public void visitEnd() {
        logger.fine("}");
        clazz = null;
    }

    public FieldVisitor visitField(int access, String name, String desc,
            String signature, Object value) {
        logger.fine("    -(field) " + name + " " + signature + " " + desc);
        clazz.add(new FieldData(clazz, access, name, desc, signature, value));
        return null;
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        logger.fine("    +(ic) " + name + " " + outerName + " " + innerName + " " + access);
        //clazz = new ClassData(access, name, innerName);
        clazz.add(new InnerClassData(clazz, access, name, outerName, innerName));
    }

    public MethodVisitor visitMethod(int access, String name, String descriptor,
            String signature, String[] exceptions) {
        logger.fine("    +(m) " + name + " " + descriptor + " " + signature + " " + exceptions);
        clazz.add(new MethodData(clazz, access, name, descriptor, signature, exceptions));
        return null;
    }

    public void visitOuterClass(String owner, String name, String desc) {
        logger.fine("    *(oc) " + name + " " + desc);
    }

    public void visitSource(String source, String debug) {
        logger.fine(" - source: " + source);
        logger.fine(" - debug: " + debug);
        clazz.setSource(source);
    }

    public List<ClassData> getClasses() {
        return new ArrayList<ClassData>(classes.values());
    }
}
