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
    public Boolean testChangeOfTypePublic;
    protected Boolean testChangeOfTypeProtected;
    private Boolean testChangeOfTypePrivate;
    
    // Change of scope
    protected String testChangeOfScopeFromPublicToProtected;
    private String testChangeOfScopeFromPublicToPrivate;
    public String testChangeOfScopeFromProtectedToPublic;
    private String testChangeOfScopeFromProtectedToPrivate;
    protected String testChangeOfScopeFromPrivateToProtected;
    public String testChangeOfScopeFromPrivateToPublic;
    
    // Static
    public static String testPublicChangeToStatic;
    public String testPublicChangeFromStatic;
    protected static String testProtectedChangeToStatic;
    protected String testProtectedChangeFromStatic;
    private static String testPrivateChangeToStatic;
    private String testPrivateChangeFromStatic;
    
    // Final
    public final String publicFinalToNoFinal = "";
    public String publicNotfinalToFinal = "";
    protected final String protectedFinalToNoFinal = "";
    protected String protectedNotfinalToFinal = "";
    private final String privateFinalToNoFinal = "";
    private String privateNotfinalToFinal = "";

    
}
