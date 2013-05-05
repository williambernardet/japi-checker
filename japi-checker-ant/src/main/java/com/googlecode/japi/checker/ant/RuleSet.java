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

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.types.DataType;

/**
 * Defines a set of rules to apply on during
 * the checking. 
 *
 */
public class RuleSet extends DataType {

	private List<Rule> rules = new ArrayList<Rule>(); 
	
	public Rule createRule() {
		Rule rule = new Rule();
		rules.add(rule);
		return rule;
	}
	
	
	public List<Rule> getRules() {
		if (this.isReference()) {
			return ((RuleSet)this.getRefid().getReferencedObject()).getRules();
		}
		return rules;
	}
	
}
