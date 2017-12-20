package graphicalsystem;

// @author Raphaël

import java.awt.geom.Ellipse2D;

public class EllipseSprite extends ShapeSprite
{
    public EllipseSprite(String name, int width, int height)
    {
        this(name, width, height, 0, 0);
    }
    
    public EllipseSprite(String name, int width, int height, int originX, int originY)
    {
        super(name, new Ellipse2D.Float(0, 0, width, height), originX, originY);
    }
}
