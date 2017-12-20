/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

/**
 * @author Raphaël
**/

public abstract class MoreGenericity
{ 
    public static <T> T checkNullArg(T arg, String fieldName)
    {
        if(arg == null)
        {
            throw new IllegalArgumentException(fieldName+" can't be null.");
        }
        else
        {
            return arg;
        }
    }
    
    /*public static <T extends Number> T checkNegArg(T arg, String fieldName)
    {
        if(arg.doubleValue() < 0)
        {
            throw new IllegalArgumentException(fieldName+" can't be negative.");
        }
        else
        {
            return arg;
        }
    }*/
    
    public static double checkNegArg(double arg, String fieldName)
    {
        if(arg < 0)
        {
            throw new IllegalArgumentException(fieldName+" can't be negative.");
        }
        else
        {
            return arg;
        }
    }
    
    public static int checkNegArg(int arg, String fieldName)
    {
        if(arg < 0)
        {
            throw new IllegalArgumentException(fieldName+" can't be negative.");
        }
        else
        {
            return arg;
        }
    }
}
