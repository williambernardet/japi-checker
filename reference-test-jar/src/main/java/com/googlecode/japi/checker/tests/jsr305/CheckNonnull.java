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
package com.googlecode.japi.checker.tests.jsr305;

import javax.annotation.Nonnull;

public class CheckNonnull {

    @Nonnull
    public Object getNonnullToNothingPublicObject() {
        return null;
    }
    
    @Nonnull
    protected Object getNonnullToNothingProtectedObject() {
        return null;
    }

    @Nonnull
    private Object getNonnullToNothingPrivateObject() {
        return null;
    }

    @Nonnull
    public Object getNonnullToNullablePublicObject() {
        return null;
    }
    
    @Nonnull
    protected Object getNonnullToNullableProtectedObject() {
        return null;
    }

    @Nonnull
    private Object getNonnullToNullablePrivateObject() {
        return null;
    }
}
