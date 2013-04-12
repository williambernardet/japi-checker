/*
 * Copyright 2013 William Bernardet
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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class AnnotationData implements Map<String, Object> {
    private String desc;
    private boolean visible;
    private Map<String, Object> parameters = new Hashtable<String, Object>();

    public AnnotationData(String desc, boolean visible) {
        this.desc = desc;
        this.visible = visible;
    }
    
    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    @Override
    public Object put(String name, Object value) {
        return parameters.put(name, value);
    }

    @Override
    public void clear() {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.parameters.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.parameters.containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return this.parameters.entrySet();
    }

    @Override
    public Object get(Object key) {
        return this.parameters.get(key);
    }

    @Override
    public boolean isEmpty() {
        return this.parameters.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return this.parameters.keySet();
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public Object remove(Object key) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public int size() {
        return this.parameters.size();
    }

    @Override
    public Collection<Object> values() {
        return this.parameters.values();
    }

}
