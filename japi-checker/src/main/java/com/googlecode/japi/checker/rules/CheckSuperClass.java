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
package com.googlecode.japi.checker.rules;

import com.googlecode.japi.checker.Reporter;
import com.googlecode.japi.checker.Reporter.Level;
import com.googlecode.japi.checker.Reporter.Report;
import com.googlecode.japi.checker.Rule;
import com.googlecode.japi.checker.RuleHelpers;
import com.googlecode.japi.checker.Scope;
import com.googlecode.japi.checker.model.ClassData;
import com.googlecode.japi.checker.model.JavaItem;

/**
 * Check if the superclass is still compatible.
 * In the following example the checked class inherits from B.
 * <code>
 * CheckedClass- 
 *               \
 *              A -> B -> C -> D
 * </code>
 *
 * The checked class will remain backward compatible if it
 * inherits from B or A in the new version. 
 *
 */
public class CheckSuperClass implements Rule {
    
    @Override
    public void checkBackwardCompatibility(Reporter reporter,
            JavaItem reference, JavaItem newItem) {
        // this check only applies on public class (and not interface).
        if (reference instanceof ClassData && !reference.isInterface() 
                && reference.getVisibility() == Scope.PUBLIC) {
            ClassData referenceClass = (ClassData)reference;
            ClassData newClass = (ClassData)newItem;
            if (!RuleHelpers.isClassPartOfClassTree(newClass.getClassDataLoader(), referenceClass.getSuperName(), newClass.getSuperName())) {
                reporter.report(new Report(Level.ERROR, "The class " + reference.getName() + " does not inherit from " + referenceClass.getSuperName() + " anymore.", reference, newItem));
            }
        }
    }
}
