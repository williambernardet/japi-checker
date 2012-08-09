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

public class MethodData extends JavaItem {
    private String signature;
    private String descriptor;
    private List<String> exceptions = new ArrayList<String>();
    private int line;
    
    public MethodData(ClassDataLoader loader, ClassData owner, int access, String name, String descriptor, String signature, String[] exceptions) {
        super(loader, owner, access, name);
        this.setSignature(signature);
        this.setDescriptor(descriptor);
        if (exceptions != null) {
            Collections.addAll(this.exceptions, exceptions);
        }
    }

    public void checkBackwardCompatibility(Reporter reporter, MethodData method, List<Rule> rules) {
    }

    public boolean isSame(MethodData method) {
        if (method == null) {
            return false;
        }
        return this.getName().equals(method.getName()) && this.getDescriptor().equals(method.getDescriptor());
    }

    /**
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param signature the signature to set
     */
    protected void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String getType() {
        return "method";
    }

    /**
     * @param descriptor the descriptor to set
     */
    protected void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * @return the descriptor
     */
    public String getDescriptor() {
        return descriptor;
    }

    /**
     * @param exceptions the exceptions to set
     */
    protected void setExceptions(List<String> exceptions) {
        this.exceptions = exceptions;
    }

    /**
     * @return the exceptions
     */
    public List<String> getExceptions() {
        return exceptions;
    }

    public void setLineNumber(int line) {
        this.line = line;
    }
    
    public int getLineNumber() {
        return line;
    }
}
