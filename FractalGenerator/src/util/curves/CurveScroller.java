/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.curves;

import geometry.StraightRectangle;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import javax.swing.BoundedRangeModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import mandelbrot.config.ZoomedFrame;
import mandelbrot.gui.GodlikePanel;
import mandelbrot.gui.ProGUI;
import mandelbrot.gui.events.CurveDisplayerModeChangeEvent;
import mandelbrot.gui.events.CurveFrameChangeEvent;
import mandelbrot.gui.listeners.CurveDisplayerModeListener;
import mandelbrot.gui.listeners.CurveFrameListener;
import static util.MoreGenericity.checkNullArg;

/**
 * @author Raphaël
**/

public class CurveScroller extends GodlikePanel implements CurveFrameListener,
        AdjustmentListener, CurveDisplayerModeListener
{
    protected static final int RULE_SIZE = 60,
                               SCROLLBAR_SIZE = 20;
    protected static final ArrayList<RuleTick> DEFAULT_TICKS = new ArrayList();
    
    static
    {
        DEFAULT_TICKS.add(new RuleTick(1, 10, false));
        DEFAULT_TICKS.add(new RuleTick(10, 16, true));
    }
    
    protected JPanel bottomLeftCorner;
    protected JPanel bottomRightCorner;
    protected CurveDisplayer displayer;
    protected JPanel upperLeftCorner;
    protected JPanel upperRightCorner;
    protected Rule xRule;
    protected JScrollBar xScrollBar;
    protected Rule yRule;
    protected JScrollBar yScrollBar;
    
    public CurveScroller(CurveDisplayer displayer, StraightRectangle viewBounds, String xAxisName, String yAxisName)
    {
        checkNullArg(displayer, "displayer");
        this.displayer = displayer;
        displayer.addCurveFrameListener(this);
        displayer.addCurveDisplayerModeListener(this);
        checkNullArg(viewBounds, "viewBounds");
        displayer.viewBounds = viewBounds;
        
        checkNullArg(xAxisName, "xAxisName");
        xRule = new Rule(DEFAULT_TICKS,
                Rule.ORIENTATION_TOP, Rule.DIRECTION_LEFT_RIGHT, xAxisName);
        
        checkNullArg(yAxisName, "yAxisName");
        yRule = new Rule(DEFAULT_TICKS,
                Rule.ORIENTATION_RIGHT, Rule.DIRECTION_RIGHT_LEFT, yAxisName);
        
        bottomLeftCorner = new JPanel();
        bottomRightCorner = new JPanel();
        upperLeftCorner = new JPanel();
        upperRightCorner = new JPanel();
        
        xScrollBar = new JScrollBar();
        xScrollBar.setOrientation(JScrollBar.HORIZONTAL);
        xScrollBar.setUnitIncrement(1);
        xScrollBar.addAdjustmentListener(this);
        
        yScrollBar = new JScrollBar();
        yScrollBar.setOrientation(JScrollBar.VERTICAL);
        yScrollBar.setUnitIncrement(1);
        yScrollBar.addAdjustmentListener(this);
        
        setName("parent");
        displayer.setName("manipulator");
        xRule.setName("xRule");
        yRule.setName("yRule");
        bottomLeftCorner.setName("bottomLeftCorner");
        bottomRightCorner.setName("bottomRightCorner");
        upperLeftCorner.setName("upperLeftCorner");
        upperRightCorner.setName("upperRightCorner");
        xScrollBar.setName("xScrollBar");
        yScrollBar.setName("yScrollBar");
        
        addComponent(bottomLeftCorner);
        bottomLeftCorner.setBackground(Color.LIGHT_GRAY);
        addConstraint(this, bottomLeftCorner, Border.LEFT, Border.LEFT);
        addConstraint(this, bottomLeftCorner, Border.BOTTOM, Border.BOTTOM);
        setFixedWidth(bottomLeftCorner, RULE_SIZE);
        setFixedHeight(bottomLeftCorner, RULE_SIZE);
        
        addComponent(upperRightCorner);
        upperRightCorner.setBackground(Color.LIGHT_GRAY);
        addConstraint(this, upperRightCorner, Border.RIGHT, Border.RIGHT);
        addConstraint(this, upperRightCorner, Border.TOP, Border.TOP);
        setFixedWidth(upperRightCorner, SCROLLBAR_SIZE);
        setFixedHeight(upperRightCorner, SCROLLBAR_SIZE);
        
        addComponent(bottomRightCorner);
        bottomRightCorner.setBackground(Color.LIGHT_GRAY);
        addConstraint(this, bottomRightCorner, Border.RIGHT, Border.RIGHT);
        addConstraint(this, bottomRightCorner, Border.BOTTOM, Border.BOTTOM);
        addConstraint(bottomLeftCorner, bottomRightCorner, Border.TOP, Border.TOP);
        addConstraint(upperRightCorner, bottomRightCorner, Border.LEFT, Border.LEFT);
        
        addComponent(upperLeftCorner);
        upperLeftCorner.setBackground(Color.LIGHT_GRAY);
        addConstraint(this, upperLeftCorner, Border.LEFT, Border.LEFT);
        addConstraint(this, upperLeftCorner, Border.TOP, Border.TOP);
        addConstraint(bottomLeftCorner, upperLeftCorner, Border.RIGHT, Border.RIGHT);
        addConstraint(upperRightCorner, upperLeftCorner, Border.BOTTOM, Border.BOTTOM);
        
        addComponent(xRule);
        xRule.setBackground(Color.CYAN);
        addConstraint(this, xRule, Border.BOTTOM, Border.BOTTOM);
        addConstraint(bottomLeftCorner, xRule, Border.RIGHT, Border.LEFT);
        addConstraint(upperRightCorner, xRule, Border.LEFT, Border.RIGHT);
        addConstraint(bottomLeftCorner, xRule, Border.TOP, Border.TOP);
        
        addComponent(yRule);
        yRule.setBackground(Color.CYAN);
        addConstraint(this, yRule, Border.LEFT, Border.LEFT);
        addConstraint(upperRightCorner, yRule, Border.BOTTOM, Border.TOP);
        addConstraint(bottomLeftCorner, yRule, Border.TOP, Border.BOTTOM);
        addConstraint(bottomLeftCorner, yRule, Border.RIGHT, Border.RIGHT);
        
        addComponent(xScrollBar);
        addConstraint(this, xScrollBar, Border.TOP, Border.TOP);
        addConstraint(upperRightCorner, xScrollBar, Border.LEFT, Border.RIGHT);
        addConstraint(bottomLeftCorner, xScrollBar, Border.RIGHT, Border.LEFT);
        addConstraint(upperRightCorner, xScrollBar, Border.BOTTOM, Border.BOTTOM);
        
        addComponent(yScrollBar);
        addConstraint(this, yScrollBar, Border.RIGHT, Border.RIGHT);
        addConstraint(upperRightCorner, yScrollBar, Border.LEFT, Border.LEFT);
        addConstraint(bottomLeftCorner, yScrollBar, Border.TOP, Border.BOTTOM);
        addConstraint(upperRightCorner, yScrollBar, Border.BOTTOM, Border.TOP);
        
        addComponent(displayer);
        displayer.setBackground(Color.ORANGE);
        addConstraint(bottomLeftCorner, displayer, Border.RIGHT, Border.LEFT);
        addConstraint(bottomLeftCorner, displayer, Border.TOP, Border.BOTTOM);
        addConstraint(upperRightCorner, displayer, Border.LEFT, Border.RIGHT);
        addConstraint(upperRightCorner, displayer, Border.BOTTOM, Border.TOP);
    }
    
    public CurveScroller(CurveDisplayer displayer, StraightRectangle viewBounds)
    {
        this(displayer, viewBounds, "", "");
    }
    
    public CurveScroller()
    {
        this(new CurveDisplayer(), StraightRectangle.getInfiniteFrame());
    }
    
    public static void main(String[] args)
    {
        try
        {
            for(javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch(ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(ProGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        JFrame f = new JFrame("Test CurveEditor");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        Spline s = new Spline(new double[]{1,2,3,4,5,6,7,8,8.5,9,9.5}, new double[]{2,5,2,4,3,7,4,2,5,7,6}, new Integer[]{3,1,3,1,3,1,3,1,3,1});
        CurveScroller scroller = new CurveScroller(new CurveDisplayer(new StraightRectangle(0, 0, 10, 10), s), new StraightRectangle(-10, -10, 20, 20), "Temps (s)", "Vitesse de zoom");
        scroller.setPreferredSize(new Dimension(1000, 400));
        //scroller.getDisplayer().setPointsBounds(new StraightRectangle(2, 2, 8, 8));
        //scroller.getDisplayer().setCurveBounds(new StraightRectangle(0, 2, 10, 6));
        scroller.getDisplayer().setTranslatable(true);
        scroller.getDisplayer().setScalable(true);
        f.setContentPane(scroller);
        //scroller.setDebugMode(true);
        f.pack();
        f.setVisible(true);
    }
    
    @Override
    public void curveFrameChanged(CurveFrameChangeEvent e)
    {
        if(e.getFrame() != null)
        {
            StraightRectangle newFrame = e.getFrame();
            xRule.min = newFrame.getxMin();
            xRule.max = newFrame.getxMax();
            yRule.min = newFrame.getyMin();
            yRule.max = newFrame.getyMax();
            xRule.repaint();
            yRule.repaint();
            
            double resolution = displayer.getXResolution();
            int min = (int)Math.ceil(displayer.getViewBounds().getxMin()*resolution),
                max = (int)Math.floor(displayer.getViewBounds().getxMax()*resolution),
                extent = displayer.getWidth(),
                value = Math.min(Math.max((int)Math.round(newFrame.getxMin()*resolution), min), max);
            xScrollBar.setEnabled(true);
            xScrollBar.setValues(value, extent, min, max);
            
            resolution = displayer.getYResolution();
            min = (int)Math.ceil(displayer.getViewBounds().getyMin()*resolution);
            max = (int)Math.floor(displayer.getViewBounds().getyMax()*resolution);
            extent = displayer.getHeight();
            value = Math.min(Math.max((int)Math.round(newFrame.getyMin()*resolution), min), max);
            yScrollBar.setEnabled(true);
            yScrollBar.setValues(max-(value+extent-min), extent, min, max);
        }
        else
        {
            xRule.minInitialized = false;
            xRule.maxInitialized = false;
            
            yRule.minInitialized = false;
            yRule.maxInitialized = false;
            
            xRule.repaint();
            yRule.repaint();
            
            xScrollBar.setEnabled(false);
            yScrollBar.setEnabled(false);
        }
    }
    
    public String getxAxisName()
    {
        return xRule.mainLabel;
    }
    
    public void setxAxisName(String xAxisName)
    {
        xRule.setMainLabel(xAxisName);
    }
    
    public String getyAxisName()
    {
        return yRule.mainLabel;
    }
    
    public void setyAxisName(String yAxisName)
    {
        yRule.setMainLabel(yAxisName);
    }
    
    public CurveDisplayer getDisplayer()
    {
        return displayer;
    }
    
    @Override
    public void adjustmentValueChanged(AdjustmentEvent e)
    {
        if(((JScrollBar)e.getAdjustable()).getValueIsAdjusting())
        {
            ZoomedFrame zoomedFrame = displayer.zoomedFrame;
            BoundedRangeModel m = ((JScrollBar)e.getAdjustable()).getModel();
            int min = m.getMinimum(),
                max = m.getMaximum(),
                extent = m.getExtent(),
                value = m.getValue();

            if(e.getAdjustable() == xScrollBar)
            {
                double newXVal;
                
                if(value == min)
                {
                    newXVal = displayer.getViewBounds().getxMin()+zoomedFrame.getWidth()/2;
                }
                else if(value == max)
                {
                    newXVal = displayer.getViewBounds().getxMax()-zoomedFrame.getWidth()/2;
                }
                else
                {
                    newXVal = value/displayer.getXResolution()+zoomedFrame.getWidth()/2;
                }
                
                zoomedFrame.getCenter().setX(newXVal);
                xRule.setMin(zoomedFrame.getxMin());
                xRule.setMax(zoomedFrame.getxMax());
                xRule.repaint();
            }
            else
            {
                double newYVal;
                value = max-(value+extent-min);
                
                if(value == min)
                {
                    newYVal = displayer.getViewBounds().getyMin()+zoomedFrame.getHeight()/2;
                }
                else if(value == max)
                {
                    newYVal = displayer.getViewBounds().getyMax()-zoomedFrame.getHeight()/2;
                }
                else
                {
                    newYVal = value/displayer.getYResolution()+zoomedFrame.getHeight()/2;
                }
                
                zoomedFrame.getCenter().setY(newYVal);
                yRule.setMin(zoomedFrame.getyMin());
                yRule.setMax(zoomedFrame.getyMax());
                yRule.repaint();
            }
            
            displayer.repaint();
        }
    }
    
    @Override
    public void curveDisplayerModeChanged(CurveDisplayerModeChangeEvent e)
    {
        xScrollBar.setEnabled(e.isTranslatable());
        yScrollBar.setEnabled(e.isTranslatable());
    }
}