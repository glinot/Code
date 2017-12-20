package graphicalsystem;

// @author Raphaël

import graphicalsystem.IntPoint;
import java.awt.Graphics;

public abstract class Sprite
{
    protected String name;
    protected int originX, originY;
    
    public Sprite(String name, int originX, int originY)
    {
        this.name = name;
        this.originX = originX;
        this.originY = originY;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public int getOriginX()
    {
        return this.originX;
    }
    
    public int getOriginY()
    {
        return this.originY;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setOrigin(int originX, int originY)
    {
        this.originX = originX;
        this.originY = originY;
    }
    
    public void setOriginX(int originX)
    {
        this.originX = originX;
    }
    
    public void setOriginY(int originY)
    {
        this.originY = originY;
    }
    
    public abstract int getWidth();
    public abstract int getHeight();
    public abstract boolean isTransparent(int originRelativXPos, int originRelativYPos);
    public abstract void draw(Graphics g, int xPos, int yPos);
    public abstract void draw(Graphics g, IntPoint pos);
    public abstract void drawFaded(Graphics g, int xPos, int yPos, float alphaRate);
    public abstract void drawFaded(Graphics g, IntPoint pos, float alphaRate);
}
