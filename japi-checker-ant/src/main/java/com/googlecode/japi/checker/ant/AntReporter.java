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
package com.googlecode.japi.checker.ant;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.googlecode.japi.checker.Reporter;
import com.googlecode.japi.checker.model.MethodData;

/**
 * Reporter implementation that uses an Ant task
 * logging system. 
 *
 */
class AntReporter implements Reporter {

	private Task task;
	
	/**
	 * New reporter configured with a Ant task.
	 * @param task
	 */
	public AntReporter(Task task) {
		this.task = task;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void report(Report report) {
		task.log(format(report),
				this.getAntLogLevel(report));
	}

	private int getAntLogLevel(Report report) {
		if (report.level == Reporter.Level.ERROR) {
			return Project.MSG_ERR;
		} else if (report.level == Reporter.Level.WARNING) {
			return Project.MSG_WARN;
		}
		return Project.MSG_INFO;
	}
	
    private String format(Report report) {
    	if (report.source == null) {
    		return report.message;
    	}
    	return report.level.toString() + ": " + report.source + getLine(report) + ": " + report.message;
    }
    
    private static String getLine(Report report) {
        if (report.newItem instanceof MethodData) {
            return "(" + ((MethodData)report.newItem).getLineNumber() + ")";
        }
        return "";
    }

}
