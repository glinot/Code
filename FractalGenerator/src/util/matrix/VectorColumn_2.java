package util.matrix;

/**@author Raphaël*/


public class VectorColumn_2
{
    public double a1,
                  a2;
    
    public VectorColumn_2() {}
    
    public VectorColumn_2(double[] a)
    {
        if(a != null && a.length == 2)
        {
            a1 = a[0];
            a2 = a[1];
        }
    }
    
    public VectorColumn_2(double a1,
                          double a2)
    {
        this.a1 = a1;
        this.a2 = a2;
    }
    
    public double[] toArray()
    {
        return new double[]{a1, a2};
    }
    
    public Double[] toBoxedArray()
    {
        return new Double[]{a1, a2};
    }
    
    @Override
    public String toString()
    {
        return "|"+a1+"\n"
              +"|"+a2+"\n";
    }
}
