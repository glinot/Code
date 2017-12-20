/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.curves;

import geometry.Vector2D;
import graphicalsystem.IntPoint;
import java.util.ArrayList;
import java.util.Arrays;
import util.MoreArrays;

/**
 * @author Raphaël
**/

public abstract class ManipulableFunction extends Function2D
{
    public enum propertyType
    {
        points, intervals
    }
    
    ManipulablePoint[] points;
    int n; // number of points, so there is n-1 intervals
    private ArrayList<Property> properties;
    
    public ManipulableFunction(double[][] xyPoints)
    {
        if(xyPoints == null)
        {
            n = 0;
            points = new ManipulablePoint[n];
        }
        else
        {
            MoreArrays.sortXYPoints(xyPoints);
            
            n = xyPoints.length;
            points = new ManipulablePoint[n];
            
            for(int i = 0; i < n; i++)
            {
                double[] point = xyPoints[i];
                points[i] = new ManipulablePoint(point[0], point[1]);
            }
        }
    }
    
    public ManipulableFunction(double[] xPoints, double[] yPoints)
    {
        this(MoreArrays.mergeXYVectors(xPoints, yPoints));
    }
    
    protected final void initProperties(Property ... properties)
    {
        this.properties = new ArrayList();
        
        if(properties != null)
        {
            for(Property prop : properties)
            {
                if(prop != null)
                {
                    this.properties.add(prop);
                    prop.initialize();
                }
            }
        }
    }
    
    @Override
    public abstract Double eval(double x);
    
    public final ManipulablePoint addPoint(double x, double y)
    {
        ManipulablePoint point = new ManipulablePoint(x, y);
        
        for(int i = 0; i < displayers.size(); i++)
        {
            point.screenPos.add(new IntPoint());
        }
        
        privateAddPoint(point, false);
        return point;
    }
    
    public final ManipulablePoint addPoint(double[] xyPoint)
    {
        return addPoint(xyPoint[0], xyPoint[1]);
    }
    
    public final ManipulablePoint addPoint(Vector2D point)
    {
        return addPoint(point.getX(), point.getY());
    }
    
    private void privateAddPoint(ManipulablePoint point, boolean moving)
    {
        double x = point.x, y = point.y;
        int i;
        
        for(i = 0; i < n; i++)
        {
            if(points[i].x >= x)
            {
                while(points[i].x == x && points[i].y <= y)
                {
                    i++;
                    
                    if(i == n)
                    {
                        break;
                    }
                }
                
                break;
            }
        }
        
        ManipulablePoint[] newPoints = new ManipulablePoint[n+1];
        System.arraycopy(points, 0, newPoints, 0, i);
        newPoints[i] = point;
        System.arraycopy(points, i, newPoints, i+1, n-i);
        
        points = newPoints;
        n++;
        
        updateAfterAdding(i, moving);
    }
    
    private void updateAfterAdding(int index, boolean moving)
    {
        for(Property prop : properties)
        {
            if(moving && prop.persistent)
            {
                if(prop.removalIndex != null)
                {
                    prop.components.add(prop.removalIndex, prop.pendingComponent);
                    prop.removalIndex = null;
                    prop.pendingComponent = null;
                }
            }
            else
            {
                Integer addingIndex;
                
                if(prop.type == propertyType.intervals && index == n-1)
                {
                    if(n > 1)
                    {
                        addingIndex = n-2;
                    }
                    else // if n = 1 (so index = 0)
                    {
                        addingIndex = null;
                    }
                }
                else
                {
                    addingIndex = index;
                }
                
                if(addingIndex != null)
                {
                    prop.components.add(addingIndex, null);
                }
                
                // not adding a component doesn't prohibit updating other components
                int[] updatesToDo = prop.updatesToDoAfterAdding(index, n);
                
                if(updatesToDo != null)
                {
                    for(int i : updatesToDo)
                    {
                        prop.update(i);
                    }
                }
            }
        }
        
        if(!moving)
        {
            fireCurveChangeEvent();
        }
    }
    
    public final void removePoint(ManipulablePoint point)
    {
        for(int i = 0; i < n; i++)
        {
            if(points[i] == point)
            {
                privateRemovePoint(i, false);
                point.screenPos.clear();
                point.removed = true;
                return;
            }
        }
        
        throw new IllegalArgumentException("Point not found in the list.");
    }
    
    private void privateRemovePoint(int index, boolean moving)
    {
        ManipulablePoint[] newPoints = new ManipulablePoint[n-1];
        
        System.arraycopy(points, 0, newPoints, 0, index);
        System.arraycopy(points, index+1, newPoints, index, n-index-1);
        
        points = newPoints;
        n--;
        
        updateAfterRemoving(index, moving);
    }
    
    private void updateAfterRemoving(int index, boolean moving)
    {
        int oldN = n+1;
        
        for(Property prop : properties)
        {
            Integer removalIndex;
            
            if(prop.type == propertyType.intervals && index == oldN-1)
            {
                if(oldN > 1)
                {
                    removalIndex = oldN-2;
                }
                else
                {
                    removalIndex = null;
                }
            }
            else
            {
                removalIndex = index;
            }
            
            if(removalIndex != null)
            {
                if(moving && prop.persistent)
                {
                    prop.pendingComponent = prop.get(removalIndex);
                    prop.removalIndex = removalIndex;
                }
                
                prop.components.remove(removalIndex.intValue());
            }
            
            if(!(moving && prop.persistent))
            {
                int[] updatesToDo = prop.updatesToDoAfterRemoving(index, n);
                
                if(updatesToDo != null)
                {
                    for(int i : updatesToDo)
                    {
                        prop.update(i);
                    }
                }
            }
        }
        
        if(!moving)
        {
            fireCurveChangeEvent();
        }
    }
    
    public final void movePoint(ManipulablePoint point, double newX, double newY)
    {
        int i;
        
        for(i = 0; i < n; i++)
        {
            if(points[i] == point)
            {
                point.x = newX;
                point.y = newY;
                privateRemovePoint(i, true);
                privateAddPoint(point, true);
                
                fireCurveChangeEvent();
                return;
            }
        }
        
        throw new IllegalArgumentException("Point not found in the list. (index "+i+", size "+n+")");
    }
    
    public final void movePoint(ManipulablePoint point, Vector2D newPos)
    {
        movePoint(point, newPos.getX(), newPos.getY());
    }
    
    protected final int getLeftNearestPointIndex(double x)
    {
        int i;
        
        for(i = n-1; i > -1; i--)
        {
            if(points[i].x <= x)
            {   
                break;
            }
        }
        
        return i;
    }
    
    public final int getN()
    {
        return n;
    }
    
    public final ManipulablePoint getPoint(int index)
    {
        return points[constrainIndex(index)];
    }
    
    public final int indexOf(ManipulablePoint point)
    {
        for(int i = 0; i < n; i++)
        {
            ManipulablePoint p = points[i];
            
            if(p == point)
            {
                return i;
            }
        }
        
        return -1;
    }
    
    public double getX(int index)
    {
        return points[constrainIndex(index)].x;
    }
    
    public double getY(int index)
    {
        return points[constrainIndex(index)].y;
    }
    
    int constrainIndex(int index)
    {
        if(index >= 0 && index < n)
            return index;
        else
            throw new IllegalArgumentException("Index "+index
                    +" doesn't correspond to any point.");
    }
    
    int constrainPropertyIndex(int index, Property prop)
    {
        if(index >= 0 && index < prop.components.size())
            return index;
        else
            throw new IllegalArgumentException("Index "+index
                    +" doesn't correspond to any component of property "
                    +prop.name+".");
    }
    
    protected abstract class Property<T>
    {
        private ArrayList<T> components;
        private propertyType type;
        private boolean initialized;
        private String name;
        private boolean persistent;
        private T pendingComponent;
        private Integer removalIndex;
        
        protected Property(ArrayList<T> components, propertyType type, String name)
        {
            if(components == null)
                throw new IllegalArgumentException("Argument \"components\" can't be null.");
            else if((type == propertyType.points && components.size() == n
                  || type == propertyType.intervals &&
                    (components.size() == n-1 || n == 0 && components.isEmpty())))
                this.components = components;
            else
                throw new IllegalArgumentException("Wrong components number.");
            
            initTypeAndName(type, name);
            
            if(this.components != null)
            {
                initialized = true;
            }
        }
        
        protected Property(T[] components, propertyType type, String name)
        {
            this(new ArrayList(Arrays.asList(components)), type, name);
        }
        
        protected Property(propertyType type, String name)
        {
            this.components = new ArrayList();
            initTypeAndName(type, name);
        }
        
        private void initTypeAndName(propertyType type, String name)
        {
            if(type == null)
                this.type = propertyType.points;
            else
                this.type = type;
            
            if(name == null)
                this.name = this.toString();
            else
                this.name = name;
        }
        
        private void initialize()
        {
            if(!initialized)
            {
                switch(type)
                {
                    case points :
                        for(int i = 0; i < n; i++)
                        {
                            components.add(compute(i, n));
                        }
                        break;
                    case intervals :
                        for(int i = 0; i < n-1; i++)
                        {
                            components.add(compute(i, n));
                        }
                        break;
                }
                
                initialized = true;
            }
        }
        
        protected abstract int[] updatesToDoAfterAdding(int index, int n);
        protected abstract int[] updatesToDoAfterRemoving(int index, int n);
        protected abstract T compute(int index, int n);
        
        public final T get(int index)
        {
            if(initialized)
            {
                return components.get(constrainPropertyIndex(index, this));
            }
            else
                throw new IllegalStateException("Illegal use of property "+name+"."
                        + " It has not been initialized yet."
                        + " Try to change the order of your properties.");
        }
        
        protected final int size()
        {
            return components.size();
        }
        
        protected final propertyType getType()
        {
            return type;
        }
        
        protected final boolean isInitialized()
        {
            return initialized;
        }
        
        protected final String getName()
        {
            return name;
        }
        
        protected final void setName(String name)
        {
            this.name = name;
        }
        
        protected final boolean isPersistent()
        {
            return persistent;
        }
        
        protected final void setPersistent(boolean persistent)
        {
            this.persistent = persistent;
        }
        
        private void update(int index)
        {
            try
            {
                int constIndex = constrainPropertyIndex(index, this);
                components.set(constIndex, compute(constIndex, n));
            }
            catch(IllegalArgumentException e) {}
        }
    }
    
    @Override
    void addCurveDisplayer(CurveDisplayer displayer)
    {
        if(!displayers.contains(displayer))
        {
            displayers.add(displayer);
            
            for(ManipulablePoint point : points)
            {
                point.screenPos.add(new IntPoint());
            }
        }
    }
    
    @Override
    void removeCurveDisplayer(CurveDisplayer displayer)
    {
        if(displayers.contains(displayer))
        {
            int index = displayers.indexOf(displayer);
            displayers.remove(index);
            
            for(ManipulablePoint point : points)
            {
                point.screenPos.remove(index);
            }
        }
    }
    
    public class ManipulablePoint
    {
        double x, y;
        private final ArrayList<IntPoint> screenPos;
        private boolean removed;
        
        public ManipulablePoint(double x, double y)
        {
            this.x = x;
            this.y = y;
            screenPos = new ArrayList();
        }
        
        public double getX()
        {
            return x;
        }
        
        public double getY()
        {
            return y;
        }
        
        public IntPoint getScreenPos(CurveDisplayer displayer)
        {
            if(displayers.contains(displayer) && !removed)
            {
                return screenPos.get(displayers.indexOf(displayer));
            }
            else
            {
                throw new IllegalArgumentException("This point doesn't belong to the displayer.");
            }
        }
        
        public void setScreenPos(CurveDisplayer displayer, IntPoint point)
        {
            if(displayers.contains(displayer) && !removed)
            {
                screenPos.set(displayers.indexOf(displayer), point);
            }
            else
            {
                throw new IllegalArgumentException("This point doesn't belong to the displayer.");
            }
        }
        
        public int getXScreenPos(CurveDisplayer displayer)
        {
            return getScreenPos(displayer).getAbs();
        }
        
        public int getYScreenPos(CurveDisplayer displayer)
        {
            return getScreenPos(displayer).getOrd();
        }
        
        public void setXScreenPos(CurveDisplayer displayer, int X)
        {
            getScreenPos(displayer).setAbs(X);
        }
        
        public void setYScreenPos(CurveDisplayer displayer, int Y)
        {
            getScreenPos(displayer).setAbs(Y);
        }
    }
}
