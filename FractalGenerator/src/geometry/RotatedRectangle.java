package geometry;

// @author Raphaël


public class RotatedRectangle extends Shape2D
{
    protected double centerX, centerY, halfWidth, halfHeight, theta, cosTheta, sinTheta;
    
    public RotatedRectangle(double centerX, double centerY, double width, double height, double theta)
    {
        this.centerX = centerX;
        this.centerY = centerY;
        halfWidth = width/2;
        halfHeight = height/2;
        this.theta = theta;
        cosTheta = Math.cos(theta);
        sinTheta = Math.sin(theta);
    }
    
    public RotatedRectangle(Vector2D center, double width, double height, double theta)
    {
        this(center.getX(), center.getY(), width, height, theta);
    }
    
    @Override
    public double getxMin()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public double getxMax()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public double getyMin()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public double getyMax()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public Shape2D getTranslatedInstance(double Dx, double Dy)
    {
        centerX += Dx;
        centerY += Dy;
        
        return this;
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
        return Math.abs(cosTheta*(x-centerX)+sinTheta*(y-centerY)) < halfWidth
            && Math.abs(cosTheta*(y-centerY)-sinTheta*(x-centerX)) < halfHeight;
    }
    
    @Override
    public boolean contains(double x, double y)
    {
        return Math.abs(cosTheta*(x-centerX)+sinTheta*(y-centerY)) <= halfWidth
            && Math.abs(cosTheta*(y-centerY)-sinTheta*(x-centerX)) <= halfHeight;
    }
}
