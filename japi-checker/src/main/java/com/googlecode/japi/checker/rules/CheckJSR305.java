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
import com.googlecode.japi.checker.Severity;
import com.googlecode.japi.checker.model.JavaItem;
import com.googlecode.japi.checker.model.MethodData;

public class CheckJSR305 implements Rule {
    private static final String JSR305_NONNULL_DESC = "Ljavax/annotation/Nonnull;";
    private static final String JSR305_NULLABLE_DESC = "Ljavax/annotation/Nullable;";
    
    
    @Override
    public void checkBackwardCompatibility(Reporter reporter, JavaItem reference, JavaItem newItem) {
        if (reference.getVisibility() != Scope.PRIVATE && reference instanceof MethodData) {
            MethodData referenceMethod = (MethodData)reference;
            MethodData newMethod = (MethodData)newItem;
            if (referenceMethod.getAnnotation(JSR305_NONNULL_DESC) != null &&
                    newMethod.getAnnotation(JSR305_NULLABLE_DESC) != null) {
                reporter.report(new Report(Severity.ERROR, "The reference method declares the JSR305 Nonnull annotation, but the new method declares to be Nullable.", reference, newItem));
            } else if (referenceMethod.getAnnotation(JSR305_NONNULL_DESC) != null &&
                    newMethod.getAnnotation(JSR305_NONNULL_DESC) == null) {
                reporter.report(new Report(Severity.ERROR, "The reference method declares the JSR305 Nonnull annotation, but not the new method.", reference, newItem));
            } 
        }
    }

}
