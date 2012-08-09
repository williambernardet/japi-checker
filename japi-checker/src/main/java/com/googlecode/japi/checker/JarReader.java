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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.objectweb.asm.ClassReader;


public class JarReader extends AbstractClassReader {
    private File filename;
    private ClassDataLoader loader;
    
    public JarReader(File filename, ClassDataLoader loader) {
        this.filename = filename;
        this.loader = loader;
    }
    
    @Override
    public void read() throws IOException {
        this.clear();
        FileInputStream fis = null;
        ZipInputStream zis = null;
        try {
        fis = new FileInputStream(this.filename);
        zis = new ZipInputStream(new BufferedInputStream(fis));
        ZipEntry entry = null;
        byte buffer[] = new byte[2048];
        int count = 0;
        while((entry = zis.getNextEntry()) != null) {
            if (entry.getName().endsWith(".class")) {
                ClassDumper dumper = new ClassDumper(loader); 

                ByteArrayOutputStream os =  new ByteArrayOutputStream();
                while ((count = zis.read(buffer)) != -1) {
                    os.write(buffer, 0, count);
                }
                ClassReader cr = new ClassReader(os.toByteArray());
                cr.accept(dumper, 0);

                this.put(entry.getName(), dumper.getClasses());
            }
        }
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (zis != null) {
                zis.close();
            }
        }
    }

}
