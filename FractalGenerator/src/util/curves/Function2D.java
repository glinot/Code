/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.curves;

import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import mandelbrot.gui.listeners.CurveListener;

/**
 * @author Raphaël
**/

public abstract class Function2D
{
    final ArrayList<CurveListener> curveListeners;
    final ArrayList<CurveDisplayer> displayers;
    
    public Function2D()
    {
        curveListeners = new ArrayList();
        displayers = new ArrayList();
    }
    
    public abstract Double eval(double x);
    
    public void addCurveListener(CurveListener listener)
    {
        if(!curveListeners.contains(listener))
        {
            curveListeners.add(listener);
        }
    }
    
    public void removeCurveListener(CurveListener listener)
    {
        curveListeners.remove(listener);
    }
    
    public void fireCurveChangeEvent()
    {
        ChangeEvent e = new ChangeEvent(this);
        
        for(CurveListener curveListener : curveListeners)
        {
            curveListener.curveChanged(e);
        }
    }
    
    void addCurveDisplayer(CurveDisplayer displayer)
    {
        if(!displayers.contains(displayer))
        {
            displayers.add(displayer);
        }
    }
    
    void removeCurveDisplayer(CurveDisplayer displayer)
    {
        displayers.remove(displayer);
    }
}
