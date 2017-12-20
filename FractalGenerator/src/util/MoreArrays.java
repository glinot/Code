package util;

/**@author Raphaël*/

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public class MoreArrays
{
    public static double[][] mergeXYVectors(double[] xVector, double[] yVector)
    {
        if(xVector == null || yVector == null || xVector.length != yVector.length)
        {
            return null;
        }
        
        double[][] mergedVectors = new double[xVector.length][2];
        
        for(int i = 0; i < xVector.length; i++)
        {
            mergedVectors[i][0] = xVector[i];
            mergedVectors[i][1] = yVector[i];
        }
        
        return mergedVectors;
    }
    
    public static double[][] demergeXYPoints(double[][] xyPoints)
    {
        if(xyPoints == null)
        {
            return null;
        }
        
        double[][] demergedPoints = new double[2][xyPoints.length];
        
        for(int i = 0; i < xyPoints.length; i++)
        {
            demergedPoints[0][i] = xyPoints[i][0];
            demergedPoints[1][i] = xyPoints[i][1];
        }
        
        return demergedPoints;
    }
        
    public static void sortXYPoints(double[][] xyPoints)
    {
        if(xyPoints == null)
        {
            return;
        }
        
        Arrays.sort(xyPoints, new Comparator<double[]>()
        {
            @Override
            public int compare(double[] a, double[] b)
            {
                // assumes array length is 2
                double x, y;
                
                if (a[0] != b[0])
                {
                    x = a[0];
                    y = b[0];
                }
                else
                {
                    x = a[1];
                    y = b[1];
                }
                
                if(x < y)
                    return -1;
                else if(x == y)
                    return 0;
                else
                    return 1;
            }
        });
    }
    
    public static int indexOf(Object element, Object[] tab)
    {
        for(int k = 0; k < tab.length; k++)
        {
            if(tab[k] == element)
            {
                return k;
            }
        }
        
        return -1;
    }
    
    public static int[] toIntArray(List<Integer> list)
    {
        int[] ret = new int[list.size()];
        int i = 0;
        
        for(Iterator<Integer> it = list.iterator(); it.hasNext(); ret[i++] = it.next());
        
        return ret;
    }
    
    public static void main(String[] args)
    {
        double[][][] tab = new double[3][][];
        tab[0] = new double[][]{{1,2,3},{4,5}};
        tab[1] = new double[][]{{6,2,5},{0,1}};
        tab[2] = new double[][]{{0,0,1},{3,2}};
        
        for (double[][] tab1 : tab)
        {
            for (double[] tab11 : tab1)
            {
                System.out.println(Arrays.toString(tab11));
            }
        }
    }
}
