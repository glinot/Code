/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot.rendering.parameterscalculator;

import mandelbrot.rendering.iterationcalculator.IterationResultSimple;

/**
 *
 * @author Gregoire
 */
public class ParameterCalculatorSimpleLimit extends ParameterCalculatorSimple{

    private int i_max;
    public ParameterCalculatorSimpleLimit( int i_max) {
        this.i_max = i_max;
    }

    
    @Override
    public ParameterResult computeParameters(IterationResultSimple iterationResult) {
        double x = i_max-iterationResult.getN()<100? 0:0;
        return new ParameterResult(x, x, x, iterationResult.isIsInMdbSet());
    }

    
    
    
}
