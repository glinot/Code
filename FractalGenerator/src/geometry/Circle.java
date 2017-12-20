package geometry;

// @author Raphaël


public class Circle extends Shape2D
{
    protected double centerX, centerY, radius, squaredRadius;
    
    public Circle(double centerX, double centerY, double radius)
    {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        squaredRadius = radius*radius;
    }
    
    public Circle(Vector2D center, double radius)
    {
        this(center.getX(), center.getY(), radius);
    }
    
    @Override
    public double getxMin()
    {
        return centerX-radius;
    }
    
    @Override
    public double getxMax()
    {
        return centerX+radius;
    }
    
    @Override
    public double getyMin() 
    {
        return centerY-radius;
    }
    
    @Override
    public double getyMax()
    {
        return centerY+radius;
    }
    
    @Override
    public Shape2D getTranslatedInstance(double Dx, double Dy)
    {
        return null;
    }
    
    @Override
    public Shape2D getRelativelyRotatedInstance(double Dtheta)
    {
        return null;
    }
    
    @Override
    public Shape2D getAbsolutelyRotatedInstance(double theta)
    {
        return null;
    }
    
    @Override
    public Shape2D getSimplifiedInstance()
    {
        return null;
    }
    
    @Override
    public boolean strictlyContains(double x, double y)
    {
        return (x-centerX)*(x-centerX) + (y-centerY)*(y-centerY) < squaredRadius;
    }
    
    @Override
    public boolean contains(double x, double y)
    {
        return (x-centerX)*(x-centerX) + (y-centerY)*(y-centerY) <= squaredRadius;        
    }
}
