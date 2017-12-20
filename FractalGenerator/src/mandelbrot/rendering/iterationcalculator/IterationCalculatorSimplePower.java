/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot.rendering.iterationcalculator;

import geometry.Shape2D;
import java.util.Arrays;

/**
 *
 * @author Gregoire
 */
public class IterationCalculatorSimplePower extends IterationCalculatorSimple{

    private int power;
    public IterationCalculatorSimplePower(Shape2D limitShape, int iterMax,int power) {
        super(limitShape, iterMax);
        this.power=power;
    }

    @Override
    public IterationResultSimple computeIteration(double c_r, double c_i) {
        
        double z_r = 0,
               z_i = 0,
               tmp;
        int i = 0;
        
        while(limitShape.strictlyContains(z_r, z_i) && i < maxIter)
        {
            i++; // computes term n°i
            double [] cRes = computeComplexPower(z_r, z_i, power);
            z_r = cRes[0] + c_r;
            z_i = cRes[1] + c_i;
        }
        return new IterationResultSimple(c_r, c_i, z_r, z_i, i,i==maxIter);
    }

   private static double[] computeComplexPower(double z_r,double z_i,int power){
       double zRes_r =z_r;
       double zRes_i=z_i;
       double temp=0;
       int i =1;
       while(i<power){
           temp=zRes_r;
           zRes_r = zRes_r*z_r-z_i*zRes_i;
           zRes_i=z_r*zRes_i+temp*z_i;
           i++;
       }
       return new double[]{zRes_r,zRes_i};
   }
    public static void main(String[] args) {
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

   
    
}
