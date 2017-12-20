package util.matrix;

/**@author Raphaël*/


public class Matrix_4x4 extends Matrix
{
    public double a11, a12, a13, a14,
                  a21, a22, a23, a24,
                  a31, a32, a33, a34,
                  a41, a42, a43, a44;
    
    public Matrix_4x4() {}
    
    public Matrix_4x4(double[][] a)
    {
        if(a != null && a.length == 4 && a[0] != null && a[0].length == 4
                                      && a[1] != null && a[1].length == 4
                                      && a[2] != null && a[2].length == 4
                                      && a[3] != null && a[3].length == 4)
        {
            a11 = a[0][0]; a12 = a[0][1]; a13 = a[0][2]; a14 = a[0][3];
            a21 = a[1][0]; a22 = a[1][1]; a23 = a[1][2]; a24 = a[1][3];
            a31 = a[2][0]; a32 = a[2][1]; a33 = a[2][2]; a34 = a[2][3];
            a41 = a[3][0]; a42 = a[3][1]; a43 = a[3][2]; a44 = a[3][3];
        }
    }
    
    public Matrix_4x4(double a11, double a12, double a13, double a14,
                      double a21, double a22, double a23, double a24,
                      double a31, double a32, double a33, double a34,
                      double a41, double a42, double a43, double a44)
    {
        this.a11 = a11; this.a12 = a12; this.a13 = a13; this.a14 = a14;
        this.a21 = a21; this.a22 = a22; this.a23 = a23; this.a24 = a24;
        this.a31 = a31; this.a32 = a32; this.a33 = a33; this.a34 = a34;
        this.a41 = a41; this.a42 = a42; this.a43 = a43; this.a44 = a44;
    }
    
    public VectorColumn_4 solveCramerSystem(VectorColumn_4 B)
    {
        double detA = determinant(),
               detA1 = CramerDeterminant(B, 1),
               detA2 = CramerDeterminant(B, 2),
               detA3 = CramerDeterminant(B, 3),
               detA4 = CramerDeterminant(B, 4);
        
        return new VectorColumn_4(detA1/detA,
                                  detA2/detA,
                                  detA3/detA,
                                  detA4/detA);
    }
    
    public double determinant()
    {
        return a11 * (a22 * (a33*a44-a34*a43)
                    + a23 * (a34*a42-a32*a44)
                    + a24 * (a32*a43-a33*a42))
             - a12 * (a21 * (a33*a44-a34*a43)
                    + a23 * (a34*a41-a31*a44)
                    + a24 * (a31*a43-a33*a41))
             + a13 * (a21 * (a32*a44-a34*a42)
                    + a22 * (a34*a41-a31*a44)
                    + a24 * (a31*a42-a32*a41))
             - a14 * (a21 * (a32*a43-a33*a42)
                    + a22 * (a33*a41-a31*a43)
                    + a23 * (a31*a42-a32*a41));
    }
    
    public double CramerDeterminant(VectorColumn_4 B, int k)
    {
        switch(k)
        {
            case 1 :
                return B.a1 * (a22 * (a33*a44-a34*a43)
                            + a23 * (a34*a42-a32*a44)
                            + a24 * (a32*a43-a33*a42))
                     - a12 * (B.a2 * (a33*a44-a34*a43)
                            + a23 * (a34*B.a4-B.a3*a44)
                            + a24 * (B.a3*a43-a33*B.a4))
                     + a13 * (B.a2 * (a32*a44-a34*a42)
                            + a22 * (a34*B.a4-B.a3*a44)
                            + a24 * (B.a3*a42-a32*B.a4))
                     - a14 * (B.a2 * (a32*a43-a33*a42)
                            + a22 * (a33*B.a4-B.a3*a43)
                            + a23 * (B.a3*a42-a32*B.a4));
            case 2 :
                return a11 * (B.a2 * (a33*a44-a34*a43)
                            + a23 * (a34*B.a4-B.a3*a44)
                            + a24 * (B.a3*a43-a33*B.a4))
                     - B.a1 * (a21 * (a33*a44-a34*a43)
                            + a23 * (a34*a41-a31*a44)
                            + a24 * (a31*a43-a33*a41))
                     + a13 * (a21 * (B.a3*a44-a34*B.a4)
                            + B.a2 * (a34*a41-a31*a44)
                            + a24 * (a31*B.a4-B.a3*a41))
                     - a14 * (a21 * (B.a3*a43-a33*B.a4)
                            + B.a2 * (a33*a41-a31*a43)
                            + a23 * (a31*B.a4-B.a3*a41));
            case 3 :
                return a11 * (a22 * (B.a3*a44-a34*B.a4)
                            + B.a2 * (a34*a42-a32*a44)
                            + a24 * (a32*B.a4-B.a3*a42))
                     - a12 * (a21 * (B.a3*a44-a34*B.a4)
                            + B.a2 * (a34*a41-a31*a44)
                            + a24 * (a31*B.a4-B.a3*a41))
                     + B.a1 * (a21 * (a32*a44-a34*a42)
                            + a22 * (a34*a41-a31*a44)
                            + a24 * (a31*a42-a32*a41))
                     - a14 * (a21 * (a32*B.a4-B.a3*a42)
                            + a22 * (B.a3*a41-a31*B.a4)
                            + B.a2 * (a31*a42-a32*a41));
            case 4 :
                return a11 * (a22 * (a33*B.a4-B.a3*a43)
                            + a23 * (B.a3*a42-a32*B.a4)
                            + B.a2 * (a32*a43-a33*a42))
                     - a12 * (a21 * (a33*B.a4-B.a3*a43)
                            + a23 * (B.a3*a41-a31*B.a4)
                            + B.a2 * (a31*a43-a33*a41))
                     + a13 * (a21 * (a32*B.a4-B.a3*a42)
                            + a22 * (B.a3*a41-a31*B.a4)
                            + B.a2 * (a31*a42-a32*a41))
                     - B.a1 * (a21 * (a32*a43-a33*a42)
                            + a22 * (a33*a41-a31*a43)
                            + a23 * (a31*a42-a32*a41));
            default :
                return 0;
        }
    }
    
    public static void main(String[] args)
    {
        Matrix_4x4 M = new Matrix_4x4
        (1,1,0,0,
         1,0,1,0,
         0,1,1,0,
         1,1,1,-1);
        
        VectorColumn_4 B = new VectorColumn_4(10, 20, 24, 0);
        
        System.out.println("solution :\n"+M.solveCramerSystem(B));
    }
}
