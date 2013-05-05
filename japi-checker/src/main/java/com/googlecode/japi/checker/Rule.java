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

import javax.annotation.Nonnull;

import com.googlecode.japi.checker.model.JavaItem;

/**
 * Interface defining a validation rule. 
 *
 */
public interface Rule {
	
	/**
	 * 
	 * @param reporter the reporter where to report issues to, never null.
	 * @param reference the reference item, never null.
	 * @param newItem the new item, never null.
	 */
    void checkBackwardCompatibility(@Nonnull Reporter reporter, JavaItem reference, JavaItem newItem);
    
}
