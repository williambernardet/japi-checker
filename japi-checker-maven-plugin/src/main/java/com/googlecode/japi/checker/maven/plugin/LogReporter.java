/*
 * Copyright 2011 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.japi.checker.maven.plugin;

import org.apache.maven.plugin.logging.Log;

import com.googlecode.japi.checker.Reporter;
import com.googlecode.japi.checker.Severity;
import com.googlecode.japi.checker.model.MethodData;

public class LogReporter implements Reporter {

    private Log log;
    private Severity display = Severity.WARNING;
    
    public LogReporter(Log log, Severity display) {
        this(log);
        this.display = display;
    }

    public LogReporter(Log log) {
        this.log = log;
    }
    
    public void report(Report report) {
        if (report.getSeverity().ordinal() <= display.ordinal()) {
            if (report.getSeverity() == Severity.ERROR) {
                log.error(format(report));
            } else if (report.getSeverity() == Severity.WARNING) {
                log.warn(format(report));
            } else if (report.getSeverity() == Severity.INFO) {
                log.info(format(report));
            } else if (report.getSeverity() == Severity.DEBUG) {
                log.debug(format(report));
            }
        }
    }

    private String format(Report report) {
    	if (report.getSource() == null) {
    		return report.getMessage();
    	}
        return report.getSource() + getLine(report) + ": " + report.getMessage();
    }
    
    private static String getLine(Report report) {
        if (report.getNewItem() instanceof MethodData) {
            return "(" + ((MethodData)report.getNewItem()).getLineNumber() + ")";
        }
        return "";
    }
}
