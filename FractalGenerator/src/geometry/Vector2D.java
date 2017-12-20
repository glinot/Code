package geometry;

// @author Raphaël

import graphicalsystem.IntPoint;
import util.Reflection;


public final class Vector2D//, XMLCompatible
{
    //public static String VECTEUR_2D = "vecteur_2d", X_VAL = "x_val", Y_VAL = "y_val";
    
    protected double x, y;
    
    public Vector2D(double a, double b, boolean polar)
    {
        if(polar)
        {
            x = a*Math.cos(b);
            y = a*Math.sin(b);
        }
        else
        {
            x = a;
            y = b;
        }
    }
    
    public Vector2D(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
    
    public Vector2D(Vector2D vect)
    {
        x = vect.x;
        y = vect.y;
    }
    
    public Vector2D()
    {
        x = 0;
        y = 0;
    }
    
    public double getX()
    {
        return x;
    }
    
    public void setX(double x)
    {
        this.x = x;
    }
    
    public double getY()
    {
        return y;
    }
    
    public void setY(double y)
    {
        this.y = y;
    }
    
    public void setCoord(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
    
    public void setPolCoord(double modulus, double argument)
    {
        x = modulus*Math.cos(argument);
        y = modulus*Math.sin(argument);
    }
    
    public double getModulus()
    {
        return Math.sqrt(x*x+y*y);
    }
    
    public double getSquaredModulus()
    {
        return x*x+y*y;
    }
    
    public double getArgument()
    {
        return Math.atan2(y, x);
    }
    
    public void addDx(double Dx)
    {
        x += Dx;
    }
    
    public void addDy(double Dy)
    {
        y += Dy;
    }
    
    public void add(double Dx, double Dy)
    {
        x += Dx;
        y += Dy;
    }
    
    public void add(Vector2D vect)
    {
        x += vect.x;
        y += vect.y;
    }
    
    public void substractDx(double Dx)
    {
        x -= Dx;
    }
    
    public void substractDy(double Dy)
    {
        y -= Dy;
    }
    
    public void substract(double Dx, double Dy)
    {
        x -= Dx;
        y -= Dy;
    }
    
    public void substract(Vector2D vect)
    {
        x -= vect.x;
        y -= vect.y;
    }
    
    public void multiply(double scalar)
    {
        x *= scalar;
        y *= scalar;
    }
    
    public void divide(double scalar)
    {
        x /= scalar;
        y /= scalar;
    }
    
    public double scalar(Vector2D vect)
    {
        return x*vect.x+y*vect.y;
    }
    
    public Vector2D getUnitVector()
    {
        double modulus = getModulus();
        return new Vector2D(x/modulus, y/modulus);
    }
    
    public double angleWith(Vector2D vect)
    {
        return vect.getArgument()-getArgument();
    }
    
    public IntPoint truncateToIntPoint()
    {
        return new IntPoint((int)x, (int)y);
    }
    
    public IntPoint truncateToLowerIntPoint()
    {
        return new IntPoint((int)Math.floor(x), (int)Math.floor(y));
    }
    
    public IntPoint truncateToUpperIntPoint()
    {
        return new IntPoint((int)Math.ceil(x), (int)Math.ceil(y));
    }
    
    public IntPoint roundToIntPoint()
    {
        return new IntPoint((int)Math.round(x), (int)Math.round(y));
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
    
    @Override
    public Vector2D clone()
    {
        return new Vector2D(x, y);
    }
    
    /*@Override
    public Element getXMLElement()
    {
        Element vect = new Element(VECTEUR_2D);
            Attribute xValue = new Attribute(X_VAL, ""+xVal);
            Attribute yValue = new Attribute(Y_VAL,""+yVal);
        vect.setAttribute(xValue);
        vect.setAttribute(yValue);
        
        return vect;
    }
    
    @Override
    public void loadElement(Element vect)
    {
        String xValue = vect.getAttributeValue(X_VAL),
               yValue = vect.getAttributeValue(Y_VAL);
        this.xVal = Double.parseDouble(xValue);
        this.yVal = Double.parseDouble(yValue);
    }
    
    public static void main(String[] args)
    {
        Element elem = new Element("ListeVecteurs2D");
        
        for(int i = 0; i < 20; i++)
        {
            elem.addContent(new Vector2D(i, i).getXMLElement());
        }
        
        XMLFileIO.writeXML(elem, new File("D:\\lol.xml"));
    }*/
}