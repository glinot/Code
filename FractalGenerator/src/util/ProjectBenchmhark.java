/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Gregoire
 */

public class ProjectBenchmhark {
    public static int getNbOfLine(File f) throws FileNotFoundException, IOException{
        BufferedReader r = new BufferedReader(new FileReader(f));
        int res=0;
        while(r.readLine() != null){
            res++;
        }
        r.close();
        return res;
    }
    public static  int getNbOfLinesInFolder(File folderSrc) throws IOException{
        int n = 0;
        for(File f: folderSrc.listFiles()){
            System.out.println(""+f.getPath());
            if(f.isDirectory()){
                n+= getNbOfLinesInFolder(f);
            }
            else{
                n+=getNbOfLine(f);
            }
        }
        return n;
    }
    public static void main(String[] args) throws IOException  {
        System.out.println(""+getNbOfLinesInFolder(new File("src")));
        
    }
}
