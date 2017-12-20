package mandelbrot.config;

// @authors Gregoire & Raphaël

import java.awt.Color;
import util.Strings;


public abstract class IndexedPalette
{
    public static final Color ERROR_COLOR = Color.RED;
    
    protected int[] colors;
    
    public IndexedPalette(int colorsNbr)
    {
        updateColorsNbr(colorsNbr);
    }
    
    public final void updateColorsNbr(int colorsNbr)
    {
        colors = new int[colorsNbr];
        
        for(int i = 0; i < colorsNbr; i++)
        {
            colors[i] = computeColor(i);
        }
    }
    
    protected abstract int computeColor(int i);
    
    public static int rgbToInt(int r, int g, int b)
    {
        return new Color(r, g, b).getRGB();
    }
    
    public int getColor(int index)
    {
        if(index < 0 || index >= colors.length)
        {
            return ERROR_COLOR.getRGB();
        }
        else
        {
            return colors[index];
        }
    }
    
    public int[] getColors()
    {
        return colors;
    }
    
    public void setColors(int[] colors)
    {
        this.colors = colors;
    }
    
    @Override
    public String toString()
    {
        String result = "IndexedPalette{\n"
                + Strings.completeWithChar("i", Strings.integerLength(colors.length-1), ' ')
                + " -> Color{   R,   G,   B}";
        
        for(int i = 0; i < colors.length; i++)
        {
            Color c = new Color(colors[i]);
            result += "\n" + Strings.completeWithChar(i, Strings.integerLength(colors.length-1), ' ')
                    + " -> Color{ "+ Strings.completeWithChar(c.getRed(), 3, ' ')
                    + ", " + Strings.completeWithChar(c.getGreen(), 3, ' ')
                    + ", " + Strings.completeWithChar(c.getBlue(), 3, ' ') + '}';
        }
        
        return result + '}';
    }
    
    public static void main(String[] args)
    {
        IndexedPalette IP = new IndexedPaletteExample(20);
        System.out.println(IP);
    }
}
