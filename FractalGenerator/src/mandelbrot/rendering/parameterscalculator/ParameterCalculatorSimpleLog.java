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
public class ParameterCalculatorSimpleLog extends ParameterCalculatorSimple {

    @Override
    public ParameterResult computeParameters(IterationResultSimple iterationResult) {

        double zn_r = iterationResult.getZn_r();
        double zn_i = iterationResult.getZn_i();
        int n = iterationResult.getN();

        
        
        double mod = Math.sqrt(zn_r * zn_r + zn_i * zn_i);
        double x = n + 1 - Math.log(Math.log(mod)) / Math.log(2);
        
        return new ParameterResult(x,x,x,iterationResult.isIsInMdbSet());
    }

}
