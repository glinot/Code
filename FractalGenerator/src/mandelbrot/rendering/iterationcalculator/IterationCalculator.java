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
public abstract class IterationCalculator <T extends IterationResult>{
    protected Shape2D limitShape;
    protected int maxIter;

    public IterationCalculator(Shape2D limitShape, int iterMax) {
        this.limitShape = limitShape;
        this.maxIter = iterMax;
    }
    /**
     * Computes mandelbrot iterations
     * @param c_r real part of complex at compute point 
     * @param c_i imaginary part of complex at compute point 
     * @return IterationResult child 
     */
    public abstract T computeIteration(double c_r, double c_i);

    public Shape2D getLimitShape() {
        return limitShape;
    }

    public void setLimitShape(Shape2D limitShape) {
        this.limitShape = limitShape;
    }

    public int getMaxIter() {
        return maxIter;
    }

    public void setMaxIter(int maxIter) {
        this.maxIter = maxIter;
    }

  
    
    
    
    
}
