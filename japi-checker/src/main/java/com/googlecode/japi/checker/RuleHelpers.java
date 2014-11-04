/*
 * Copyright 2012 William Bernardet
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

import java.util.ArrayList;
import java.util.List;

import com.googlecode.japi.checker.model.ClassData;
import com.googlecode.japi.checker.model.MethodData;

/**
 * Some common functions which ease the writing of rules. 
 *
 */
public final class RuleHelpers {
    
    private RuleHelpers() {}
    
    /**
     * 
     * For the following given inheritance tree:
     * <code>
     *    A -> B -> C -> D
     *  </code>
     *  
     *  A call to <code>isClassPartOfClassTree(loader, "B", "A");</code> will return true, as A inherits from B. 
     *  But calling <code>isClassPartOfClassTree(loader, "B", "C");</code> will return false.
     * </code>
     * 
     * @param loader the loader to use for extracting class information.
     * @param classname the class to look for in the inheritance tree.
     * @param topLevelClassname the top-level element of the tree to start
     * @return Returns true if classname is found in topLevelClassname
     *           inheritance tree (or equal to it), false otherwise.
     */
    public static boolean isClassPartOfClassTree(ClassDataLoader loader, String classname, String topLevelClassname) {
        if (topLevelClassname != null) {
            if (classname.equals(topLevelClassname)) {
                return true;
            }
            ClassData superClass = loader.fromName(topLevelClassname);
            if (superClass != null) {
                return isClassPartOfClassTree(loader, classname, superClass.getSuperName());
            }
        }
        return false;
    }

    /**
     * Return the list of all methods provided by this class including from
     * inheritance.
     * @param clazz the class to start recursing from.
     * @return All the methods implemented by the class or the interface.
     */
    public static List<MethodData> getClassMethodRecursive(ClassData clazz) {
        List<MethodData> result = new ArrayList<MethodData>();
        result.addAll(clazz.getMethods());
        ClassData superClass = clazz.getClassDataLoader().fromName(clazz.getSuperName());
        if (superClass != null) {
            for (MethodData method : getClassMethodRecursive(superClass)) {
                if (!containsSame(result, method)) {
                    result.add(method);
                }
            }
        }
        // In case of interface let's include all interface inheritance tree.
        if (clazz.isInterface()) {
            for (String ifaceName : clazz.getInterfaces()) {
                ClassData iface = clazz.getClassDataLoader().fromName(ifaceName);
                if (iface != null) {
                    for (MethodData method : getClassMethodRecursive(iface)) {
                        if (!containsSame(result, method)) {
                            result.add(method);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Check if a list already contains a method similar to the one provided.
     * @param methods
     * @param method
     * @return
     */
    private static boolean containsSame(List<MethodData> methods, MethodData method) {
        for (MethodData m : methods) {
            if (method.isSame(m)) {
                return true;
            }
        }
        return false;
    }
}
