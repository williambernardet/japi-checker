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

import com.googlecode.japi.checker.Severity;
import com.googlecode.japi.checker.rules.CheckChangeOfScope;
import com.googlecode.japi.checker.rules.CheckFieldChangeOfType;
import com.googlecode.japi.checker.rules.CheckFieldChangeToStatic;
import com.googlecode.japi.checker.rules.CheckFieldChangeToTransient;
import com.googlecode.japi.checker.rules.CheckInheritanceChanges;
import com.googlecode.japi.checker.rules.CheckMethodChangedToFinal;
import com.googlecode.japi.checker.rules.CheckMethodChangedToStatic;
import com.googlecode.japi.checker.rules.CheckMethodExceptions;
import com.googlecode.japi.checker.rules.CheckMethodVariableArity;
import com.googlecode.japi.checker.rules.CheckRemovedMethod;
import com.googlecode.japi.checker.rules.CheckSerialVersionUIDField;
import com.googlecode.japi.checker.rules.CheckSuperClass;
import com.googlecode.japi.checker.rules.ClassChangedToAbstract;
import com.googlecode.japi.checker.rules.ClassChangedToFinal;
import com.googlecode.japi.checker.rules.ClassChangedToInterface;
import com.googlecode.japi.checker.rules.InterfaceChangedToClass;

public class TestBCChecker extends AbstractBCCheckerUnitTest {
    
    @Test
    public void testBCCheckerInclude() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(null, "**/Nothing*.class");
        assertEquals(2, reporter.getMessages().size()); // 2 info message about reading jar files.
        assertEquals(0, reporter.count(Severity.ERROR));
        assertEquals(0, reporter.count(Severity.WARNING));
    }

    
    @Test
    public void testCheckerClassRemoved() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(null, "**/RemovedClass.class");
        assertEquals(1, reporter.count(Severity.ERROR));
        reporter.assertContains(Severity.ERROR, "Public class com/googlecode/japi/checker/tests/RemovedClass has been removed.");
    }

    @Test
    public void testClassToInterface() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(ClassChangedToInterface.class, "**/ClassToInterface.class");
        assertEquals(1, reporter.count(Severity.ERROR));
        reporter.assertContains(Severity.ERROR, "The interface com/googlecode/japi/checker/tests/ClassToInterface has been changed into an class.");
    }

    @Test
    public void testClassToAbstract() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(ClassChangedToAbstract.class, "**/ClassToAbstract.class");
        reporter.assertContains(Severity.ERROR, "The class com/googlecode/japi/checker/tests/ClassToAbstract has been made abstract.");
    }

    @Test
    public void testCheckChangeOfScope() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckChangeOfScope.class, "**/PublicClassToProtected.class");
        reporter.assertContains(Severity.ERROR, "The visibility of the <init> method has been changed from PUBLIC to NO_SCOPE");
        reporter.assertContains(Severity.ERROR, "The visibility of the com/googlecode/japi/checker/tests/PublicClassToProtected class has been changed from PUBLIC to NO_SCOPE");
        assertEquals(2, reporter.count(Severity.ERROR));
    }

    @Test
    public void testCheckChangeOfScopeForFieldPublicScope() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckChangeOfScope.class, "**/PublicScopeFieldTestCases.class");
        reporter.assertContains(Severity.ERROR, "The visibility of the testChangeOfScopeFromPublicToProtected field has been changed from PUBLIC to PROTECTED");
        reporter.assertContains(Severity.ERROR, "The visibility of the testChangeOfScopeFromPublicToPrivate field has been changed from PUBLIC to PRIVATE");
        reporter.assertContains(Severity.ERROR, "The visibility of the testChangeOfScopeFromProtectedToPrivate field has been changed from PROTECTED to PRIVATE");
        reporter.assertContains(Severity.WARNING, "The visibility of the testChangeOfScopeFromProtectedToPublic field has been changed from PROTECTED to PUBLIC");
        reporter.assertContains(Severity.WARNING, "The visibility of the testChangeOfScopeFromPrivateToPublic field has been changed from PRIVATE to PUBLIC");
        reporter.assertContains(Severity.WARNING, "The visibility of the testChangeOfScopeFromPrivateToProtected field has been changed from PRIVATE to PROTECTED");
        assertEquals(3, reporter.count(Severity.ERROR));
        assertEquals(3, reporter.count(Severity.WARNING));
    }
    
    @Test
    public void testCheckChangeOfScopeForFieldPackageScope() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckChangeOfScope.class, "**/PackageScopeFieldTestCases.class");
        assertEquals(0, reporter.count(Severity.ERROR));
        assertEquals(0, reporter.count(Severity.WARNING));
    }

    @Test
    public void testCheckFieldChangeOfTypePublicClass() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckFieldChangeOfType.class, "**/PublicScopeFieldTestCases.class");
        reporter.assertContains(Severity.ERROR, "field testChangeOfTypePublic has been modified from Ljava/lang/String; to Ljava/lang/Boolean;");
        reporter.assertContains(Severity.ERROR, "field testChangeOfTypeProtected has been modified from Ljava/lang/String; to Ljava/lang/Boolean;");
        assertEquals(2, reporter.count(Severity.ERROR));
    }

    @Test
    public void testCheckFieldChangeOfTypePackageClass() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckFieldChangeOfType.class, "**/PackageScopeFieldTestCases.class");
        assertEquals(0, reporter.count(Severity.ERROR));
    }

    @Test
    public void testCheckFieldChangeToStaticPublicScope() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckFieldChangeToStatic.class, "**/PublicScopeFieldTestCases.class");
        reporter.assertContains(Severity.ERROR, "The field testPublicChangeToStatic(Ljava/lang/String;) is now static.");
        reporter.assertContains(Severity.ERROR, "The field testProtectedChangeToStatic(Ljava/lang/String;) is now static.");
        reporter.assertContains(Severity.ERROR, "The field testPublicChangeFromStatic(Ljava/lang/String;) is not static anymore.");
        reporter.assertContains(Severity.ERROR, "The field testProtectedChangeFromStatic(Ljava/lang/String;) is not static anymore.");
        assertEquals(4, reporter.count(Severity.ERROR));
    }

    @Test
    public void testCheckFieldChangeToTransientPublicScope() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckFieldChangeToTransient.class, "**/PublicScopeFieldTestCases.class");
        reporter.assertContains(Severity.WARNING, "The field publicNotTransientToTransient(Ljava/lang/String;) is now transient.");
        reporter.assertContains(Severity.WARNING, "The field protectedNotTransientToTransient(Ljava/lang/String;) is now transient.");
        reporter.assertContains(Severity.WARNING, "The field privateNotTransientToTransient(Ljava/lang/String;) is now transient.");
        reporter.assertContains(Severity.ERROR, "The field publicTransientToNoTransient(Ljava/lang/String;) is not transient anymore.");
        reporter.assertContains(Severity.ERROR, "The field protectedTransientToNoTransient(Ljava/lang/String;) is not transient anymore.");
        reporter.assertContains(Severity.ERROR, "The field privateTransientToNoTransient(Ljava/lang/String;) is not transient anymore.");
        assertEquals(3, reporter.count(Severity.WARNING));
        assertEquals(3, reporter.count(Severity.ERROR));
    }

    
    @Test
    public void testCheckFieldChangeToStaticPackageScope() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckFieldChangeToStatic.class, "**/PackageScopeFieldTestCases.class");
        assertEquals(0, reporter.count(Severity.ERROR));
    }

    @Test
    public void testClassChangedToFinal() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(ClassChangedToFinal.class, "**/PublicClassToFinal.class");
        reporter.assertContains(Severity.ERROR, "The class com/googlecode/japi/checker/tests/PublicClassToFinal has been made final, this breaks inheritance.");
        assertEquals(1, reporter.count(Severity.ERROR));
    }
 
    @Test
    public void testCheckInheritanceChanges() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckInheritanceChanges.class, "**/CheckInheritanceChanges.class");
        reporter.assertContains(Severity.ERROR, "extends java/util/ArrayList and not java/util/Vector anymore.");
        reporter.assertContains(Severity.ERROR, "is not implementing java/io/Serializable anymore.");
        assertEquals(2, reporter.count(Severity.ERROR));
    }
    
    @Test
    public void testCheckRemovedMethod() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckRemovedMethod.class, "**/CheckRemovedMethod.class");
        reporter.assertContains(Severity.ERROR, "Could not find method publicMethodRemoved(()V) in newer version.");
        reporter.assertContains(Severity.ERROR, "Could not find method protectedMethodRemoved(()V) in newer version.");
        assertEquals(2, reporter.count(Severity.ERROR));
    }
    
    @Test
    public void testCheckMethodException() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckMethodExceptions.class, "**/CheckMethodException.class");
        reporter.assertContains(Severity.ERROR, "method publicAddedException(()V) is now throwing java/lang/Exception.");
        reporter.assertContains(Severity.ERROR, "method protectedAddedException(()V) is now throwing java/lang/Exception.");
        reporter.assertContains(Severity.ERROR, "method publicRemovedException(()V) is not throwing java/lang/Exception anymore.");
        reporter.assertContains(Severity.ERROR, "method protectedRemovedException(()V) is not throwing java/lang/Exception anymore.");
        assertEquals(4, reporter.count(Severity.ERROR));
    }

    @Test
    public void testCheckMethodExceptionInheritance() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckMethodExceptions.class, "**/CheckMethodExceptionInheritance.class");
        assertEquals(0, reporter.count(Severity.ERROR));
    }

    
    @Test
    public void testCheckerInnerClassRemoved() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(null, "**/InnerClassRemoved*.class");
        assertEquals(4, reporter.count(Severity.ERROR));
        //reporter.assertContains(Level.ERROR, "Public class com/googlecode/japi/checker/tests/RemovedClass has been removed.");
    }

    @Test
    public void testCheckMethodChangedToFinal() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckMethodChangedToFinal.class, "**/CheckMethodAccess.class");
        reporter.assertContains(Severity.ERROR, "The method publicToFinal(()V) has been made final, this now prevents overriding.");
        reporter.assertContains(Severity.ERROR, "The method protectedToFinal(()V) has been made final, this now prevents overriding.");
        assertEquals(2, reporter.count(Severity.ERROR));
    }

    @Test
    public void testCheckMethodChangedToStatic() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckMethodChangedToStatic.class, "**/CheckMethodAccess.class");
        reporter.assertContains(Severity.ERROR, "The method publicToStatic(()V) has been made static.");
        reporter.assertContains(Severity.ERROR, "The method protectedToStatic(()V) has been made static.");
        reporter.assertContains(Severity.ERROR, "The method publicFromStatic(()V) is not static anymore.");
        reporter.assertContains(Severity.ERROR, "The method protectedFromStatic(()V) is not static anymore");
        assertEquals(4, reporter.count(Severity.ERROR));
    }

    @Test
    public void testCheckInterfaceChangedToClass() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(InterfaceChangedToClass.class, "**/InterfaceToClass.class");
        assertEquals(1, reporter.count(Severity.ERROR));
        reporter.assertContains(Severity.ERROR, "The class com/googlecode/japi/checker/tests/InterfaceToClass has been change into an interface.");
    }

    @Test
    public void testCheckClassBaseClassChangedBaseClassRemoved() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckSuperClass.class, "**/inheritance/removebaseclass/A.class");
        assertEquals(1, reporter.count(Severity.ERROR));
        reporter.assertContains(Severity.ERROR, "The class com/googlecode/japi/checker/tests/inheritance/removebaseclass/A does not inherit from com/googlecode/japi/checker/tests/inheritance/removebaseclass/B anymore.");
    }

    @Test
    public void testCheckClassBaseClassChangedBaseClassChangedWithoutBreakingTheInheritance() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckSuperClass.class, "**/inheritance/changetree/A.class");
        assertEquals(0, reporter.count(Severity.ERROR));
        //reporter.assertContains(Level.ERROR, "The class com/googlecode/japi/checker/tests/inheritance/removebaseclass/A does not inherit from com/googlecode/japi/checker/tests/inheritance/removebaseclass/B anymore.");
    }

    @Test
    public void testCheckClassBaseClassChangedBaseClassWithSameClass() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckSuperClass.class, "**/inheritance/changetree/B.class");
        assertEquals(0, reporter.count(Severity.ERROR));
        //reporter.assertContains(Level.ERROR, "The class com/googlecode/japi/checker/tests/inheritance/removebaseclass/A does not inherit from com/googlecode/japi/checker/tests/inheritance/removebaseclass/B anymore.");
    }

    
    @Test
    public void testCheckSerialVersionUIDFieldSameUID() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckSerialVersionUIDField.class, "**/SameUID.class");
        assertEquals(0, reporter.count(Severity.ERROR));
    }

    @Test
    public void testCheckSerialVersionUIDFieldDifferentUID() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckSerialVersionUIDField.class, "**/DifferentUID.class");
        assertEquals(1, reporter.count(Severity.ERROR));
        reporter.assertContains(Severity.ERROR, "The value of the serialVersionUID field has changed from 0xa64662a9226655f7 to 0xa64662a9226655ad.");
    }

    @Test
    public void testCheckSerialVersionUIDFieldInvalidTypeUID() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckSerialVersionUIDField.class, "**/InvalidTypeUID.class");
        assertEquals(1, reporter.count(Severity.ERROR));
        reporter.assertContains(Severity.ERROR, "The type for field serialVersionUID is invalid, it must be a long.");
    }
    
    @Test
    public void testCheckSerialVersionUIDFieldInvalidTypeInNewUID() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckSerialVersionUIDField.class, "**/InvalidTypeInNewUID.class");
        assertEquals(1, reporter.count(Severity.ERROR));
        reporter.assertContains(Severity.ERROR, "The type for field serialVersionUID is invalid, it must be a long.");
    }
    

    @Test
    public void testArityChange() throws InstantiationException, IllegalAccessException, IOException {
        BasicReporter reporter = check(CheckMethodVariableArity.class, "**/CheckMethodVariableArity.class");
        assertEquals(2, reporter.count(Severity.ERROR));
        reporter.assertContains(Severity.ERROR, "The parameter of the publicArityToArray has been changed from variable arity to array");
        reporter.assertContains(Severity.ERROR, "The parameter of the protectedArityToArray has been changed from variable arity to array");
    }
}
