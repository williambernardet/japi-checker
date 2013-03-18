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

import java.util.ArrayList;
import java.util.List;

public class ReadClassesException extends ReadClassException {

    private static final long serialVersionUID = 3850123619855985428L;
    private List<ReadClassException> causes = new ArrayList<ReadClassException>();
    public ReadClassesException() {
        super("");
    }

    public String getMessage() {
        StringBuilder result = new StringBuilder();
        for (ReadClassException cause : causes) {
            result.append(cause.getMessage());
            result.append("\n");
        }
        return result.toString();
    }
    
    public void add(ReadClassException cause) {
        causes.add(cause);
    }
    
    public void throwIfNeeded() throws ReadClassException {
        if (causes.size() > 0) {
            throw this;
        }
    }
    
}
