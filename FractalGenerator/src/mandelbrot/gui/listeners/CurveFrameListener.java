/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mandelbrot.gui.listeners;

import mandelbrot.gui.events.CurveFrameChangeEvent;

/**
 * @author Raphaël
**/

public interface CurveFrameListener
{
    public void curveFrameChanged(CurveFrameChangeEvent e);
}
