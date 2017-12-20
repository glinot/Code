/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.matrix;

/**
 * @author Raphaël
**/

public class VectorColumn_3
{
    public double a1,
                  a2,
                  a3;
    
    public VectorColumn_3() {}
    
    public VectorColumn_3(double[] a)
    {
        if(a != null && a.length == 3)
        {
            a1 = a[0];
            a2 = a[1];
            a3 = a[2];
        }
    }
    
    public VectorColumn_3(double a1,
                          double a2,
                          double a3)
    {
        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;
    }
    
    public double[] toArray()
    {
        return new double[]{a1, a2, a3};
    }
    
    public Double[] toBoxedArray()
    {
        return new Double[]{a1, a2, a3};
    }
    
    @Override
    public String toString()
    {
        return "|"+a1+"\n"
              +"|"+a2+"\n"
              +"|"+a3;
    }
}
