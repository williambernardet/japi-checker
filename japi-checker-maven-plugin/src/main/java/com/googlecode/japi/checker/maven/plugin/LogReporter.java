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
import com.googlecode.japi.checker.model.MethodData;

public class LogReporter implements Reporter {

    private Log log;
    private Level display = Level.WARNING;
    
    public LogReporter(Log log, Level display) {
        this(log);
        this.display = display;
    }

    public LogReporter(Log log) {
        this.log = log;
    }
    
    public void report(Report report) {
        if (report.level.ordinal() <= display.ordinal()) {
            if (report.level == Level.ERROR) {
                log.error(format(report));
            } else if (report.level == Level.WARNING) {
                log.warn(format(report));
            } else if (report.level == Level.INFO) {
                log.info(format(report));
            } else if (report.level == Level.DEBUG) {
                log.debug(format(report));
            }
        }
    }

    private String format(Report report) {
        return report.source + getLine(report) + ": " + report.message;
    }
    
    private static String getLine(Report report) {
        if (report.newItem instanceof MethodData) {
            return "(" + ((MethodData)report.newItem).getLineNumber() + ")";
        }
        return "";
    }
}
