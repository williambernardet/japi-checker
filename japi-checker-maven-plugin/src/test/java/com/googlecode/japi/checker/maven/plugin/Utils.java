package com.googlecode.japi.checker.maven.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipOutputStream;

import org.codehaus.plexus.util.IOUtil;


class Utils {
    
    private Utils() {}
    
    
    public static void createJarFromDirectory(File filename, File directory) throws IOException {
        JarOutputStream jar = new JarOutputStream(new FileOutputStream(filename));
        try {
            zipDir(jar, directory, null);
        } finally {
            jar.close();
        }
    }

    private static void zipDir(ZipOutputStream zip, File directory, String subpath) throws IOException {
        if (subpath == null) {
            subpath = ""; 
        }
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                zipDir(zip, file, subpath + subpath + file.getName() + "/");
            } else {
                JarEntry entry = new JarEntry(subpath + file.getName());
                entry.setTime(file.lastModified());
                zip.putNextEntry(entry);
                IOUtil.copy(new FileInputStream(file), zip, 2048);
            }
        }
    }
    
}
