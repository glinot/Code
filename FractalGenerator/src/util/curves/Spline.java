/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.curves;

import util.MoreArrays;
import util.MoreMath;

/**
 * @author Raphaël
**/

public class Spline extends ManipulableFunction
{
    public static final int INVERTED_STEP = -1, STEP = 0, SEGMENT = 1, POLY_CUBIC = 3;
    
    private Property<Integer> curvesType;
    private Property<Double> tangents;
    private Property<Double[]> polyCoeff;
    
    public Spline(double[][] xyPoints, Integer[] curvesTypeArray)
    {
        super(xyPoints);
        
        curvesType = new Property<Integer>(curvesTypeArray, propertyType.intervals, "types")
        {
            @Override
            public int[] updatesToDoAfterAdding(int index, int n)
            {
                if(index == n-1)
                {
                    return new int[]{n-2};
                }
                else
                {
                    return new int[]{index};
                }
            }
            
            @Override
            public int[] updatesToDoAfterRemoving(int index, int n)
            {
                return new int[]{};
            }
            
            @Override
            public Integer compute(int index, int n)
            {
                if(index-1 >= 0)
                {
                    return get(index-1);
                }
                else if(1 < n-1)
                {
                    return get(1);
                }
                else
                {
                    return POLY_CUBIC;
                }
            }
        };
        curvesType.setPersistent(true);
        
        tangents = new Property<Double>(propertyType.points, "tangents")
        {
            @Override
            public int[] updatesToDoAfterAdding(int index, int n)
            {
                return new int[]{index-3, index-2, index-1, index, index+1, index+2, index+3};
            }
            
            @Override
            public int[] updatesToDoAfterRemoving(int index, int n)
            {
                return new int[]{index-2, index-1, index, index+1};
            }
            
            @Override
            public Double compute(int index, int n)
            {
                if(index-1 >= 0 && curvesType.get(index-1) == POLY_CUBIC
                || index < n-1 && curvesType.get(index) == POLY_CUBIC)
                {
                    if(index > 0 && index < n-1)
                    {
                        int leftType = curvesType.get(index-1),
                            rightType = curvesType.get(index);
                        
                        if(leftType == POLY_CUBIC && rightType == POLY_CUBIC)
                        {
                            return MoreMath.computeSlope(getX(index-1), getY(index-1),
                                                         getX(index+1), getY(index+1));
                        }
                        else if(leftType == SEGMENT)
                        {
                            return MoreMath.computeSlope(getX(index-1), getY(index-1),
                                                         getX(index), getY(index));
                        }
                        else if(rightType == SEGMENT)
                        {
                            return MoreMath.computeSlope(getX(index), getY(index),
                                                         getX(index+1), getY(index+1));
                        }
                        else
                        {
                            return 0.;
                        }
                    }
                    else if(index == 0)
                    {
                        return MoreMath.computeSlope(getX(0), getY(0),
                                                     getX(1), getY(1));
                    }
                    else
                    {
                        return MoreMath.computeSlope(getX(n-2), getY(n-2),
                                                     getX(n-1), getY(n-1));
                    }
                }
                else
                {
                    return null;
                }
            }
        };
        
        polyCoeff = new Property<Double[]>(propertyType.intervals, "polyCoeff")
        {
            @Override
            public int[] updatesToDoAfterAdding(int index, int n)
            {
                return new int[]{index-3, index-2, index-1, index, index+1, index+2};
            }
            
            @Override
            public int[] updatesToDoAfterRemoving(int index, int n)
            {
                return new int[]{index-2, index-1, index};
            }
            
            @Override
            public Double[] compute(int index, int n)
            {
                if(curvesType.get(index) == null)
                {
                    return null;
                }
                else
                {
                    switch(curvesType.get(index))
                    {
                        case INVERTED_STEP :
                            return new Double[]{getY(index)};
                        case STEP :
                            return new Double[]{getY(index+1)};
                        case SEGMENT :
                            return MoreMath.findSegmentPolynomial(
                                    getX(index), getY(index),
                                    getX(index+1), getY(index+1));
                        default : //POLY_CUBIC
                            return MoreMath.findCubicPolynomial(
                                    getX(index), getY(index), tangents.get(index),
                                    getX(index+1), getY(index+1), tangents.get(index+1));
                    }
                }
            }
        };
        
        initProperties(curvesType, tangents, polyCoeff);
    }
    
    public Spline(double[] xVector, double[] yVector, Integer[] curvesType)
    {
        this(MoreArrays.mergeXYVectors(xVector, yVector), curvesType);
    }
    
    @Override
    public Double eval(double x)
    {
        int n = getN();
        
        if(n > 1)
        {
            int index = getLeftNearestPointIndex(x);
            
            if(index == -1)
                index = 0;
            else if(index == n-1)
                index = n-2;
            
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
