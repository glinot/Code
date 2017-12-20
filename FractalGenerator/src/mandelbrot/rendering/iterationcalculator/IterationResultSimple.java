/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot.rendering.iterationcalculator;

/**
 *
 * @author Gregoire
 */
public class IterationResultSimple extends IterationResult {

    /**
     *
     * @param c_r real part of complex at compute point 
     * @param c_i imaginary part of complex at compute point
     * @param zn_r real part after n iterations of mandelbrot
     * @param zn_i imaginary part after n iterations of mandelbrot
     * @param n Escape index of mandelbrot iterations
     * @param isInMdbSet say if point is in MdbSet
     */
    public IterationResultSimple(double c_r, double c_i, double zn_r, double zn_i, int n, boolean isInMdbSet) {
        super(n, isInMdbSet);
        this.c_r = c_r;
        this.c_i = c_i;
        this.zn_r = zn_r;
        this.zn_i = zn_i;
    }

  
    private double c_r;
    private double c_i;
    private double zn_r;
    private double zn_i;

    public double getC_r() {
        return c_r;
    }

    public void setC_r(double c_r) {
        this.c_r = c_r;
    }

    public double getC_i() {
        return c_i;
    }

    public void setC_i(double c_i) {
        this.c_i = c_i;
    }

    public double getZn_r() {
        return zn_r;
    }

    public void setZn_r(double zn_r) {
        this.zn_r = zn_r;
    }

    public double getZn_i() {
        return zn_i;
    }

    public void setZn_i(double zn_i) {
        this.zn_i = zn_i;
    }
}
