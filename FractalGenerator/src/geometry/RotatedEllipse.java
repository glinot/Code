package geometry;

// @author Raphaël


public class RotatedEllipse extends Shape2D
{
    protected double centerX, centerY, halfWidth, squaredHalfWidth, halfHeight, squaredHalfHeight,
                     theta, cosTheta, sinTheta;
    
    public RotatedEllipse(double centerX, double centerY, double halfWidth, double halfHeight, double theta)
    {
        this.centerX = centerX;
        this.centerY = centerY;
        this.halfWidth = halfWidth;
        squaredHalfWidth = halfWidth*halfWidth;
        this.halfHeight = halfHeight;
        squaredHalfHeight = halfHeight*halfHeight;
        this.theta = theta;
        cosTheta = Math.cos(theta);
        sinTheta = Math.sin(theta);
    }
    
    public RotatedEllipse(Vector2D center, double halfWidth, double halfHeight, double theta)
    {
        this(center.getX(), center.getY(), halfWidth, halfHeight, theta);
    }
    
    @Override
    public double getxMin()
    {
        return 0;
    }
    
    @Override
    public double getxMax()
    {
        return 0;
    }
    
    @Override
    public double getyMin() 
    {
        return 0;
    }
    
    @Override
    public double getyMax()
    {
        return 0;
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
        double xPrime = cosTheta*(x-centerX)+sinTheta*(y-centerY),
               yPrime = cosTheta*(y-centerY)-sinTheta*(x-centerX);
        return xPrime*xPrime/squaredHalfWidth + yPrime*yPrime/squaredHalfHeight < 1;
    }
    
    @Override
    public boolean contains(double x, double y)
    {
        double xPrime = cosTheta*(x-centerX)+sinTheta*(y-centerY),
               yPrime = cosTheta*(y-centerY)-sinTheta*(x-centerX);
        return xPrime*xPrime/squaredHalfWidth + yPrime*yPrime/squaredHalfHeight <= 1;
    }
}
