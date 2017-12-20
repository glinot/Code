package util;

// @author Raphaël

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import mandelbrot.controller.Controller;


public final class Files
{
    public static void safelySaveScreenshot(BufferedImage img, String folderPath)
    {
        File folder = new File(folderPath);
        
        if(!folder.exists())
        {
            folder.mkdirs();
        }
        
        File file;
        int i = 0;
        
        do
        {
            file = new File(folderPath + File.separatorChar + "capture_"
                    + Strings.completeWithChar(i, 6, '0') + ".png");
            i++;
        }
        while(file.exists());

        saveBufferedImage(img, file);
    }
    
    // save a BufferedImage in the specified File
    public static void saveBufferedImage(BufferedImage img, File file)
    {
        try
        {
            ImageIO.write(img, "PNG", file);
        }
        catch(IOException ex)
        {
            Logger.getLogger(Files.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static BufferedImage loadImage(String fileBaseName)
    {
        try
        {
            return ImageIO.read(new File("images" + File.separatorChar + fileBaseName + ".png"));
        }
        catch(IOException ex)
        {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static boolean checkImageExists(BufferedImage image , File f ){ 
        if(f.isDirectory()){
            String dirTemp  =System.getProperty("java.io.tmpdir");
            String tempFileName = dirTemp+"\\tmpFract.png";
            File tempFile = new File(tempFileName);
            try {
                ImageIO.write(image, "png", tempFile);
            } catch (IOException ex) {
                Logger.getLogger(Files.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            String hash1 ="";
            try {
                hash1= getMD5(tempFile);
            } catch (IOException ex) {
                Logger.getLogger(Files.class.getName()).log(Level.SEVERE, null, ex);
            }
            for(String md5 : getAllFilesMd5(f, "png")){
                if(md5.equals(hash1))
                    
                    return true;
               
            }
            
            
            
            
        }
        return false;
    }
    private static ArrayList<String> getAllFilesMd5(File folder ,final  String ext){
        ArrayList<String> res  = new ArrayList<>();
        if(folder.isDirectory()){
            for(File f : folder.listFiles(new FileFilter() {

                @Override
                public boolean accept(File pathname) { return pathname.getName().endsWith(ext);}
            })){
                
                try {
                    res.add(getMD5(f));
                } catch (IOException ex) {
                    Logger.getLogger(Files.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
                
            }
        }
        return  res;
    }
            
    private static String getMD5(File f) throws IOException {
      // return DigestUtils.md5Hex(new FileInputStream(f));
        return "";
    }
    public static void main(String[] args) throws IOException {
        BufferedImage img = ImageIO.read(new File("D:\\25.png"));
        long n =System.nanoTime();
        
        System.out.println(checkImageExists(img, new File("D:\\"))); 
        System.out.println(""+(System.nanoTime()-n)/(Math.pow(10, 9)));
   }
}
