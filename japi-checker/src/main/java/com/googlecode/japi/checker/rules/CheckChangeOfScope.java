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

import com.googlecode.japi.checker.Reporter;
import com.googlecode.japi.checker.Scope;
import com.googlecode.japi.checker.Reporter.Report;
import com.googlecode.japi.checker.Rule;
import com.googlecode.japi.checker.model.ClassData;
import com.googlecode.japi.checker.model.JavaItem;

public class CheckChangeOfScope implements Rule {

    @Override
    public void checkBackwardCompatibility(Reporter reporter,
            JavaItem reference, JavaItem newItem) {
        if (reference.getOwner() == null || (reference.getOwner().getVisibility() == Scope.PUBLIC || reference.getOwner().getVisibility() == Scope.PROTECTED)) {
            if (newItem.getVisibility().getValue() < reference.getVisibility().getValue()) {
                // lower visibility
                reporter.report(new Report(Reporter.Level.ERROR, "The visibility of the " + newItem.getName() + " " + newItem.getType() + " has been changed from " +
                    reference.getVisibility() + " to " + newItem.getVisibility(), reference, newItem));
            } else if (newItem.getVisibility().getValue() == reference.getVisibility().getValue()) {
                reporter.report(new Report(Reporter.Level.INFO, "The visibility of the " + newItem.getName() + " " + newItem.getType() + " has not changed", reference, newItem));
            } else {
                reporter.report(new Report(Reporter.Level.WARNING, "The visibility of the " + newItem.getName() + " " + newItem.getType() + " has been changed from " +
                    reference.getVisibility() + " to " + newItem.getVisibility(), reference, newItem));
            }
        }
    }
    
    private static ClassData getRootClass(JavaItem item) {
        if (item.getOwner() == null) {
            return (ClassData)item;
        }
        return item.getOwner();
    }

}
