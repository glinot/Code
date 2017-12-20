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
public class IterationCalculatorSimplePowerNonInteger extends IterationCalculatorSimple{

    private double power;
    public IterationCalculatorSimplePowerNonInteger(Shape2D limitShape, int iterMax,double power) {
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

   private static double[] computeComplexPower(double z_r,double z_i,double power){
       double theta =Math.atan2(z_r, z_i)/power;
       double m1 = Math.sqrt(z_r*z_r+z_i*z_i);
       double mod = Math.exp(power*Math.log(m1));
       return new double[]{mod*Math.cos(theta),mod*Math.sin(theta)};
   }
    public static void main(String[] args) {
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

   
    
}
