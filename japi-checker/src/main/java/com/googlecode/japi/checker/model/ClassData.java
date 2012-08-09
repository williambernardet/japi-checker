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
package com.googlecode.japi.checker.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.googlecode.japi.checker.ClassDataLoader;
import com.googlecode.japi.checker.Reporter;
import com.googlecode.japi.checker.Rule;

public class ClassData extends JavaItem {
    private List<MethodData> methods = new ArrayList<MethodData>();
    private List<FieldData> fields = new ArrayList<FieldData>();
    private List<AttributeData> attributes = new ArrayList<AttributeData>();
    private List<InnerClassData> innerClasses = new ArrayList<InnerClassData>();
    private String signature;
    private String superName;
    private List<String> interfaces = new ArrayList<String>();
    private int version;
    private String source;

    public ClassData(ClassDataLoader loader, ClassData owner, int access, String name, String signature, String superName, String[] interfaces, int version) {
        super(loader, owner, access, name);
        this.setSignature(signature);
        this.superName = superName;
        Collections.addAll(this.interfaces, interfaces);
        this.version = version;
    }
    
    public void add(MethodData method) {
        methods.add(method);
    }
    
    public void add(AttributeData attribute) {
        attributes.add(attribute);
    }
    
    public void add(FieldData field) {
        fields.add(field);
    }
    
    public void checkBackwardCompatibility(Reporter reporter, ClassData clazz, List<Rule> rules) {
        for (FieldData oldField : clazz.fields) {
            for (FieldData newField: this.fields) {
                if (oldField.isSame(newField)) {
                    newField.checkBackwardCompatibility(reporter, oldField, rules);
                    for (Rule rule : rules) {
                        rule.checkBackwardCompatibility(reporter, oldField, newField);
                    }
                    break;
                }
            }
        }
        for (MethodData oldMethod : clazz.methods) {
            for (MethodData newMethod: this.methods) {
                if (oldMethod.isSame(newMethod)) {
                    newMethod.checkBackwardCompatibility(reporter, oldMethod, rules);
                    for (Rule rule : rules) {
                        rule.checkBackwardCompatibility(reporter, oldMethod, newMethod);
                    }
                    break;
                }
            }
        }
    }

    public boolean isSame(ClassData newClazz) {
        return this.getName().equals(newClazz.getName());
    }
    

    /**
     * @param signature the signature to set
     */
    protected void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    @Override
    public String getType() {
        return this.isInterface() ? "interface" : "class";
    }

    /**
     * @return the superName
     */
    public String getSuperName() {
        return superName;
    }

    /**
     * @param superName the superName to set
     */
    protected void setSuperName(String superName) {
        this.superName = superName;
    }

    /**
     * @return the interfaces
     */
    public List<String> getInterfaces() {
        return interfaces;
    }

    /**
     * @param interfaces the interfaces to set
     */
    protected void setInterfaces(List<String> interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    protected void setVersion(int version) {
        this.version = version;
    }

    /**
     * @return the methods
     */
    public List<MethodData> getMethods() {
        return methods;
    }

    /**
     * @param methods the methods to set
     */
    protected void setMethods(List<MethodData> methods) {
        this.methods = methods;
    }

    /**
     * @return the fields
     */
    public List<FieldData> getFields() {
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    protected void setFields(List<FieldData> fields) {
        this.fields = fields;
    }

    /**
     * @return the attributes
     */
    public List<AttributeData> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    protected void setAttributes(List<AttributeData> attributes) {
        this.attributes = attributes;
    }


    public void add(InnerClassData clazz) {
        innerClasses.add(clazz);
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }
    
    public String getFilename() {
        if (this.getName().lastIndexOf('/') != -1) {
            return this.getName().substring(0, this.getName().lastIndexOf('/') + 1) + getSource();
        }
        return this.getSource();
    }
}
