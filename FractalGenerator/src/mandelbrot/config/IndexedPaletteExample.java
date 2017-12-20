package mandelbrot.config;

// @authors Gregoire & Raphael

import java.awt.Color;



public final class IndexedPaletteExample extends IndexedPalette
{   
    public IndexedPaletteExample(int colorsNbr)
    {
        super(colorsNbr);
    }
    
    @Override
    protected int computeColor(int i)
    {
        return Color.HSBtoRGB((i%colors.length)/(colors.length+0.0f),
                ((2*i)%colors.length)/(colors.length+0.0f),
                ((3*i)%colors.length)/(colors.length+0.0f));
    }
}
