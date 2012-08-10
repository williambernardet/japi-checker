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
package com.googlecode.japi.checker.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class will convert a Ant pattern (containing **) into a regular expression.
 * It allows to run each matching independently.
 * 
 * @author william.bernardet@gmail.com
 *
 */
public class AntPatternMatcher {

    private static Map<String, AntPatternMatcher> matchers = new HashMap<String, AntPatternMatcher>();
    private boolean casesensitive = true;
    private String expression;
    private Pattern pattern;

    public AntPatternMatcher(String expression) {
        this.setExpression(expression);
    }
    
    public AntPatternMatcher(String expression, boolean casesensitive) {
        this.casesensitive = casesensitive;
        this.setExpression(expression);
    }


    public String getExpression() {
        return expression;
    }

    static public boolean matches(String expression, String path, boolean casesensitive) {
        if (matchers.containsKey(getKey(casesensitive, expression))) {
            return matchers.get(getKey(casesensitive, expression)).matches(path);
        } else {
            AntPatternMatcher matcher = new AntPatternMatcher(expression, casesensitive);
            matchers.put(matcher.toString(), matcher);
            return matcher.matches(path);
        }
    }
    
    static public boolean matches(String expression, String path) {
        return matches(expression, path, true);
    }

    /**
     * 
     * @param expression
     */
    public void setExpression(String expression) {
        this.expression = expression;
        int flags = 0;
        if (!casesensitive) {
            flags = Pattern.CASE_INSENSITIVE;
        }
        this.pattern = Pattern.compile(AntPatternMatcher.convertToRegexp(expression), flags);
    }


    /**
     * Get the expression as pattern.   
     * @return
     */
    public Pattern getPattern() {
        return pattern;
    }


    /**
     * Matches a path to the expression.
     * @param path the path to match.
     * @return true if the path matches the expression.
     */
    public boolean matches(String path) {
        return matches(new File(path));
    }

    /**
     * Matches a path to the expression.
     * @param path the path to match.
     * @return true if the path matches the expression.
     */
    public boolean matches(File path) {
        return pattern.matcher(normalizePath(path.getPath())).matches();
    }
    
    /**
     * Convert an Ant Pattern into a regular expression.
     * It supports the following patterns:
     * '**': matches zero or several subdirs.
     * '*': matches zero or several characters.
     * '?': matches one character.  
     * @param expression
     * @return a string representing the expression as a regular expression.
     */
    static public String convertToRegexp(String expression) {
        int i = 0;
        int n = expression.length();
        String result = "";
        expression = normalizePath(expression);
        while (i < n) {
            char c = expression.charAt(i++);
            if (c == '*') {
                // identifying a **
                if (i < expression.length() && expression.charAt(i) == '*') {
                    result =
                            result + "(?:(?:^|"
                                    + File.separator.replace("\\", "\\\\")
                                    + ")[^"
                                    + File.separator.replace("\\", "\\\\")
                                    + "]+)*(?:^|"
                                    + File.separator.replace("\\", "\\\\")
                                    + "|$)";
                    i++;
                    // skipping next \ or /
                    if ((i < expression.length())
                            && (expression.charAt(i) == File.separatorChar)) {
                        i++;
                    }
                } else {
                    result =
                            result + "[^"
                                    + File.separator.replace("\\", "\\\\")
                                    + "]*";
                }
            } else if (c == '?') {
                result =
                        result + "[^" + File.separator.replace("\\", "\\\\")
                                + "]";
            } else if (c == '[') {
                int j = i;
                if (j < n && expression.charAt(j) == '!') {
                    j++;
                }
                if (j < n && expression.charAt(j) == ']') {
                    j++;
                }
                while (j < n && expression.charAt(j) != ']') {
                    j++;
                }
                if (j >= n) {
                    result = result + "\\[";
                } else {
                    String stuff =
                            expression.substring(i, j).replace("\\", "\\\\");
                    i = j + 1;
                    if (stuff.charAt(0) == '!') {
                        stuff = "^" + stuff.substring(1);
                    } else if (stuff.charAt(0) == '^') {
                        stuff = "\\" + stuff;
                    }
                    result = result + "[" + stuff + "]";
                }
            } else {
                if (c == File.separatorChar && i + 2 <= expression.length()
                        && expression.charAt(i) == '*'
                        && expression.charAt(i + 1) == '*') {
                    // pass
                } else {
                    result = result + Pattern.quote("" + c);
                }
            }
        }
        return result + "$";
    }
    
    static private String getKey(boolean casesensitive, String expression) {
        return "<" + casesensitive + "," + expression + ">";        
    }
    
    public String toString() {
        return getKey(casesensitive, expression);
    }

    private static String normalizePath(String path) {
        if ('/' == File.separatorChar) {
            return path.replace('\\', File.separatorChar);
        } else {
            return path.replace('/', File.separatorChar);
        }
    }
}
