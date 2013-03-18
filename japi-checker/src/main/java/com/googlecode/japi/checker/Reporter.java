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

import com.googlecode.japi.checker.model.ClassData;
import com.googlecode.japi.checker.model.JavaItem;

public interface Reporter {
    public enum Level {ERROR, WARNING, INFO, DEBUG};
    
    void report(Report report);
    
    public static class Report {
        public final Level level;
        public final String message;
        public final JavaItem referenceItem; // can be null
        public final JavaItem newItem; // can be null
        public final String source; // can be null
        
        public Report(Level level, String message, JavaItem referenceItem, JavaItem newItem) {
            this.level = level;
            this.message = message;
            this.referenceItem = referenceItem;
            this.newItem = newItem;
            this.source = (referenceItem.getOwner() == null ? ((ClassData)referenceItem).getFilename() : referenceItem.getOwner().getFilename());
        }
        
        public Report(Level level, String message) {
            this.level = level;
            this.message = message;
            this.referenceItem = null;
            this.newItem = null;
            this.source = null; 
        }
    }
    
}
