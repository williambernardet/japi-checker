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
package com.googlecode.japi.checker.reporters;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

import com.googlecode.japi.checker.Reporter;
import com.googlecode.japi.checker.model.MethodData;

public class WriterReporter implements Reporter {
    private Logger logger = Logger.getLogger(WriterReporter.class.getName());
    private Writer writer;
    
    public WriterReporter(Writer writer) {
        this.writer = writer;
    }
    
    @Override
    public void report(Report report) {
        if (this.writer != null) {
           try {
               writer.append(format(report) + "\n");
           } catch (IOException e) {
               logger.severe(e.getMessage());
           }
        }
    }

    protected String format(Report report) {
        if (report.getSource() != null) {
            return report.getSeverity().name() + ": " + report.getSource() + getLine(report) + ": " + report.getMessage();
        } else {
            return report.getSeverity().name() + ": " + report.getMessage();
        }
    }
    
    protected static String getLine(Report report) {
        if (report.getNewItem() != null && report.getNewItem() instanceof MethodData) {
            return "(" + ((MethodData)report.getNewItem()).getLineNumber() + ")";
        }
        return "";
    }
}
