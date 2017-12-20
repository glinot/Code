package mandelbrot.rendering.threading;

// @author Gregoire

public class Parcel
{
    protected int X, DX, // X and deltaX
                  Y, DY; // Y and deltaY
    protected double x0;
    protected double y0;
    protected double resolution;
    
    public Parcel(int X, int Y , int DX, int DY, double x0, double y0, double resolution)
    {
        this.X = X;
        this.Y = Y;
        this.DX = DX;
        this.DY = DY;
        this.x0 = x0;
        this.y0 = y0;
        this.resolution = resolution;
    }

    public int getX() {
        return X;
    }

    public int getDX() {
        return DX;
    }

    public int getY() {
        return Y;
    }

    public int getDY() {
        return DY;
    }

    public double getX0() {
        return x0;
    }

    public double getY0() {
        return y0;
    }

    public double getResolution() {
        return resolution;
    }

    public void setX(int X) {
        this.X = X;
    }

    public void setDX(int DX) {
        this.DX = DX;
    }

    public void setY(int Y) {
        this.Y = Y;
    }

    public void setDY(int DY) {
        this.DY = DY;
    }

    public void setX0(double x0) {
        this.x0 = x0;
    }

    public void setY0(double y0) {
        this.y0 = y0;
    }

    public void setResolution(double resolution) {
        this.resolution = resolution;
    }

    @Override
    public String toString() {
        return "Parcel{" + "X=" + X + ", DX=" + DX + ", Y=" + Y + ", DY=" + DY + ", x0=" + x0 + ", y0=" + y0 + ", resolution=" + resolution + '}';
    }
    
}
