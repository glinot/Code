/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot.rendering.threading;

/**
 *
 * @author Gregoire
 */
public interface ParcelRendererListenner {
    public void parcelRendered();
    public void queueIsEmpty();
    public void jobIsDone();
}
