package geometry;

// @author Raphaël


public class RotatedCenteredEllipse extends Shape2D
{
    protected double halfWidth, squaredHalfWidth, halfHeight, squaredHalfHeight,
                     theta, cosTheta, sinTheta;
    
    public RotatedCenteredEllipse(double halfWidth, double halfHeight, double theta)
    {
        this.halfWidth = halfWidth;
        squaredHalfWidth = halfWidth*halfWidth;
        this.halfHeight = halfHeight;
        squaredHalfHeight = halfHeight*halfHeight;
        this.theta = theta;
        cosTheta = Math.cos(theta);
        sinTheta = Math.sin(theta);
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
        double xPrime = cosTheta*x+sinTheta*y, yPrime = cosTheta*y-sinTheta*x;
        return xPrime*xPrime/squaredHalfWidth + yPrime*yPrime/squaredHalfHeight < 1;
    }
    
    @Override
    public boolean contains(double x, double y)
    {
        double xPrime = cosTheta*x+sinTheta*y, yPrime = cosTheta*y-sinTheta*x;
        return xPrime*xPrime/squaredHalfWidth + yPrime*yPrime/squaredHalfHeight <= 1;
    }
}
