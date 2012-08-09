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

import com.googlecode.japi.checker.ClassDataLoader;


public class InnerClassData extends JavaItem {

    private String outerName;
    private String innerName;
    
    public InnerClassData(ClassDataLoader loader, ClassData owner, int access, String name, String outerName,
            String innerName) {
        super(loader, owner, access, name);
        this.setOuterName(outerName);
        this.setInnerName(innerName);
    }

    @Override
    public String getType() {
        return "class";
    }

    /**
     * @param outerName the outerName to set
     */
    protected void setOuterName(String outerName) {
        this.outerName = outerName;
    }

    /**
     * @return the outerName
     */
    public String getOuterName() {
        return outerName;
    }

    /**
     * @param innerName the innerName to set
     */
    protected void setInnerName(String innerName) {
        this.innerName = innerName;
    }

    /**
     * @return the innerName
     */
    public String getInnerName() {
        return innerName;
    }

    
}
