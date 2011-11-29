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

class PackageScopeFieldTestCases {
    // change of type
    public String testChangeOfTypePublic;
    protected String testChangeOfTypeProtected;
    private String testChangeOfTypePrivate;
    
    // Change of scope
    public String testChangeOfScopeFromPublicToProtected;
    public String testChangeOfScopeFromPublicToPrivate;
    protected String testChangeOfScopeFromProtectedToPublic;
    protected String testChangeOfScopeFromProtectedToPrivate;
    private String testChangeOfScopeFromPrivateToProtected;
    private String testChangeOfScopeFromPrivateToPublic;
    
    // Static
    public String testPublicChangeToStatic;
    public static String testPublicChangeFromStatic;
    protected String testProtectedChangeToStatic;
    protected static String testProtectedChangeFromStatic;
    private String testPrivateChangeToStatic;
    private static String testPrivateChangeFromStatic;
    
    // Final
    public final String publicFinalToNoFinal = "";
    public String publicNotfinalToFinal = "";
    protected final String protectedFinalToNoFinal = "";
    protected String protectedNotfinalToFinal = "";
    private final String privateFinalToNoFinal = "";
    private String privateNotfinalToFinal = "";

    
}
