package util;

// @author Raphaël

import util.matrix.Matrix_2x2;
import util.matrix.Matrix_4x4;
import util.matrix.VectorColumn_2;
import util.matrix.VectorColumn_4;

public final class MoreMath
{
    // rounds x with n decimals
    public static double round(double x, int n)
    {
        double tenPower = intPow(10, n);
        return Math.round(x*tenPower)/tenPower;
    }
    
    // compute x^n
    public static double intPow(double x, int n)
    {
        double result = 1;
        
        if(n >= 0)
        {
            for(int i = 0; i < n; i++)
            {
                result *= x;
            }
        }
        else
        {
            for(int i = 0; i < -n; i++)
            {
                result /= x;
            }
        }
        
        return result;
    }
    
    public static double computeLagInterp(double[] pointsX, double[] pointsY, double x)
    {
        double sum = 0.0, product;
        int n = pointsX.length;
        
        for(int i = 0; i < n; i++)
        {
            product = 1.0;
            
            for(int j = 0; j < n; j++)
            {
                if(j != i)
                {
                    product *= (x-pointsX[j]) / (pointsX[i]-pointsX[j]);
                }
            }
            
            sum += pointsY[i] * product;
        }
        
        return sum;
    }
    
    public static double computeLagInterpWithReplic(double[] pointsX, double[] pointsY, double x, int N)
    {
        double sum = 0.0, product;
        int n = pointsX.length;
        
        for(int i = -N*n; i < (N+1)*n; i++)
        {
            product = 1.0;
            
            for(int j = -N*n; j < (N+1)*n; j++)
            {
                if(j != i)
                {
                    product *= (x-pointsX[MoreMath.mod(j, n)]-j/n) / (pointsX[MoreMath.mod(i, n)]+i/n-pointsX[MoreMath.mod(j, n)]-j/n);
                }
            }
            
            sum += pointsY[MoreMath.mod(i, n)] * product;
        }
        
        return sum;
    }
    
    public static double computeSegIntepolation(double[] pointsX, double[] pointsY, double x)
    {
        int i;
        x = mod(x, 1);

        for(i = 0; i < pointsX.length; i++)
        {
            if(pointsX[i] > x)
            {
                break;
            }
        }

        double x1, y1, x2, y2;

        if(i == 0)
        {
            x1 = pointsX[pointsX.length-1] - 1;
            y1 = pointsY[pointsX.length-1];
            x2 = pointsX[0];
            y2 = pointsY[0];
        }
        else if(i == pointsX.length)
        {
            x1 = pointsX[pointsX.length-1];
            y1 = pointsY[pointsX.length-1];
            x2 = pointsX[0] + 1;
            y2 = pointsY[0];
        }
        else
        {
            x1 = pointsX[i-1];
            y1 = pointsY[i-1];
            x2 = pointsX[i];
            y2 = pointsY[i];
        }

        return y1+(x-x1)*(y2-y1)/(x2-x1);
    }
    
    public static Double[] findCubicPolynomial(double x1, double y1, double T1, double x2, double y2, double T2)
    {
        double x1S = x1*x1, x1C = x1S*x1,
               x2S = x2*x2, x2C = x2S*x2;
        
        return new Matrix_4x4
            (1, x1,  x1S,   x1C,
             0,  1, 2*x1, 3*x1S,
             1, x2,  x2S,   x2C,
             0,  1, 2*x2, 3*x2S)
            .solveCramerSystem(new VectorColumn_4
            (y1, T1, y2, T2)).toBoxedArray();
    }
    
    public static Double[] findSegmentPolynomial(double x1, double y1, double x2, double y2)
    {
        return new Matrix_2x2
            (1, x1,
             1, x2)
            .solveCramerSystem(new VectorColumn_2
            (y1, y2)).toBoxedArray();
    }
    
    public static double computePolyImage(Double[] coeff, double x)
    {
        double result = 0, fact = 1;
        
        if(coeff != null)
        {
            for(double a : coeff)
            {
                result += a*fact;
                fact *= x;
            }
        }
        
        return result;
    }
    
    // computes the integer dividend with a positive remainder
    public static int div(int a, int b)
    {
        return (int)Math.floor(((double)a) / b);
    }
    
    public static int div(double a, double b)
    {
        return (int)Math.floor(a / b);
    }
    
    // computes the positive remainder after an integer division
    // Makes sense only for b positive.
    public static int mod(int a, int b)
    {
        return (a %= b) < 0 ? a+b : a;
    }
    
    public static double mod(double a, int b)
    {
        return (a %= b) < 0 ? a+b : a;
    }
    
    public static double mod(double a, double b)
    {
        return a - div(a, b)*b;
    }
    
    public static double computeSlope(double x1, double y1, double x2, double y2)
    {
        return (y2-y1) / (x2-x1);
    }
    
    public static int limit(int n, int nMin, int nMax)
    {
        return Math.min(Math.max(n, nMin), nMax);
    }
    
    public static float limit(float x, float xMin, float xMax)
    {
        return Math.min(Math.max(x, xMin), xMax);
    }
    
    public static double limit(double x, double xMin, double xMax)
    {
        return Math.min(Math.max(x, xMin), xMax);
    }
    
    public static boolean isFiniteValue(double x)
    {
        return x != Double.NaN && x != Double.NEGATIVE_INFINITY && x != Double.POSITIVE_INFINITY;
    }
    
    public static void main(String[] args)
    {
        System.out.println(10%7+"\n"+((-10)%7)+"\n"+(10%(-7))+"\n"+((-10)%(-7))+"\n");
        System.out.println(div(22,6.5)+"\n"+div(-22,6.5)+"\n"+div(22,-6.5)+"\n"+div(-22,-6.5)+"\n");
        
        System.out.println(0.9-1.0+" WTF fucking rounding errors !");
        
        System.out.println(mod(-12.45, 1));
        
        System.out.println("mod x = "+mod(1.7,1));
        
        System.out.println(limit(50, 0, 255));
    }
}
