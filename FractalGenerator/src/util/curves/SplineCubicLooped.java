/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.curves;

import util.MoreMath;

/**
 * @author Raphaël
**/

public class SplineCubicLooped extends LoopedFunction
{
    private Property<Double> tangents;
    private Property<Double[]> polyCoeff;
    
    public SplineCubicLooped(double[] pointsX, double[] pointsY, double loopStart, double loopEnd)
    {
        super(pointsX, pointsY, loopStart, loopEnd);
        
        tangents = new Property<Double>(propertyType.points, "tangents")
        {
            @Override
            public int[] updatesToDoAfterAdding(int index, int n)
            {
                return new int[]{index-1, index, index+1};
            }
            
            @Override
            public int[] updatesToDoAfterRemoving(int index, int n)
            {
                return new int[]{index-1, index};
            }
            
            @Override
            public Double compute(int index, int n)
            {
                return MoreMath.computeSlope(getX(index-1), getY(index-1),
                                             getX(index+1), getY(index+1));
            }
        };
        
        polyCoeff = new Property<Double[]>(propertyType.points, "polyCoeff")
        {
            @Override
            public int[] updatesToDoAfterAdding(int index, int n)
            {
                return new int[]{index-2, index-1, index, index+1};
            }
            
            @Override
            public int[] updatesToDoAfterRemoving(int index, int n)
            {
                return new int[]{index-2, index-1, index};
            }
            
            @Override
            public Double[] compute(int index, int n)
            {
                return MoreMath.findCubicPolynomial(
                        getX(index), getY(index), tangents.get(index),
                        getX(index+1), getY(index+1), tangents.get(index+1));
            }
        };
        
        initProperties(tangents, polyCoeff);
    }
    
    @Override
    public Double eval(double x)
    {
        if(n > 0)
        {
            x = MoreMath.mod(x-loopStart, loopEnd-loopStart);
            int index = getLeftNearestPointIndex(x);
            
            if(index == -1)
            {
                index = n-1;
                x += loopEnd-loopStart;
            }
            
            Double[] localPoly = polyCoeff.get(index);
            
            if(localPoly != null)
            {
                return MoreMath.computePolyImage(localPoly, x);
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }
}
