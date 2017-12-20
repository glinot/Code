package geometry;

// @author Raphaël


public class RotatedCenteredRectangle extends Shape2D
{
    protected double halfWidth, halfHeight, theta, cosTheta, sinTheta;
    
    public RotatedCenteredRectangle(double width, double height, double theta)
    {
        halfWidth = width/2;
        halfHeight = height/2;
        this.theta = theta;
        cosTheta = Math.cos(theta);
        sinTheta = Math.sin(theta);
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
        return Math.abs(cosTheta*x+sinTheta*y) < halfWidth
            && Math.abs(cosTheta*y-sinTheta*x) < halfHeight;
    }
    
    @Override
    public boolean contains(double x, double y)
    {
        return Math.abs(cosTheta*x+sinTheta*y) <= halfWidth
            && Math.abs(cosTheta*y-sinTheta*x) <= halfHeight;
    }
}
