/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mandelbrot.gui.listeners;

import mandelbrot.gui.events.CurveDisplayerModeChangeEvent;

/**
 * @author Raphaël
**/

public interface CurveDisplayerModeListener
{
    public void curveDisplayerModeChanged(CurveDisplayerModeChangeEvent e);
}
