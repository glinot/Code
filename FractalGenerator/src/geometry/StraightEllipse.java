package geometry;

// @author Raphaël


public class StraightEllipse extends Shape2D
{
    protected double centerX, centerY, halfWidth, squaredHalfWidth, halfHeight, squaredHalfHeight;
    
    public StraightEllipse(double centerX, double centerY, double halfWidth, double halfHeight)
    {
        this.centerX = centerX;
        this.centerY = centerY;
        this.halfWidth = halfWidth;
        squaredHalfWidth = halfWidth*halfWidth;
        this.halfHeight = halfHeight;
        squaredHalfHeight = halfHeight*halfHeight;
    }
    
    public StraightEllipse(Vector2D center, double halfWidth, double halfHeight)
    {
        this(center.getX(), center.getY(), halfWidth, halfHeight);
    }
    
    @Override
    public double getxMin()
    {
        return centerX-halfWidth;
    }
    
    @Override
    public double getxMax()
    {
        return centerX+halfWidth;
    }
    
    @Override
    public double getyMin() 
    {
        return centerY-halfHeight;
    }
    
    @Override
    public double getyMax()
    {
        return centerY+halfHeight;
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
        return (x-centerX)*(x-centerX)/squaredHalfWidth + (y-centerY)*(y-centerY)/squaredHalfHeight < 1;
    }
    
    @Override
    public boolean contains(double x, double y)
    {
        return (x-centerX)*(x-centerX)/squaredHalfWidth + (y-centerY)*(y-centerY)/squaredHalfHeight <= 1;
    }
}
