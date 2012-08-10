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
package com.googlecode.japi.checker.rules;

import java.util.List;

import com.googlecode.japi.checker.ClassDataLoader;
import com.googlecode.japi.checker.Reporter;
import com.googlecode.japi.checker.Rule;
import com.googlecode.japi.checker.RuleHelpers;
import com.googlecode.japi.checker.Scope;
import com.googlecode.japi.checker.Reporter.Level;
import com.googlecode.japi.checker.Reporter.Report;
import com.googlecode.japi.checker.model.JavaItem;
import com.googlecode.japi.checker.model.MethodData;

/**
 * This rule checks if a method is still throwing a compatible
 * set of exceptions. 
 * The check is not occurring for private scope.
 * '
 */
public class CheckMethodExceptions implements Rule {


    /**
     * Implementation of the check.
     */
    @Override
    public void checkBackwardCompatibility(Reporter reporter,
            JavaItem reference, JavaItem newItem) {
        if (reference instanceof MethodData && reference.getVisibility() != Scope.PRIVATE) {
            MethodData referenceMethod = (MethodData)reference;
            MethodData newMethod = (MethodData)newItem;
            for (String exception : referenceMethod.getExceptions()) {
                if (!isCompatibleWithAnyOfTheException(newItem.getClassDataLoader(), exception, newMethod.getExceptions())) {
                    reporter.report(new Report(Level.ERROR, referenceMethod.getName() + " is not throwing " + exception + " anymore.", reference, newItem));
                }
            }
            for (String exception : newMethod.getExceptions()) {
                if (!hasCompatibleExceptionInItsHierarchy(newItem.getClassDataLoader(), exception, referenceMethod.getExceptions())) {
                    reporter.report(new Report(Level.ERROR, referenceMethod.getName() + " is now throwing " + exception + ".", reference, newItem));
                }
            }
        }
    }
    
    /**
     * Check if exception is part of inheritance tree of any of the referenceExceptions members.
     * @param loader
     * @param exception
     * @param referenceExceptions
     * @return
     */
    private boolean isCompatibleWithAnyOfTheException(ClassDataLoader loader, String exception, List<String> referenceExceptions) {
        for (String referenceException : referenceExceptions) {
            if (RuleHelpers.isClassPartOfClassTree(loader, exception, referenceException)) {
                return true;
            }
        } 
        return false;
    }
    
    /**
     * Check if any of the referenceException are part of the inherirance tree of exception. 
     * @param loader
     * @param exception
     * @param referenceExceptions
     * @return
     */
    private boolean hasCompatibleExceptionInItsHierarchy(ClassDataLoader loader, String exception, List<String> referenceExceptions) {
        for (String referenceException : referenceExceptions) {
            if (RuleHelpers.isClassPartOfClassTree(loader, referenceException, exception)) {
                return true;
            }
        } 
        return false;
    }
    
}
