/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mandelbrot.gui;

import util.curves.CurveDisplayer;
import geometry.StraightRectangle;
import java.awt.Color;
import java.awt.Graphics;
import mandelbrot.config.color.ColorPalette;
import mandelbrot.config.color.ColorPalette.ColorComponent;
import util.curves.Function2D;

/**
 * @author Raphaël
**/

public class ColorGradientChooser extends CurveDisplayer
{
    private ColorComponent component;
    
    public ColorGradientChooser(Function2D function, ColorComponent component)
    {
        super(function, Color.WHITE, new StraightRectangle(0, 0, 1, 1));
        this.component = component;
    }
    
    public ColorGradientChooser(ColorComponent component)
    {
        this(null, component);
    }
    
    @Override
    protected void preCurveDraw(Graphics g)
    {
        Function2D f = getCurve().getFunction();
        Double eval;
        
        for(int i = 0; i < getWidth(); i++)
        {
            eval = f.eval(convertXtoReal(i));
            
            if(eval != null)
            {
                g.setColor(ColorPalette.createColor(eval.floatValue(), component));
            }
            else
            {
                g.setColor(Color.BLACK);
            }
            
            g.drawLine(i, 0, i, getHeight()-1);
        }
    }
    
    public ColorComponent getComponent()
    {
        return component;
    }
    
    public void setComponent(ColorComponent component)
    {
        this.component = component;
    }
}
