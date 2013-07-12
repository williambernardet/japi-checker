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
package com.googlecode.japi.checker;

import com.googlecode.japi.checker.Reporter;

/**
 * Reporter implementation which is able to count message
 * having a particilar severity. Default severity is ERROR.
 *
 */
public class SeverityCountReporter implements Reporter {
    private int count;
    private Severity severity;
    
    /**
     * Default constructor using ERROR severity.
     */
    public SeverityCountReporter() {
    	this(Severity.ERROR);
    }

    /**
     * Count for a custom level.
     * @param severity the severity to look for.
     */
    public SeverityCountReporter(Severity severity) {
    	this.severity = severity; 
    }

    /**
     * Count message of given severity.
     */
    @Override
    public void report(Report report) {
        if (severity == report.getSeverity()) {
            count++;
        }
    }
    
    /**
     * Get the number message with specified severity found.
     * @return the number of message found
     */
    public int getCount() {
        return count;
    }

    /**
     * Returns true if any message of configured severity is found.
     */
    public boolean hasSeverity() {
        return count > 0;
    }
    
}
