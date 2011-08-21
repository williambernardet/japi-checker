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
import static org.junit.Assert.fail;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.japi.checker.Reporter.Level;
import com.googlecode.japi.checker.rules.CheckChangeOfScope;
import com.googlecode.japi.checker.rules.CheckFieldChangeOfType;
import com.googlecode.japi.checker.rules.CheckFieldChangeToStatic;
import com.googlecode.japi.checker.rules.CheckInheritanceChanges;
import com.googlecode.japi.checker.rules.CheckMethodChangedToFinal;
import com.googlecode.japi.checker.rules.CheckMethodChangedToStatic;
import com.googlecode.japi.checker.rules.CheckMethodException;
import com.googlecode.japi.checker.rules.CheckRemovedMethod;
import com.googlecode.japi.checker.rules.ClassChangedToAbstract;
import com.googlecode.japi.checker.rules.ClassChangedToFinal;
import com.googlecode.japi.checker.rules.ClassChangedToInterface;

public class TestBCChecker {
    
    private File reference;
    private File newVersion;
    
    @Before
    public void setUp() {
        for (String file : System.getProperty("java.class.path").split(File.pathSeparator)) {
            if (file.contains("reference-test-jar")) {
                reference = new File(file);
                System.out.println(reference);
            } else if (file.contains("new-test-jar")) {
                newVersion = new File(file);
                System.out.println(newVersion);
            }
        }
    }

    @Test
    public void testBCCheckerInclude() throws InstantiationException, IllegalAccessException {
        BasicReporter reporter = check(null, "**/Nothing*.class");
        assertEquals(0, reporter.getMessages().size());
    }

    
    @Test
    public void testCheckerClassRemoved() throws InstantiationException, IllegalAccessException {
        BasicReporter reporter = check(null, "**/RemovedClass.class");
        assertEquals(1, reporter.count(Level.ERROR));
        reporter.assertContains(Level.ERROR, "Public class com/googlecode/japi/checker/tests/RemovedClass has been removed.");
    }

    @Test
    public void testClassToInterface() throws InstantiationException, IllegalAccessException {
        BasicReporter reporter = check(ClassChangedToInterface.class, "**/ClassToInterface.class");
        reporter.assertContains(Level.ERROR, "com/googlecode/japi/checker/tests/ClassToInterface: the class has been change into an interface.");
    }

    @Test
    public void testClassToAbstract() throws InstantiationException, IllegalAccessException {
        BasicReporter reporter = check(ClassChangedToAbstract.class, "**/ClassToAbstract.class");
        reporter.assertContains(Level.ERROR, "com/googlecode/japi/checker/tests/ClassToAbstract: the class has been change to be abstract.");
    }

    @Test
    public void testCheckChangeOfScope() throws InstantiationException, IllegalAccessException {
        BasicReporter reporter = check(CheckChangeOfScope.class, "**/PublicClassToProtected.class");
        reporter.assertContains(Level.ERROR, "The visibility of the <init> method has been changed from PUBLIC to NO_SCOPE");
        reporter.assertContains(Level.ERROR, "The visibility of the com/googlecode/japi/checker/tests/PublicClassToProtected class has been changed from PUBLIC to NO_SCOPE");
        assertEquals(2, reporter.count(Level.ERROR));
    }

    @Test
    public void testCheckChangeOfScopeForField() throws InstantiationException, IllegalAccessException {
        BasicReporter reporter = check(CheckChangeOfScope.class, "**/FieldTestCases.class");
        reporter.assertContains(Level.ERROR, "The visibility of the testChangeOfScopeFromPublicToProtected field has been changed from PUBLIC to PROTECTED");
        reporter.assertContains(Level.ERROR, "The visibility of the testChangeOfScopeFromPublicToPrivate field has been changed from PUBLIC to PRIVATE");
        reporter.assertContains(Level.ERROR, "The visibility of the testChangeOfScopeFromProtectedToPrivate field has been changed from PROTECTED to PRIVATE");
        reporter.assertContains(Level.WARNING, "The visibility of the testChangeOfScopeFromProtectedToPublic field has been changed from PROTECTED to PUBLIC");
        reporter.assertContains(Level.WARNING, "The visibility of the testChangeOfScopeFromPrivateToPublic field has been changed from PRIVATE to PUBLIC");
        reporter.assertContains(Level.WARNING, "The visibility of the testChangeOfScopeFromPrivateToProtected field has been changed from PRIVATE to PROTECTED");
        assertEquals(3, reporter.count(Level.ERROR));
        assertEquals(3, reporter.count(Level.WARNING));
    }

    @Test
    public void testCheckFieldChangeOfType() throws InstantiationException, IllegalAccessException {
        BasicReporter reporter = check(CheckFieldChangeOfType.class, "**/FieldTestCases.class");
        reporter.assertContains(Level.ERROR, "field testChangeOfTypePublic has been modified from Ljava/lang/String; to Ljava/lang/Boolean;");
        reporter.assertContains(Level.ERROR, "field testChangeOfTypeProtected has been modified from Ljava/lang/String; to Ljava/lang/Boolean;");
        assertEquals(2, reporter.count(Level.ERROR));
    }

    @Test
    public void testCheckFieldChangeToStatic() throws InstantiationException, IllegalAccessException {
        BasicReporter reporter = check(CheckFieldChangeToStatic.class, "**/FieldTestCases.class");
        reporter.assertContains(Level.ERROR, "field testPublicChangeToStatic is now static.");
        reporter.assertContains(Level.ERROR, "field testProtectedChangeToStatic is now static.");
        reporter.assertContains(Level.ERROR, "field testPublicChangeFromStatic is not static anymore.");
        reporter.assertContains(Level.ERROR, "field testProtectedChangeFromStatic is not static anymore.");
        assertEquals(4, reporter.count(Level.ERROR));
    }

    @Test
    public void testClassChangedToFinal() throws InstantiationException, IllegalAccessException {
        BasicReporter reporter = check(ClassChangedToFinal.class, "**/PublicClassToFinal.class");
        reporter.assertContains(Level.ERROR, "com/googlecode/japi/checker/tests/PublicClassToFinal: the class has been made final, this breaks inheritance.");
        assertEquals(1, reporter.count(Level.ERROR));
    }
 
    @Test
    public void testCheckInheritanceChanges() throws InstantiationException, IllegalAccessException {
        BasicReporter reporter = check(CheckInheritanceChanges.class, "**/CheckInheritanceChanges.class");
        reporter.assertContains(Level.ERROR, "com/googlecode/japi/checker/tests/CheckInheritanceChanges: extends java/util/ArrayList and not java/util/Vector anymore.");
        reporter.assertContains(Level.ERROR, "com/googlecode/japi/checker/tests/CheckInheritanceChanges: is not implementing java/io/Serializable anymore.");
        assertEquals(2, reporter.count(Level.ERROR));
    }
    
    @Test
    public void testCheckRemovedMethod() throws InstantiationException, IllegalAccessException {
        BasicReporter reporter = check(CheckRemovedMethod.class, "**/CheckRemovedMethod.class");
        reporter.assertContains(Level.ERROR, "com/googlecode/japi/checker/tests/CheckRemovedMethod: Could not find method publicMethodRemoved in newer version.");
        reporter.assertContains(Level.ERROR, "com/googlecode/japi/checker/tests/CheckRemovedMethod: Could not find method protectedMethodRemoved in newer version.");
        assertEquals(2, reporter.count(Level.ERROR));
    }
    
    @Test
    public void testCheckMethodException() throws InstantiationException, IllegalAccessException {
        BasicReporter reporter = check(CheckMethodException.class, "**/CheckMethodException.class");
        reporter.assertContains(Level.ERROR, "publicAddedException is now throwing java/lang/Exception.");
        reporter.assertContains(Level.ERROR, "protectedAddedException is now throwing java/lang/Exception.");
        reporter.assertContains(Level.ERROR, "publicRemovedException is not throwing java/lang/Exception anymore.");
        reporter.assertContains(Level.ERROR, "protectedRemovedException is not throwing java/lang/Exception anymore.");
        assertEquals(4, reporter.count(Level.ERROR));
    }
    
    @Test
    public void testCheckerInnerClassRemoved() throws InstantiationException, IllegalAccessException {
        BasicReporter reporter = check(null, "**/InnerClassRemoved*.class");
        assertEquals(4, reporter.count(Level.ERROR));
        //reporter.assertContains(Level.ERROR, "Public class com/googlecode/japi/checker/tests/RemovedClass has been removed.");
    }

    @Test
    public void testCheckMethodChangedToFinal() throws InstantiationException, IllegalAccessException {
        BasicReporter reporter = check(CheckMethodChangedToFinal.class, "**/CheckMethodAccess.class");
        reporter.assertContains(Level.ERROR, "com/googlecode/japi/checker/tests/CheckMethodAccess: the method publicToFinal has been made final, this now prevents overriding.");
        reporter.assertContains(Level.ERROR, "com/googlecode/japi/checker/tests/CheckMethodAccess: the method protectedToFinal has been made final, this now prevents overriding.");
        assertEquals(2, reporter.count(Level.ERROR));
    }

    @Test
    public void testCheckMethodChangedToStatic() throws InstantiationException, IllegalAccessException {
        BasicReporter reporter = check(CheckMethodChangedToStatic.class, "**/CheckMethodAccess.class");
        reporter.assertContains(Level.ERROR, "com/googlecode/japi/checker/tests/CheckMethodAccess: the method publicToStatic has been made static.");
        reporter.assertContains(Level.ERROR, "com/googlecode/japi/checker/tests/CheckMethodAccess: the method protectedToStatic has been made static.");
        reporter.assertContains(Level.ERROR, "com/googlecode/japi/checker/tests/CheckMethodAccess: the method publicFromStatic is not static anymore.");
        reporter.assertContains(Level.ERROR, "com/googlecode/japi/checker/tests/CheckMethodAccess: the method protectedFromStatic is not static anymore");
        assertEquals(4, reporter.count(Level.ERROR));
    }

    public BasicReporter check(Class<? extends Rule> clazz, String ... includes) throws InstantiationException, IllegalAccessException {
        BCChecker checker = new BCChecker(reference, newVersion);
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
        checker.checkBacwardCompatibility(reporter, rules);
        return reporter;
    }
    
    public static class BasicReporter implements Reporter {
        List<Message> messages = new ArrayList<Message>();
        
        @Override
        public void report(Level level, String message) {
            System.out.println(level.toString() + ": " + message);
            messages.add(new Message(level, message));
        }
        
        public List<Message> getMessages() {
            return messages;
        }

        public int count(Level level) {
            int count = 0;
            for (Message message : messages) {
                if (message.level == level) {
                    count++;
                }
            }
            return count;
        }
        
        public void assertContains(Level level, String str) {
            for (Message message : messages) {
                if (message.level == level && message.message.contains(str)) {
                    return;
                }
            }
            fail("Could not find message containing: " + str);
        }
        
        class Message {
            public Level level;
            public String message;
            public Message(Level level, String message) {
                this.level = level;
                this.message = message;
            }
        }
    }

}
