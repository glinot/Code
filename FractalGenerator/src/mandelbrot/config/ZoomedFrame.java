package mandelbrot.config;

// @author Gregoire & Raphael

import geometry.StraightRectangle;
import geometry.Vector2D;
import mandelbrot.rendering.MdbRenderer;
import static util.MoreGenericity.checkNullArg;
import util.Reflection;


public class ZoomedFrame
{
    protected StraightRectangle initialFrame;
    protected Vector2D center;
    protected double horZoom, vertZoom;
    
    public ZoomedFrame(StraightRectangle initialFrame, Vector2D center, double horZoom, double vertZoom)
    {
        updateInitialFrame(initialFrame);
        updateCenter(center);
        
        this.horZoom = horZoom;
        this.vertZoom = vertZoom;
    }
    
    public ZoomedFrame(StraightRectangle initialFrame, Vector2D center, double zoom)
    {
        this(initialFrame, center, zoom, zoom);
    }
    
    public ZoomedFrame(StraightRectangle initialFrame, Vector2D center)
    {
        this(initialFrame, center, 1);
    }
    
    public ZoomedFrame(StraightRectangle initialFrame)
    {
        this(initialFrame, initialFrame.getCenter());
    }
    
    private void updateInitialFrame(StraightRectangle initialFrame)
    {
        this.initialFrame = checkNullArg(initialFrame, "initialFrame");
    }
    
    private void updateCenter(Vector2D center)
    {
        this.center = checkNullArg(center, "center");
    }
    
    public double getWidth()
    {
        return initialFrame.getWidth()/horZoom;
    }
    
    public double getHeight()
    {
        return initialFrame.getHeight()/vertZoom;
    }
    
    public double getxMin()
    {
        return center.getX() - getWidth()/2;
    }
    
    public double getxMax()
    {
        return center.getX() + getWidth()/2;
    }
    
    public double getyMin()
    {
        return center.getY() - getHeight()/2;
    }
    
    public double getyMax()
    {
        return center.getY() + getHeight()/2;
    }
    
    public StraightRectangle getFrameInstance()
    {
       double halfCurrentWidth = getWidth()/2,
              halfCurrentHeight = getHeight()/2,
              x1 = center.getX() - halfCurrentWidth,
              x2 = center.getX() + halfCurrentWidth,
              y1 = center.getY() - halfCurrentHeight,
              y2 = center.getY() + halfCurrentHeight;
       return new StraightRectangle(x1, y1, x2, y2);
    }
    
    public double getFormat()
    {
        return initialFrame.getWidth()/initialFrame.getHeight();
    }
    
    public void zoomIn(double zoomMultiplier)
    {
        horZoom *= zoomMultiplier;
        vertZoom *= zoomMultiplier;
    }
    
    public void zoomInHor(double zoomMultiplier)
    {
        horZoom *= zoomMultiplier;
    }
    
    public void zoomInVert(double zoomMultiplier)
    {
        vertZoom *= zoomMultiplier;
    }
    
    public void zoomOut(double zoomDivider)
    {
        horZoom /= zoomDivider;
        vertZoom /= zoomDivider;
    }
    
    public void zoomOutHor(double zoomDivider)
    {
        horZoom /= zoomDivider;
    }
    
    public void zoomOutVert(double zoomDivider)
    {
        vertZoom /= zoomDivider;
    }
    
    public StraightRectangle getInitialFrame()
    {
        return initialFrame;
    }
    
    public Vector2D getCenter()
    {
        return center;
    }
    
    public double getZoom()
    {
        return horZoom;
    }
    
    public double getHorZoom()
    {
        return horZoom;
    }
    
    public double getVertZoom()
    {
        return vertZoom;
    }
    
    public void setInitialFrame(StraightRectangle initialFrame)
    {
        updateInitialFrame(initialFrame);
    }
    
    public void setCenter(Vector2D center)
    {
        updateCenter(center);
    }
    
    public void setZoom(double zoom)
    {
        horZoom = zoom;
        vertZoom = zoom;
    }
    
    public void setHorZoom(double horZoom)
    {
        this.horZoom = horZoom;
    }
    
    public void setVertZoom(double vertZoom)
    { 
        this.vertZoom = vertZoom;
    }
    
    @Override
    public String toString()
    {
        return Reflection.describeAttributes(this, true);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        return Reflection.checkAttributesEquality(this, obj, true);
    }
    
    public static void main(String args[])
    {
        ZoomedFrame ZF1 = new ZoomedFrame(MdbRenderer.DEFAULT_INITIAL_FRAME);
        System.out.println(ZF1);
        ZoomedFrame ZF2 = new ZoomedFrame(new StraightRectangle(0, 0, 1, 1), new Vector2D(2, 2), 45);
        System.out.println(ZF1.equals(ZF2));
   }
}
