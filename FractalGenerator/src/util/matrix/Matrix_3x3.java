/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.matrix;

/**
 * @author Raphaël
**/

public class Matrix_3x3
{
    public double a11, a12, a13,
                  a21, a22, a23,
                  a31, a32, a33;
    
    public Matrix_3x3() {}
    
    public Matrix_3x3(double[][] a)
    {
        if(a != null && a.length == 3 && a[0] != null && a[0].length == 3
                                      && a[1] != null && a[1].length == 3
                                      && a[2] != null && a[2].length == 3)
        {
            a11 = a[0][0]; a12 = a[0][1]; a13 = a[0][2];
            a21 = a[1][0]; a22 = a[1][1]; a23 = a[1][2];
            a31 = a[2][0]; a32 = a[2][1]; a33 = a[2][2];
        }
    }
    
    public Matrix_3x3(double a11, double a12, double a13,
                      double a21, double a22, double a23,
                      double a31, double a32, double a33)
    {
        this.a11 = a11; this.a12 = a12; this.a13 = a13;
        this.a21 = a21; this.a22 = a22; this.a23 = a23;
        this.a31 = a31; this.a32 = a32; this.a33 = a33;
    }
    
    public VectorColumn_3 solveCramerSystem(VectorColumn_3 B)
    {
        double detA = determinant(),
               detA1 = CramerDeterminant(B, 1),
               detA2 = CramerDeterminant(B, 2),
               detA3 = CramerDeterminant(B, 3);
        
        return new VectorColumn_3(detA1/detA,
                                  detA2/detA,
                                  detA3/detA);
    }
    
    public double determinant()
    {
        return a11*(a22*a33-a32*a23)
              -a21*(a12*a33-a32*a13)
              +a31*(a12*a23-a22*a13);
    }
    
    public double CramerDeterminant(VectorColumn_3 B, int k)
    {
        switch(k)
        {
            case 1 :
                return B.a1*(a22*a33-a32*a23)
                      -B.a2*(a12*a33-a32*a13)
                      +B.a3*(a12*a23-a22*a13);
            case 2 :
                return a11*(B.a2*a33-B.a3*a23)
                      -a21*(B.a1*a33-B.a3*a13)
                      +a31*(B.a1*a23-B.a2*a13);
            case 3 :
                return a11*(a22*B.a3-a32*B.a2)
                      -a21*(a12*B.a3-a32*B.a1)
                      +a31*(a12*B.a2-a22*B.a1);
            default :
                return 0;
        }
    }
    
    public static void main(String[] args)
    {
        Matrix_3x3 M = new Matrix_3x3
        (1,1,0,
         1,0,1,
         0,1,1);
        
        VectorColumn_3 B = new VectorColumn_3(10, 20, 24);
        
        System.out.println("solution :\n"+M.solveCramerSystem(B));
    }
}
