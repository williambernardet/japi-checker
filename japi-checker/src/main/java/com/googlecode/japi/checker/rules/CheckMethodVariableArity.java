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
package com.googlecode.japi.checker.rules;

import com.googlecode.japi.checker.Reporter;
import com.googlecode.japi.checker.Scope;
import com.googlecode.japi.checker.Reporter.Report;
import com.googlecode.japi.checker.Rule;
import com.googlecode.japi.checker.Severity;
import com.googlecode.japi.checker.model.JavaItem;
import com.googlecode.japi.checker.model.MethodData;

/**
 * Checks that a method is not changing one of its parameter from
 * arity format (vararg) to array. This is break the backward compatibility, 
 * for example in the following case:
 * <pre>
 * method(String ... args);
 * </pre>
 * 
 * The followin calls would not work anymore:
 * <pre>
 * method("A", "B");
 * </pre>
 * Only the following call would still work: 
 * <pre>
 * method(new String []{"A", "B"});
 * </pre>
 * 
 * The array to arity should not cause any problems, the array will be unroll.
 * 
 * @author Tomas Rohovsky
 *
 */
public class CheckMethodVariableArity implements Rule {
	
	@Override
    public void checkBackwardCompatibility(Reporter reporter, JavaItem reference, JavaItem newItem) {
    	if (reference instanceof MethodData &&
    			reference.getVisibility().isMoreVisibleThan(Scope.NO_SCOPE) &&
    			((MethodData) reference).isVariableArity() &&
    			!((MethodData) newItem).isVariableArity()) {
			reporter.report(new Report(Severity.ERROR, "The parameter of the " + newItem.getName()
					+ " has been changed from variable arity to array", newItem, reference));
        }
    }
}
