/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mandelbrot.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author Raphaël
**/

public class GodlikePanel extends JPanel implements ComponentListener
{
    protected ArrayList<ConstrainedComponent> components;
    protected ArrayList<Constraint> constraints;
    protected ArrayList<Constraint> constraintsToSatisfy;
    protected String errorLog;
    protected ArrayList<Constraint> overConstraints;
    protected boolean debugMode;
    
    public enum Border
    {
        LEFT("left"), RIGHT("right"), TOP("top"), BOTTOM("bottom");
        
        String name;
        
        Border(String name)
        {
            this.name = name;
        }
        
        public boolean isHorizontal()
        {
            return this == LEFT || this == RIGHT;
        }
        
        public boolean isVertical()
        {
            return this == TOP || this == BOTTOM;
        }
        
        public Border opposite()
        {
            switch(this)
            {
                case LEFT  : return RIGHT;
                case RIGHT : return LEFT;
                case TOP   : return BOTTOM;
                default    : return TOP;
            }
        }
        
        public int direction()
        {
            switch(this)
            {
                case LEFT  : case TOP  : return -1;
                case RIGHT : default   : return  1;
            }
        }
        
        @Override
        public String toString()
        {
            return name+" border";
        }
    }
    
    public GodlikePanel(boolean doubleBuffered)
    {
        super(null, doubleBuffered);
        ConstrainedComponent mainComp = new ConstrainedComponent(this);
        mainComp.horizontalResizable = false;
        mainComp.verticalResizable = false;
        mainComp.leftFixed = true;
        mainComp.rightFixed = true;
        mainComp.topFixed = true;
        mainComp.bottomFixed = true;
        
        components = new ArrayList();
        components.add(mainComp);
        constraints = new ArrayList();
        constraintsToSatisfy = new ArrayList();
        overConstraints = new ArrayList();
        debugMode = false;
        
        addComponentListener(this);
    }
    
    public GodlikePanel()
    {
        this(false);
    }
    
    public boolean isDebugMode()
    {
        return debugMode;
    }
    
    public void setDebugMode(boolean debugMode)
    {
        if(debugMode)
        {
            for(ConstrainedComponent constComp : components)
            {
                if(constComp.component.getName() == null)
                {
                    if(constComp.component == this)
                    {
                        throw new IllegalStateException("All components need a name for debug mode, even the parent component.");
                    }
                    else
                    {
                        throw new IllegalStateException("All children components need a name for debug mode.");
                    }
                }
            }
        }
        
        this.debugMode = debugMode;
    }
    
    public void addComponent(Component comp)
    {
        try
        {
            ConstrainedComponent constComp = getConstrainedComponent(comp);
        }
        catch(NullPointerException e)
        {
            add(comp);
            components.add(new ConstrainedComponent(comp));
        }
    }
    
    public void addConstraint(Component comp1, Component comp2, Border border1, Border border2, int distance)
    {
        if(comp1 == null) throw new NullPointerException("Parameter comp1 mustn't be null.");
        if(comp2 == null) throw new NullPointerException("Parameter comp2 mustn't be null.");
        if(comp1 == comp2) throw new IllegalArgumentException("A constraint can only be added between two different components.");
        if(border1.isHorizontal() && border2.isVertical() || border1.isVertical() && border2.isHorizontal())
        {throw new IllegalArgumentException("Borders are not compatible.");}
        
        ConstrainedComponent constComp1 = getConstrainedComponent(comp1),
                             constComp2 = getConstrainedComponent(comp2);
        Constraint c = new Constraint(constComp1, constComp2, border1, border2, distance);
        
        constraints.add(c);
        constComp1.constraints.add(c);
        constComp2.constraints.add(c);
    }
    
    public void addConstraint(Component comp1, Component comp2, Border border1, Border border2)
    {
        addConstraint(comp1, comp2, border1, border2, 0);
    }
    
    protected ConstrainedComponent getConstrainedComponent(Component comp) throws NullPointerException
    {
        if(comp == null) throw new NullPointerException("Parameter comp mustn't be null.");
        
        for(ConstrainedComponent constComp : components)
        {
            if(constComp.component == comp)
            {
                return constComp;
            }
        }
        
        throw new NullPointerException("Component not added to the GodlikePanel.");
    }
    
    public final void setHorizontalResizable(Component comp)
    {
        getConstrainedComponent(comp).horizontalResizable = true;
    }
    
    public final void setVerticalResizable(Component comp)
    {
        getConstrainedComponent(comp).verticalResizable = true;
    }
    
    public void setFixedWidth(Component comp, int width)
    {
        ConstrainedComponent constComp = getConstrainedComponent(comp);
        
        if(comp == this) throw new IllegalArgumentException("You can't fix the main component's size.");
        
        constComp.horizontalResizable = false;
        comp.setSize(width, (int)comp.getSize().getHeight());
    }
    
    public void setFixedHeight(Component comp, int height)
    {
        ConstrainedComponent constComp = getConstrainedComponent(comp);
        
        if(comp == this) throw new IllegalArgumentException("You can't fix the main component's size.");
        
        constComp.verticalResizable = false;
        comp.setSize((int)comp.getSize().getWidth(), height);
    }
    
    public void addUnsatisfiedConstraints(ArrayList<Constraint> source, ArrayList<Constraint> destination)
    {
        for(Constraint constraint : source)
        {
            if(!constraint.satisfied && !destination.contains(constraint))
            {
                destination.add(constraint);
                
                if(debugMode) System.out.println("add "+constraint);
            }
        }
    }
    
    protected void layoutComponents()
    {
        errorLog = "";
        overConstraints.clear();
        ConstrainedComponent mainComp = components.get(0);
        mainComp.left = 0;
        mainComp.right = getWidth();
        mainComp.top = 0;
        mainComp.bottom = getHeight();
        
        for(int i = 1; i < components.size(); i++)
        {
            ConstrainedComponent constComp = components.get(i);
            constComp.leftFixed = false;
            constComp.rightFixed = false;
            constComp.topFixed = false;
            constComp.bottomFixed = false;
        }
        
        for(Constraint constraint : constraints)
        {
            constraint.satisfied = false;
        }
        
        constraintsToSatisfy.clear();
        if(debugMode) System.out.println("\nGodlikePanel - debug mode\n"
                + " - \"add\" means that the constraint is added to the list of constraints to satisfy.\n"
                + "   The constraints are satisfied according to their order in the list.\n"
                + " - \"fix\" means that the component's border is calculated from a fixed border of the other component\n");
        
        for(Constraint constraint : mainComp.constraints)
        {
            constraintsToSatisfy.add(constraint);
            if(debugMode) System.out.println("add "+constraint);
        }
        
        if(debugMode) System.out.println();
        
        while(constraintsToSatisfy.size() > 0)
        {
            if(debugMode) System.out.println("satifying "+constraintsToSatisfy.get(0));
            addUnsatisfiedConstraints(constraintsToSatisfy.get(0).satisfy(), constraintsToSatisfy);
            if(debugMode) System.out.println("constraint satisfied\n");
            constraintsToSatisfy.remove(0);
        }
        
        for(int i = 1; i < components.size(); i++)
        {
            ConstrainedComponent constComp = components.get(i);
            Component comp = constComp.component;
            comp.setLocation(constComp.left, constComp.top);
            
            if(constComp.horizontalResizable)
            {
                comp.setSize(constComp.right-constComp.left, (int)comp.getSize().getHeight());
            }
            
            if(constComp.verticalResizable)
            {
                comp.setSize((int)comp.getSize().getWidth(), constComp.bottom-constComp.top);
            }
        }
        
        if(debugMode)
        {
            System.out.println("layout finished :");
            
            for(ConstrainedComponent constComp : components)
            {
                Component comp = constComp.component;
                System.out.println(comp.getName()+" : location : "+comp.getLocation() + ", size : "+comp.getSize());
            }
            
            if(overConstraints.size() > 0)
            {
                errorLog += "\n-> "+overConstraints.size()
                        + (overConstraints.size() > 1 ? " constraints are" : " constraint is")
                        + " overconstraining the set :\n";
                
                for(Constraint constraint : overConstraints)
                {
                    errorLog += constraint+"\n";
                }
            }
            
            ArrayList<Constraint> unreachableConstraints = new ArrayList();
            
            for(Constraint constraint : constraints)
            {
                if(!constraint.satisfied)
                {
                    unreachableConstraints.add(constraint);
                }
            }
            
            if(unreachableConstraints.size() > 0)
            {
                errorLog += "\n-> "+unreachableConstraints.size()
                      +" constraint"+(unreachableConstraints.size() > 1 ? "s are" : " is")
                      +" unreachable by propagation from the parent component :\n";
                
                for(Constraint constraint : unreachableConstraints)
                {
                    errorLog += constraint+"\n";
                }
            }
            
            String unconstrainedComp = "";
            int unconstrainedCompNbr = 0;
            
            for(ConstrainedComponent constComp : components)
            {
                if(!constComp.leftFixed || !constComp.rightFixed || !constComp.topFixed || !constComp.bottomFixed)
                {
                    unconstrainedComp += constComp.component.getName()+" :\n"
                            + (constComp.leftFixed ? "" : Border.LEFT+" not constrained\n") 
                            + (constComp.rightFixed ? "" : Border.RIGHT+" not constrained\n")
                            + (constComp.topFixed ? "" : Border.TOP+" not constrained\n")
                            + (constComp.bottomFixed ? "" : Border.BOTTOM+" not constrained\n");
                    unconstrainedCompNbr++;
                }
            }
            
            if(unconstrainedCompNbr > 0)
            {
                errorLog += "\n-> "+unconstrainedCompNbr+" component"+
                            (unconstrainedCompNbr > 1 ? "s are" : " is")
                            +" not fully constrained :\n"+unconstrainedComp;
            }
            
            System.out.println("\nError log :\n"
                    + (errorLog.isEmpty() ? "\nno error\n" : errorLog));
        }
    }
    
    protected class ConstrainedComponent
    {
        public Component component;
        public ArrayList<Constraint> constraints;
        public boolean horizontalResizable, verticalResizable;
        public int left, right, top, bottom;
        public boolean leftFixed, rightFixed, topFixed, bottomFixed;
        
        public ConstrainedComponent(Component comp)
        {
            component = comp;
            constraints = new ArrayList();
            horizontalResizable = true;
            verticalResizable = true;
        }
        
        public boolean isFixed(Border border)
        {
            switch(border)
            {
                case LEFT  : return leftFixed;
                case RIGHT : return rightFixed;
                case TOP   : return topFixed;
                default    : return bottomFixed;
            }
        }
        
        public void fix(Border border)
        {
            switch(border)
            {
                case LEFT  : leftFixed   = true; break;
                case RIGHT : rightFixed  = true; break;
                case TOP   : topFixed    = true; break;
                default    : bottomFixed = true; break;
            }
        }
        
        public boolean isResizable(boolean horizontally)
        {
            return horizontally ? horizontalResizable : verticalResizable;
        }
        
        public int getSize(boolean horizontally)
        {
            return horizontally ? (int)component.getSize().getWidth()
                    : (int)component.getSize().getHeight();
        }
        
        public int getValue(Border border)
        {
            switch(border)
            {
                case LEFT  : return left;
                case RIGHT : return right;
                case TOP   : return top;
                default    : return bottom;
            }
        }
        
        public void setValue(Border border, int value)
        {
            switch(border)
            {
                case LEFT  : left   = value; break;
                case RIGHT : right  = value; break;
                case TOP   : top    = value; break;
                default    : bottom = value; break;
            }
        }
        
        public ArrayList<Constraint> getAssociatedConstraints(Border fixedBorder)
        {
            ArrayList<Constraint> assocConstraints = new ArrayList();
            
            for(Constraint constraint : constraints)
            {
                if(constraint.comp1 == this && constraint.border1 == fixedBorder || constraint.comp2 == this && constraint.border2 == fixedBorder)
                {
                    assocConstraints.add(constraint);
                }
            }
            
            return assocConstraints;
        }
    }
    
    protected class Constraint
    {
        ConstrainedComponent comp1, comp2;
        Border border1, border2;
        int distance;
        boolean satisfied;
        
        public Constraint(ConstrainedComponent comp1, ConstrainedComponent comp2, Border border1, Border border2, int distance)
        {
            this.comp1 = comp1;
            this.comp2 = comp2;
            this.border1 = border1;
            this.border2 = border2;
            this.distance = distance;
            satisfied = false;
        }
        
        public ArrayList<Constraint> satisfy()
        {
            ArrayList<Constraint> nextConstraints = new ArrayList();
            ConstrainedComponent refComp, compToFix;
            Border refBorder, borderToFix;
            
            if(comp1.isFixed(border1) && comp2.isFixed(border2))
            {
                refComp = comp1;
                compToFix = comp2;
                refBorder = border1;
                borderToFix = border2;
                if(debugMode) overConstraints.add(this);
            }
            else if(comp1.isFixed(border1))
            {
                refComp = comp1;
                compToFix = comp2;
                refBorder = border1;
                borderToFix = border2;
            }
            else
            {
                refComp = comp2;
                compToFix = comp1;
                refBorder = border2;
                borderToFix = border1;
            }
            // According to the propagation algorithm, there is always at least one already fixed border.             
            
            compToFix.setValue(borderToFix, refComp.getValue(border1) + distance);
            compToFix.fix(borderToFix);
            if(debugMode) System.out.println("fix "+compToFix.component.getName()+"'s "+borderToFix+" at "+(refComp.getValue(refBorder) + distance));

            nextConstraints.addAll(compToFix.getAssociatedConstraints(borderToFix));

            if(!compToFix.isResizable(borderToFix.isHorizontal()))
            {
                compToFix.setValue(borderToFix.opposite(), compToFix.getValue(borderToFix)
                        + borderToFix.opposite().direction()*compToFix.getSize(borderToFix.isHorizontal()));
                compToFix.fix(borderToFix.opposite());
                if(debugMode) System.out.println("fix "+compToFix.component.getName()+"'s "+borderToFix.opposite()+" at "+(compToFix.getValue(borderToFix)
                        + borderToFix.opposite().direction()*compToFix.getSize(borderToFix.isHorizontal()))+" ("+(borderToFix.isHorizontal() ? "width " : "height ")+compToFix.getSize(borderToFix.isHorizontal())+")");

                nextConstraints.addAll(compToFix.getAssociatedConstraints(borderToFix.opposite()));
            }

            satisfied = true;            
            return nextConstraints;
        }
        
        @Override
        public String toString()
        {
            return "constraint between "+comp1.component.getName()+"'s "+border1+" and "+comp2.component.getName()+"'s "+border2+", distance "+distance;
        }
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        paintComponents(g);
    }
    
    @Override
    public void componentResized(ComponentEvent e)
    {
        layoutComponents();
    }
    
    @Override
    public void componentMoved(ComponentEvent e)
    {
    }
    
    @Override
    public void componentShown(ComponentEvent e)
    {
    }
    
    @Override
    public void componentHidden(ComponentEvent e)
    {
    }
    
    public static void main(String[] args)
    {
        JFrame f = new JFrame("Test Layout");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        GodlikePanel p = new GodlikePanel();
        p.setName("p");
        p.setPreferredSize(new Dimension(300, 300));
        
        JPanel p1 = new JPanel();
        p1.setName("p1");
        p1.setBackground(Color.RED);
        
        JPanel p2 = new JPanel();
        p2.setName("p2");
        p2.setBackground(Color.BLUE);
        
        JPanel p3 = new JPanel();
        p3.setName("p3");
        p3.setBackground(Color.CYAN);
        
        JPanel p4 = new JPanel();
        p4.setName("p4");
        p4.setBackground(Color.GREEN);
        
        p.addComponent(p1);
        p.addConstraint(p, p1, Border.LEFT, Border.LEFT, 20);
        p.addConstraint(p1, p, Border.RIGHT, Border.RIGHT, 40);
        p.addConstraint(p, p1, Border.TOP, Border.TOP, 60);
        p.addConstraint(p1, p, Border.BOTTOM, Border.BOTTOM, 200);
        
        p.addComponent(p2);
        p.addConstraint(p1, p2, Border.BOTTOM, Border.TOP);
        p.addConstraint(p2, p, Border.BOTTOM, Border.BOTTOM, 10);
        p.addConstraint(p1, p2, Border.LEFT, Border.LEFT, 10);
        p.addConstraint(p2, p1, Border.RIGHT, Border.RIGHT, 100);
        
        p.addComponent(p3);
        p.setFixedWidth(p3, 50);
        p.setFixedHeight(p3, 50);
        p.addConstraint(p2, p3, Border.RIGHT, Border.LEFT, 15);
        p.addConstraint(p1, p3, Border.BOTTOM, Border.TOP, 15);
        
        p.addComponent(p4);
        p.addConstraint(p3, p4, Border.RIGHT, Border.LEFT, 5);
        p.addConstraint(p4, p, Border.RIGHT, Border.RIGHT, 5);
        p.addConstraint(p3, p4, Border.TOP, Border.TOP, 5);
        p.addConstraint(p3, p4, Border.BOTTOM, Border.BOTTOM, 0);
        
        p.setDebugMode(true);
        
        f.setContentPane(p);
        f.pack();
        f.setVisible(true);
    }
}
