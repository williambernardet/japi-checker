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
package com.googlecode.japi.checker.tests;

public class CheckMethodAccess {

    public final void publicToFinal() {}
    protected final void protectedToFinal() {}
    private final void privateToFinal() {}

    public static void publicToStatic() {}
    protected static void protectedToStatic() {}
    private static void privateToStatic() {}
 
    public void publicFromStatic() {}
    protected void protectedFromStatic() {}
    private void privateFromStatic() {}

}
