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

import java.util.List;

import com.googlecode.japi.checker.ClassDataLoader;
import com.googlecode.japi.checker.Reporter;
import com.googlecode.japi.checker.Rule;

public class FieldData extends JavaItem {
    private String descriptor;
    private String signature;
    private Object value;

    public FieldData(ClassDataLoader loader, ClassData owner, int access, String name, String descriptor, String signature, Object value) {
        super(loader, owner, access, name);
        this.setDescriptor(descriptor);
        this.setSignature(signature);
        this.setValue(value);
    }

    /**
     * @param description the description to set
     */
    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * @return the description
     */
    public String getDescriptor() {
        return descriptor;
    }

    /**
     * @param signature the signature to set
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }
    
    public boolean isSame(FieldData reference) {
        return this.getName().equals(reference.getName());
    }
    
    public boolean hasSameType(FieldData reference) {
        return this.getDescriptor().equals(reference.getDescriptor());
    }

    public void checkBackwardCompatibility(Reporter reporter,
            FieldData oldField, List<Rule> rules) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getType() {
        return "field";
    }
}
