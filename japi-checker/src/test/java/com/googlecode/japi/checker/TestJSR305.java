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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.googlecode.japi.checker.Reporter.Level;
import com.googlecode.japi.checker.rules.CheckJSR305;

public class TestJSR305 extends AbstractBCCheckerUnitTest {

    /**
     * We should find 2 issues on public and 2 issues on protected,
     * private methods are ignored...
     */
    @Test
    public void testCheckJSR305NonNullRemoved() throws InstantiationException,
        IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckJSR305.class, "**/CheckNonnull.class");
        assertEquals(4, reporter.count(Level.ERROR));
    }

}
