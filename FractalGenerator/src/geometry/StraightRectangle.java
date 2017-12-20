package geometry;

// @author Raphaël

import static util.MoreGenericity.checkNegArg;
import static util.MoreGenericity.checkNullArg;
import util.Reflection;
import util.curves.IntFrame;


public final class StraightRectangle extends Shape2D
{   
    private double xMin, xMax, yMin, yMax;
    
    public StraightRectangle(double x1, double y1, double x2, double y2)
    {
        xMin = Math.min(x1, x2);
        xMax = Math.max(x1, x2);
        yMin = Math.min(y1, y2);
        yMax = Math.max(y1, y2);
    }
    
    public StraightRectangle(Vector2D center, double width, double height)
    {
        checkNullArg(center, "center");
        checkNegArg(width, "width");
        checkNegArg(height, "height");
        
        xMin = center.getX()-width/2;
        xMax = center.getX()+width/2;
        yMin = center.getY()-height/2;
        yMax = center.getY()+height/2;
    }
    
    public StraightRectangle(Vector2D p1, Vector2D p2)
    {
        checkNullArg(p1, "p1");
        checkNullArg(p2, "p2");
        
        xMin = Math.min(p1.getX(), p2.getX());
        xMax = Math.max(p1.getX(), p2.getX());
        yMin = Math.min(p1.getY(), p2.getY());
        yMax = Math.max(p1.getY(), p2.getY());
    }
    
    public StraightRectangle() 
    {
        xMin = 0;
        xMax = 0;
        yMin = 0;
        yMax = 0;
    }
    
    @Override
    public double getxMin()
    {
        return xMin;
    }
    
    public void setxMin(double xMin)
    {
        if(xMin > xMax)
        {
            throw new IllegalArgumentException("xMin can't be superior to xMax.");
        }
        else
        {
            this.xMin = xMin;
        }
    }
    
    @Override
    public double getxMax()
    {
        return xMax;
    }
    
    public void setxMax(double xMax)
    {
        if(xMax < xMin)
        {
            throw new IllegalArgumentException("xMax can't be inferior to xMin.");
        }
        else
        {
            this.xMax = xMax;
        }
    }
    
    @Override
    public double getyMin()
    {
        return yMin;
    }
    
    public void setyMin(double yMin)
    {
        if(yMin > yMax)
        {
            throw new IllegalArgumentException("yMin can't be superior to yMax.");
        }
        else
        {
            this.yMin = yMin;
        }
    }
    
    @Override
    public double getyMax()
    {
        return yMax;
    }
    
    public void setyMax(double yMax)
    {
        if(yMax < yMin)
        {
            throw new IllegalArgumentException("yMax can't be inferior to yMin.");
        }
        else
        {
            this.yMax = yMax;
        }
    }
    
    @Override
    public String toString()
    {
        return Reflection.describeAttributes(this, false);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        return Reflection.checkAttributesEquality(this, obj, false);
    }
    
    @Override
    public StraightRectangle clone()
    {
        return new StraightRectangle(xMin, yMin, xMax, yMax);
    }
    
    @Override
    public StraightRectangle getTranslatedInstance(double Dx, double Dy)
    {
        return new StraightRectangle(xMin+Dx, yMin+Dy, xMax+Dx, yMax+Dy);
    }
    
    @Override
    public Shape2D getRelativelyRotatedInstance(double Dtheta)
    {
        return getAbsolutelyRotatedInstance(Dtheta);
    }
    
    @Override
    public Shape2D getAbsolutelyRotatedInstance(double theta)
    {
        double width = getWidth(), height = getHeight();
        return new RotatedRectangle(xMin+width/2, yMin+height/2, width, height, theta);
    }
    
    @Override
    public Shape2D getSimplifiedInstance()
    {
        return clone();
    }
    
    @Override
    public boolean strictlyContains(double x, double y)
    {
        return x > xMin && x < xMax && y > yMin && y < yMax;
    }
    
    @Override
    public boolean contains(double x, double y)
    {
        return x >= xMin && x <= xMax && y >= yMin && y <= yMax;        
    }
    
    public StraightRectangle getIntersection(StraightRectangle rect)
    {
        if(rect == null)
        {
            return null;
        }
        
        double interXMin = Math.max(xMin, rect.xMin),
               interXMax = Math.min(xMax, rect.xMax),
               interYMin = Math.max(yMin, rect.yMin),
               interYMax = Math.min(yMax, rect.yMax);
        
        if(interXMin <= interXMax && interYMin <= interYMax)
        {
            return new StraightRectangle(interXMin, interYMin, interXMax, interYMax);
        }
        else
        {
            return null;
        }
    }
    
    public IntFrame getNearestInsideIntFrame()
    {
        int xMin = (int)Math.ceil(this.xMin), yMin = (int)Math.ceil(this.yMin),
            xMax = (int)Math.floor(this.xMax), yMax = (int)Math.floor(this.yMax);
        
        if(xMin <= xMax && yMin <= yMax)
        {
            return new IntFrame(xMin, yMin, xMax, yMax);
        }
        else
        {
            return null;
        }
    }
    
    public IntFrame getNearestOutsideIntFrame()
    {
        return new IntFrame((int)Math.floor(xMin), (int)Math.ceil(xMax),
                            (int)Math.floor(yMin), (int)Math.ceil(yMax));
    }
    
    
    public static StraightRectangle getInfiniteFrame()
    {
        return new StraightRectangle(
                Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }
}
