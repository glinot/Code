package graphicalsystem;

// @author Raphaël

import java.awt.Graphics;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class GraphicalObjectList<T extends GraphicalObject> extends GraphicalObject
{
    private final ArrayList<T> objList;
    
    public GraphicalObjectList(String name)
    {
        super(name, null);
        
        this.objList = new ArrayList();
    }
    
    public T get(int index)
    {
        return this.objList.get(index);
    }
    
    public int getIndex(T object)
    {
        return this.objList.indexOf(object);
    }
    
    public int size()
    {
        return this.objList.size();
    }
    
    public void set(int index, T object)
    {
        this.objList.set(index, object);
    }
    
    public void setAllObjectsVisible(boolean isVisible)
    {
        for(int i = 0; i < this.objList.size(); i++)
        {
            this.objList.get(i).visible = isVisible;
        }
    }
    
    public void setAllObjectsSolid(boolean isSolid)
    {
        for(int i = 0; i < this.objList.size(); i++)
        {
            this.objList.get(i).solid = isSolid;
        }
    }
    
    public void add(T object)
    {
        this.objList.add(object);
    }
    
    public void add(int index, T object)
    {
        this.objList.add(index, object);
    }
    
    public void remove(T object)
    {
        this.objList.remove(object);
    }
    
    public void remove(int index)
    {
        this.objList.remove(index);
    }
    
    public void move(T object, int newIndex)
    {
        this.objList.remove(object);
        this.objList.add(newIndex, object);
    }
    
    public void putDown(T object)
    {
        int index = this.objList.indexOf(object);
        
        this.objList.remove(object);
        this.objList.add(index-1, object);
    }
    
    public void putOnBottom(T object)
    {
        this.objList.remove(object);
        this.objList.add(0, object);
    }
    
    public void putUp(T object)
    {
        int index = this.objList.indexOf(object);
        
        this.objList.remove(object);
        this.objList.add(index+1, object);
    }
    
    public void putOnTop(T object)
    {
        this.objList.remove(object);
        this.objList.add(object);
    }
    
    @Override
    void draw(Graphics g)
    {
        if(this.visible)
        {
            for(int i = 0; i < this.objList.size(); i++)
            {
                this.objList.get(i).draw(g);
            }
        }
    }
    
    boolean checkForChange(int millisDelay)
    {
        boolean needToRepaint = false;
        
        for(GraphicalObject graphObj : this.objList)
        {
            if(graphObj instanceof GraphicalObjectList)
            {
                if(((GraphicalObjectList)graphObj).checkForChange(millisDelay))
                {
                    needToRepaint = true;
                }
            }
            else
            {
                boolean checkAlpha = graphObj.incrementAlphaRate(millisDelay),
                        checkXPos = graphObj.incrementXPos(millisDelay),
                        checkYPos = graphObj.incrementYPos(millisDelay);
                
                if(checkAlpha || checkXPos || checkYPos)
                {
                    needToRepaint = true;
                }
            }
        }
        
        return needToRepaint;
    }
    
    @Override
    public boolean checkCollision(int xPos, int yPos) 
    {
        if(this.solid)
        {
            for(GraphicalObject graphObj : this.objList)
            {
                if(graphObj.checkCollision(xPos, yPos))
                {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public void fadeWithStop(float startAlphaRate, boolean relativeToCurrent, float limitAlphaRate, boolean relativeToStart, float fadeSpeed)
    {
        for(GraphicalObject graphObj : this.objList)
        {
            graphObj.fadeWithStop(startAlphaRate, relativeToCurrent, limitAlphaRate, relativeToStart, fadeSpeed);
        }
    }
    
    @Override
    public void fadeWithStop(float limitAlphaRate, boolean relativeToStart, float fadeSpeed)
    {
        for(GraphicalObject graphObj : this.objList)
        {
            graphObj.fadeWithStop(limitAlphaRate, relativeToStart, fadeSpeed);
        }
    }
    
    @Override
    public void fade(float startAlphaRate, boolean relativeToCurrent, float fadeSpeed)
    {
        for(GraphicalObject graphObj : this.objList)
        {
            graphObj.fade(startAlphaRate, relativeToCurrent, fadeSpeed);
        }
    }
    
    @Override
    public void fade(float fadeSpeed)
    {
        for(GraphicalObject graphObj : this.objList)
        {
            graphObj.fade(fadeSpeed);
        }
    }
    
    @Override
    public void translateHorWithStop(double startX, boolean relativeToCurrent, double limitX, boolean relativeToStart, double horSpeed)
    {
        for(GraphicalObject graphObj : this.objList)
        {
            graphObj.translateHorWithStop(startX, relativeToCurrent, limitX, relativeToStart, horSpeed);
        }
    }
    
    @Override
    public void translateHorWithStop(double limitX, boolean relativeToStart, double horSpeed)
    {
        for(GraphicalObject graphObj : this.objList)
        {
            graphObj.translateHorWithStop(limitX, relativeToStart, horSpeed);
        }
    }
    
    @Override
    public void translateHor(double startX, boolean relativeToCurrent, double horSpeed)
    {
        for(GraphicalObject graphObj : this.objList)
        {
            graphObj.translateHor(startX, relativeToCurrent, horSpeed);
        }
    }
    
    @Override
    public void translateHor(double horSpeed)
    {
        for(GraphicalObject graphObj : this.objList)
        {
            graphObj.translateHor(horSpeed);
        }
    }
    
    @Override
    public void translateVertWithStop(double startY, boolean relativeToCurrent, double limitY, boolean relativeToStart, double vertSpeed)
    {
        for(GraphicalObject graphObj : this.objList)
        {
            graphObj.translateHorWithStop(startX, relativeToCurrent, limitX, relativeToStart, vertSpeed);
        }
    }
    
    @Override
    public void translateVertWithStop(double limitY, boolean relativeToStart, double vertSpeed)
    {
        for(GraphicalObject graphObj : this.objList)
        {
            graphObj.translateVertWithStop(limitY, relativeToStart, vertSpeed);
        }
    }
    
    @Override
    public void translateVert(double startY, boolean relativeToCurrent, double vertSpeed)
    {
        for(GraphicalObject graphObj : this.objList)
        {
            graphObj.translateVert(startY, relativeToCurrent, vertSpeed);
        }
    }
    
    @Override
    public void translateVert(double vertSpeed)
    {
        for(GraphicalObject graphObj : this.objList)
        {
            graphObj.translateVert(vertSpeed);
        }
    }
    
    public void sortBy(Method method, Object... arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Type returnedType = method.getReturnType();
        
        if(returnedType == Double.class || returnedType == Float.class
                || returnedType == Long.class || returnedType == Integer.class
                || returnedType == Byte.class || returnedType == Character.class)
        {
            double result = (Double)method.invoke(this, arguments);
            
        }
    }
}
