/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot.rendering.threading;

import mandelbrot.rendering.GenerationMatrix;

/**
 *
 * @author Gregoire
 */
public class PreviewGenerationMatrix extends GenerationMatrix{

    private int step;
    
    public PreviewGenerationMatrix(int width, int height,int step) {
        super(width, height);
        this.step =step;
    }

    public PreviewGenerationMatrix(boolean[] matrix, int width,int step) {
        super(matrix, width);
         this.step =step;
    }

    public PreviewGenerationMatrix(boolean[][] matrix) {
        super(matrix);
         this.step =step;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public PreviewGenerationMatrix getStretchedInstance(int newWidth, int newHeight,int step) {
      
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
        return new PreviewGenerationMatrix(stretched, newWidth,step);
        
    }
    
}
