package graphicalsystem;

// @author Raphaël

import java.awt.geom.Rectangle2D;

public class RectangleSprite extends ShapeSprite
{
    public RectangleSprite(String name, int width, int height)
    {
        this(name, width, height, 0, 0);
    }
    
    public RectangleSprite(String name, int width, int height, int originX, int originY)
    {
        super(name, new Rectangle2D.Float(0, 0, width, height), originX, originY);
    }
}
