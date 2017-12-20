
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author Gregoire
 */
public class Libs {
   public final static File libFolder = new File("lib");
   private final static File LIB_FILE = new File("ressources\\libs.conf");
    
    
   
   
   public static ArrayList<Library> neededLibs;
   static{
     neededLibs = new ArrayList<>();
}
//       try {
//           neededLibs.add(new Library(new URL("http://apache.websitebeheerjd.nl//commons/codec/binaries/commons-codec-1.10-bin.zip"), true, "commons_lib"));
//           neededLibs.add(new Library(new URL("http://xuggle.googlecode.com/svn/trunk/repo/share/java/xuggle/xuggle-xuggler/5.4/xuggle-xuggler-5.4.jar"), false,"xuggle-xuggler-5.4.jar"));
//           neededLibs.add(new Library(new URL("http://www.slf4j.org/dist/slf4j-1.7.12.zip"), true, "sl4f"));
//           neededLibs.add(new Library(new URL("http://downloads.sourceforge.net/project/jfreechart/1.%20JFreeChart/1.0.19/jfreechart-1.0.19.zip?r=http%3A%2F%2Fsourceforge.net%2Fprojects%2Fjfreechart%2Ffiles%2F1.%2520JFreeChart%2F1.0.19%2F&ts=1434997514&use_mirror=cznic"), true, "jfreechart"));
//       } catch (MalformedURLException ex) {
//           Logger.getLogger(Libs.class.getName()).log(Level.SEVERE, null, ex);
//       }
//       
//   }
//  
//   
//    
    public static boolean checkLib(Library lib,File folder){
        if(folder.isDirectory()){
            
            for(String name : lib.getFiles()){
                boolean found=false;
                for(File f : folder.listFiles()){
                    if(!f.isDirectory() && name.equals(f.getName())){
                        found=true;
                        break;
                    }
                }
                if(!found){
                    return false;
                }
            }
        }
        else{
            return false;
        }
        return true;
        
    }
    public static File downloadFile(Library l) throws IOException{
       URL libUrl = l.getUrl();
        System.out.println("DOWNLOADING "+l.getFiles());
       
        ReadableByteChannel channel = Channels.newChannel(libUrl.openStream());
        String fName =l.isIsZipped()? ((int)(Math.random()*100000))+".zip":l.getFiles().get(0);
        File f = new File(libFolder.getPath()+File.separator+fName);
        FileOutputStream  fos = new FileOutputStream(f);
        fos.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
        
        fos.close();
        return f;
    }

   
   
   
    
    public static void main(String[] args) {
        
       getMissingLibs();
        
    }
    
    
    
    public static void getMissingLibs() {
        libFolder.mkdirs();
       try {
           parseFile();
           
           for(Library l: neededLibs){
               if(!checkLib(l, libFolder)){
                   File file= downloadFile(l);
                   if(l.isIsZipped()){
                       for(String name :l.getFiles()){
                           System.out.println("-----UNZIPING  "+name);
                           ZipUtils.unzip(name, file, libFolder);
                       }
                       file.delete();
                   }
                   
               }
           }
           
           
           
           
           
           
       } catch (IOException ex) {
           Logger.getLogger(Libs.class.getName()).log(Level.SEVERE, null, ex);
       }
        
    }
    
    public static void parseFile() throws FileNotFoundException, IOException{
            BufferedReader r = new BufferedReader(new FileReader(LIB_FILE));
            
            String line = "";
            while((line = r.readLine()) != null){
              if(line.length()>=2){
                  if(!line.substring(0, 2).equals("//")){
                      String[] lineS = line.split(" ");
                      if(lineS.length>=3){
                          Library lib = new Library();
                          lib.setUrl(new URL(lineS[0]));
                          lib.setIsZipped(Boolean.parseBoolean(lineS[1]));
                          int i =2;
                          while(i<lineS.length){
                              if(lineS[i]!=""){
                                  
                                  lib.getFiles().add(lineS[i]);
                              }
                              i++;
                          }
                          neededLibs.add(lib);
                          
                      }
                  }
              }
            }
        
        
    }
}
    
