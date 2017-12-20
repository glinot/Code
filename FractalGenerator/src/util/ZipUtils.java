/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author Gregoire
 */
public class ZipUtils {

    public static void unzip(File zipFile, File folder) throws IOException {
        if (!folder.exists()) {
            folder.mkdir();
        }
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));

        ZipEntry e;
        while ((e = zis.getNextEntry()) != null) {

            File toWrite = new File(folder.getPath() + File.separator + e.getName());
            String name = e.getName().replace(File.separator, "/");

            if (e.isDirectory()) {
                toWrite.mkdirs();
            } else {
                if (name.contains("/")) {
                    String[] path = name.split("/");
                    String dir = "";
                    for (int i = 0; i < path.length - 1; i++) {
                        dir += path[i] + File.separator;
                    }
                    File dirF = new File(folder.getPath() + File.separator + dir);
                    dirF.mkdirs();

                }

                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(toWrite));
                byte[] in = new byte[4096];
                int read = 0;
                while ((read = zis.read(in)) != -1) {
                    bos.write(in, 0, read);
                }
                bos.close();
            }

            zis.closeEntry();
        }

        zis.close();
    }

    public static void unzip(String fileName, File zip, File folder) throws FileNotFoundException, IOException {
        folder.mkdirs();
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zip));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if (entry.getName().contains(fileName)) {
                File f = new File(folder.getPath() + File.separator + fileName);
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
                byte[] in = new byte[4096];
                int read = 0;
                while ((read = zis.read(in)) != -1) {
                    bos.write(in, 0, read);
                }
                bos.close();
            }
            zis.closeEntry();
        }
        zis.close();

    }

    public static void main(String[] args) throws IOException {

    }

}