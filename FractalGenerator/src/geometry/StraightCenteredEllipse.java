package geometry;

// @author Raphaël


public class StraightCenteredEllipse extends Shape2D
{
    protected double halfWidth, squaredHalfWidth, halfHeight, squaredHalfHeight;
    
    public StraightCenteredEllipse(double halfWidth, double halfHeight)
    {
        this.halfWidth = halfWidth;
        squaredHalfWidth = halfWidth*halfWidth;
        this.halfHeight = halfHeight;
        squaredHalfHeight = halfHeight*halfHeight;
    }
    
    @Override
    public double getxMin()
    {
        return -halfWidth;
    }
    
    @Override
    public double getxMax()
    {
        return halfWidth;
    }
    
    @Override
    public double getyMin() 
    {
        return -halfHeight;
    }
    
    @Override
    public double getyMax()
    {
        return halfHeight;
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
        return x*x/squaredHalfWidth + y*y/squaredHalfHeight < 1;
    }
    
    @Override
    public boolean contains(double x, double y)
    {
        return x*x/squaredHalfWidth + y*y/squaredHalfHeight <= 1;        
    }
}
