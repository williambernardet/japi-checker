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
import static org.junit.Assert.assertEquals;
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
import org.junit.Test;

import com.googlecode.japi.checker.Reporter.Level;
import com.googlecode.japi.checker.model.MethodData;
import com.googlecode.japi.checker.rules.CheckChangeOfScope;
import com.googlecode.japi.checker.rules.CheckFieldChangeOfType;
import com.googlecode.japi.checker.rules.CheckFieldChangeToStatic;
import com.googlecode.japi.checker.rules.CheckFieldChangeToTransient;
import com.googlecode.japi.checker.rules.CheckInheritanceChanges;
import com.googlecode.japi.checker.rules.CheckMethodChangedToFinal;
import com.googlecode.japi.checker.rules.CheckMethodChangedToStatic;
import com.googlecode.japi.checker.rules.CheckMethodExceptions;
import com.googlecode.japi.checker.rules.CheckRemovedMethod;
import com.googlecode.japi.checker.rules.CheckSerialVersionUIDField;
import com.googlecode.japi.checker.rules.CheckSuperClass;
import com.googlecode.japi.checker.rules.ClassChangedToAbstract;
import com.googlecode.japi.checker.rules.ClassChangedToFinal;
import com.googlecode.japi.checker.rules.ClassChangedToInterface;
import com.googlecode.japi.checker.rules.InterfaceChangedToClass;

public class TestBCChecker {
    
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
    }

    @Test
    public void testBCCheckerInclude() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(null, "**/Nothing*.class");
        assertEquals(0, reporter.getMessages().size());
    }

    
    @Test
    public void testCheckerClassRemoved() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(null, "**/RemovedClass.class");
        assertEquals(1, reporter.count(Level.ERROR));
        reporter.assertContains(Level.ERROR, "Public class com/googlecode/japi/checker/tests/RemovedClass has been removed.");
    }

    @Test
    public void testClassToInterface() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(ClassChangedToInterface.class, "**/ClassToInterface.class");
        assertEquals(1, reporter.count(Level.ERROR));
        reporter.assertContains(Level.ERROR, "The interface com/googlecode/japi/checker/tests/ClassToInterface has been changed into an class.");
    }

    @Test
    public void testClassToAbstract() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(ClassChangedToAbstract.class, "**/ClassToAbstract.class");
        reporter.assertContains(Level.ERROR, "The class com/googlecode/japi/checker/tests/ClassToAbstract has been made abstract.");
    }

    @Test
    public void testCheckChangeOfScope() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckChangeOfScope.class, "**/PublicClassToProtected.class");
        reporter.assertContains(Level.ERROR, "The visibility of the <init> method has been changed from PUBLIC to NO_SCOPE");
        reporter.assertContains(Level.ERROR, "The visibility of the com/googlecode/japi/checker/tests/PublicClassToProtected class has been changed from PUBLIC to NO_SCOPE");
        assertEquals(2, reporter.count(Level.ERROR));
    }

    @Test
    public void testCheckChangeOfScopeForFieldPublicScope() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckChangeOfScope.class, "**/PublicScopeFieldTestCases.class");
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
    public void testCheckChangeOfScopeForFieldPackageScope() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckChangeOfScope.class, "**/PackageScopeFieldTestCases.class");
        assertEquals(0, reporter.count(Level.ERROR));
        assertEquals(0, reporter.count(Level.WARNING));
    }

    @Test
    public void testCheckFieldChangeOfTypePublicClass() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckFieldChangeOfType.class, "**/PublicScopeFieldTestCases.class");
        reporter.assertContains(Level.ERROR, "field testChangeOfTypePublic has been modified from Ljava/lang/String; to Ljava/lang/Boolean;");
        reporter.assertContains(Level.ERROR, "field testChangeOfTypeProtected has been modified from Ljava/lang/String; to Ljava/lang/Boolean;");
        assertEquals(2, reporter.count(Level.ERROR));
    }

    @Test
    public void testCheckFieldChangeOfTypePackageClass() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckFieldChangeOfType.class, "**/PackageScopeFieldTestCases.class");
        assertEquals(0, reporter.count(Level.ERROR));
    }

    @Test
    public void testCheckFieldChangeToStaticPublicScope() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckFieldChangeToStatic.class, "**/PublicScopeFieldTestCases.class");
        reporter.assertContains(Level.ERROR, "The field testPublicChangeToStatic is now static.");
        reporter.assertContains(Level.ERROR, "The field testProtectedChangeToStatic is now static.");
        reporter.assertContains(Level.ERROR, "The field testPublicChangeFromStatic is not static anymore.");
        reporter.assertContains(Level.ERROR, "The field testProtectedChangeFromStatic is not static anymore.");
        assertEquals(4, reporter.count(Level.ERROR));
    }

    @Test
    public void testCheckFieldChangeToTransientPublicScope() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckFieldChangeToTransient.class, "**/PublicScopeFieldTestCases.class");
        reporter.assertContains(Level.WARNING, "The field publicNotTransientToTransient is now transient.");
        reporter.assertContains(Level.WARNING, "The field protectedNotTransientToTransient is now transient.");
        reporter.assertContains(Level.WARNING, "The field privateNotTransientToTransient is now transient.");
        reporter.assertContains(Level.ERROR, "The field publicTransientToNoTransient is not transient anymore.");
        reporter.assertContains(Level.ERROR, "The field protectedTransientToNoTransient is not transient anymore.");
        reporter.assertContains(Level.ERROR, "The field privateTransientToNoTransient is not transient anymore.");
        assertEquals(3, reporter.count(Level.WARNING));
        assertEquals(3, reporter.count(Level.ERROR));
    }

    
    @Test
    public void testCheckFieldChangeToStaticPackageScope() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckFieldChangeToStatic.class, "**/PackageScopeFieldTestCases.class");
        assertEquals(0, reporter.count(Level.ERROR));
    }

    @Test
    public void testClassChangedToFinal() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(ClassChangedToFinal.class, "**/PublicClassToFinal.class");
        reporter.assertContains(Level.ERROR, "The class com/googlecode/japi/checker/tests/PublicClassToFinal has been made final, this breaks inheritance.");
        assertEquals(1, reporter.count(Level.ERROR));
    }
 
    @Test
    public void testCheckInheritanceChanges() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckInheritanceChanges.class, "**/CheckInheritanceChanges.class");
        reporter.assertContains(Level.ERROR, "extends java/util/ArrayList and not java/util/Vector anymore.");
        reporter.assertContains(Level.ERROR, "is not implementing java/io/Serializable anymore.");
        assertEquals(2, reporter.count(Level.ERROR));
    }
    
    @Test
    public void testCheckRemovedMethod() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckRemovedMethod.class, "**/CheckRemovedMethod.class");
        reporter.assertContains(Level.ERROR, "Could not find method publicMethodRemoved in newer version.");
        reporter.assertContains(Level.ERROR, "Could not find method protectedMethodRemoved in newer version.");
        assertEquals(2, reporter.count(Level.ERROR));
    }
    
    @Test
    public void testCheckMethodException() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckMethodExceptions.class, "**/CheckMethodException.class");
        reporter.assertContains(Level.ERROR, "publicAddedException is now throwing java/lang/Exception.");
        reporter.assertContains(Level.ERROR, "protectedAddedException is now throwing java/lang/Exception.");
        reporter.assertContains(Level.ERROR, "publicRemovedException is not throwing java/lang/Exception anymore.");
        reporter.assertContains(Level.ERROR, "protectedRemovedException is not throwing java/lang/Exception anymore.");
        assertEquals(4, reporter.count(Level.ERROR));
    }

    @Test
    public void testCheckMethodExceptionInheritance() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckMethodExceptions.class, "**/CheckMethodExceptionInheritance.class");
        assertEquals(0, reporter.count(Level.ERROR));
    }

    
    @Test
    public void testCheckerInnerClassRemoved() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(null, "**/InnerClassRemoved*.class");
        assertEquals(4, reporter.count(Level.ERROR));
        //reporter.assertContains(Level.ERROR, "Public class com/googlecode/japi/checker/tests/RemovedClass has been removed.");
    }

    @Test
    public void testCheckMethodChangedToFinal() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckMethodChangedToFinal.class, "**/CheckMethodAccess.class");
        reporter.assertContains(Level.ERROR, "The method publicToFinal has been made final, this now prevents overriding.");
        reporter.assertContains(Level.ERROR, "The method protectedToFinal has been made final, this now prevents overriding.");
        assertEquals(2, reporter.count(Level.ERROR));
    }

    @Test
    public void testCheckMethodChangedToStatic() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckMethodChangedToStatic.class, "**/CheckMethodAccess.class");
        reporter.assertContains(Level.ERROR, "The method publicToStatic has been made static.");
        reporter.assertContains(Level.ERROR, "The method protectedToStatic has been made static.");
        reporter.assertContains(Level.ERROR, "The method publicFromStatic is not static anymore.");
        reporter.assertContains(Level.ERROR, "The method protectedFromStatic is not static anymore");
        assertEquals(4, reporter.count(Level.ERROR));
    }

    @Test
    public void testCheckInterfaceChangedToClass() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(InterfaceChangedToClass.class, "**/InterfaceToClass.class");
        assertEquals(1, reporter.count(Level.ERROR));
        reporter.assertContains(Level.ERROR, "The class com/googlecode/japi/checker/tests/InterfaceToClass has been change into an interface.");
    }

    @Test
    public void testCheckClassBaseClassChangedBaseClassRemoved() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckSuperClass.class, "**/inheritance/removebaseclass/A.class");
        assertEquals(1, reporter.count(Level.ERROR));
        reporter.assertContains(Level.ERROR, "The class com/googlecode/japi/checker/tests/inheritance/removebaseclass/A does not inherit from com/googlecode/japi/checker/tests/inheritance/removebaseclass/B anymore.");
    }

    @Test
    public void testCheckClassBaseClassChangedBaseClassChangedWithoutBreakingTheInheritance() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckSuperClass.class, "**/inheritance/changetree/A.class");
        assertEquals(0, reporter.count(Level.ERROR));
        //reporter.assertContains(Level.ERROR, "The class com/googlecode/japi/checker/tests/inheritance/removebaseclass/A does not inherit from com/googlecode/japi/checker/tests/inheritance/removebaseclass/B anymore.");
    }

    @Test
    public void testCheckClassBaseClassChangedBaseClassWithSameClass() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckSuperClass.class, "**/inheritance/changetree/B.class");
        assertEquals(0, reporter.count(Level.ERROR));
        //reporter.assertContains(Level.ERROR, "The class com/googlecode/japi/checker/tests/inheritance/removebaseclass/A does not inherit from com/googlecode/japi/checker/tests/inheritance/removebaseclass/B anymore.");
    }

    
    @Test
    public void testCheckSerialVersionUIDFieldSameUID() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckSerialVersionUIDField.class, "**/SameUID.class");
        assertEquals(0, reporter.count(Level.ERROR));
    }

    @Test
    public void testCheckSerialVersionUIDFieldDifferentUID() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckSerialVersionUIDField.class, "**/DifferentUID.class");
        assertEquals(1, reporter.count(Level.ERROR));
        reporter.assertContains(Level.ERROR, "The value of the serialVersionUID field has changed from 0xa64662a9226655f7 to 0xa64662a9226655ad.");
    }

    @Test
    public void testCheckSerialVersionUIDFieldInvalidTypeUID() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckSerialVersionUIDField.class, "**/InvalidTypeUID.class");
        assertEquals(1, reporter.count(Level.ERROR));
        reporter.assertContains(Level.ERROR, "The type for field serialVersionUID is invalid, it must be a long.");
    }
    
    @Test
    public void testCheckSerialVersionUIDFieldInvalidTypeInNewUID() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckSerialVersionUIDField.class, "**/InvalidTypeInNewUID.class");
        assertEquals(1, reporter.count(Level.ERROR));
        reporter.assertContains(Level.ERROR, "The type for field serialVersionUID is invalid, it must be a long.");
    }

    public BasicReporter check(Class<? extends Rule> clazz, String ... includes) throws InstantiationException, IllegalAccessException, IOException {
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
        List<Report> messages = new ArrayList<Report>();
        
        @Override
        public void report(Report report) {
            System.out.println(report.level.toString() + ": " + report.source + getLine(report) + ": " + report.message);
            messages.add(report);
        }
        
        private static String getLine(Report report) {
            if (report.newItem instanceof MethodData) {
                return "(" + ((MethodData)report.newItem).getLineNumber() + ")";
            }
            return "";
        }
        
        public List<Report> getMessages() {
            return messages;
        }

        public int count(Level level) {
            int count = 0;
            for (Report message : messages) {
                if (message.level == level) {
                    count++;
                }
            }
            return count;
        }
        
        public void assertContains(Level level, String str) {
            for (Report message : messages) {
                if (message.level == level && message.message.contains(str)) {
                    return;
                }
            }
            fail("Could not find message containing: " + str);
        }

    }

}
