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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;


public class DirectoryReader extends AbstractClassReader {

    private File path;
    private ClassDataLoader loader;
    
    public DirectoryReader(File path, ClassDataLoader loader) {
        this.path = path;
        this.loader = loader;
    }

    @Override
    public void read() throws IOException {
        clear();
        scanDir(this.path, null);
    }

    private void scanDir(File dir, String path) throws IOException {
        byte buffer[] = new byte[2048]; 
        if (path == null) {
            path = "";
        }
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scanDir(file, path + file.getName() + "/");
            } else if (file.getName().endsWith(".class")) {
                ClassDumper dumper = new ClassDumper(loader); 
                ByteArrayOutputStream os =  new ByteArrayOutputStream();
                InputStream is = null;
                try {
                    is = new FileInputStream(file);
                    int count = 0;
                    while ((count = is.read(buffer)) != -1) {
                        os.write(buffer, 0, count);
                    }
                    ClassReader cr = new ClassReader(os.toByteArray());
                    cr.accept(dumper, 0);
                    this.put(path + file.getName(), dumper.getClasses());
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
            }
        }
    }

}
