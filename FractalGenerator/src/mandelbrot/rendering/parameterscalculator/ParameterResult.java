/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot.rendering.parameterscalculator;

/**
 *
 * @author Gregoire
 */
public class ParameterResult {
    double param1,param2,param3;
    boolean isInMdbSet;

    public ParameterResult(double param1, double param2, double param3, boolean isInMdbSet) {
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.isInMdbSet = isInMdbSet;
    }

    public boolean isInMdbSet() {
        return isInMdbSet;
    }

    public void setIsInMdbSet(boolean isInMdbSet) {
        this.isInMdbSet = isInMdbSet;
    }
    
    
    public double getParam1() {
        return param1;
    }

    public void setParam1(double param1) {
        this.param1 = param1;
    }

    public double getParam2() {
        return param2;
    }

    public void setParam2(double param2) {
        this.param2 = param2;
    }

    public double getParam3() {
        return param3;
    }

    public void setParam3(double param3) {
        this.param3 = param3;
    }
    
}
