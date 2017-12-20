package graphicalsystem;

// @author Raphaël

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ImageSprite extends Sprite
{
    public static final Composite DEFAULT_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
    
    private BufferedImage image;
    private int backgroundRGB, leftInset, rightInset, topInset, bottomInset;
    
    public ImageSprite(String name, BufferedImage image)
    {
        this(name, image, 0, 0);
    }
    
    public ImageSprite(String name, BufferedImage image, int originX, int originY)
    {
        super(name, originX, originY);
        
        ArrayList<Integer> edgeColors = new ArrayList();
        int imageWidth = image.getWidth(), imageHeight = image.getHeight(),
            currentColor, currentColorOccurrence,
            maxOccurrence = 0, dominantColor = 0;
        
        this.image = image;
        
        for(int i = 0; i < imageWidth; i++)
        {
            edgeColors.add(new Integer(image.getRGB(i, 0)));
            edgeColors.add(new Integer(image.getRGB(i, imageHeight-1)));
        }
        
        for(int j = 1; j < imageHeight-1; j++)
        {
            edgeColors.add(new Integer(image.getRGB(0, j)));
            edgeColors.add(new Integer(image.getRGB(imageWidth-1, j)));
        }
        
        int k;
        
        while(!edgeColors.isEmpty())
        {
            k = 0;
            currentColor = edgeColors.get(k).intValue();
            currentColorOccurrence = 1;
            edgeColors.remove(k);

            while(k < edgeColors.size())
            {
                if(edgeColors.get(k).intValue() == currentColor)
                {
                    currentColorOccurrence++;
                    edgeColors.remove(k);
                }
                else
                {
                    k++;
                }
            }

            if(currentColorOccurrence > maxOccurrence)
            {
                dominantColor = currentColor;
                maxOccurrence = currentColorOccurrence;
            }
        }

        this.backgroundRGB = dominantColor;
        
        // calcul bord gauche
        int i = 0, j;
        
        while(i < this.image.getWidth())
        {
            j = 0;
            
            while(j < this.image.getHeight())
            {
                if(this.image.getRGB(i, j) != this.backgroundRGB)
                {
                    break;
                }
                
                j++;
            }
            
            if(j < this.image.getHeight())
            {
                break;
            }
            
            i++;
        }
        
        this.leftInset = i;
        
        // calcul bord droit
        i = this.image.getWidth()-1;
        
        while(i > -1)
        {
            j = 0;
            
            while(j < this.image.getHeight())
            {
                if(this.image.getRGB(i, j) != this.backgroundRGB)
                {
                    break;
                }
                
                j++;
            }
            
            if(j < this.image.getHeight())
            {
                break;
            }
            
            i--;
        }
        
        this.rightInset = this.image.getWidth()-1-i;
        
        // calcul bord supérieur
        j = 0;
        
        while(j < this.image.getHeight())
        {
            i = 0;
            
            while(i < this.image.getWidth())
            {
                if(this.image.getRGB(i, j) != this.backgroundRGB)
                {
                    break;
                }
                
                i++;
            }
            
            if(i < this.image.getWidth())
            {
                break;
            }
            
            j++;
        }
        
        this.topInset = j;
        
        // calcul bord inférieur
        j = this.image.getHeight()-1;
        
        while(j > -1)
        {
            i = 0;
            
            while(i < this.image.getWidth())
            {
                if(this.image.getRGB(i, j) != this.backgroundRGB)
                {
                    break;
                }
                
                i++;
            }
            
            if(i < this.image.getWidth())
            {
                break;
            }
            
            j--;
        }
        
        this.bottomInset = this.image.getHeight()-1-j;
    }
    
    public BufferedImage getImage()
    {
        return this.image;
    }
    
    @Override
    public int getWidth()
    {
        return this.image.getWidth();
    }
    
    @Override
    public int getHeight()
    {
        return this.image.getHeight();
    }
    
    public int getBackgroundRGB()
    {
        return this.backgroundRGB;
    }
    
    public int getLeftInset()
    {
        return this.leftInset;
    }
    
    public int getRightInset()
    {
        return this.rightInset;
    }
    
    public int getTopInset()
    {
        return this.topInset;
    }
    
    public int getbottomInset()
    {
        return this.bottomInset;
    }
    
    public int getEffectiveWidth()
    {
        return this.image.getWidth() - this.leftInset - this.rightInset;
    }
    
    public int getEffectiveHeight()
    {
        return this.image.getHeight() - this.topInset - this.bottomInset;
    }
    
    @Override
    public boolean isTransparent(int originRelativXPos, int originRelativYPos)
    {
        int XposOnImage = originRelativXPos + this.originX,
            YposOnImage = originRelativYPos + this.originY;
        
        if(XposOnImage > -1 && XposOnImage < this.image.getWidth()
                && YposOnImage > -1 && YposOnImage < this.image.getHeight())
        {
            return this.image.getRGB(XposOnImage, YposOnImage) == this.backgroundRGB;
        }
        
        return true;
    }
    
    @Override
    public void draw(Graphics g, int xPos, int yPos)
    {
        int drawXPos = xPos - this.originX,
            drawYPos = yPos - this.originY;
        
        g.drawImage(this.image, drawXPos, drawYPos, null);
    }
    
    @Override
    public void draw(Graphics g, IntPoint pos)
    {
        this.draw(g, pos.getAbs(), pos.getOrd());
    }
    
    @Override
    public void drawFaded(Graphics g, int xPos, int yPos, float alphaRate)
    {
        Graphics2D g2D = (Graphics2D)g;
        int drawXPos = xPos - this.originX,
            drawYPos = yPos - this.originY;
        
        g2D.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, alphaRate));
        g2D.drawImage(this.image, drawXPos, drawYPos, null);
        g2D.setComposite(DEFAULT_COMPOSITE);
    }
    
    @Override
    public void drawFaded(Graphics g, IntPoint pos, float alphaRate)
    {
        this.drawFaded(g, pos.getAbs(), pos.getOrd(), alphaRate);
    }
}
