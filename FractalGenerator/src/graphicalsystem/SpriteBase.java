package graphicalsystem;

// @author Raphaël

import graphicalsystem.Sprite;
import graphicalsystem.ImageSprite;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class SpriteBase
{
    public static final ArrayList<ImageSprite> PINS, SQUARES;
    public static final ImageSprite 
            DARK_BLUE_PIN, DARK_GREEN_PIN, FUSHIA_PIN, GRAPE_PIN, GREY_PIN,
            LIGHT_BLUE_PIN, LIGHT_GREEN_PIN, ORANGE_PIN, RED_PIN, YELLOW_PIN,
            
            DARK_BLUE_SQUARE, DARK_GREEN_SQUARE, FUSHIA_SQUARE, GRAPE_SQUARE,
            GREY_SQUARE, LIGHT_BLUE_SQUARE, LIGHT_GREEN_SQUARE, ORANGE_SQUARE,
            RED_SQUARE, YELLOW_SQUARE;
    public static final String IMAGE_FOLDER = "images", DEFAULT_EXTENSION = "png";
    public static int spritesAdded = 0;
    
    static
    {        
        DARK_BLUE_PIN = createImageSprite("dark_blue_pin");
        DARK_GREEN_PIN = createImageSprite("dark_green_pin");
        FUSHIA_PIN = createImageSprite("fushia_pin");
        GRAPE_PIN = createImageSprite("grape_pin");
        GREY_PIN = createImageSprite("grey_pin");
        LIGHT_BLUE_PIN = createImageSprite("light_blue_pin");
        LIGHT_GREEN_PIN = createImageSprite("light_green_pin");
        ORANGE_PIN = createImageSprite("orange_pin");
        RED_PIN = createImageSprite("red_pin");
        YELLOW_PIN = createImageSprite("yellow_pin");

        DARK_BLUE_SQUARE = createImageSprite("dark_blue_square");
        DARK_GREEN_SQUARE = createImageSprite("dark_green_square");
        FUSHIA_SQUARE = createImageSprite("fushia_square");
        GRAPE_SQUARE = createImageSprite("grape_square");
        GREY_SQUARE = createImageSprite("grey_square");
        LIGHT_BLUE_SQUARE = createImageSprite("light_blue_square");
        LIGHT_GREEN_SQUARE = createImageSprite("light_green_square");
        ORANGE_SQUARE = createImageSprite("orange_square");
        RED_SQUARE = createImageSprite("red_square");
        YELLOW_SQUARE = createImageSprite("yellow_square");
        
        PINS = createImageSpriteRepertory(10);
        setOrigin(PINS, PINS.get(0).getWidth()/2, PINS.get(0).getHeight());
        SQUARES = createImageSpriteRepertory(10);
        setOrigin(SQUARES, SQUARES.get(0).getWidth()/2, SQUARES.get(0).getHeight()/2);
    }
    
    public static ImageSprite createImageSprite(String fileBaseName)
    {
        return new ImageSprite(fileBaseName, loadImage(fileBaseName));
    }
    
    public static BufferedImage loadImage(String fileBaseName)
    {
        try
        {
            return ImageIO.read(new File(IMAGE_FOLDER + "/" + fileBaseName + "." + DEFAULT_EXTENSION));
        }
        catch(IOException ex)
        {
            Logger.getLogger(SpriteBase.class.getName()).log(Level.SEVERE, null, ex);
            
            return null;
        }
    }
    
    public static Sprite get(String spriteName)
    {
        Field[] fields = SpriteBase.class.getDeclaredFields();
        
        for(Field field : fields)
        {
            if(field.getGenericType() == Sprite.class)
            {
                Sprite sprite = (Sprite)field.getGenericType();
                
                if(sprite.getName().equals(spriteName))
                {
                    return sprite;
                }
            }
        }
        
        return null;
    }
    
    public static ArrayList<ImageSprite> createImageSpriteRepertory(int numberToAdd)
    {
        Field[] fields = SpriteBase.class.getDeclaredFields();
        ArrayList<ImageSprite> imgSprites = new ArrayList();
        int spriteIndex = 0, initialSpriteNumber = spritesAdded;
        
        for(Field field : fields)
        {
            if(field.getGenericType() == ImageSprite.class)
            {
                if(spriteIndex >= initialSpriteNumber)
                {
                    try
                    {
                        imgSprites.add((ImageSprite)field.get(null));
                        spritesAdded++;
                        
                        if(imgSprites.size() == numberToAdd)
                        {
                            break;
                        }
                    }
                    catch(IllegalArgumentException | IllegalAccessException ex)
                    {
                        Logger.getLogger(SpriteBase.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                spriteIndex++;
            }
        }
        
        return imgSprites;
    }
    
    public static void setOrigin(ArrayList<ImageSprite> spriteList, int originX, int originY)
    {
        for(ImageSprite imgSprite : spriteList)
        {
            imgSprite.setOrigin(originX, originY);
        }
    }
}
