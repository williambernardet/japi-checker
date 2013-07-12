/*
 * Copyright 2013 Tomas Rohovsky
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
package com.googlecode.japi.checker.cli;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.japi.checker.Reporter;
import com.googlecode.japi.checker.Severity;
import com.googlecode.japi.checker.model.MethodData;

/**
 * Reporter implementation for command line tool.
 *  
 * @author Tomas Rohovsky
 * @author William Bernardet
 */
public class CLIReporter implements Reporter {
    private List<Report> reports = new ArrayList<Report>();

    public void report(Report report) {
        if ((report.getSeverity() == Severity.ERROR ||
        		report.getSeverity() == Severity.WARNING)) {
                System.out.println(format(report));
        }
        reports.add(report);
    }

    private String format(Report report) {
    	if (report.getSource() == null) {
    		return report.getMessage();
    	}
    	return report.getSeverity().toString() + ": " + report.getSource() + getLine(report) + ": " + report.getMessage();
    }
    
    private static String getLine(Report report) {
        if (report.getNewItem() instanceof MethodData) {
            return "(" + ((MethodData)report.getNewItem()).getLineNumber() + ")";
        }
        return "";
    }
    
    public List<Report> getReports() {
        return reports;
    }

    public int getCount(Severity severity) {
        int count = 0;
        for (Report report : reports) {
            if (report.getSeverity() == severity) {
            	count++;
            }
        }
        return count;
    }
}
