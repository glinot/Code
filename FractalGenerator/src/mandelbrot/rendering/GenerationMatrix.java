package mandelbrot.rendering;

// @author Raphaël

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;


public class GenerationMatrix
{
    protected boolean[] matrix;
    protected int width, height;
    
    public GenerationMatrix(int width, int height)
    {
        matrix = new boolean[width*height];
        this.width = width;
        this.height = height;
    }
    
    public GenerationMatrix(boolean[] matrix, int width)
    {
        this.matrix = matrix;
        this.width = width;
        height = matrix.length / width;
    }
    
    public GenerationMatrix(boolean[][] matrix)
    {
        width = matrix.length;
        height = matrix[0].length;
        this.matrix = new boolean[width*height];
        
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                this.matrix[phi(x, y, width)] = matrix[x][y];
            }
        }
    }
    
    public boolean[] getMatrix()
    {
        return matrix;
    }
    
    public boolean getValAt(int n)
    {
        return matrix[n];
    }
    
    public boolean getValAt(int x, int y)
    {
        return matrix[phi(x, y)];
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    public void setMatrix(boolean[] matrix, int width)
    {
        this.matrix = matrix;
        this.width = width;
        height = matrix.length / width;
    }
    
    public void setMatrix(boolean[][] matrix)
    {
        width = matrix.length;
        height = matrix[0].length;
        this.matrix = new boolean[width*height];
        
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                this.matrix[phi(x, y, width)] = matrix[x][y];
            }
        }
    }
    
    public void setValAt(int n, boolean value)
    {
        matrix[n] = value;
    }
    
    public void setValAt(int x, int y, boolean value)
    {
        matrix[phi(x, y)] = value;
    }
    
    public GenerationMatrix getPortionInstance(int x0, int y0, int width, int height)
    {
        boolean[] portion = new boolean[width*height];
        
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                if(isInBoundaries(x0+x, y0+y))
                {
                    portion[phi(x, y, width)] = matrix[phi(x0+x, y0+y)];
                }
                else
                {
                    portion[phi(x, y, width)] = false;
                }
            }
        }
        
        return new GenerationMatrix(portion, width);
    }
    
    
    // stretch a matrix to the new width and height
    public GenerationMatrix getStretchedInstance(int newWidth, int newHeight)
    {
        boolean[] horStretched = new boolean[newWidth*height],
                  stretched = new boolean[newWidth*newHeight];
        double sizeSum;
        int sequelStart, sequelSize, nextSequelStart;
        boolean boolValue;
        
        // horizontal stretch
        for(int y = 0; y < height; y++)
        {
            nextSequelStart = 0;
            sizeSum = 0;
            int x = 0;
            
            while(x < width)
            {
                sequelSize = 0;
                boolValue = matrix[phi(x, y)];
                
                do
                {
                    sequelSize++;
                    x++;
                    
                    if(x == width)
                    {
                        break;
                    }
                } while(matrix[phi(x, y)] == boolValue);
                
                sequelStart = nextSequelStart;
                sizeSum += sequelSize*newWidth/(width+0.0);
                nextSequelStart = (int)sizeSum;
                
                for(int i = sequelStart; i < nextSequelStart; i++)
                {
                    horStretched[phi(i, y, newWidth)] = boolValue;
                }
            }
        }
        
        // vertical stretch
        for(int x = 0; x < newWidth; x++)
        {
            nextSequelStart = 0;
            sizeSum = 0;
            int y = 0;
            
            while(y < height)
            {
                sequelSize = 0;
                boolValue = horStretched[phi(x, y, newWidth)];
                
                do
                {
                    sequelSize++;
                    y++;
                    
                    if(y == height)
                    {
                        break;
                    }
                }while(horStretched[phi(x, y, newWidth)] == boolValue);
                
                sequelStart = nextSequelStart;
                sizeSum += sequelSize*newHeight/(height+0.0);
                nextSequelStart = (int)sizeSum;
                
                for(int i = sequelStart; i < nextSequelStart; i++)
                {
                    stretched[phi(x, i, newWidth)] = boolValue;
                }
            }
        }
        
        return new GenerationMatrix(stretched, newWidth);
    }
    
    // trim a layer of ones in favor of the zeros
    public void trim()
    {
        ArrayList<Integer> borderPointsX = new ArrayList(),
                           borderPointsY = new ArrayList();
        boolean leftExists, rightExists, upExists, downExists, pointAdded;
        
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                if(matrix[phi(x, y)])
                {
                    leftExists = x > 0;
                    rightExists = x < width-1;
                    upExists = y > 0;
                    downExists = y < height-1;
                    
                    if((rightExists                && !matrix[phi(x+1, y)])
                     /*|| rightExists && downExists  && !matrix[phi(x+1, y+1)]*/
                     || downExists                 && !matrix[phi(x, y+1)]
                     /*|| downExists  && leftExists  && !matrix[phi(x-1, y+1)]*/
                     || leftExists                 && !matrix[phi(x-1, y)]
                     /*|| leftExists  && upExists    && !matrix[phi(x-1, y-1)]*/
                     || upExists                   && !matrix[phi(x, y-1)]
                     /*|| upExists    && rightExists && !matrix[phi(x+1, y-1)]*/)
                    {
                        borderPointsX.add(x);
                        borderPointsY.add(y);
                    }
                }
            }
        }
        
        for(int i = 0; i < borderPointsX.size(); i++)
        {
            matrix[phi(borderPointsX.get(i), borderPointsY.get(i))] = false;
        }
    }
    
    @Override
    public String toString()
    {
        String result = "";
        
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                result += matrix[phi(x, y)] ? " 1" : " 0";
            }
            
            result += '\n';
        }
        
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof GenerationMatrix)
        {
            GenerationMatrix gm = (GenerationMatrix)obj;
            return Arrays.equals(matrix, gm.matrix) && width == gm.width;
        }
        else
        {
            return false;
        }
    }
    
    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 37 * hash + Arrays.hashCode(matrix);
        hash = 37 * hash + width;
        hash = 37 * hash + height;
        
        return hash;
    }
    
    @Override
    public GenerationMatrix clone() throws CloneNotSupportedException
    {
        super.clone();
        
        return new GenerationMatrix(matrix.clone(), width);
    }
    
    public boolean isInBoundaries(int x, int y)
    {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    public int phi(int x, int y)
    {
        return y*width + x;
    }
    
    public static int phi(int x, int y, int width)
    {
        return y*width + x;
    }
    
    public static void main(String[] args)
    {
        boolean[][] m = new boolean[][]{{false, false,  true,  true, false, false},
                                        {false, false,  true,  true,  true,  true},
                                        {false, false,  true,  true,  true, false},
                                        {false,  true,  true,  true,  true, false},
                                        { true,  true,  true,  true, false, false},
                                        { true,  true,  true,  true, false,  true}};
        GenerationMatrix gm = new GenerationMatrix(m);
        System.out.println(gm);
        gm = gm.getStretchedInstance(20, 20);
        System.out.println(gm);
        System.out.println(gm.getPortionInstance(12, 15, 10, 10));
    }
}
