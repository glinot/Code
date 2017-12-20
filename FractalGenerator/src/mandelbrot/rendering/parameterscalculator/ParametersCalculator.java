/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot.rendering.parameterscalculator;

import mandelbrot.rendering.iterationcalculator.IterationResult;

/**
 *
 * @author Gregoire
 * @param <T> 
 */
public abstract class ParametersCalculator <T extends IterationResult> {

    /**
     * 
     * @param iterationResult
     * @return double[] three dimentional tab containing the 3  parameters
     */
    public abstract ParameterResult computeParameters(T iterationResult);
}
 