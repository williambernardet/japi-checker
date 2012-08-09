/*
 * Copyright 2012 William Bernardet
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.googlecode.japi.checker.model.ClassData;
import com.googlecode.japi.checker.utils.AntPatternMatcher;

/**
 * Basic implementation of the ClassDataLoader.
 * It populates itself thanks to the read method. 
 */
class DefaultClassDataLoader implements ClassDataLoader {
    private Map<URI, AbstractClassReader> readers = new Hashtable<URI, AbstractClassReader>(); 
    
    /**
     * Read a set of classes via this ClassDataLoader. The idea is similar to the regular
     * Java ClassLoader but for ClassData type of objects.
     * @param filename the archive file or directory to read class file from. 
     * @throws IOException thrown in case of error while extracting the class data.
     */
    protected void read(File filename) throws IOException {
        AbstractClassReader reader;
        if (filename.isDirectory()) {
            reader = new DirectoryReader(filename, this);
        } else {
            reader = new JarReader(filename, this);
        }
        reader.read();
        readers.put(filename.toURI(), reader);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ClassData fromName(String name) {
        for (AbstractClassReader reader : readers.values()) {
            for (ClassData clazz : reader.getClasses()) {
                if (clazz.getName().equals(name)) {
                    return clazz;
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ClassData> getClasses(URI uri) {
        List<ClassData> result = new ArrayList<ClassData>();
        if (readers.containsKey(uri)) {
            result.addAll(readers.get(uri).getClasses());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ClassData> getClasses() {
        List<ClassData> result = new ArrayList<ClassData>();
        for (AbstractClassReader reader : readers.values()) {
            result.addAll(reader.getClasses());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ClassData> getClasses(URI uri, List<AntPatternMatcher> includes, List<AntPatternMatcher> excludes) {
        List<ClassData> result = new ArrayList<ClassData>();
        if (readers.containsKey(uri)) {
            result.addAll(readers.get(uri).getClasses(includes, excludes));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void read(URI uri) throws IOException {
        if ("file".equals(uri.getScheme())) {
            read(new File(uri.getPath()));
        } else {
            throw new IOException("Unsupported scheme: " + uri.getScheme());
        }
    }
}
