package util.matrix;

// @author Raphaël


public class Matrix_2x2 extends Matrix
{
    public double a11, a12,
                  a21, a22;
    
    public Matrix_2x2() {}
    
    public Matrix_2x2(double[][] a)
    {
        if(a != null && a.length == 2 && a[0] != null && a[0].length == 2
                                      && a[1] != null && a[1].length == 2)
        {
            a11 = a[0][0]; a12 = a[0][1];
            a21 = a[1][0]; a22 = a[1][1];
        }
    }
    
    public Matrix_2x2(double a11, double a12,
                      double a21, double a22)
    {
        this.a11 = a11; this.a12 = a12;
        this.a21 = a21; this.a22 = a22;
    }
    
    public VectorColumn_2 solveCramerSystem(VectorColumn_2 B)
    {
        double detA = determinant(),
               detA1 = CramerDeterminant(B, 1),
               detA2 = CramerDeterminant(B, 2);
        
        return new VectorColumn_2(detA1/detA,
                                  detA2/detA);
    }
    
    public double determinant()
    {
        return a11*a22 - a21*a12;
    }
    
    public double CramerDeterminant(VectorColumn_2 B, int k)
    {
        switch(k)
        {
            case 1 :
                return B.a1*a22 - B.a2*a12;
            case 2 :
                return a11*B.a2 - a21*B.a1;
            default :
                return 0;
        }
    }
    
    public static void main(String[] args)
    {
        Matrix_2x2 M = new Matrix_2x2
        (1,2,
         3,4);
        
        VectorColumn_2 B = new VectorColumn_2(7, 2);
        
        System.out.println("solution :\n"+M.solveCramerSystem(B));
    }
}
