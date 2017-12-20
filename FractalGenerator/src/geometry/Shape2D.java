package geometry;

// @author Raphaël

public abstract class Shape2D
{
    public abstract double getxMin();
    public abstract double getxMax();
    public abstract double getyMin();
    public abstract double getyMax();
    
    public final double getWidth()
    {
        return getxMax()-getxMin();
    }
    
    public final double getHeight()
    {
        return getyMax()-getyMin();
    }
    
    public final Vector2D getCenter()
    {
        return new Vector2D(getxMin()+getWidth()/2, getyMin()+getHeight()/2);
    }
    
    public abstract Shape2D getTranslatedInstance(double Dx, double Dy);
    public abstract Shape2D getRelativelyRotatedInstance(double Dtheta);
    public abstract Shape2D getAbsolutelyRotatedInstance(double theta);
    
    public final Shape2D getTranslatedInstance(Vector2D vect)
    {
        return getTranslatedInstance(vect.getX(), vect.getY());
    }
    
    /* return an equivalent instance which is "centered" if the center of the original shape is at (0,0)
       and "straight" if its angle is a multiple of Pi/2 */
    public abstract Shape2D getSimplifiedInstance();
    
    // used to know if a point is contained in the shape
    public abstract boolean strictlyContains(double x, double y);
    public abstract boolean contains(double x, double y);
}
