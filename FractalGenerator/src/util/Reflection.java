package util;

// @author Raphaël

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class Reflection
{
    public static String describeAttributes(Object obj, boolean describeDeeply)
    {
        if(obj == null)
        {
            return "null object";
        }
        
        try
        {
            String description = obj.getClass().getName() + "{";
            Field[] fields = obj.getClass().getDeclaredFields();
            
            for(int i = 0; i < fields.length; i++)
            {
                Field field = fields[i];
                
                if(!Modifier.isStatic(field.getModifiers()))
                {
                    field.setAccessible(true);
                    description += field.getName()
                            + (field.getType().isPrimitive() ? "=" + field.get(obj)
                            : (describeDeeply ? "=" + field.get(obj) : ""));
                    
                    if(i < fields.length-1)
                    {
                        description += ", ";
                    }
                }
            }
            
            return description + "}";
        }
        catch(IllegalArgumentException | IllegalAccessException ex)
        {
            Logger.getLogger(Reflection.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
    public static boolean checkAttributesEquality(Object obj1, Object obj2, boolean checkDeeply)
    {
        if(obj1 == obj2)
        {
            return true;
        }
        
        if(obj1 == null || obj2 == null || obj1.getClass() != obj2.getClass())
        {
            return false;
        }
        
        try
        {
            Field[] fields = obj1.getClass().getDeclaredFields();
            
            for(Field field : fields)
            {
                if(!Modifier.isStatic(field.getModifiers()))
                {
                    field.setAccessible(true);
                    
                    if(checkDeeply && !field.get(obj1).equals(field.get(obj2))
                    || !checkDeeply && field.get(obj1) != field.get(obj2))
                    {
                        return false;
                    }
                }
            }
            
            return true;
        }
        catch(IllegalArgumentException | IllegalAccessException ex)
        {
            Logger.getLogger(Reflection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
