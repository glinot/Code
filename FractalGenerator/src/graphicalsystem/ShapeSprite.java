package graphicalsystem;

// @author Raphaël

import graphicalsystem.IntPoint;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

public abstract class ShapeSprite extends Sprite
{
    public static final Color defaultFillingColor = new Color(255, 255, 255, 0);
    
    protected Shape shape;
    protected Color fillingColor;
    
    public ShapeSprite(String name, Shape shape, int originX, int originY)
    {
        super(name, originX, originY);
        
        this.shape = shape;
        this.fillingColor = defaultFillingColor;
    }
    
    public Shape getShape()
    {
        return this.shape;
    }
    
    @Override
    public int getWidth()
    {
        return (int)this.shape.getBounds2D().getWidth();
    }
    
    @Override
    public int getHeight()
    {
        return (int)this.shape.getBounds2D().getHeight();
    }
    
    public Color getFillingColor()
    {
        return this.fillingColor;
    }
    
    public void setFillingColor(Color fillingColor)
    {
        this.fillingColor = fillingColor;
    }
    
    @Override
    public boolean isTransparent(int originRelativXPos, int originRelativYPos)
    {
        int XPosOnShape = originRelativXPos + this.originX,
            YPosOnShape = originRelativYPos + this.originY;
        
        return !this.shape.intersects(XPosOnShape, YPosOnShape, 1, 1);
    }
    
    @Override
    public void draw(Graphics g, int xPos, int yPos)
    {
        int drawXPos = xPos - this.originX,
            drawYPos = yPos - this.originY;
        
        g.setColor(this.fillingColor);
        g.translate(drawXPos, drawYPos);
        ((Graphics2D)g).fill(this.shape);
        g.translate(-drawXPos, -drawYPos);
    }
    
    @Override
    public void draw(Graphics g, IntPoint pos)
    {
        this.draw(g, pos.getAbs(), pos.getOrd());
    }
    
    @Override
    public void drawFaded(Graphics g, int xPos, int yPos, float alphaRate)
    {
        Color fadedColor = new Color(this.fillingColor.getRed(),
                                     this.fillingColor.getGreen(),
                                     this.fillingColor.getBlue(),
                                     (int)(this.fillingColor.getAlpha()*alphaRate));
        int drawXPos = xPos - this.originX,
            drawYPos = yPos - this.originY;
        
        g.setColor(fadedColor);
        g.translate(drawXPos, drawYPos);
        ((Graphics2D)g).fill(this.shape);
        g.translate(-drawXPos, -drawYPos);
    }
    
    @Override
    public void drawFaded(Graphics g, IntPoint pos, float alphaRate)
    {
        this.drawFaded(g, pos.getAbs(), pos.getOrd(), alphaRate);
    }
}
