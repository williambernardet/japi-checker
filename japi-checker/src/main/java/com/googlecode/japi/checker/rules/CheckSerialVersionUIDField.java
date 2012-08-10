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
import com.googlecode.japi.checker.Rule;
import com.googlecode.japi.checker.Reporter.Level;
import com.googlecode.japi.checker.Reporter.Report;
import com.googlecode.japi.checker.model.FieldData;
import com.googlecode.japi.checker.model.JavaItem;

/**
 * This test looks for modifications to serialVersionUID class attribute.
 * This field is used by the serialized interface to identify the class
 * so changing its value is not a good idea from BC prospective. 
 */
public class CheckSerialVersionUIDField implements Rule {

    @Override
    public void checkBackwardCompatibility(Reporter reporter,
            JavaItem reference, JavaItem newItem) {
        // private static final long serialVersionUID = 338331310737246989L;
        if (reference instanceof FieldData) {
            FieldData referenceField = (FieldData)reference;
            FieldData newField = (FieldData)newItem;
            if ("serialVersionUID".equals(referenceField.getName())) {
                // J means long.
                if (!"J".equals(referenceField.getDescriptor())) {
                    reporter.report(new Report(Level.ERROR, "The type for field serialVersionUID is invalid, it must be a long.", reference, newItem));
                    return;
                }
                if (!"J".equals(newField.getDescriptor())) {
                    reporter.report(new Report(Level.ERROR, "The type for field serialVersionUID is invalid, it must be a long.", reference, newItem));
                    return;
                }
                System.out.println(referenceField.getValue().getClass().getName());
                if (((Long)referenceField.getValue()).longValue() != ((Long)newField.getValue()).longValue()) {
                    reporter.report(new Report(Level.ERROR, "The value of the serialVersionUID" +
                            " field has changed from " + toHex(referenceField) + " to "  + toHex(newField) + ".", reference, newItem));
                }
            }
        }
    }
    
    private static String toHex(FieldData field) {
        return String.format("0x%x", ((Long)field.getValue()).longValue());
    }

}
