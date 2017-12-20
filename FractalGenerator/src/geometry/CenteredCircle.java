package geometry;

//@author Raphaël


public class CenteredCircle extends Shape2D
{
    protected double radius, squaredRadius;
    
    public CenteredCircle(double radius)
    {
        this.radius = radius;
        squaredRadius = radius*radius;
    }
    
    @Override
    public double getxMin()
    {
        return -radius;
    }
    
    @Override
    public double getxMax()
    {
        return radius;
    }
    
    @Override
    public double getyMin()
    {
        return -radius;
    }
    
    @Override
    public double getyMax()
    {
        return radius;
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
        return x*x + y*y < squaredRadius;
    }
    
    @Override
    public boolean contains(double x, double y)
    {
        return x*x + y*y <= squaredRadius;
    }
}
