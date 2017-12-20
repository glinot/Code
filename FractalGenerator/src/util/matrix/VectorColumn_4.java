package util.matrix;

/**@author Raphaël*/


public class VectorColumn_4
{
    public double a1,
                  a2,
                  a3,
                  a4;
    
    public VectorColumn_4() {}
    
    public VectorColumn_4(double[] a)
    {
        if(a != null && a.length == 4)
        {
            a1 = a[0];
            a2 = a[1];
            a3 = a[2];
            a4 = a[3];
        }
    }
    
    public VectorColumn_4(double a1,
                          double a2,
                          double a3,
                          double a4)
    {
        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;
        this.a4 = a4;
    }
    
    public double[] toArray()
    {
        return new double[]{a1, a2, a3, a4};
    }
    
    public Double[] toBoxedArray()
    {
        return new Double[]{a1, a2, a3, a4};
    }
    
    @Override
    public String toString()
    {
        return "|"+a1+"\n"
              +"|"+a2+"\n"
              +"|"+a3+"\n"
              +"|"+a4+"";
    }
}
