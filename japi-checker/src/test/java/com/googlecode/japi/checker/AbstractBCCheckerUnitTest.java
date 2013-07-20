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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;

import com.googlecode.japi.checker.model.MethodData;

public class AbstractBCCheckerUnitTest {

    private File reference;
    private File newVersion;
    private Handler handler = new Handler() {

        @Override
        public void close() throws SecurityException {
        }

        @Override
        public void flush() {
        }

        @Override
        public void publish(LogRecord record) {
            System.out.println(record.getMessage());
        }
        
    };
    
    @Before
    public void setUp() {
        Logger.getLogger(ClassDumper.class.getName()).setLevel(java.util.logging.Level.ALL);
        Logger.getLogger(ClassDumper.class.getName()).addHandler(handler);
        Logger.getLogger(AnnotationDumper.class.getName()).setLevel(java.util.logging.Level.ALL);
        Logger.getLogger(AnnotationDumper.class.getName()).addHandler(handler);
        Logger.getLogger(MethodDumper.class.getName()).setLevel(java.util.logging.Level.ALL);
        Logger.getLogger(MethodDumper.class.getName()).addHandler(handler);
        System.out.println("==================================");
        for (String file : System.getProperty("java.class.path").split(File.pathSeparator)) {
            if (file.contains("reference-test-jar")) {
                reference = new File(file);
                System.out.println(reference);
            } else if (file.contains("new-test-jar")) {
                newVersion = new File(file);
                System.out.println(newVersion);
            }
        }
        assertNotNull("The reference library is not found.", reference);
        assertNotNull("The newVersion library is not found.", newVersion);
    }
    
    @After
    public void tearDown() {
        Logger.getLogger(ClassDumper.class.getName()).removeHandler(handler);
        Logger.getLogger(AnnotationDumper.class.getName()).removeHandler(handler);
        Logger.getLogger(MethodDumper.class.getName()).removeHandler(handler);
    }
    
    /**
     * Run a check with specified rule on a set of class referenced by the includes list.
     * A reporter is returned. It can be used to assert the result of the check.
     * @param clazz
     * @param includes
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public BasicReporter check(Class<? extends Rule> clazz, String ... includes) throws InstantiationException, IllegalAccessException, IOException {
        BCChecker checker = new BCChecker();
        BasicReporter reporter = new BasicReporter();
        List<Rule> rules = new ArrayList<Rule>();
        if (clazz != null) {
            rules.add(clazz.newInstance());
        }
        if (includes != null) {
            for (String include : includes) {
                checker.addInclude(include);
            }
        }
        checker.setRules(rules);
        checker.setReporter(reporter);
        checker.checkBacwardCompatibility(reference, newVersion);
        return reporter;
    }
    
    public static class BasicReporter implements Reporter {
        List<Report> messages = new ArrayList<Report>();
        
        @Override
        public void report(Report report) {
            System.out.println(report.getSeverity().toString() + ": " + report.getSource() + getLine(report) + ": " + report.getMessage());
            messages.add(report);
        }
        
        private static String getLine(Report report) {
            if (report.getNewItem() instanceof MethodData) {
                return "(" + ((MethodData)report.getNewItem()).getLineNumber() + ")";
            }
            return "";
        }
        
        public List<Report> getMessages() {
            return messages;
        }

        public int count(Severity severity) {
            int count = 0;
            for (Report message : messages) {
                if (message.getSeverity() == severity) {
                    count++;
                }
            }
            return count;
        }
        
        public void assertContains(Severity severity, String str) {
            for (Report message : messages) {
                if (message.getSeverity() == severity && message.getMessage().contains(str)) {
                    return;
                }
            }
            fail("Could not find message containing: " + str);
        }

    }
    
}
