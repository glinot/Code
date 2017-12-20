/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mandelbrot.gui.events;

import geometry.StraightRectangle;

/**
 * @author Raphaël
**/

public class CurveFrameChangeEvent
{
    protected Object source;
    protected StraightRectangle frame;
    
    public CurveFrameChangeEvent(Object source, StraightRectangle frame)
    {
        this.source = source;
        this.frame = frame;
    }
    
    public Object getSource()
    {
        return source;
    }
    
    public void setSource(Object source)
    {
        this.source = source;
    }
    
    public StraightRectangle getFrame()
    {
        return frame;
    }
    
    public void setFrame(StraightRectangle frame)
    {
        this.frame = frame;
    }
}
