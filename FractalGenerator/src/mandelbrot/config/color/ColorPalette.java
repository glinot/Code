package mandelbrot.config.color;

/**@author Raphaël*/

import java.awt.Color;
import mandelbrot.rendering.parameterscalculator.ParameterResult;
import util.curves.Function2D;
import util.MoreMath;


public class ColorPalette
{
    public enum ColorComponent
    {
        red, green, blue, hue, saturation, brightness
    }
    
    // color functions must be in [0;1]x[0;1] and preferably looped
    protected Function2D comp1Function, comp2Function, comp3Function;
    protected double comp1StartX, comp1EndX, comp2StartX, comp2EndX, comp3StartX, comp3EndX;
    protected boolean HSBMode;
    protected int mdbSetColor;
    
    public ColorPalette(Function2D comp1Function, double comp1StartX, double comp1EndX, Function2D comp2Function, double comp2StartX, double comp2EndX, Function2D comp3Function, double comp3StartX, double comp3EndX, boolean HSBMode)
    {
        this.comp1Function = comp1Function;
        this.comp2Function = comp2Function;
        this.comp3Function = comp3Function;
        this.comp1StartX = comp1StartX;
        this.comp1EndX = comp1EndX;
        this.comp2StartX = comp2StartX;
        this.comp2EndX = comp2EndX;
        this.comp3StartX = comp3StartX;
        this.comp3EndX = comp3EndX;
        this.HSBMode = HSBMode;
        mdbSetColor = Color.BLACK.getRGB();
    }
    
    public int getColorFromParam(ParameterResult parameterResult)
    {
        if(parameterResult == null)
            return 0;
       
        if(parameterResult.isInMdbSet())
        {
            return mdbSetColor;
        }
        else
        {
            float comp1 = (float)comp1Function.eval(MoreMath.mod((parameterResult.getParam1() - comp1StartX) / (comp1EndX - comp1StartX), 1)).doubleValue(),
                  comp2 = (float)comp2Function.eval(MoreMath.mod((parameterResult.getParam2() - comp2StartX) / (comp2EndX - comp2StartX), 1)).doubleValue(),
                  comp3 = (float)comp3Function.eval(MoreMath.mod((parameterResult.getParam3() - comp3StartX) / (comp3EndX - comp3StartX), 1)).doubleValue();
            
            return createColor(comp1, comp2, comp3, parameterResult.isInMdbSet()).getRGB();
       }
    }
    
    public static Color createColor(float comp1, float comp2, float comp3, boolean HSBMode)
    {
        if(HSBMode)
        {
            return new Color(Color.HSBtoRGB(MoreMath.limit(comp1, 0, 1),
                                            MoreMath.limit(comp2, 0, 1),
                                            MoreMath.limit(comp3, 0, 1)));
        }
        else
        {
            return new Color(MoreMath.limit(Math.round(comp1*256), 0, 255),
                             MoreMath.limit(Math.round(comp2*256), 0, 255),
                             MoreMath.limit(Math.round(comp3*256), 0, 255));
        }
    }
    
    public static Color createColor(float comp, ColorComponent colorComponent)
    {
        switch(colorComponent)
        {
            case red :
                return createColor(comp, 0, 0, false);
            case green :
                return createColor(0, comp, 0, false);
            case blue :
                return createColor(0, 0, comp, false);
            case hue :
                return createColor(comp, 1, 0.5f, true);
            case saturation :
                return createColor(0, comp, 0.5f, true);
            case brightness :
                return createColor(0, 0, comp, true);
            default :
                return null;
        }
    }
    
    public Function2D getComp1Function()
    {
        return comp1Function;
    }
    
    public Function2D getComp2Function()
    {
        return comp2Function;
    }
    
    public Function2D getComp3Function()
    {
        return comp3Function;
    }
    
    public double getComp1StartX()
    {
        return comp1StartX;
    }
    
    public double getComp1EndX()
    {
        return comp1EndX;
    }
    
    public double getComp2StartX()
    {
        return comp2StartX;
    }
    
    public double getComp2EndX()
    {
        return comp2EndX;
    }
    
    public double getComp3StartX()
    {
        return comp3StartX;
    }
    
    public double getComp3EndX()
    {
        return comp3EndX;
    }
    
    public boolean isHSBMode()
    {
        return HSBMode;
    }
    
    public boolean isRGBMode()
    {
        return !HSBMode;
    }
    
    public void setComp1Function(Function2D comp1Function)
    {
        this.comp1Function = comp1Function;
    }
    
    public void setComp2Function(Function2D comp2Function)
    {
        this.comp2Function = comp2Function;
    }
    
    public void setComp3Function(Function2D comp3Function)
    {
        this.comp3Function = comp3Function;
    }
    
    public void setComp1StartX(double comp1StartX)
    {
        this.comp1StartX = comp1StartX;
    }
    
    public void setComp1EndX(double comp1EndX)
    {
        this.comp1EndX = comp1EndX;
    }
    
    public void setComp2StartX(double comp2StartX)
    {
        this.comp2StartX = comp2StartX;
    }
    
    public void setComp2EndX(double comp2EndX)
    {
        this.comp2EndX = comp2EndX;
    }
    
    public void setComp3StartX(double comp3StartX)
    {
        this.comp3StartX = comp3StartX;
    }
    
    public void setComp3EndX(double comp3EndX)
    {
        this.comp3EndX = comp3EndX;
    }
    
    public void setHSBMode()
    {
        HSBMode = true;
    }
    
    public void setRGBMode()
    {
        HSBMode = false;
    }
    
    public int getMdbSetColor()
    {
        return mdbSetColor;
    }
    
    public void setMdbSetColor(int mdbSetColor)
    {
        this.mdbSetColor = mdbSetColor;
    }
    
    public static void main(String[] args)
    {
        System.out.println(Color.HSBtoRGB(563455, 1, 1));
        System.out.println(new Color(-1, 1, 1).getRGB());
    }   
}
