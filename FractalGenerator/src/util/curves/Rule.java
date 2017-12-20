/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.curves;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.util.ArrayList;
import util.MoreMath;

/**
 * @author Raphaël
**/

public class Rule extends JPanel
{
    public static final int ORIENTATION_RIGHT = 0, ORIENTATION_TOP = 1,
            ORIENTATION_LEFT = 2, ORIENTATION_BOTTOM = 3;
    public static final int DIRECTION_LEFT_RIGHT = 0, DIRECTION_RIGHT_LEFT = 1;
    public static final int ALIGNMENT_LEFT = 0, ALIGNMENT_CENTER = 1,
            ALIGNMENT_RIGHT = 2;
    
    double min, max; // real min and max values
    double unit; // real value of a unit
    ArrayList<RuleTick> ticks; /* array of ticks to paint - The last 
    added has the highest priority */
    int ticksOrientation;
    int ticksDirection;
    String mainLabel;
    int mainLabelAlignment;
    int spaceBetweenMainLabelAndBorder;
    Font mainLabelFont;
    Color mainLabelColor;
    boolean minInitialized, maxInitialized;
    
    ArrayList<JPanel> gridDisplayers;
    
    public Rule(double min, double max, ArrayList<RuleTick> ticks,
            int orientation, int direction, String mainLabel)
    {
        minInitialized = MoreMath.isFiniteValue(min);
        maxInitialized = MoreMath.isFiniteValue(max);
        
        this.min = min;
        this.max = max;
        unit = 1;
        this.ticks = ticks;
        ticksOrientation = orientation;
        ticksDirection = direction;
        this.mainLabel = mainLabel;
        mainLabelAlignment = ALIGNMENT_CENTER;
        spaceBetweenMainLabelAndBorder = 4;
        mainLabelFont = new Font("SansSerif", Font.PLAIN, 10);
        mainLabelColor = Color.BLACK;
        minInitialized = true;
        maxInitialized = true;
        
        gridDisplayers = new ArrayList();
    }
    
    public Rule(double min, double max, int orientation, int direction, String mainLabel)
    {
        this(min, max, new ArrayList(), orientation, direction, mainLabel);
    }
    
    public Rule(ArrayList<RuleTick> ticks, int orientation, int direction, String mainLabel)
    {
        this(Double.NaN, Double.NaN, ticks, orientation, direction, mainLabel);
    }
    
    public Rule(int orientation, int direction, String mainLabel)
    {
        this(new ArrayList(), orientation, direction, mainLabel);
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        if(minInitialized && maxInitialized)
        {
            ArrayList<RuleTick> displayedTicks = (ArrayList)ticks.clone();
            double extent = max-min;
            int size = getMainSize();
            double unitInPixel = unit/extent*size;
            
            for(RuleTick tick : ticks)
            {
                if((int)Math.floor(unitInPixel*tick.getIncrement()) < tick.getMinDistForVisibility())
                {
                    displayedTicks.remove(tick);
                }
            }
            
            ArrayList<Double>[] ticksRealPosition = new ArrayList[displayedTicks.size()];
            ArrayList<Integer>[] ticksPosition = new ArrayList[displayedTicks.size()];
            
            for(int i = displayedTicks.size()-1; i > -1; i--)
            {
                RuleTick tick = displayedTicks.get(i);
                int firstTickIndex = (int)Math.floor((min-getRealIncrementOffset(tick))
                        /getRealIncrement(tick)),
                    lastTickIndex = (int)Math.ceil((max-getRealIncrementOffset(tick))
                            /getRealIncrement(tick)),
                    nbrOfTicks = lastTickIndex-firstTickIndex+1;
                ArrayList<Double> realPositions = new ArrayList();
                ArrayList<Integer> positions = new ArrayList();
                
                for(int j = 0; j < nbrOfTicks; j++)
                {
                    double realPos = (firstTickIndex+j)*getRealIncrement(tick)
                            +getRealIncrementOffset(tick);
                    realPositions.add(realPos);
                    positions.add((int)Math.round((realPos-min)/extent*size));
                }
                
                ticksRealPosition[i] = realPositions;
                ticksPosition[i] = positions;
            }
            
            for(int i = displayedTicks.size()-1; i > 0; i--)
            {
                for(int j = 0; j < ticksPosition[i].size(); j++)
                {
                    int posToDelete = ticksPosition[i].get(j);
                    
                    for(int k = i-1; k > -1; k--)
                    {
                        int indexToDelete = ticksPosition[k].indexOf(posToDelete);
                        
                        if(indexToDelete > -1)
                        {
                            ticksRealPosition[k].remove(indexToDelete);
                            ticksPosition[k].remove(indexToDelete);
                        }
                    }
                }
            }
            // to review. The complexity is n^4
            
            boolean reversePos =
               ticksOrientation == ORIENTATION_RIGHT  && ticksDirection == DIRECTION_RIGHT_LEFT
            || ticksOrientation == ORIENTATION_TOP    && ticksDirection == DIRECTION_RIGHT_LEFT
            || ticksOrientation == ORIENTATION_LEFT   && ticksDirection == DIRECTION_LEFT_RIGHT
            || ticksOrientation == ORIENTATION_BOTTOM && ticksDirection == DIRECTION_LEFT_RIGHT;
            
            for(int i = 0; i < displayedTicks.size(); i++)
            {
                RuleTick tick = displayedTicks.get(i);
                ArrayList<Double> realPositions = ticksRealPosition[i];
                ArrayList<Integer> positions = ticksPosition[i];
                
                for(int j = 0; j < positions.size(); j++)
                {
                    int pos = reversePos ? size-1-positions.get(j) : positions.get(j);
                    
                    drawTick(g, tick, pos);
                    
                    if(tick.isLabelled())
                    {
                        String label = Double.toString(MoreMath.round(realPositions.get(j), 10));
                        drawLabel(g, label, tick, pos);
                    }
                    
                    for(JPanel p : gridDisplayers)
                    {
                        Graphics dispGraph = p.getGraphics();
                        g.drawLine(j, j, j, j);
                    }
                }
            }
            
            drawMainLabel(g);
        }
    }
    
    protected void drawTick(Graphics g, RuleTick tick, int directPos)
    {
        int width = getWidth(), height = getHeight(),
            length = tick.getGraphicLength();
        
        g.setColor(tick.getTickColor());
        
        switch(ticksOrientation)
        {
            case ORIENTATION_RIGHT :
                g.drawLine(width-length, directPos, width-1, directPos);
                break;
            case ORIENTATION_TOP :
                g.drawLine(directPos, 0, directPos, length-1);
                break;
            case ORIENTATION_LEFT :
                g.drawLine(0, directPos, length-1, directPos);
                break;
            default :
                g.drawLine(directPos, height-length, directPos, height-1);
                break;
        }
    }
    
    protected void drawLabel(Graphics g, String text, RuleTick tick, int directPos)
    {
        int width = getWidth(), height = getHeight(),
            length = tick.getGraphicLength(), space = tick.getSpaceBetweenLabelAndTick(),
            textWidth = getFontMetrics(tick.getLabelFont()).stringWidth(text),
            textHeight = (int)(getFontMetrics(tick.getLabelFont()).getAscent()*0.7);
        
        g.setFont(tick.getLabelFont());
        g.setColor(tick.getLabelColor());
        
        switch(ticksOrientation)
        {
            case ORIENTATION_RIGHT :
                g.drawChars(text.toCharArray(), 0, text.length(),
                        width-length-space-textWidth,
                        directPos+textHeight/2);
                break;
            case ORIENTATION_TOP :
                g.drawChars(text.toCharArray(), 0, text.length(),
                        directPos-textWidth/2,
                        length+space+textHeight-1);
                break;
            case ORIENTATION_LEFT :
                g.drawChars(text.toCharArray(), 0, text.length(),
                        length+space,
                        directPos+textHeight/2);
                break;
            default :
                g.drawChars(text.toCharArray(), 0, text.length(),
                        directPos-textWidth/2,
                        height-length-space-1);
                break;
        }
    }
    
    protected void drawMainLabel(Graphics g)
    {
        g.setFont(mainLabelFont);
        g.setColor(mainLabelColor);
        Rectangle2D textBounds = getFontMetrics(mainLabelFont).getStringBounds(mainLabel, g);
        int textWidth = (int)Math.round(textBounds.getWidth()),
            textHeight = (int)Math.round(textBounds.getHeight()),
            ruleSize = getMainSize(), labelMainPos;
        
        switch(mainLabelAlignment)
        {
            case ALIGNMENT_LEFT :
                labelMainPos = spaceBetweenMainLabelAndBorder+textWidth/2;
                break;
            case ALIGNMENT_CENTER :
                labelMainPos = ruleSize/2;
                break;
            default :
                labelMainPos = ruleSize-spaceBetweenMainLabelAndBorder-textWidth/2;
        }
        
        switch(ticksOrientation)
        {
            case ORIENTATION_RIGHT :
                labelMainPos = ruleSize-labelMainPos;
                g.translate(spaceBetweenMainLabelAndBorder+textHeight/2, labelMainPos);
                ((Graphics2D)g).rotate(-Math.PI/2);
                g.drawChars(mainLabel.toCharArray(), 0, mainLabel.length(),
                        -textWidth/2, textHeight/2);
                ((Graphics2D)g).rotate(Math.PI/2);
                g.translate(-(spaceBetweenMainLabelAndBorder+textHeight/2), -labelMainPos);
                break;
            case ORIENTATION_TOP :
                g.drawChars(mainLabel.toCharArray(), 0, mainLabel.length(),
                        labelMainPos-textWidth/2, getHeight()-1-spaceBetweenMainLabelAndBorder);
                break;
            case ORIENTATION_LEFT :
                g.translate(getWidth()-1-spaceBetweenMainLabelAndBorder-textHeight/2, labelMainPos);
                ((Graphics2D)g).rotate(Math.PI/2);
                g.drawChars(mainLabel.toCharArray(), 0, mainLabel.length(),
                        -textWidth/2, textHeight/2);
                ((Graphics2D)g).rotate(-Math.PI/2);
                g.translate(-(getWidth()-1-spaceBetweenMainLabelAndBorder-textHeight/2), -labelMainPos);
                break;
            case ORIENTATION_BOTTOM :
                g.drawChars(mainLabel.toCharArray(), 0, mainLabel.length(),
                        labelMainPos-textWidth/2, spaceBetweenMainLabelAndBorder+textHeight);
                break;
        }
    }
    
    public double getMin()
    {
        return min;
    }
    
    public void setMin(double min)
    {   
        if(maxInitialized && min > max)
        {
            throw new IllegalArgumentException("min can't be superior to max.");
        }
        
        this.min = min;
        minInitialized = true;
        
        if(maxInitialized)
        {
            repaint();
        }
    }
    
    public double getMax()
    {
        return max;
    }
    
    public void setMax(double max)
    {
        if(minInitialized && max < min)
        {
            throw new IllegalArgumentException("max can't be inferior to min.");
        }
        
        this.max = max;
        maxInitialized = true;
        
        if(minInitialized)
        {
            repaint();
        }
    }
    
    public void setBounds(double min, double max)
    {
        if(min > max)
        {
            throw new IllegalArgumentException("min can't be superior to max.");
        }
        
        this.min = min;
        this.max = max;
        minInitialized = true;
        maxInitialized = true;
        repaint();
    }
    
    public double getUnit()
    {
        return unit;
    }
    
    public void setUnit(double unit)
    {
        this.unit = unit;
        repaint();
    }
    
    public ArrayList<RuleTick> getTicks()
    {
        return ticks;
    }
    
    public void setTicks(ArrayList<RuleTick> ticks)
    {
        this.ticks = ticks;
        repaint();
    }
    
    public int getTicksOrientation()
    {
        return ticksOrientation;
    }
    
    public void setTicksOrientation(int ticksOrientation)
    {
        this.ticksOrientation = ticksOrientation;
        repaint();
    }
    
    public int getTicksDirection()
    {
        return ticksDirection;
    }
    
    public void setTicksDirection(int ticksDirection)
    {
        this.ticksDirection = ticksDirection;
        repaint();
    }
    
    public String getMainLabel()
    {
        return mainLabel;
    }
    
    public void setMainLabel(String mainLabel)
    {
        this.mainLabel = mainLabel;
        repaint();
    }
    
    public int getMainLabelAlignment()
    {
        return mainLabelAlignment;
    }
    
    public void setMainLabelAlignment(int mainLabelAlignment)
    {
        this.mainLabelAlignment = mainLabelAlignment;
        repaint();
    }
    
    public int getSpaceBetweenMainLabelAndBorder()
    {
        return spaceBetweenMainLabelAndBorder;
    }
    
    public void setSpaceBetweenMainLabelAndBorder(int spaceBetweenMainLabelAndBorder)
    {
        this.spaceBetweenMainLabelAndBorder = spaceBetweenMainLabelAndBorder;
        repaint();
    }
    
    public Font getMainLabelFont()
    {
        return mainLabelFont;
    }
    
    public void setMainLabelFont(Font mainLabelFont)
    {
        this.mainLabelFont = mainLabelFont;
        repaint();
    }
    
    public Color getMainLabelColor()
    {
        return mainLabelColor;
    }
    
    public void setMainLabelColor(Color mainLabelColor)
    {
        this.mainLabelColor = mainLabelColor;
        repaint();
    }
    
    public double getRealIncrement(RuleTick tick)
    {
        return tick.getIncrement()*unit;
    }
    
    public double getRealIncrementOffset(RuleTick tick)
    {
        return tick.getIncrementOffset()*unit;
    }
    
    public int getMainSize()
    {
        return (ticksOrientation == ORIENTATION_TOP
             || ticksOrientation == ORIENTATION_BOTTOM ? getWidth() : getHeight());
    }
    
    public boolean isHorizontal()
    {
        return ticksOrientation == ORIENTATION_TOP || ticksOrientation == ORIENTATION_BOTTOM;
    }
    
    public boolean isVertical()
    {
        return ticksOrientation == ORIENTATION_RIGHT || ticksOrientation == ORIENTATION_LEFT;
    }
    
    public static void main(String[] args)
    {
        ArrayList<Double> arr = new ArrayList();
        arr.add(2.56);
        System.out.println(arr.contains(2.56));
        arr.add(3.5);
        arr.add(2.56);
        arr.remove(2.56);
        
        for(double d : arr)
        {
            System.out.println("d="+d);
        }
        
        ArrayList<Double>[] arr2 = new ArrayList[3];
        arr2[0] = new ArrayList();
        arr2[0].add(-453.45);
    }
}
