package graphicalsystem;

// @author Raphaël

import geometry.Vector2D;
import static util.MoreGenericity.checkNullArg;
import util.Reflection;


public class IntPoint
{
    private int abs, ord;
    
    public IntPoint()
    {
        abs = 0;
        ord = 0;
    }
    
    public IntPoint(int abs, int ord)
    {
        this.abs = abs;
        this.ord = ord;
    }
    
    public int getAbs()
    {
        return abs;
    }
    
    public int getOrd()
    {
        return ord;
    }
    
    public void setAbs(int abs)
    {
        this.abs = abs;
    }
    
    public void setOrd(int ord)
    {
        this.ord = ord;
    }
    
    public void addDX(int DX)
    {
        abs += DX;
    }
    
    public void addDY(int DY)
    {
        ord += DY;
    }
    
    public void add(int DX, int DY)
    {
        abs += DX;
        ord += DY;
    }
    
    public void add(IntPoint point)
    {
        checkNullArg(point, "point");
        add(point.abs, point.ord);
    }
    
    public void setCoordinates(int abs, int ord)
    {
        this.abs = abs;
        this.ord = ord;
    }
    
    public Vector2D toVector2D()
    {
        return new Vector2D(abs, ord);
    }
    
    @Override
    public String toString()
    {
        return Reflection.describeAttributes(this, false);
    }
    
    @Override
    public IntPoint clone() throws CloneNotSupportedException
    {
        super.clone();
        return new IntPoint(abs, ord);
    }
}
