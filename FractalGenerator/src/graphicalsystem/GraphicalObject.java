package graphicalsystem;

// @author Raphaël

import geometry.Vector2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

public class GraphicalObject
{
    public enum Direction {NEGATIVE, NONE, POSITIVE}
    
    protected String name;
    protected Sprite sprite;
    protected Vector2D screenPos, speed;
    protected ArrayList<String> text;
    protected int textXOffset, textYOffset, spaceBetweenLines;
    protected Color textColor;
    protected Font textFont;
    protected boolean visible, solid;
    
    float alphaRate, startAlphaRate, limitAlphaRate, fadeSpeed;
    Direction fadeDirection;
    boolean fadeStopCondition, fading;
    
    double startX, startY, limitX, limitY;
    Direction horDirection, vertDirection;
    boolean horTranslationStopCondition, vertTranslationStopCondition,
            horTranslating, vertTranslating;
    
    public GraphicalObject(String name, Sprite sprite)
    {
        this.name = name;
        this.sprite = sprite;
        this.screenPos = new Vector2D();
        this.speed = new Vector2D();
        this.text = new ArrayList();
        this.textXOffset = 10;
        this.textYOffset = 20;
        this.spaceBetweenLines = 20;
        this.textColor = Color.BLACK;
        this.textFont = Font.decode(Font.DIALOG);
        this.visible = true;
        this.solid = true;
        
        this.alphaRate = 1;
        this.fading = false;
        
        this.horTranslating = false;
        this.vertTranslating = false;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public Sprite getSprite()
    {
        return this.sprite;
    }
    
    public Vector2D getScreenPos()
    {
        return this.screenPos;
    }
    
    public double getX()
    {
        return this.screenPos.getX();
    }
    
    public int getIntX()
    {
        return (int)Math.round(this.screenPos.getX());
    }
    
    public double getY()
    {
        return this.screenPos.getY();
    }
    
    public int getIntY()
    {
        return (int)Math.round(this.screenPos.getY());
    }
    
    public Vector2D getSpeed()
    {
        return this.speed;
    }
    
    public double getXSpeed()
    {
        return this.speed.getX();
    }
    
    public double getYSpeed()
    {
        return this.speed.getY();
    }
    
    public String getText()
    {
        String result = "";
        
        for(int i = 0; i < this.text.size(); i++)
        {
            result += this.text.get(i);
            
            if(i < this.text.size()-1)
            {
                result += "\n";
            }
        }
        
        return result;
    }
    
    public int getTextXOffset()
    {
        return this.textXOffset;
    }
    
    public int getTextYOffset()
    {
        return this.textYOffset;
    }
    
    public int getSpaceBetweenLines()
    {
        return this.spaceBetweenLines;
    }
    
    public boolean isVisible()
    {
        return this.visible;
    }
    
    public boolean isSolid()
    {
        return this.solid;
    }
    
    public float getAlphaRate()
    {
        return this.alphaRate;
    }
    
    public float getStartAlphaRate()
    {
        return this.startAlphaRate;
    }
    
    public float getLimitAlphaRate()
    {
        return this.limitAlphaRate;
    }
    
    public float getFadeSpeed()
    {
        return this.fadeSpeed;
    }
    
    public Direction getFadeDirection()
    {
        return this.fadeDirection;
    }
    
    public boolean isFading()
    {
        return this.fading;
    }
    
    public double getStartX()
    {
        return this.startX;
    }
    
    public double getStartY()
    {
        return this.startY;
    }
    
    public double getLimitX()
    {
        return this.limitX;
    }
    
    public double getLimitY()
    {
        return this.limitY;
    }
    
    public Direction getHorDirection()
    {
        return this.horDirection;
    }
    
    public Direction getVertDirection()
    {
        return this.vertDirection;
    }
    
    public boolean isHorTranslating()
    {
        return this.horTranslating;
    }
    
    public boolean isVertTranslating()
    {
        return this.vertTranslating;
    }
    
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setSprite(Sprite sprite)
    {
        this.sprite = sprite;
    }
    
    public void setScreenPos(Vector2D screenPos)
    {
        this.screenPos = screenPos;
    }
    
    public void setScreenPos(IntPoint screenPos)
    {
        this.screenPos = screenPos.toVector2D();
    }
    
    public void setScreenPos(int xPos, int yPos)
    {
        this.screenPos.setCoord(xPos, yPos);
    }
    
    public void setX(double x)
    {
        this.screenPos.setX(x);
    }
    
    public void setY(double y)
    {
        this.screenPos.setY(y);
    }
    
    public void setText(String text)
    {
        this.text.clear();
        
        if(text.length() > 0)
        {
            String line;
            int i = 0;
            
            while(i < text.length()+1)
            {
                line = "";

                while(i < text.length())
                {
                    if(text.charAt(i) == '\n')
                    {
                        break;
                    }

                    line += text.charAt(i);
                    i++;
                }

                this.text.add(line);
                i++;
            }
        }
    }
    
    public void setTextXOffset(int textXOffset)
    {
        this.textXOffset = textXOffset;
    }
    
    public void setTextYOffset(int textYOffset)
    {
        this.textYOffset = textYOffset;
    }
    
    public void setSpaceBetweenLines(int spaceBetweenLines)
    {
        this.spaceBetweenLines = spaceBetweenLines;
    }
    
    public void setVisible(boolean isVisible)
    {
        this.visible = isVisible;
    }
    
    public void setSolid(boolean isSolid)
    {
        this.solid = isSolid;
    }
    
    
    public boolean checkCollision(int xPos, int yPos)
    {
        if(this.solid)
        {
            int XPosOnObject = xPos - this.getIntX(),
                YPosOnObject = yPos - this.getIntY();

            return !this.sprite.isTransparent(XPosOnObject, YPosOnObject);
        }
        
        return false;
    }
    
    public boolean checkCollision(IntPoint pos)
    {
        return this.checkCollision(pos.getAbs(), pos.getOrd());
    }
    
    void draw(Graphics g)
    {
        if(this.visible)
        {
            if(this.alphaRate == 1)
            {
                this.sprite.draw(g, this.screenPos.roundToIntPoint());
                this.drawText(g);
            }
            else
            {
                this.sprite.drawFaded(g, this.screenPos.roundToIntPoint(), this.alphaRate);
                this.drawFadedText(g);
            }
        }
    }
    
    void drawText(Graphics g)
    {
        g.setColor(this.textColor);
        g.setFont(this.textFont);
            
        for(int i = 0; i < this.text.size(); i++)
        {
            g.drawChars(this.text.get(i).toCharArray(), 0, this.text.get(i).length(),
                    this.getIntX()+this.textXOffset,
                    this.getIntY()+this.textYOffset+i*this.spaceBetweenLines);
        }
    }
    
    void drawFadedText(Graphics g)
    {
        Color fadedColor = new Color(this.textColor.getRed(),
                                     this.textColor.getGreen(),
                                     this.textColor.getBlue(),
                                     (int)(this.textColor.getAlpha()*this.alphaRate));
        g.setColor(fadedColor);
        g.setFont(this.textFont);
            
        for(int i = 0; i < this.text.size(); i++)
        {
            g.drawChars(this.text.get(i).toCharArray(), 0, this.text.get(i).length(),
                    this.getIntX()+this.textXOffset,
                    this.getIntY()+this.textYOffset+i*this.spaceBetweenLines);
        }
    }
    
    public void fadeWithStop(float startAlphaRate, boolean relativeToCurrent, float limitAlphaRate, boolean relativeToStart, float fadeSpeed)
    {
        float absoluteStart = startAlphaRate + (relativeToCurrent ? this.alphaRate : 0),
              absoluteLimit = limitAlphaRate + (relativeToStart ? absoluteStart : 0);
        
        this.alphaRate = absoluteStart;
        this.startAlphaRate = absoluteStart;
        this.limitAlphaRate = absoluteLimit;
        this.fadeStopCondition = true;
        
        if(absoluteLimit > absoluteStart)
        {
            this.fadeDirection = Direction.POSITIVE;
            this.fadeSpeed = Math.abs(fadeSpeed);
            this.fading = true;
        }
        else if(absoluteLimit < absoluteStart)
        {
            this.fadeDirection = Direction.NEGATIVE;
            this.fadeSpeed = -Math.abs(fadeSpeed);
            this.fading = true;
        }
        else
        {
            this.fadeDirection = Direction.NONE;
            this.fadeSpeed = 0;
            this.fading = false;
        }
    }
    
    public void fadeWithStop(float limitAlphaRate, boolean relativeToStart, float fadeSpeed)
    {
        this.fadeWithStop(0, true, limitAlphaRate, relativeToStart, fadeSpeed);
    }
    
    public void fade(float startAlphaRate, boolean relativeToCurrent, float fadeSpeed)
    {
        float absoluteStart = startAlphaRate + (relativeToCurrent ? this.alphaRate : 0);
        
        this.alphaRate = absoluteStart;
        this.startAlphaRate = absoluteStart;
        this.fadeSpeed = fadeSpeed;
        this.fadeStopCondition = false;
        
        if(fadeSpeed > 0)
        {
            this.fadeDirection = Direction.POSITIVE;
            this.fading = true;
        }
        else if(fadeSpeed < 0)
        {
            this.fadeDirection = Direction.NEGATIVE;
            this.fading = true;
        }
        else
        {
            this.fadeDirection = Direction.NONE;
            this.fading = false;
        }
    }
    
    public void fade(float fadeSpeed)
    {
        this.fade(0, true, fadeSpeed);
    }
    
    boolean incrementAlphaRate(int millisDelay)
    {
        if(this.fading)
        {
            float newAlphaRate = this.alphaRate + this.fadeSpeed * millisDelay / 1000;

            if(this.fadeStopCondition
                    && this.fadeDirection == Direction.POSITIVE
                    && newAlphaRate >= this.limitAlphaRate)
            {
                this.alphaRate = this.limitAlphaRate;
                this.fading = false;
            }
            else if(this.fadeStopCondition
                    && this.fadeDirection == Direction.NEGATIVE
                    && newAlphaRate <= this.limitAlphaRate)
            {
                this.alphaRate = this.limitAlphaRate;
                this.fading = false;
            }
            else
            {
                this.alphaRate = newAlphaRate;
            }

            return true;
        }
        
        return false;
    }
    
    public void translateHorWithStop(double startX, boolean relativeToCurrent, double limitX, boolean relativeToStart, double horSpeed)
    {
        double absoluteStart = startX + (relativeToCurrent ? this.screenPos.getX() : 0),
               absoluteLimit = limitX + (relativeToStart ? absoluteStart : 0);
        
        this.screenPos.setX(absoluteStart);
        this.startX = absoluteStart;
        this.limitX = absoluteLimit;
        this.horTranslationStopCondition = true;
        
        if(absoluteLimit > absoluteStart)
        {
            this.horDirection = Direction.POSITIVE;
            this.speed.setX(Math.abs(horSpeed));
            this.horTranslating = true;
        }
        else if(absoluteLimit < absoluteStart)
        {
            this.horDirection = Direction.NEGATIVE;
            this.speed.setX(-Math.abs(horSpeed));
            this.horTranslating = true;
        }
        else
        {
            this.horDirection = Direction.NONE;
            this.speed.setX(0);
            this.horTranslating = false;
        }
    }
    
    public void translateHorWithStop(double limitX, boolean relativeToStart, double horSpeed)
    {
        this.translateHorWithStop(0, true, limitX, relativeToStart, horSpeed);
    }
    
    public void translateHor(double startX, boolean relativeToCurrent, double horSpeed)
    {
        double absoluteStart = startX + (relativeToCurrent ? this.screenPos.getX() : 0);
        
        this.screenPos.setX(absoluteStart);
        this.startX = absoluteStart;
        this.speed.setX(horSpeed);
        this.horTranslationStopCondition = false;
        
        if(horSpeed > 0)
        {
            this.horDirection = Direction.POSITIVE;
            this.horTranslating = true;
        }
        else if(horSpeed < 0)
        {
            this.horDirection = Direction.NEGATIVE;
            this.horTranslating = true;
        }
        else
        {
            this.horDirection = Direction.NONE;
            this.horTranslating = false;
        }
    }
    
    public void translateHor(double horSpeed)
    {
        this.translateHor(0, true, horSpeed);
    }
    
    public void translateVertWithStop(double startY, boolean relativeToCurrent, double limitY, boolean relativeToStart, double vertSpeed)
    {
        double absoluteStart = startY + (relativeToCurrent ? this.screenPos.getY() : 0),
               absoluteLimit = limitY + (relativeToStart ? absoluteStart : 0);
        
        this.screenPos.setY(absoluteStart);
        this.startY = absoluteStart;
        this.limitY = absoluteLimit;
        this.vertTranslationStopCondition = true;
        
        if(absoluteLimit > absoluteStart)
        {
            this.vertDirection = Direction.POSITIVE;
            this.speed.setY(Math.abs(vertSpeed));
            this.vertTranslating = true;
        }
        else if(absoluteLimit < absoluteStart)
        {
            this.vertDirection = Direction.NEGATIVE;
            this.speed.setY(-Math.abs(vertSpeed));
            this.vertTranslating = true;
        }
        else
        {
            this.vertDirection = Direction.NONE;
            this.speed.setY(0);
            this.vertTranslating = false;
        }
    }
    
    public void translateVertWithStop(double limitY, boolean relativeToStart, double vertSpeed)
    {
        this.translateVertWithStop(0, true, limitY, relativeToStart, vertSpeed);
    }
    
    public void translateVert(double startY, boolean relativeToCurrent, double vertSpeed)
    {
        double absoluteStart = startY + (relativeToCurrent ? this.screenPos.getY() : 0);
        
        this.screenPos.setY(absoluteStart);
        this.startY = absoluteStart;
        this.speed.setY(vertSpeed);
        this.vertTranslationStopCondition = false;
        
        if(vertSpeed > 0)
        {
            this.vertDirection = Direction.POSITIVE;
            this.vertTranslating = true;
        }
        else if(vertSpeed < 0)
        {
            this.vertDirection = Direction.NEGATIVE;
            this.vertTranslating = true;
        }
        else
        {
            this.vertDirection = Direction.NONE;
            this.vertTranslating = false;
        }
    }
    
    public void translateVert(double vertSpeed)
    {
        this.translateVert(0, true, vertSpeed);
    }
    
    boolean incrementXPos(int millisDelay)
    {
        if(this.horTranslating)
        {
            double newXPos = this.getX() + this.getXSpeed()*millisDelay/1000;

            if(this.horTranslationStopCondition
                    && this.horDirection == Direction.POSITIVE
                    && newXPos >= this.limitX)
            {
                this.screenPos.setX(this.limitX);
                this.horTranslating = false;
            }
            else if(this.horTranslationStopCondition
                    && this.horDirection == Direction.NEGATIVE
                    && newXPos <= this.limitX)
            {
                this.screenPos.setX(this.limitX);
                this.horTranslating = false;
            }
            else
            {
                this.screenPos.setX(newXPos);
            }
            
            return true;
        }
        
        return false;
    }
    
    boolean incrementYPos(int millisDelay)
    {
        if(this.vertTranslating)
        {
            double newYPos = this.getY() + this.getYSpeed()*millisDelay/1000;

            if(this.vertTranslationStopCondition
                    && this.vertDirection == Direction.POSITIVE
                    && newYPos >= this.limitY)
            {
                this.screenPos.setY(this.limitY);
                this.vertTranslating = false;
            }
            else if(this.vertTranslationStopCondition
                    && this.vertDirection == Direction.NEGATIVE
                    && newYPos <= this.limitY)
            {
                this.screenPos.setY(this.limitY);
                this.vertTranslating = false;
            }
            else
            {
                this.screenPos.setY(newYPos);
            }
            
            return true;
        }
        
        return false;
    }
}
