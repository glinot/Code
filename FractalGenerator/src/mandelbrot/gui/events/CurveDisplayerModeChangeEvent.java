/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mandelbrot.gui.events;

/**
 * @author Raphaël
 */

public class CurveDisplayerModeChangeEvent
{
    protected Object source;
    protected boolean manipulable, translatable, scalable;
    
    public CurveDisplayerModeChangeEvent(Object source, boolean manipulable, boolean translatable, boolean scalable)
    {
        this.source = source;
        this.manipulable = manipulable;
        this.translatable = translatable;
        this.scalable = scalable;
    }
    
    public Object getSource()
    {
        return source;
    }
    
    public void setSource(Object source)
    {
        this.source = source;
    }
    
    public boolean isManipulable()
    {
        return manipulable;
    }
    
    public void setManipulable(boolean manipulable)
    {
        this.manipulable = manipulable;
    }
    
    public boolean isTranslatable()
    {
        return translatable;
    }
    
    public void setTranslatable(boolean translatable)
    {
        this.translatable = translatable;
    }
    
    public boolean isScalable()
    {
        return scalable;
    }
    
    public void setScalable(boolean scalable)
    {
        this.scalable = scalable;
    }
}
