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
public  abstract  class ParameterCalculatorSimple extends ParametersCalculator<IterationResultSimple> {

    @Override
    public abstract ParameterResult computeParameters(IterationResultSimple iterationResult) ;

}
