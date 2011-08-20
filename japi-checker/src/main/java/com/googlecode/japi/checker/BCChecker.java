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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.objectweb.asm.ClassReader;

import com.googlecode.japi.checker.Reporter.Level;
import com.googlecode.japi.checker.model.ClassData;
import com.googlecode.japi.checker.utils.AntPatternMatcher;

public class BCChecker {

    private File reference;
    private File newArtifact;
    private List<AntPatternMatcher> includes = new ArrayList<AntPatternMatcher>();
    private List<AntPatternMatcher> excludes = new ArrayList<AntPatternMatcher>();
    
    public BCChecker(File reference, File newArtifact) {
        if (!reference.isDirectory() && !reference.getName().toLowerCase().endsWith(".jar")) {
            throw new IllegalArgumentException("must be a jar file");
        }
        if (!reference.isDirectory() && !newArtifact.getName().toLowerCase().endsWith(".jar")) {
            throw new IllegalArgumentException("must be a jar file");
        }
        this.reference = reference;
        this.newArtifact = newArtifact;
    }
 
    public void addInclude(String include) {
        includes.add(new AntPatternMatcher(include));
    }

    public void addExclude(String exclude) {
        excludes.add(new AntPatternMatcher(exclude));
    }
    
    public void checkBacwardCompatibility(Reporter reporter, List<Rule> rules) {
        if (rules == null) {
            rules = Collections.emptyList();
        }
        try {
            List<ClassData> referenceData = readData(reference);
            List<ClassData> newData = readData(newArtifact);
            for (ClassData clazz : referenceData) {
                boolean found = false;
                for (ClassData newClazz : newData) {
                    if (clazz.isSame(newClazz)) {
                        newClazz.checkBackwardCompatibility(reporter, clazz, rules);
                        for (Rule rule : rules) {
                            rule.checkBackwardCompatibility(reporter, clazz, newClazz);
                        }
                        found = true;
                        break;
                    }
                }
                if (!found && clazz.getVisibility() == Scope.PUBLIC) {
                    reporter.report(Level.ERROR, "Public class " + clazz.getName() + " has been removed.");
                }
            }
        } catch (IOException e) {
            reporter.report(Level.ERROR, "Error opening jar file: " + e.getMessage());
        }
    }
    
    
    private List<ClassData>readData(File file) throws IOException {
        if (file.isDirectory()) {
            return this.readDataFromDir(file, null);
        } else {
            return this.readDataFromJar(file);
        }
    }

    private List<ClassData> readDataFromDir(File dir, String path) throws IOException {
        List<ClassData> data = new ArrayList<ClassData>();
        byte buffer[] = new byte[2048]; 
        if (path == null) {
            path = "";
        }
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                data.addAll(readDataFromDir(file, path + file.getName() + "/"));
            } else if (file.getName().endsWith(".class") && shouldCheck(path + file.getName())) {
                ByteArrayOutputStream os =  new ByteArrayOutputStream();
                InputStream is = new FileInputStream(file);
                int count = 0;
                while ((count = is.read(buffer)) != -1) {
                    os.write(buffer, 0, count);
                }
                ClassDumper dumper = new ClassDumper();
                ClassReader cr = new ClassReader(os.toByteArray());
                cr.accept(dumper, 0);
                data.add(dumper.getClazz());
            }
        }
        return data;
    }
    
    private List<ClassData> readDataFromJar(File jar) throws IOException {
        List<ClassData> data = new ArrayList<ClassData>();
        FileInputStream fis = new FileInputStream(jar);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
        ZipEntry entry = null;
        byte buffer[] = new byte[2048];
        int count = 0;
        while((entry = zis.getNextEntry()) != null) {
            if (entry.getName().endsWith(".class") && shouldCheck(entry.getName())) {
                ByteArrayOutputStream os =  new ByteArrayOutputStream();
                while ((count = zis.read(buffer)) != -1) {
                    os.write(buffer, 0, count);
                }
                ClassDumper dumper = new ClassDumper();
                ClassReader cr = new ClassReader(os.toByteArray());
                cr.accept(dumper, 0);
                data.add(dumper.getClazz());
            }
        }
        return data;
    }
    
    protected boolean shouldCheck(String subpath) {
        boolean included = includes.size() == 0 ? true : false;
        for (AntPatternMatcher inc : includes) {
            if (inc.matches(subpath)) {
                included = true;
                break;
            }
        }
        for (AntPatternMatcher exc : excludes) {
            if (exc.matches(subpath)) {
                return false;
            }
        }
        return included;
    }
}