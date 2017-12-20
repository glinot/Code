package util;

// @author Raphaël


public final class Strings
{
    // add as many times a char on the left as needed to match the total size
    public static String completeWithChar(String s, int totalSize, char c)
    {
        int chainLength = s.length();
        
        for(int i = chainLength; i < totalSize; i++)
        {
            s = c + s;
        }
        
        return s;
    }
    
    public static String completeWithChar(int n, int totalSize, char c)
    {
        return completeWithChar(Integer.toString(n), totalSize, c);
    }
    
    public static int integerLength(int n)
    {
        return Integer.toString(n).length();
    }
}
