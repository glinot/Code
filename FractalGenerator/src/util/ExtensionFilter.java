/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author Gregoire
 */
 public class ExtensionFilter implements FileFilter{

        private String ext;

        public ExtensionFilter(String ext) {
            this.ext = ext;
        }
       

        @Override
        public boolean accept(File pathname) {
            return pathname.getName().endsWith(ext);
        
        }
        
    }