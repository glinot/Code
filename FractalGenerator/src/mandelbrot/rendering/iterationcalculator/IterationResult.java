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
public class IterationResult {
    protected int n ;
    protected boolean isInMdbSet;

    public IterationResult(int n, boolean isInMdbSet) {
        this.n = n;
        this.isInMdbSet = isInMdbSet;
    }

    public boolean isIsInMdbSet() {
        return isInMdbSet;
    }

    public void setIsInMdbSet(boolean isInMdbSet) {
        this.isInMdbSet = isInMdbSet;
    }
    

   

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    
}
