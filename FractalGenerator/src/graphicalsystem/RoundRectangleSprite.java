package graphicalsystem;

// @author Raphaël

import java.awt.geom.RoundRectangle2D;

public class RoundRectangleSprite extends ShapeSprite
{
    public RoundRectangleSprite(String name, int width, int height, int cornerHorRadius, int cornerVertRadius)
    {
        this(name, width, height, cornerHorRadius, cornerVertRadius, 0, 0);
    }
    
    public RoundRectangleSprite(String name, int width, int height, int cornerHorRadius, int cornerVertRadius, int originX, int originY)
    {
        super(name, new RoundRectangle2D.Float(0, 0, width, height, cornerHorRadius, cornerVertRadius), originX, originY);
    }
}
