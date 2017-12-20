/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.curves;

import util.MoreArrays;
import static util.MoreMath.div;
import static util.MoreMath.mod;

/**
 * @author Raphaël
**/

public abstract class LoopedFunction extends ManipulableFunction
{
    final double loopStart, loopEnd;
    
    public LoopedFunction(double[][] xyPoints, double loopStart, double loopEnd)
    {
        super(xyPoints);
        this.loopStart = Math.min(loopStart, loopEnd);
        this.loopEnd = Math.max(loopStart, loopEnd);
    }
    
    public LoopedFunction(double[] pointsX, double[] pointsY, double loopStart, double loopEnd)
    {
        this(MoreArrays.mergeXYVectors(pointsX, pointsY), loopStart, loopEnd);
    }
    
    public double getLoopStart()
    {
        return loopStart;
    }
    
    public double getLoopEnd()
    {
        return loopEnd;
    }
    
    @Override
    public double getX(int index)
    {
        return points[constrainIndex(index)].x+div(index, n)*(loopEnd-loopStart);
    }
    
    @Override
    protected int constrainIndex(int index)
    {
        if(n > 0)
            return mod(index, n);
        else
            throw new IllegalArgumentException("Index "+index
                    +" doesn't correspond to any point.");
    }
    
    @Override
    protected int constrainPropertyIndex(int index, Property prop)
    {
        int size = prop.size();
        
        if(size > 0)
            return mod(index, size);
        else
            throw new IllegalArgumentException("Index "+index
                    +" doesn't correspond to any component of property "
                    +prop.getName()+".");
    }
}
