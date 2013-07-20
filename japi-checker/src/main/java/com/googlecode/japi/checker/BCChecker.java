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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.googlecode.japi.checker.Severity;
import com.googlecode.japi.checker.Reporter.Report;
import com.googlecode.japi.checker.model.ClassData;
import com.googlecode.japi.checker.utils.AntPatternMatcher;

/**
 * The class driving the backward compatibility checks.
 * It handles the Rules, Reporter, dependencies and
 *  include/exclude mechanism for the checking.
 *  
 * @author william.bernardet@gmail.com
 * 
 */
public class BCChecker {
    private List<File> referenceClasspath = new ArrayList<File>();
    private List<File> newArtifactClasspath = new ArrayList<File>();
    private List<AntPatternMatcher> includes = new ArrayList<AntPatternMatcher>();
    private List<AntPatternMatcher> excludes = new ArrayList<AntPatternMatcher>();
    private ClassDataLoaderFactory classDataLoaderFactory = new DefaultClassDataLoaderFactory();
    private boolean warnOnDependencyLoadingError;
    private Reporter reporter;
    private List<Rule> rules = Collections.emptyList();
    
 
    /**
     * Add a path either a jar or a directory to the reference artifact classpath.
     * @param path
     */
    public void addToReferenceClasspath(File path) {
        this.referenceClasspath.add(path);
    }

    /**
     * Add a path either a jar or a directory to the new artifact classpath.
     * @param path
     */
    public void addToNewArtifactClasspath(File path) {
        this.newArtifactClasspath.add(path);
    }
    
    /**
     * Add an include pattern for the class file scanning.
     * e.g: org/myproject/mypackage/api/&#42;&#42;/&#42;.class
     * @param include
     */
    public void addInclude(String include) {
        includes.add(new AntPatternMatcher(include));
    }

    /**
     * Add an exclude pattern for the class file scanning.
     * e.g: org/myproject/mypackage/api/&#42;&#42;/&#42;.class
     * @param include
     */
    public void addExclude(String exclude) {
        excludes.add(new AntPatternMatcher(exclude));
    }

    /**
     * Defines a custom reporter.
     * @param reporter
     */
    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    /**
     * Get the current reporter.
     * @return
     */
    public Reporter getReporter() {
        return this.reporter;
    }
    
    /**
     * Defines the rules to apply during the check.
     * @param rules
     */
    public void setRules(List<Rule> rules) {
        if (rules == null) {
            rules = Collections.emptyList();
        }
        this.rules = rules;
    }

    /**
     * Run the check between the reference and the newArtifact.
     * @param reference
     * @param newArtifact
     * @throws IOException
     */
    public void checkBacwardCompatibility(File reference, File newArtifact) throws IOException {
        if (reference == null) {
            throw new IllegalArgumentException("The reference parameter cannot be null.");
        }
        if (newArtifact == null) {
            throw new IllegalArgumentException("The newArtifact parameter cannot be null.");
        }
        if (!reference.isDirectory() && !Utils.isArchive(reference)) {
            throw new IllegalArgumentException("reference must be either a directory" +
                    " or a jar (or a zip kind of archive) file");
        }
        if (!newArtifact.isDirectory() && !Utils.isArchive(newArtifact)) {
            throw new IllegalArgumentException("new artifact must be either a directory" + 
                    " or a jar (or a zip kind of archive) file");
        }
        Reporter reporter = this.getReporter();
        if (reporter == null) {
            // if reporter is not defined just stub it...
            reporter = new Reporter() {
                @Override
                public void report(Report report) { }
            };
        }
        ClassDataLoader referenceDataLoader = classDataLoaderFactory.createClassDataLoader();
        reporter.report(new Report(Severity.INFO, "Reading reference artifact: " + reference));
        referenceDataLoader.read(reference.toURI());
        for (File file : this.referenceClasspath) {
            try {
                reporter.report(new Report(Severity.INFO, "Reading reference dependency: " + file));
                referenceDataLoader.read(file.toURI());
            } catch (ReadClassException e){
                if (this.shouldWarnOnDependencyLoadingError()) {
                    reporter.report(new Report(Severity.WARNING, e.getMessage()));
                } else {
                    throw e;
                }
            }
        }
        List<ClassData> referenceData = referenceDataLoader.getClasses(reference.toURI(), includes, excludes);
        ClassDataLoader newArtifactDataLoader = classDataLoaderFactory.createClassDataLoader();
        reporter.report(new Report(Severity.INFO, "Reading artifact: " + newArtifact));
        newArtifactDataLoader.read(newArtifact.toURI());
        for (File file : this.newArtifactClasspath) {
            try {
                reporter.report(new Report(Severity.INFO, "Reading dependency: " + file));
                newArtifactDataLoader.read(file.toURI());
            } catch (ReadClassException e){
                if (this.shouldWarnOnDependencyLoadingError()) {
                    reporter.report(new Report(Severity.WARNING, e.getMessage()));
                } else {
                    throw e;
                }
            }
        }
        List<ClassData> newData = newArtifactDataLoader.getClasses(newArtifact.toURI(), includes, excludes);
        for (ClassData clazz : referenceData) {
            boolean found = false;
            for (ClassData newClazz : newData) {
                if (clazz.isSame(newClazz)) {
                    for (Rule rule : rules) {
                        rule.checkBackwardCompatibility(reporter, clazz, newClazz);
                    }
                    newClazz.checkBackwardCompatibility(reporter, clazz, rules);
                    found = true;
                    break;
                }
            }
            if (!found && clazz.getVisibility() == Scope.PUBLIC) {
                reporter.report(new Report(Severity.ERROR, "Public class " + clazz.getName() + " has been removed.", clazz, null));
            }
        }
    }
    
   /**
     * Defines if the checker should just warn on dependency loading error via the reporter,
     *  or simply fails.
     * @param warnOnDependencyLoadingError
     */
    public void setWarnOnDependencyLoadingError(boolean warnOnDependencyLoadingError) {
        this.warnOnDependencyLoadingError = warnOnDependencyLoadingError;
    }
    
    /**
     * 
     * @return
     */
    public boolean shouldWarnOnDependencyLoadingError() {
        return this.warnOnDependencyLoadingError;
    }

}
