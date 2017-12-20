/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.curves;

import mandelbrot.gui.listeners.CurveListener;
import geometry.StraightRectangle;
import geometry.Vector2D;
import graphicalsystem.IntPoint;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import mandelbrot.config.ZoomedFrame;
import mandelbrot.gui.events.CurveDisplayerModeChangeEvent;
import mandelbrot.gui.events.CurveFrameChangeEvent;
import mandelbrot.gui.listeners.CurveDisplayerModeListener;
import mandelbrot.gui.listeners.CurveFrameListener;
import static util.MoreArrays.toIntArray;
import static util.MoreGenericity.checkNullArg;
import util.MoreMath;
import util.curves.ManipulableFunction.ManipulablePoint;

/**
 * @author Raphaël
**/

public class CurveDisplayer extends JPanel
    implements MouseListener, MouseMotionListener, MouseWheelListener
{
    // constants
    static final int POINT_HIT_BOX_SIZE = 12,
                     DEFAULT_POINT_SIZE = 10,
                     HIGHLIGHTED_POINT_SIZE = 14,
                     FIRST_OUTLINE_THICKNESS = 1,
                     SECOND_OUTLINE_THICKNESS = 1;
    static final double DEFAULT_ZOOM_MULTIPLIER = 1.2;
    
    // attributes
    ArrayList<CurveListener> curveListeners;
    ArrayList<CurveFrameListener> curveFrameListeners;
    ArrayList<CurveDisplayerModeListener> curveDisplayerModeListeners;
    ZoomedFrame zoomedFrame;
    Curve curve;
    StraightRectangle viewBounds, pointsBounds, curveBounds;
    ManipulablePoint highlightedPoint;
    IntPoint lastMousePos;
    boolean draggingPoint;
    int horZoomCount, vertZoomCount;
    double horZoomMultiplier, vertZoomMultiplier;
    boolean manipulable, translatable, scalable;
    double pixelXOffset, pixelYOffset;
    int lastWidth, lastHeight;
    boolean curveChanged, curveFrameChanged, curveDisplayerModeChanged;
    
    // constructors
    public CurveDisplayer(Function2D function, Color color, boolean visible, StraightRectangle view)
    {
        curveListeners = new ArrayList();
        curveFrameListeners = new ArrayList();
        curveDisplayerModeListeners = new ArrayList();
        
        updateCurve(function, color, visible, true);
        updateZoomedFrame(view, view != null ? view.getCenter() : null,
                0, DEFAULT_ZOOM_MULTIPLIER,
                0, DEFAULT_ZOOM_MULTIPLIER,
                true);
        curveDisplayerModeChanged = true;
        
        manipulable = true;
        pixelXOffset = 0.5;
        pixelYOffset = 0.5;
        
        lastWidth = Integer.MIN_VALUE;
        lastHeight = Integer.MIN_VALUE;
        
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }
    
    public CurveDisplayer(Function2D function, Color color, StraightRectangle view)
    {
        this(function, color, true, view);
    }
    
    public CurveDisplayer(StraightRectangle view, Function2D function)
    {
        this(function, Color.WHITE, view);
    }
    
    public CurveDisplayer()
    {
        this(null, null, false, null);
    }
    
    // private attribute update methods
    private void updateCurve(Function2D function, Color color, boolean visible, boolean initialization)
    {
        if(curve != null)
        {
            curve.function.removeCurveDisplayer(this);
        }
        
        if(function != null && color != null)
        {
            curve = new Curve(function, color, visible);
            function.addCurveDisplayer(this);
        }
        else
        {
            curve = null;
        }
        
        curveChanged = true;
        
        if(!initialization)
        {
            repaint();
        }
    }
    
    private void updateZoomedFrame(
            StraightRectangle initialFrame, Vector2D center,
            int horZoomCount, double horZoomMultiplier,
            int vertZoomCount, double vertZoomMultiplier,
            boolean initialization)
    {
        this.horZoomCount = horZoomCount;
        this.horZoomMultiplier = horZoomMultiplier;
        this.vertZoomCount = vertZoomCount;
        this.vertZoomMultiplier = vertZoomMultiplier;
        
        if(initialFrame != null && center != null)
        {   
            zoomedFrame = new ZoomedFrame(initialFrame.clone(), center.clone(),
                    computeZoom(horZoomCount, horZoomMultiplier),
                    computeZoom(vertZoomCount, vertZoomMultiplier));
        }
        else
        {
            zoomedFrame = null;
        }
        
        curveFrameChanged = true;
        
        if(!initialization)
        {
            repaint();
        }
    }
    
    private void updateHorZoom()
    {
        zoomedFrame.setHorZoom(computeZoom(horZoomCount, horZoomMultiplier));
    }
    
    private void updateVertZoom()
    {
        zoomedFrame.setVertZoom(computeZoom(vertZoomCount, vertZoomMultiplier));
    }
    
    // curveListeners methods
    public final ArrayList<CurveListener> getCurveListeners()
    {
        return curveListeners;
    }
    
    public final void setCurveListeners(ArrayList<CurveListener> curveListeners)
    {
        if(curveListeners != null)
        {
            this.curveListeners = curveListeners;
        }
        else
        {
            throw new IllegalArgumentException("CurveListeners can't be a null ArrayList.");
        }
    }
    
    public final void addCurveListener(CurveListener listener)
    {
        if(listener != null)
        {
            if(!curveListeners.contains(listener))
            {
                curveListeners.add(listener);
            }
        }
        else
        {
            throw new IllegalArgumentException("You can't add a null CurveListener.");
        }
    }
    
    public final void removeCurveListener(CurveListener listener)
    {
        curveListeners.remove(listener);
    }
    
    private void fireCurveChangeEvent()
    {
        ChangeEvent e = new ChangeEvent(this);
        
        for(CurveListener curveListener : curveListeners)
        {
            curveListener.curveChanged(e);
        }
    }
    
    public void fireCurveChangeEvent(CurveListener listener)
    {
        if(curveListeners.contains(listener))
        {
            listener.curveChanged(new ChangeEvent(this));
        }
        else
        {
            throw new IllegalArgumentException("This listener is not added to the list of CurveListener.");
        }
    }
    
    // curveFrameListeners methods
    public final ArrayList<CurveFrameListener> getCurveFrameListeners()
    {
        return curveFrameListeners;
    }
    
    public final void setCurveFrameListeners(ArrayList<CurveFrameListener> curveFrameListeners)
    {
        if(curveFrameListeners != null)
        {
            this.curveFrameListeners = curveFrameListeners;
        }
        else
        {
            throw new IllegalArgumentException("CurveFrameListeners can't be a null ArrayList.");
        }
    }
    
    public final void addCurveFrameListener(CurveFrameListener listener)
    {
        if(listener != null)
        {
            if(!curveFrameListeners.contains(listener))
            {
                curveFrameListeners.add(listener);
            }
        }
        else
        {
            throw new IllegalArgumentException("You can't add a null CurveFrameListener.");
        }
    }
    
    public final void removeCurveFrameListener(CurveFrameListener listener)
    {
        curveFrameListeners.remove(listener);
    }
    
    private void fireCurveFrameChangeEvent()
    {
        CurveFrameChangeEvent e = new CurveFrameChangeEvent(this,
                zoomedFrame != null ? zoomedFrame.getFrameInstance() : null);
        
        for(CurveFrameListener curveFrameListener : curveFrameListeners)
        {
            curveFrameListener.curveFrameChanged(e);
        }
    }
    
    public final void fireCurveFrameChangeEvent(CurveFrameListener listener)
    {
        if(curveFrameListeners.contains(listener))
        {
            listener.curveFrameChanged(new CurveFrameChangeEvent(this,
                    zoomedFrame != null ? zoomedFrame.getFrameInstance() : null));
        }
        else
        {
            throw new IllegalArgumentException("This listener is not added to the list of CurveFrameListener.");
        }
    }
    
    // curveDisplayerModeListeners methods
    public final ArrayList<CurveDisplayerModeListener> getCurveDisplayerModeListeners()
    {
        return curveDisplayerModeListeners;
    }
    
    public final void setCurveDisplayerModeListeners(ArrayList<CurveDisplayerModeListener>
            curveDisplayerModeListeners)
    {
        if(curveDisplayerModeListeners != null)
        {
            this.curveDisplayerModeListeners = curveDisplayerModeListeners;
        }
        else
        {
            throw new IllegalArgumentException("CurveDisplayerModeListeners can't be a null ArrayList.");
        }
    }
    
    public final void addCurveDisplayerModeListener(CurveDisplayerModeListener listener)
    {
        if(listener != null)
        {
            if(!curveDisplayerModeListeners.contains(listener))
            {
                curveDisplayerModeListeners.add(listener);
            }
        }
        else
        {
            throw new IllegalArgumentException("You can't add a null CurveDisplayerModeListener.");
        }
    }
    
    public final void removeCurveDisplayerModeListener(CurveDisplayerModeListener listener)
    {
        curveDisplayerModeListeners.remove(listener);
    }
    
    private void fireCurveDisplayerModeChangeEvent()
    {
        CurveDisplayerModeChangeEvent e = new CurveDisplayerModeChangeEvent(
                this, manipulable, translatable, scalable);
        
        for(CurveDisplayerModeListener curveDisplayerModeListener : curveDisplayerModeListeners)
        {
            curveDisplayerModeListener.curveDisplayerModeChanged(e);
        }
    }
    
    public void fireCurveDisplayerModeChangeEvent(CurveDisplayerModeListener listener)
    {
        if(curveDisplayerModeListeners.contains(listener))
        {
            listener.curveDisplayerModeChanged(new CurveDisplayerModeChangeEvent(
                    this, manipulable, translatable, scalable));
        }
        else
        {
            throw new IllegalArgumentException("This listener is not added to the list of CurveDisplayerModeListener.");
        }
    }
    
    // other getters and setters
    public final StraightRectangle getCurrentFrame()
    {
        return zoomedFrame.getFrameInstance();
    }
    
    public final void setZoomedFrame(
            StraightRectangle frame, Vector2D center,
            int horZoomCount, double horZoomMultiplier,
            int vertZoomCount, double vertZoomMultiplier)
    {
        checkNullArg(frame, "frame");
        checkNullArg(center, "center");
        
        updateZoomedFrame(frame, center,
                horZoomCount, horZoomMultiplier,
                vertZoomCount, vertZoomMultiplier,
                false);
    }
    
    public final void setZoomedFrame(
            StraightRectangle frame, Vector2D center,
            int zoomCount, double zoomMultiplier)
    {
        setZoomedFrame(frame, center, zoomCount, zoomMultiplier, zoomCount, zoomMultiplier);
    }
    
    public final void setView(StraightRectangle frame, Vector2D center)
    {
        setZoomedFrame(frame, center, 0, horZoomMultiplier, 0, vertZoomMultiplier);
    }
    
    public final void setView(StraightRectangle frame)
    {
        setView(frame, frame != null ? frame.getCenter() : null);
    }
    
    public final int getHorZoomCount()
    {
        return horZoomCount;
    }
    
    public final void setHorZoomCount(int horZoomCount)
    {
        this.horZoomCount = horZoomCount;
        updateHorZoom();
        
        curveFrameChanged = true;
        
        if(curve != null)
        {
            curve.samplingNeeded = true;
            repaint();
        }
    }
    
    public final int getVertZoomCount()
    {
        return vertZoomCount;
    }
    
    public final void setVertZoomCount(int vertZoomCount)
    {
        this.vertZoomCount = vertZoomCount;
        updateVertZoom();
        
        curveFrameChanged = true;
        
        if(curve != null)
        {
            curve.samplingNeeded = true;
            repaint();
        }
    }
    
    public final void setZoomCount(int zoomCount)
    {
        this.horZoomCount = zoomCount;
        this.vertZoomCount = zoomCount;
        updateHorZoom();
        updateVertZoom();
        
        curveFrameChanged = true;
        
        if(curve != null)
        {
            curve.samplingNeeded = true;
            repaint();
        }
    }
    
    public final double getHorZoomMultiplier()
    {
        return horZoomMultiplier;
    }
    
    public final void setHorZoomMultiplier(double horZoomMultiplier)
    {
        this.horZoomMultiplier = horZoomMultiplier;
        updateHorZoom();
        
        curveFrameChanged = true;
        
        if(curve != null)
        {
            curve.samplingNeeded = true;
            repaint();
        }
    }
    
    public final double getVertZoomMultiplier()
    {
        return horZoomMultiplier;
    }
    
    public final void setVertZoomMultiplier(double vertZoomMultiplier)
    {
        this.vertZoomMultiplier = vertZoomMultiplier;
        updateVertZoom();
        
        curveFrameChanged = true;
        
        if(curve != null)
        {
            curve.samplingNeeded = true;
            repaint();
        }
    }
    
    public final Curve getCurve()
    {
        return curve;
    }
    
    public final void setCurve(Function2D function, Color color, boolean visible)
    {
        updateCurve(function, color, visible, false);
    }
    
    public final void setCurve(Function2D function, Color color)
    {
        setCurve(function, color, true);
    }
    
    public final void setCurve(Function2D function)
    {
        setCurve(function, Color.WHITE);
    }
    
    public final StraightRectangle getViewBounds()
    {
        return viewBounds;
    }
    
    public final void setViewBounds(StraightRectangle viewBounds)
    {
        this.viewBounds = viewBounds;
    }
    
    public final StraightRectangle getPointsBounds()
    {
        return pointsBounds;
    }
    
    public final void setPointsBounds(StraightRectangle pointsBounds)
    {
        this.pointsBounds = pointsBounds;
    }
    
    public final StraightRectangle getCurveBounds()
    {
        return curveBounds;
    }
    
    public final void setCurveBounds(StraightRectangle curveBounds)
    {
        this.curveBounds = curveBounds;
    }
    
    public final boolean isManipulable()
    {
        return manipulable;
    }
    
    public final void setManipulable(boolean manipulable)
    {   
        if(manipulable != this.manipulable)
        {
            if(!manipulable)
            {
                highlightedPoint = null;
                draggingPoint = false;
            }
            
            this.manipulable = manipulable;
            curveDisplayerModeChanged = true;
            repaint();
        }
    }
    
    public final boolean isTranslatable()
    {
        return translatable;
    }
    
    public final void setTranslatable(boolean translatable)
    {
        if(translatable != this.translatable)
        {
            this.translatable = translatable;
            fireCurveDisplayerModeChangeEvent();
        }
    }
    
    public final boolean isScalable()
    {
        return scalable;
    }
    
    public final void setScalable(boolean scalable)
    {
        if(scalable != this.scalable)
        {
            this.scalable = scalable;
            fireCurveDisplayerModeChangeEvent();
        }
    }
    
    // paint methods
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        preCurveDraw(g);
        
        if(curve != null && curve.visible && zoomedFrame != null)
        {
            if(curve.samplingNeeded || getWidth() != lastWidth || getHeight() != lastHeight)
            {
                lastWidth = getWidth();
                lastHeight = getHeight();
                curve.doSampling();
                curve.samplingNeeded = false;
            }
            
            Graphics2D g2D = (Graphics2D)g;
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2D.setStroke(new BasicStroke(4));
            g.setColor(Color.BLACK);
            drawPolylines(g);
            
            g2D.setStroke(new BasicStroke(2));
            g.setColor(curve.color);
            drawPolylines(g);
            
            if(curve.function instanceof ManipulableFunction && manipulable)
            {
                ManipulableFunction f = (ManipulableFunction)curve.function;
                
                for(ManipulablePoint point : f.points)
                {
                    int bigPoint = (point == highlightedPoint ? HIGHLIGHTED_POINT_SIZE : DEFAULT_POINT_SIZE),
                        mediumPoint = bigPoint - 2*FIRST_OUTLINE_THICKNESS,
                        littlePoint = mediumPoint - 2*SECOND_OUTLINE_THICKNESS,
                        pointX = getXScreenPos(point),
                        pointY = getYScreenPos(point);
                    
                    g2D.setColor(Color.BLACK);
                    g2D.fillOval(pointX-bigPoint/2, pointY-bigPoint/2, bigPoint, bigPoint);
                    g2D.setColor(Color.WHITE);
                    g2D.fillOval(pointX-mediumPoint/2, pointY-mediumPoint/2, mediumPoint, mediumPoint);
                    g2D.setColor(Color.BLACK);
                    g2D.fillOval(pointX-littlePoint/2, pointY-littlePoint/2, littlePoint, littlePoint);
                }
            }
        }
        
        if(curveChanged)
        {
            fireCurveChangeEvent();
            curveChanged = false;
        }
        
        if(curveFrameChanged)
        {
            fireCurveFrameChangeEvent();
            curveFrameChanged = false;
        }
        
        if(curveDisplayerModeChanged)
        {
            fireCurveDisplayerModeChangeEvent();
            curveDisplayerModeChanged = false;
        }
    }
    
    private void drawPolylines(Graphics g)
    {
        for(int i = 0; i < curve.XSequences.size(); i++)
        {
            g.drawPolyline(curve.XSequences.get(i), curve.YSequences.get(i),
                    curve.XSequences.get(i).length);
        }
    }
    
    protected void preCurveDraw(Graphics g) {}
    
    // graphical paramaters computation
    public final double getXResolution()
    {
        return getWidth()/zoomedFrame.getWidth();
    }
    
    public final double getYResolution()
    {
        return getHeight()/zoomedFrame.getHeight();
    }
    
    public final double getPixelWidth()
    {
        return zoomedFrame.getWidth()/getWidth();
    }
    
    public final double getPixelHeight()
    {
        return zoomedFrame.getHeight()/getHeight();
    }
    
    protected double convertXtoPixel(double x)
    {
        return (x - pixelXOffset*getPixelWidth() - zoomedFrame.getxMin())
                * getXResolution();
    }
    
    protected int convertXtoIntPixel(double x)
    {
        return (int)Math.round(convertXtoPixel(x));
    }
    
    protected double convertYtoPixel(double y)
    {
        return (zoomedFrame.getyMax() - pixelYOffset*getPixelHeight() - y)
                * getYResolution();
    }
    
    protected int convertYtoIntPixel(double y)
    {
        return (int)Math.round(convertYtoPixel(y));
    }
    
    protected IntPoint computeScreenPos(ManipulablePoint point)
    {
        checkNullArg(point, "point");
        return new IntPoint(convertXtoIntPixel(point.x), convertYtoIntPixel(point.y));
    }
    
    protected double convertXtoReal(int X)
    {
        return zoomedFrame.getxMin() + pixelXOffset*getPixelWidth()
                + X/getXResolution();
    }
    
    protected double convertYtoReal(int Y)
    {
        return zoomedFrame.getyMin() + pixelYOffset*getPixelHeight()
                + (getHeight()-1-Y)/getYResolution();
    }
    
    protected Vector2D convertToReal(IntPoint point)
    {
        checkNullArg(point, "point");
        return new Vector2D(convertXtoReal(point.getAbs()),
                convertYtoReal(point.getOrd()));
    }
    
    private static double computeZoom(int zoomCount, double zoomMultiplier)
    {
        return MoreMath.intPow(zoomMultiplier, zoomCount);
    }
    
    // MouseListener methods
    @Override
    public void mouseClicked(MouseEvent e)
    {
    }
    
    @Override
    public void mousePressed(MouseEvent e)
    {
        if(curve.function instanceof ManipulableFunction && manipulable)
        {
            ManipulableFunction f = (ManipulableFunction)curve.function;
            
            if(e.getButton() == MouseEvent.BUTTON3)
            {
                ManipulablePoint point = checkCollisionWithPoints(e.getX(), e.getY());
                
                if(point == null) // creating a new point
                {
                    IntPoint newPoint = getNearestConstrainedPoint(null, e.getX(), e.getY());
                    
                    if(newPoint != null)
                    {
                        curveChanged = true;
                        highlightedPoint = f.addPoint(convertToReal(newPoint));
                    }
                }
                else // removing an existant point
                {
                    curveChanged = true;
                    f.removePoint(point);
                    highlightedPoint = null;
                }
            }
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        if(draggingPoint && !contains(e.getPoint()))
        {
            highlightedPoint = null;
            repaint();
        }
        
        draggingPoint = false;
    }
    
    @Override
    public void mouseEntered(MouseEvent e)
    {
    }
    
    @Override
    public void mouseExited(MouseEvent e)
    {
        if(manipulable)
        {
            if(!draggingPoint)
            {
                highlightedPoint = null;
                repaint();
            }
        }
    }
    
    // MouseMotionListener methods
    @Override
    public void mouseDragged(MouseEvent e)
    {
        if(highlightedPoint != null)
        {
            ManipulableFunction f = (ManipulableFunction)curve.function;
            draggingPoint = true;
            IntPoint newPoint = getNearestConstrainedPoint(highlightedPoint, e.getX(), e.getY());
            
            curveChanged = true;
            
            if(newPoint != null)
            {
                f.movePoint(highlightedPoint, convertToReal(newPoint));
            }
            else
            {
                f.removePoint(highlightedPoint);
                highlightedPoint = null;
                draggingPoint = false;
            }
        }
        else if(translatable)
        {
            zoomedFrame.getCenter().add((lastMousePos.getAbs()-e.getX())/getXResolution(),
                    (e.getY()-lastMousePos.getOrd())/getYResolution());
            
            if(viewBounds != null)
            {
                if(zoomedFrame.getxMin() < viewBounds.getxMin())
                {
                    zoomedFrame.getCenter().setX(viewBounds.getxMin()+zoomedFrame.getWidth()/2);
                }
                else if(zoomedFrame.getxMax() > viewBounds.getxMax())
                {
                    zoomedFrame.getCenter().setX(viewBounds.getxMax()-zoomedFrame.getWidth()/2);
                }
                
                if(zoomedFrame.getyMin() < viewBounds.getyMin())
                {
                    zoomedFrame.getCenter().setY(viewBounds.getyMin()+zoomedFrame.getHeight()/2);
                }
                else if(zoomedFrame.getyMax() > viewBounds.getyMax())
                {
                    zoomedFrame.getCenter().setY(viewBounds.getyMax()-zoomedFrame.getHeight()/2);
                }
            }
            
            curveFrameChanged = true;
            
            if(curve != null)
            {
                curve.samplingNeeded = true;
                repaint();
            }
        }
        
        lastMousePos = new IntPoint(e.getX(), e.getY());
    }
    
    @Override
    public void mouseMoved(MouseEvent e)
    {
        if(curve.function instanceof ManipulableFunction && manipulable)
        {
            ManipulablePoint point = checkCollisionWithPoints(e.getX(), e.getY());
            
            if(point != highlightedPoint)
            {
                highlightedPoint = point;
                repaint();
            }
        }
        
        lastMousePos = new IntPoint(e.getX(), e.getY());
    }
    
    // MouseWheelListener method
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if(scalable)
        {
            if(e.isControlDown())
            {
                int DX;
                
                if(highlightedPoint != null)
                {
                    DX = convertXtoIntPixel(highlightedPoint.getX())-getWidth()/2;
                }
                else
                {
                    DX = e.getX()-getWidth()/2;
                }
                
                double Dx1 = DX/getXResolution();
                horZoomCount -= e.getWheelRotation();
                zoomedFrame.setHorZoom(computeZoom(horZoomCount, horZoomMultiplier));
                double Dx2 = DX/getXResolution();
                zoomedFrame.getCenter().addDx(Dx1-Dx2);
                
                if(viewBounds != null)
                {
                    if(zoomedFrame.getWidth() > viewBounds.getWidth())
                    {
                        zoomedFrame.setHorZoom(zoomedFrame.getInitialFrame().getWidth()/viewBounds.getWidth());
                        zoomedFrame.getCenter().setX(viewBounds.getCenter().getX());
                        horZoomCount = (int)Math.floor(Math.log(zoomedFrame.getInitialFrame().getWidth()/zoomedFrame.getWidth())
                        / Math.log(horZoomMultiplier));
                    }
                    else
                    {
                        if(zoomedFrame.getxMin() < viewBounds.getxMin())
                        {
                            zoomedFrame.getCenter().setX(viewBounds.getxMin()+zoomedFrame.getWidth()/2);
                        }
                        else if(zoomedFrame.getxMax() > viewBounds.getxMax())
                        {
                            zoomedFrame.getCenter().setX(viewBounds.getxMax()-zoomedFrame.getWidth()/2);
                        }
                    }
                }
            }
            else
            {
                int DY;
                
                if(highlightedPoint != null)
                {
                    DY = getHeight()/2-convertYtoIntPixel(highlightedPoint.getY());
                }
                else
                {
                    DY = getHeight()/2-e.getY();
                }
                
                double Dy1 = DY/getYResolution();
                vertZoomCount -= e.getWheelRotation();
                zoomedFrame.setVertZoom(computeZoom(vertZoomCount, vertZoomMultiplier));
                double Dy2 = DY/getYResolution();
                zoomedFrame.getCenter().addDy(Dy1-Dy2);
                
                if(viewBounds != null)
                {
                    if(zoomedFrame.getHeight() > viewBounds.getHeight())
                    {
                        zoomedFrame.setVertZoom(zoomedFrame.getInitialFrame().getHeight()/viewBounds.getHeight());
                        zoomedFrame.getCenter().setY(viewBounds.getCenter().getY());
                        vertZoomCount = (int)Math.floor(Math.log(zoomedFrame.getInitialFrame().getHeight()/zoomedFrame.getHeight())
                        / Math.log(vertZoomMultiplier));
                    }
                    else
                    {
                        if(zoomedFrame.getyMin() < viewBounds.getyMin())
                        {
                            zoomedFrame.getCenter().setY(viewBounds.getyMin()+zoomedFrame.getHeight()/2);
                        }
                        else if(zoomedFrame.getyMax() > viewBounds.getyMax())
                        {
                            zoomedFrame.getCenter().setY(viewBounds.getyMax()-zoomedFrame.getHeight()/2);
                        }
                    }
                }
            }
            
            curveFrameChanged = true;
            
            if(curve != null)
            {
                curve.samplingNeeded = true;
                repaint();
            }
        }
    }
    
    // private functional methods
    private ManipulablePoint checkCollisionWithPoints(int X, int Y)
    {
        ManipulableFunction f = (ManipulableFunction)curve.function;
        int i;
        
        for(i = 0; i < f.getN(); i++)
        {
            ManipulablePoint point = f.getPoint(i);
            int pointX = getXScreenPos(point),
                pointY = getYScreenPos(point);
            
            if(X >= pointX-POINT_HIT_BOX_SIZE/2 && X <= pointX+POINT_HIT_BOX_SIZE/2
            && Y >= pointY-POINT_HIT_BOX_SIZE/2 && Y <= pointY+POINT_HIT_BOX_SIZE/2)
            {
                return point;
            }
        }
        
        return null;
    }
    
    private IntPoint getNearestConstrainedPoint(ManipulablePoint movingPoint, int newX, int newY)
    {
        IntFrame pointsArea = new IntFrame(0, 0, getWidth()-1, getHeight()-1);
        
        if(pointsBounds != null)
        {
            pointsArea = pointsArea.getIntersection(
                    toPixels(pointsBounds).getNearestInsideIntFrame());
        }
        
        if(pointsArea != null)
        {   
            IntPoint newPoint = pointsArea.getNearestInsidePoint(newX, newY);
            newX = newPoint.getAbs();
            
            if(checkVerticalPointPresence(movingPoint, newX)) // pixel abscissa already taken
            {
                Integer leftShift = 0,
                        rightShift = 0;
                
                do
                {
                    leftShift++;
                    
                    if(newX-leftShift < pointsArea.getxMin())
                    {
                        leftShift = null;
                        break;
                    }
                } while(checkVerticalPointPresence(movingPoint, newX-leftShift));
                
                do
                {
                    rightShift++;
                    
                    if(newX+rightShift > pointsArea.getxMax())
                    {
                        rightShift = null;
                        break;
                    }
                } while(checkVerticalPointPresence(movingPoint, newX+rightShift));
                
                if(leftShift != null && rightShift != null)
                {
                    newPoint.addDX(rightShift <= leftShift ? rightShift : -leftShift);
                }
                else if(leftShift != null)
                {
                    newPoint.addDX(-leftShift);
                }
                else if(rightShift != null)
                {
                    newPoint.addDX(rightShift);
                }
                else
                {
                    return null;
                }
            }
            
            return newPoint;
        }
        else
        {
            return null;
        }
    }
    
    private boolean checkVerticalPointPresence(ManipulablePoint movingPoint, int X)
    {
        ManipulableFunction f = (ManipulableFunction)curve.function;
        
        for(ManipulablePoint p : f.points)
        {
            if(p != movingPoint && getXScreenPos(p) == X)
            {
                return true;
            }
        }
        
        return false;
    }
    
    private StraightRectangle toPixels(StraightRectangle frame)
    {
        return new StraightRectangle(
                convertXtoPixel(frame.getxMin()),
                convertYtoPixel(frame.getyMax()),
                convertXtoPixel(frame.getxMax()),
                convertYtoPixel(frame.getyMin()));
    }
    
    // inner class
    public class Curve implements CurveListener
    {
        private Function2D function;
        private Color color;
        private boolean visible;
        private final ArrayList<int[]> XSequences, YSequences;
        private boolean samplingNeeded;
        
        public Curve(Function2D function, Color color, boolean visible)
        {
            checkAndUpdateFunction(function);
            function.addCurveListener(this);
            checkAndUpdateColor(color);
            this.visible = visible;
            XSequences = new ArrayList();
            YSequences = new ArrayList();
            samplingNeeded = true;
        }
        
        private void checkAndUpdateFunction(Function2D function)
        {
            this.function = checkNullArg(function, "function");
        }
        
        private void checkAndUpdateColor(Color color)
        {
            this.color = checkNullArg(color, "color");
        }
        
        public final Function2D getFunction() 
        {
            return function;
        }
        
        public final void setFunction(Function2D function)
        {
            if(function != this.function)
            {
                checkAndUpdateFunction(function);
                samplingNeeded = true;
                curveChanged = true;
                repaint();
            }
        }
        
        public final Color getColor()
        {
            return color;
        }
        
        public final void setColor(Color color)
        {
            if(color != this.color)
            {
                checkAndUpdateColor(color);
                repaint();
            }
        }
        
        public final boolean isVisible()
        {
            return visible;
        }
        
        public final void setVisible(boolean visible)
        {
            if(visible != this.visible)
            {
                this.visible = visible;
                repaint();
            }
        }
        
        private void doSampling()
        {
            StraightRectangle curveArea = zoomedFrame.getFrameInstance();
            
            if(curveBounds != null)
            {
                curveArea = curveArea.getIntersection(curveBounds);
            }
            
            XSequences.clear();
            YSequences.clear();
            
            if(curveArea != null)
            {
                double pixelWidth = getPixelWidth(),
                       xOffset = CurveDisplayer.this.pixelXOffset*pixelWidth;
                int startIndex = MoreMath.div(
                        curveArea.getxMin()-zoomedFrame.getxMin()+xOffset,
                        pixelWidth),
                    endIndex = MoreMath.div(
                        curveArea.getxMax()-zoomedFrame.getxMin()+xOffset,
                        pixelWidth) - 1;
                double x = zoomedFrame.getxMin()+xOffset+startIndex*pixelWidth;
                
                mainLoop : for(int i = startIndex; i <= endIndex; i++)
                {
                    Double eval;
                    
                    if(curveBounds != null)
                    {
                        StraightRectangle bounds = curveBounds;
                        
                        while((eval = function.eval(x)) == null
                        || eval < bounds.getyMin() || eval > bounds.getyMax())
                        {
                            i++;
                            
                            if(i > endIndex)
                            {
                                break mainLoop;
                            }
                            
                            x += pixelWidth;
                        }
                    }
                    else
                    {
                        while((eval = function.eval(x)) == null)
                        {
                            i++;
                            
                            if(i > endIndex)
                            {
                                break mainLoop;
                            }
                            
                            x += pixelWidth;
                        }
                    }
                    
                    ArrayList<Integer> XPoints = new ArrayList(),
                                       YPoints = new ArrayList();
                    
                    if(curveBounds != null)
                    {
                        StraightRectangle bounds = curveBounds;
                        
                        do
                        {
                            XPoints.add(i);
                            YPoints.add(convertYtoIntPixel(eval));
                            i++;
                            
                            if(i > endIndex)
                            {
                                break;
                            }
                            
                            x += pixelWidth;
                        } while((eval = function.eval(x)) != null
                        && eval >= bounds.getyMin() && eval <= bounds.getyMax());
                    }
                    else
                    {
                        do
                        {
                            XPoints.add(i);
                            YPoints.add(convertYtoIntPixel(eval));
                            i++;
                            
                            if(i > endIndex)
                            {
                                break;
                            }
                            
                            x += pixelWidth;
                        } while((eval = function.eval(x)) != null);
                    }
                    
                    XSequences.add(toIntArray(XPoints));
                    YSequences.add(toIntArray(YPoints));
                }
            }
            
            if(curve.function instanceof ManipulableFunction)
            {
                ManipulableFunction f = (ManipulableFunction)curve.function;
                
                for(ManipulablePoint point : f.points)
                {
                    setScreenPos(point, computeScreenPos(point));
                }
            }
        }
        
        @Override
        public void curveChanged(ChangeEvent e)
        {
            if(e.getSource() == function)
            {
                samplingNeeded = true;
                repaint();
            }
        }
    }
    
    IntPoint getScreenPos(ManipulablePoint point)
    {
        return point.getScreenPos(this);
    }
    
    public int getXScreenPos(ManipulablePoint point)
    {
        return point.getXScreenPos(this);
    }
    
    public int getYScreenPos(ManipulablePoint point)
    {
        return point.getYScreenPos(this);
    }
    
    void setScreenPos(ManipulablePoint point, IntPoint screenPos)
    {
        point.setScreenPos(this, screenPos);
    }
    
    void setXScreenPos(ManipulablePoint point, int X)
    {
        point.setXScreenPos(this, X);
    }
    
    void setYScreenPos(ManipulablePoint point, int Y)
    {
        point.setYScreenPos(this, Y);
    }
}
