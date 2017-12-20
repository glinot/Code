/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.curves;

import graphicalsystem.IntPoint;
import static util.MoreGenericity.checkNullArg;
import util.MoreMath;
import util.Reflection;

/**
 * @author Raphaël
**/

public final class IntFrame
{
    private int xMin, xMax, yMin, yMax;
    
    public IntFrame()
    {
        xMin = 0;
        xMax = 0;
        yMin = 0;
        yMax = 0;
    }
    
    public IntFrame(int x1, int y1, int x2, int y2)
    {
        xMin = Math.min(x1, x2);
        xMax = Math.max(x1, x2);
        yMin = Math.min(y1, y2);
        yMax = Math.max(y1, y2);
    }
    
    public int getxMin()
    {
        return xMin;
    }
    
    public void setxMin(int xMin)
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
    
    public int getxMax()
    {
        return xMax;
    }
    
    public void setxMax(int xMax)
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
    
    public int getyMin()
    {
        return yMin;
    }
    
    public void setyMin(int yMin)
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
    
    public int getyMax()
    {
        return yMax;
    }
    
    public void setyMax(int yMax)
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
    
    public IntFrame getIntersection(IntFrame frame)
    {
        if(frame == null)
        {
            return null;
        }
        
        int interXMin = Math.max(xMin, frame.xMin),
            interXMax = Math.min(xMax, frame.xMax),
            interYMin = Math.max(yMin, frame.yMin),
            interYMax = Math.min(yMax, frame.yMax);
        
        if(interXMin <= interXMax && interYMin <= interYMax)
        {
            return new IntFrame(interXMin, interYMin, interXMax, interYMax);
        }
        else
        {
            return null;
        }
    }
    
    public IntPoint getNearestInsidePoint(int X, int Y)
    {
        return new IntPoint(MoreMath.limit(X, xMin, xMax),
                            MoreMath.limit(Y, yMin, yMax));
    }
    
    public IntPoint getNearestInsidePoint(IntPoint point)
    {
        checkNullArg(point, "point");
        return getNearestInsidePoint(point.getAbs(), point.getOrd());
    }
    
    @Override
    public String toString()
    {
        return Reflection.describeAttributes(this, false);
    }
}
