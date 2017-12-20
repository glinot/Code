/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.reflexion;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import mandelbrot.controller.Controller;
import mandelbrot.gui.ProGUI;

/**
 *
 * @author Gregoire
 */
public class MoreReflexion {
    
    public static void listAllClassAttributes(Object o){
        Field[] a  = o.getClass().getDeclaredFields();
        for (Field a1 : a) {
            System.out.println(a1.getName());
        }
                
    }
    public static void listAllMethod(Object o){
        Method[] mts = o.getClass().getDeclaredMethods();
        for (Method mt : mts) {
            System.out.println(mt.getName()+"  "+mt.isAccessible());
        }
    }
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        Controller c=new Controller();
        listAllClassAttributes(c);
        Field f = Controller.class.getDeclaredField("proGui");
        f.setAccessible(true);
        ProGUI p = (ProGUI)f.get(c);
        p.pack();
        p.setVisible(true);
        
        
    }
}
