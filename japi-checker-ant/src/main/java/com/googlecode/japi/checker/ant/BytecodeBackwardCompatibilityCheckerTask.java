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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import com.googlecode.japi.checker.BCChecker;
import com.googlecode.japi.checker.MuxReporter;
import com.googlecode.japi.checker.Rule;
import com.googlecode.japi.checker.SeverityCountReporter;

public class BytecodeBackwardCompatibilityCheckerTask extends Task {
	
	private File referenceFile;
	private File file;
	private boolean failOnError = true;
	private List<Path> classpaths = new ArrayList<Path>();
	private List<Path> referenceClasspaths = new ArrayList<Path>();
	private List<RuleSet> ruleSets = new ArrayList<RuleSet>();
			
	public File getReferenceFile() {
		return referenceFile;
	}

	public void setReferenceFile(File referenceFile) {
		this.referenceFile = referenceFile;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	public Path createReferenceClassPath() {
		Path path = new Path(this.getProject());
		referenceClasspaths.add(path);
		return path;
	}

	public Path createClassPath() {
		Path path = new Path(this.getProject());
		classpaths.add(path);
		return path;
	}

	public void add(RuleSet ruleSet) {
		ruleSets.add(ruleSet);
	}
	
	public void execute() {
		if (getReferenceFile() == null) {
			throw new BuildException("The 'referenceFile' attribute is not defined.");
		}
		if (getFile() == null) {
			throw new BuildException("The 'file' attribute is not defined.");
		}
		log("Checking " + getFile().getAbsolutePath() + " backward compatibility against " + getReferenceFile().getAbsolutePath());
		try {
			BCChecker checker = new BCChecker(getReferenceFile(), getFile());
            
			// Configuring the reporting
			MuxReporter mux = new MuxReporter();
            mux.add(new AntReporter(this));
            SeverityCountReporter ec = new SeverityCountReporter();
            mux.add(ec);
            
            // Setting up the classpaths for the reference and new version.
            for (Path path : this.referenceClasspaths) {
            	for (String filename : path.list()) {
            		checker.addToReferenceClasspath(new File(filename));
            	}
            }
            for (Path path : this.classpaths) {
            	for (String filename : path.list()) {
            		checker.addToNewArtifactClasspath(new File(filename));
            	}
            }
            
            // Load rules
            List<Rule> rules = new ArrayList<Rule>();
            for (RuleSet ruleSet : ruleSets) {
            	for (com.googlecode.japi.checker.ant.Rule rule : ruleSet.getRules()) {
                    try {
                        @SuppressWarnings("unchecked")
                        Class<Rule> clazz = (Class<Rule>)this.getClass().getClassLoader().loadClass(rule.getClassname());
                        rules.add(clazz.newInstance());
                    } catch (ClassNotFoundException e) {
                        throw new BuildException(e.getMessage(), e);
                    } catch (InstantiationException e) {
                        throw new BuildException(e.getMessage(), e);
                    } catch (IllegalAccessException e) {
                        throw new BuildException(e.getMessage(), e);
                    }
            		
            	}
            }
            // Running the check...
			checker.checkBacwardCompatibility(mux, rules);
			
			// Summary, failing ant in case of error...
            if (ec.hasSeverity()) {
                log("You have " + ec.getCount() + " backward compatibility issues.", Project.MSG_ERR);
                if (failOnError) {
                	throw new BuildException("You have " + ec.getCount() + " backward compatibility issues.");
                }
            } else {
                log("No backward compatibility issue found.");
            }
		} catch (IOException e) {
			throw new BuildException(e.getMessage(), e);
		}
	}

}
