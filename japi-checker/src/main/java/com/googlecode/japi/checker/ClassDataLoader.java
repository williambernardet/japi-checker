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

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.googlecode.japi.checker.model.ClassData;
import com.googlecode.japi.checker.utils.AntPatternMatcher;

/**
 * This class is the equivalent of the ClassLoader for the Class. It enabled us to access
 * ClassData information out of a class path.
 *
 */
public interface ClassDataLoader {

    /**
     * Read class file out of the provided URI.
     * @param uri the URI where to load class file from.
     * @throws IOException Thrown in case of error.
     */
    void read(URI uri) throws IOException;
    
    /**
     * Get ClassData out of a class name e.g: com.mycompany.mypackage.Class
     * @param name the class name
     * @return a ClassData instance or null if Not found.
     */
    @Nullable
    ClassData fromName(String name);
    
    /**
     * Get all the ClassData information which belongs to this loader.
     * @return the list of ClassData.
     */
    @Nonnull
    List<ClassData> getClasses();

    /**
     * Get all ClassData from this loaded provided by the given uri.
     * @param uri the uri to retrieve ClassData from
     * @return Returns a list of ClassData.
     */
    @Nonnull
    List<ClassData> getClasses(@Nonnull URI uri);
    
    /**
     * Get all ClassData from this loaded provided by the given uri and filtered using pattern matchers.
     * @param uri the uri to retrieve ClassData from 
     * @param includes
     * @param excludes
     * @return Returns a list of ClassData.
     */
    @Nonnull
    List<ClassData> getClasses(@Nonnull URI uri, @Nonnull List<AntPatternMatcher> includes, @Nonnull List<AntPatternMatcher> excludes);
    
}
