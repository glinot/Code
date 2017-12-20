/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot.rendering.iterationcalculator;

import geometry.Shape2D;

/**
 *
 * @author Gregoire
 */
public class IterationCalculatorSimple extends IterationCalculator<IterationResultSimple> {

    public IterationCalculatorSimple(Shape2D limitShape, int iterMax) {
        super(limitShape, iterMax);
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
            tmp = z_r;
            z_r = z_r*z_r - z_i*z_i + c_r;
            z_i = 2*z_i*tmp + c_i;
        }
        return new IterationResultSimple(c_r, c_i, z_r, z_i, i,i==maxIter);
        
    }

    
    
}
